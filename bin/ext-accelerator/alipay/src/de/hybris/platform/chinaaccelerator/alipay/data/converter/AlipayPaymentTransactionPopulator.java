package de.hybris.platform.chinaaccelerator.alipay.data.converter;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;
import de.hybris.platform.chinaaccelerator.alipay.data.ProcessingRequestData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AlipayPaymentInfoModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayTransactionStatus;

public class AlipayPaymentTransactionPopulator extends AbstractResultPopulator<ProcessingRequestData, OrderModel>{

	private ModelService modelService;
	private Logger LOG = Logger.getLogger(AlipayPaymentTransactionPopulator.class.getName());
	private TypeService typeService;
	private KeyGenerator paymentTransactionKeyGenerator;
	
	@Override
	public void populate(ProcessingRequestData requestData, OrderModel order)
			throws ConversionException {
		/*
		 * Setting up AlipayPaymentInfoModel
		 * */
		AlipayPaymentInfoModel paymentInfo = (AlipayPaymentInfoModel) order.getPaymentInfo();
		paymentInfo.setOutTradeNo(requestData.getAlipayRequestData().getOut_trade_no());
		order.setTransactionInitiateDate(new Date());
		order.setPaymentStatus(PaymentStatus.NOTPAID);
		getModelService().save(paymentInfo);
		getModelService().save(order);
		
		/*
		 * Setting up AlipayPaymentTransactionModel
		 * */
		AlipayPaymentTransactionModel transaction = (AlipayPaymentTransactionModel)getModelService().create(AlipayPaymentTransactionModel.class);
		transaction.setOrder(order);
		transaction.setCode(order.getCode());
		transaction.setUrl(requestData.getRequestUrl());
		transaction.setRequestId(requestData.getAlipayRequestData().getOut_trade_no());
		transaction.setPaymentProvider(PaymentConstants.Basic.PAYMENT_PROVIDER);
		transaction.setLatestTradeStatus(AlipayTradeStatus.PAYMENT_REQUEST.name());
		transaction.setLatestTransactionType(PaymentTransactionType.REQUEST.getCode());
		getModelService().save(transaction);
		getModelService().refresh(order);
		PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel)getModelService().create(PaymentTransactionEntryModel.class);
		entry.setAmount(BigDecimal.valueOf(order.getTotalPrice()));
		if(order.getCurrency() != null){
			entry.setCurrency(order.getCurrency());
		}
		entry.setType(PaymentTransactionType.REQUEST);
		entry.setTime(order.getTransactionInitiateDate());
		entry.setPaymentTransaction(transaction);
		entry.setRequestId(transaction.getRequestId());
		entry.setTransactionStatus(AlipayTransactionStatus.PAYMENT_REQUEST.getCode());
		entry.setTransactionStatusDetails(getTypeService().getEnumerationValue(AlipayTransactionStatus.PAYMENT_REQUEST).getName());//TODO fill in reasonable TransactionStatusDetails
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
