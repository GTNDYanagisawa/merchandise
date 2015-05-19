package de.hybris.platform.chinaaccelerator.alipay.data.converter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import cn.alipay.payment.util.UtilDate;
import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayNotifyInfoData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayTransactionStatus;
import de.hybris.platform.chinaaccelerator.alipay.order.dao.AlipayPaymentTransactionEntryDao;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

public class AlipayPaymentTransactionEntryPopulator extends AbstractResultPopulator<AlipayNotifyInfoData, PaymentTransactionModel>{

	private ModelService modelService;
	private Logger LOG = Logger.getLogger(AlipayPaymentTransactionEntryPopulator.class.getName());
	private TypeService typeService;
	private AlipayPaymentTransactionEntryDao alipayPaymentTransactionEntryDao;
	private KeyGenerator paymentTransactionKeyGenerator;

	@Override
	public void populate(AlipayNotifyInfoData notifyData,PaymentTransactionModel transaction) throws ConversionException {
		/*Fullfillment for cancel order only accepts the order with single entry.*/
		AlipayPaymentTransactionEntryModel entry = notifyData.getNotify_id()==null?null:getAlipayPaymentTransactionEntryDao().findPaymentTransactionEntryByNotifyId(notifyData.getNotify_id());
		if(entry!=null){
			if (LOG.isDebugEnabled())
			{
				LOG.debug("\n"+Calendar.getInstance().getTime() + ", update notify for "+entry.getCode()+" with notify id: " + notifyData.getNotify_id());
			}
		}else{
			entry = (AlipayPaymentTransactionEntryModel)getModelService().create(AlipayPaymentTransactionEntryModel.class);
		}
		entry.setRequestId(transaction.getRequestId());//out_trade_no.
		SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.simple);
		try {
			entry.setTime(notifyData.getNotify_time()==null?new Date():sdf.parse(notifyData.getNotify_time()));//(new SimpleDateFormat(UtilDate.simple).parse(returnData.getNotify_time()));
			
			if(notifyData.getGmt_create()!=null){
				entry.setGmtCreate(sdf.parse(notifyData.getGmt_create()));
			}
			if(notifyData.getGmt_payment()!=null){
				entry.setGmtPayment(sdf.parse(notifyData.getGmt_payment()));
			}
			if(notifyData.getGmt_close()!=null){
				entry.setGmtClose(sdf.parse(notifyData.getGmt_close()));
			}
			if(notifyData.getGmt_refund() != null){
				entry.setGmtRefund(sdf.parse(notifyData.getGmt_refund()));	
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		entry.setNotifyType(notifyData.getNotify_type());
		entry.setNotifyId(notifyData.getNotify_id());
		entry.setPaymentType(notifyData.getPayment_type());
		entry.setTradeAlipayNo(notifyData.getTrade_no());
		entry.setTradeStatus(notifyData.getTrade_status());
		entry.setRefundStatus(notifyData.getRefund_status());	
		entry.setBuyerEmail(notifyData.getBuyer_email());
		entry.setBuyerId(notifyData.getBuyer_id());
		entry.setBody(notifyData.getBody());
		entry.setIsTotalFeeAdjust(notifyData.getIs_total_fee_adjust());		
		entry.setUseCoupon(notifyData.getUse_coupon());
		entry.setOutChannelType(notifyData.getOut_channel_type());
		entry.setOutChannelAmount(notifyData.getOut_channel_amount());
		
		entry.setAmount(BigDecimal.valueOf(notifyData.getTotal_fee()));
		entry.setPaymentTransaction(transaction);
		
		PaymentTransactionType transactionType = PaymentTransactionType.NOTIFY;
		if(notifyData.getTransactionType()!=null && !notifyData.getTransactionType().isEmpty()){
			transactionType = PaymentTransactionType.valueOf(notifyData.getTransactionType());
		}
		if(transactionType.equals(PaymentTransactionType.CHECK_TRADE)){
			entry.setFlag_trade_locked(notifyData.getFlag_trade_locked());
			entry.setGmt_last_modified_time(notifyData.getGmt_last_modified_time());
			entry.setOperator_role(notifyData.getOperator_role());
			entry.setTime_out(notifyData.getTime_out());
			entry.setTime_out_type(notifyData.getTime_out_type());
			entry.setTo_buyer_fee(notifyData.getTo_buyer_fee());
			entry.setTo_seller_fee(notifyData.getTo_seller_fee());
			entry.setAdditional_trade_status(notifyData.getAdditional_trade_status());
			if (LOG.isDebugEnabled())
			{
				LOG.debug("\nSet transactionEntry - "+ entry.getRequestId()+", Trade_status: "+notifyData.getTrade_status() +", Additional_trade_status:"+notifyData.getAdditional_trade_status());
			}
			entry.setTransactionStatus(AlipayTransactionStatus.CHECK_TRADE.getCode());
			entry.setTransactionStatusDetails(getTypeService().getEnumerationValue(AlipayTransactionStatus.CHECK_TRADE).getName() + ", trade_status: " + notifyData.getTrade_status() + ", refund_status: "+ notifyData.getRefund_status());
			entry.setType(PaymentTransactionType.CHECK_TRADE);
		}else if(notifyData.getRefund_status()!=null && !notifyData.getRefund_status().isEmpty()){
			entry.setTransactionStatus(AlipayTransactionStatus.NOTIFY_AFTER_REFUND.getCode());
			entry.setTransactionStatusDetails(getTypeService().getEnumerationValue(AlipayTransactionStatus.NOTIFY_AFTER_REFUND).getName() + ", trade_status: "+  notifyData.getTrade_status() +", refund_status: "+ notifyData.getRefund_status());
			entry.setType(PaymentTransactionType.REFUND_STANDALONE);
		}else{
			entry.setTransactionStatus(AlipayTransactionStatus.NOTIFY_AFTER_PAYMENT.getCode());
			entry.setTransactionStatusDetails(getTypeService().getEnumerationValue(AlipayTransactionStatus.NOTIFY_AFTER_PAYMENT).getName() + ", trade_status: "+ notifyData.getTrade_status());
			entry.setType(PaymentTransactionType.NOTIFY);			
		}
		
		entry.setRefund_agent_pay_fee(notifyData.getRefund_agent_pay_fee());
		entry.setRefund_cash_fee(notifyData.getRefund_cash_fee());
		entry.setRefund_coupon_fee(notifyData.getRefund_coupon_fee());
		entry.setRefund_fee(notifyData.getRefund_fee());
		entry.setRefund_flow_type(notifyData.getRefund_flow_type());
		entry.setRefund_id(notifyData.getRefund_id());
		
		entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
		getModelService().save(entry);
		getModelService().refresh(transaction);		
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService() {
		return modelService;
	}

	/**
	 * @param modelService the modelService to set
	 */
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
	
	/**
	 * @return the typeService
	 */
	public TypeService getTypeService() {
		return typeService;
	}

	/**
	 * @param typeService the typeService to set
	 */
	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	/**
	 * @return the alipayPaymentTransactionEntryDao
	 */
	public AlipayPaymentTransactionEntryDao getAlipayPaymentTransactionEntryDao() {
		return alipayPaymentTransactionEntryDao;
	}

	/**
	 * @param alipayPaymentTransactionEntryDao the alipayPaymentTransactionEntryDao to set
	 */
	public void setAlipayPaymentTransactionEntryDao(
			AlipayPaymentTransactionEntryDao alipayPaymentTransactionEntryDao) {
		this.alipayPaymentTransactionEntryDao = alipayPaymentTransactionEntryDao;
	}

	/**
	 * @return the paymentTransactionKeyGenerator
	 */
	public KeyGenerator getPaymentTransactionKeyGenerator() {
		return paymentTransactionKeyGenerator;
	}

	/**
	 * @param paymentTransactionKeyGenerator the paymentTransactionKeyGenerator to set
	 */
	public void setPaymentTransactionKeyGenerator(
			KeyGenerator paymentTransactionKeyGenerator) {
		this.paymentTransactionKeyGenerator = paymentTransactionKeyGenerator;
	}
	
}
