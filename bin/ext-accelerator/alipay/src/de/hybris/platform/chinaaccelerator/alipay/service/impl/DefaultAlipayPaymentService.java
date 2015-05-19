/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;



import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import cn.alipay.payment.util.AlipayUtil;
import cn.alipay.payment.util.UtilDate;
//import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayCheckTradeResponseData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayNotifyInfoData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayReturnData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayTradeResponseData;
import de.hybris.platform.chinaaccelerator.alipay.data.ProcessingRequestData;
import de.hybris.platform.chinaaccelerator.alipay.data.converter.AlipayPaymentTransactionEntryPopulator;
import de.hybris.platform.chinaaccelerator.alipay.data.converter.AlipayPaymentTransactionPopulator;
import de.hybris.platform.chinaaccelerator.alipay.data.converter.AlipayRequestDataConverter;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.RequestServiceType;
import de.hybris.platform.chinaaccelerator.alipay.service.AlipayNotifyService;
import de.hybris.platform.chinaaccelerator.alipay.service.AlipayPaymentService;
import de.hybris.platform.chinaaccelerator.alipay.util.AlipayService;
import de.hybris.platform.chinaaccelerator.alipay.util.AlipayServiceUtil;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AlipayPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayTransactionStatus;
import de.hybris.platform.chinaaccelerator.alipay.enums.ServiceType;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class DefaultAlipayPaymentService extends DefaultPaymentServiceImpl implements AlipayPaymentService
{
	private static final Logger LOG = Logger.getLogger(DefaultAlipayPaymentService.class.getName());
	
	private final String REQ_ID = "req_id";
	private final String SIGN = "sign";
	private final String FORMAT = "format";
	private final String REQ_DATA = "req_data";
	private final String CALL_BACK_URL = "call_back_url";
	private final String ERROR_XML_TAG="<err>";
	private final String RES_DATA = "res_data";
	private final String RES_ERR = "res_error";
	private final String SEC_ID = "sec_id"; //sign type
	private final String VERSION = "v"; //version
	private final String NOTIFY_DATA = "notify_data";
	private final String SERVICE = "service";
	private final String PARTNER = "partner";
	private final String NOTIFY_ID = "notify_id";

	private AlipayRequestDataConverter requestDataConverter;
	private ModelService modelService;
	private AlipayService alipayService;
	private AlipayConfiguration alipayConfiguration;
	private AlipayPaymentTransactionEntryPopulator paymentTransactionEntryPopulator;
	private AlipayPaymentTransactionPopulator paymentTransactionPopulator;
	private UiExperienceService uiExperienceService;
	// TODO: check if ok private HashMap<UiExperienceLevel, List<ServiceType>> siteAgentServiceTypeMap;
	private TypeService typeService;
	private KeyGenerator paymentTransactionKeyGenerator;
	
	
	@Resource
	private AlipayNotifyService alipayNotifyService;;
	
	
	/**
	 * Initialize the Alipay payment - create a new AlipayPaymentTransaction with populated information, 
	 * create a new Authorization PaymentTransactionEntry. 
	 * */
	@Override
	public boolean initiate(final OrderModel order)
	{
		validateParameterNotNull(order, "The given order is null!");
		boolean response = false;
		if(order.getStatus()==null || order.getStatus().equals(OrderStatus.AWAITING_PAYMENT)){
			/* Persist the information into AlipayPaymentInfoModel */
			final PaymentInfoModel paymentInfoModel = order.getPaymentInfo();
			if (paymentInfoModel != null)
			{
				if (paymentInfoModel instanceof AlipayPaymentInfoModel)
				{
					/* Generate AlipayRequestData for constructing the Alipay Request URL */
					ProcessingRequestData requestData = getRequestDataConverter().convert(order);
					initiateRequestUrl(order, requestData, ((AlipayPaymentInfoModel) paymentInfoModel).getServiceType());
					/* Set up the Payment Transaction */ 
					getPaymentTransactionPopulator().populate(requestData, order);
				}
			}
		}else{
			LOG.error("Order "+order.getCode() +" is no longer valid for payment :"+ order.getStatus());
		}
		return response;
	}
	
	/**
	 * Generate a new requestURL depends on the OrderModel provided.
	 * */
	@Override
	public String getRequestUrl(OrderModel orderModel) {
		validateParameterNotNull(orderModel, "The given order is null!");
		if(orderModel.getStatus()==null || orderModel.getStatus().equals(OrderStatus.AWAITING_PAYMENT)){
			for (final PaymentTransactionModel transaction : orderModel.getPaymentTransactions())
			{
				if (transaction instanceof AlipayPaymentTransactionModel)
				{
					AlipayPaymentTransactionModel trans = (AlipayPaymentTransactionModel) orderModel.getPaymentTransactions().get(0);
					final ProcessingRequestData requestData = getRequestDataConverter().convert(orderModel);
					ServiceType serviceType = orderModel.getPaymentInfo()==null?null:((AlipayPaymentInfoModel)orderModel.getPaymentInfo()).getServiceType();
					initiateRequestUrl(orderModel, requestData, serviceType);
					trans.setUrl(requestData.getRequestUrl());
					getModelService().save(trans);
					getModelService().refresh(orderModel);				
					if (LOG.isDebugEnabled())
					{
					LOG.debug("\n PaymentTransaction URL: "+ trans.getUrl() +" for Order: "+ trans.getRequestId());
					}
					return trans.getUrl();
				}
			}
		}else{
			LOG.error("Order "+orderModel.getCode() +" is no longer valid for payment :"+ orderModel.getStatus());
		}
		return "";
	}

	/**
	 * Generate a new requestURL depends on the UiExperienceLevel
	 */
	private String initiateRequestUrl(final OrderModel order,
			final ProcessingRequestData requestData, ServiceType serviceType) {
		UiExperienceLevel uiExpLevel = getUiExperienceService().getDetectedUiExperienceLevel();
		
		String redirectURL = "";
		if(uiExpLevel.equals(UiExperienceLevel.MOBILE)){
			//TODO
		}
		if(redirectURL.isEmpty()){
			if (LOG.isDebugEnabled()){
				LOG.debug("\n"+"This is Desktop payment."+"\n");
			}
			redirectURL = initiateAlipayRequest(order, requestData);
		}
		requestData.setRequestUrl(redirectURL);
		return redirectURL;
	}

	/**
	 * Initiate call for Direct/Instant and Express payment
	 * */
	private String initiateAlipayRequest(final OrderModel order, final ProcessingRequestData requestData)
	{
		/* Invoke AlipayUtil to construct request URL */
		String requestUrl = getAlipayService().getRequestUrl(requestData);
		return requestUrl;
	}
	
	
	@Override
	public boolean handleResponse(OrderModel order, AlipayNotifyInfoData notifyData, Map<String, String> map, boolean isMobile){	
		validateParameterNotNull(order, "The given order is null!");	
		validateParameterNotNull(notifyData, "The given AlipayNotifyInfoData is null!");	
		validateParameterNotNull(map, "The given request parameter map is null!");	
		boolean verify = false;
		if(isMobile){
			//TODO
		}else{
			verify = getAlipayService().verify(map, getAlipayConfiguration().getWeb_partner(), getAlipayConfiguration().getWeb_key());
		}
		if(verify){
			AlipayTradeStatus tradeStatus = updatePaymentTransaction(order, notifyData, PaymentTransactionType.NOTIFY.getCode());
			if(tradeStatus==null){
				return false;
			}
			/**
			 * TRADE_FINISHED -
			 * Transaction success and passed expire date, not refundable.
			 * Transaction success and not refundable
			 * 
			 * WAIT_BUYER_PAY - 
			 * Transaction awaits user payment*/
			if(notifyData.getRefund_status()!=null){
				if (LOG.isDebugEnabled())
				{
				LOG.debug("\nNotifyData Trade status for Refund: "+ notifyData.getRefund_status() +" for Order: "+ notifyData.getOut_trade_no());
				}
			}else{ //TODO AlipayTradeStatus.TRADE_FINISHED returns for the Mobile payment, checking with Alipay on this at moment
				if(tradeStatus.equals(AlipayTradeStatus.TRADE_CLOSED) || tradeStatus.equals(AlipayTradeStatus.TRADE_SUCCESS) || tradeStatus.equals(AlipayTradeStatus.TRADE_FINISHED)){ //if TRADE_CLOSED or TRADE_SUCCESS
					if (LOG.isDebugEnabled())
					{
					LOG.debug("\n"+Calendar.getInstance().getTime() + " "+ notifyData.getTrade_status() +" for Order: "+ notifyData.getOut_trade_no() + ", notify"+ order.getCode()+"_Payment");
					}
					alipayNotifyService.executeAction(order);
				}
				else { //if(tradeStatus.equals(AlipayTradeStatus.TRADE_FINISHED) || tradeStatus.equals(AlipayTradeStatus.WAIT_BUYER_PAY) ){
					//Not ready to move onto next payment transaction state.
					if (LOG.isDebugEnabled())
					{
					LOG.debug("\nNotifyData Trade status: "+ notifyData.getTrade_status() +" for Order: "+ notifyData.getOut_trade_no());
					}
				}
			}
		}
		return verify;
	}

	/**
	 * Handle asynchronized Refund response from Alipay 
	 * */
	@Override
	public boolean handleResponse(OrderModel order, AlipayReturnData returnData){	
		return false;
	}

	/**
	 * Update the Order's AlipayPaymentTransaction with Alipay feedback calls
	 * @param order
	 * @param notifyData
	 * @return
	 */
	private AlipayTradeStatus updatePaymentTransaction(OrderModel order, AlipayNotifyInfoData notifyData, String type) {
		validateParameterNotNull(order, "The given order is null!");
		validateParameterNotNull(notifyData, "The given notifyData is null!");
		AlipayPaymentTransactionModel alipayTransaction = null;
		for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
		{
			if (transaction instanceof AlipayPaymentTransactionModel)
			{
				alipayTransaction = ((AlipayPaymentTransactionModel) transaction);
				if(notifyData.getTrade_status().equalsIgnoreCase(AlipayTradeStatus.WAIT_BUYER_PAY.name())){
					if(alipayTransaction.getLatestTradeStatus()==null || alipayTransaction.getLatestTradeStatus().equals(AlipayTradeStatus.PAYMENT_REQUEST)){
						alipayTransaction.setLatestTradeStatus(notifyData.getTrade_status());
					}
				}else{
					alipayTransaction.setLatestTradeStatus(notifyData.getTrade_status());
				}				
				alipayTransaction.setTradeAlipayNo(notifyData.getTrade_no());
				getModelService().save(alipayTransaction);
				notifyData.setTransactionType(type);
				getPaymentTransactionEntryPopulator().populate(notifyData, transaction);
				break;
			}
		}
		AlipayTradeStatus alipayTradeStatus = null;
		try{
			alipayTradeStatus = AlipayTradeStatus.valueOf(alipayTransaction.getLatestTradeStatus());
		}catch(IllegalArgumentException e){
			if (LOG.isDebugEnabled())
			{
			LOG.debug("No Enum const class AlipayTradeStatus of value of:"+ alipayTransaction.getLatestTradeStatus());
			}			
		}
		return alipayTradeStatus;
	}

    /**
     * Once the system invokes the close trade, Alipay could return T for if successes; 
     * or with an error TRADE_STATUS_NOT_AVAILD.
     * the Hybris shall then update the AlipayPaymentTransaciton to TRADE_CLOSE if successes.*/
	@Override
	public boolean closeTrade(OrderModel orderModel) {
		validateParameterNotNull(orderModel, "The given order is null!");
		final AlipayTradeResponseData closetrade = getAlipayService().initialiseCloseTradeData(orderModel.getCode());
		if (LOG.isDebugEnabled())
		{
			LOG.debug("\nCLOSE closeTrade for orderCode=" + orderModel.getCode() + ", Error:" + closetrade.getError()	+ ", Is_success: " + closetrade.getIs_success());
		}
		if(closetrade.getError()==null){
			updateStatusByCloseTrade(orderModel, closetrade);
			return true;
		}else{
			return false;
		}
	}

	private boolean updateStatusByCloseTrade(OrderModel orderModel, AlipayTradeResponseData closetrade) {
		for (final PaymentTransactionModel transaction : orderModel.getPaymentTransactions())
		{
			if (transaction instanceof AlipayPaymentTransactionModel)
			{
				AlipayPaymentTransactionModel alipayTransaction = ((AlipayPaymentTransactionModel) transaction);
				AlipayPaymentTransactionEntryModel entry = (AlipayPaymentTransactionEntryModel)getModelService().create(AlipayPaymentTransactionEntryModel.class);
				entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
				entry.setType(PaymentTransactionType.CANCEL);
				entry.setTime(new Date());
				if(closetrade.getError()!=null){
					entry.setTransactionStatus(AlipayTransactionStatus.CLOSE_TRADE_FAILED.getCode());
					entry.setTransactionStatusDetails(getTypeService().getEnumerationValue(AlipayTransactionStatus.CLOSE_TRADE_FAILED).getName()+": "+closetrade.getError());
				}else{
					entry.setTransactionStatus(AlipayTransactionStatus.CLOSE_TRADE_SUCCESS.getCode());
					entry.setTransactionStatusDetails(getTypeService().getEnumerationValue(AlipayTransactionStatus.CLOSE_TRADE_SUCCESS).getName());
					alipayTransaction.setLatestTradeStatus(AlipayTradeStatus.TRADE_CLOSED.name());
					getModelService().save(alipayTransaction);
				}
				getModelService().save(entry);
				break;
			}
		}
		return false;
	}
	
	/**
	 * Check the current trade status of the given Order.
	 * Update the Order AlipayPaymentTransaction with latest TradeStatus.
	 * and add in a new transaction entry.
	 * Return false if the check status with error TRADE_STATUS_NOT_AVAILD*/
	@Override
	public AlipayTradeStatus checkTrade(OrderModel orderModel) {
		validateParameterNotNull(orderModel, "The given order is null!");
		final AlipayCheckTradeResponseData checkTradeResponse = getAlipayService().initialiseCheckTradeData(orderModel.getCode());
		if(checkTradeResponse!=null){
			final AlipayTradeStatus tradeStatus = updateStatusByCheckTrade(orderModel, checkTradeResponse);
			if (tradeStatus!=null)
			{
				if (LOG.isDebugEnabled())
				{
				LOG.debug("CHECK orderCode=" + orderModel.getCode() + " Logged checktrade status successfully. " + "TradeStatus: "+ tradeStatus.name());
				}
				return tradeStatus;
			}
			else
			{
				if (LOG.isDebugEnabled())
				{
				LOG.debug("CEHCKERROR orderCode=" + orderModel.getCode() + " Checktrade returned with error: " + checkTradeResponse.getError());
				}
				return null;
			}
		}else{
			LOG.error("check trade with order: "+ orderModel.getCode() + " is invalid");
		}
		return null;
	}

	private AlipayTradeStatus updateStatusByCheckTrade(OrderModel orderModel,
			AlipayCheckTradeResponseData checkTradeResponse) {
		if (LOG.isDebugEnabled())
		{
		LOG.debug("updateStatusByCheckTrade - Error:"+checkTradeResponse.getError() + ", Is_success:"+checkTradeResponse.getIs_success());
		}
		if(checkTradeResponse.getError()==null){
			if(checkTradeResponse.getAlipayNotifyInfoData()!=null){
				AlipayTradeStatus tradeStatus = updatePaymentTransaction(orderModel, checkTradeResponse.getAlipayNotifyInfoData(), PaymentTransactionType.CHECK_TRADE.getCode());
				return tradeStatus;
			}
		}
		AlipayTradeStatus errorStatus = AlipayTradeStatus.valueOf(checkTradeResponse.getError());
		if(errorStatus!=null){
			return errorStatus;
		}
		return null;
	}
    
	@Override
	public PaymentTransactionEntryModel cancel(final PaymentTransactionEntryModel entry) throws AdapterException
	{
		return super.cancel(entry);
	}

	@Override
	public PaymentTransactionEntryModel capture(final PaymentTransactionModel transaction) throws AdapterException
	{
		return super.capture(transaction);
	}
	
	@Override
	public PaymentTransactionEntryModel refundStandalone(final String merchantTransactionCode, final BigDecimal amount,
			final Currency currency, final AddressModel paymentAddress, final CardInfo card) throws AdapterException
	{
		return super.refundStandalone(merchantTransactionCode, amount, currency, paymentAddress, card); //TODO
	}
	
	@Override
	public void saveErrorCallback(OrderModel orderModel, String errorCode){
		for(PaymentTransactionModel transaction : orderModel.getPaymentTransactions()){
			if(transaction instanceof AlipayPaymentTransactionModel){
				AlipayPaymentTransactionModel trans = (AlipayPaymentTransactionModel)transaction;
				trans.setErrorStatus(errorCode);
				trans.setErrorStatusTimestamp(UtilDate.getFullDate(TimeZone.getTimeZone(getAlipayConfiguration().getAlipay_timezone())));
				getModelService().save(trans);
				break;
			}
		}
	}
	/**
	 * @return the requestDataConverter
	 */
	public AlipayRequestDataConverter getRequestDataConverter()
	{
		return requestDataConverter;
	}

	/**
	 * @param requestDataConverter
	 *           the requestDataConverter to set
	 */
	public void setRequestDataConverter(final AlipayRequestDataConverter requestDataConverter)
	{
		this.requestDataConverter = requestDataConverter;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the alipayService
	 */
	public AlipayService getAlipayService() {
		return alipayService;
	}

	/**
	 * @param alipayService the alipayService to set
	 */
	public void setAlipayService(AlipayService alipayService) {
		this.alipayService = alipayService;
	}

	/**
	 * @return the alipayConfiguration
	 */
	public AlipayConfiguration getAlipayConfiguration() {
		return alipayConfiguration;
	}

	/**
	 * @param alipayConfiguration the alipayConfiguration to set
	 */
	public void setAlipayConfiguration(AlipayConfiguration alipayConfiguration) {
		this.alipayConfiguration = alipayConfiguration;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService() {
		return null; // please check spring configuration for method lookup
	}

	/**
	 * @return the paymentTransactionEntryPopulator
	 */
	public AlipayPaymentTransactionEntryPopulator getPaymentTransactionEntryPopulator() {
		return paymentTransactionEntryPopulator;
	}

	/**
	 * @param paymentTransactionEntryPopulator the paymentTransactionEntryPopulator to set
	 */
	public void setPaymentTransactionEntryPopulator(
			AlipayPaymentTransactionEntryPopulator paymentTransactionEntryPopulator) {
		this.paymentTransactionEntryPopulator = paymentTransactionEntryPopulator;
	}

	/**
	 * @return the paymentTransactionPopulator
	 */
	public AlipayPaymentTransactionPopulator getPaymentTransactionPopulator() {
		return paymentTransactionPopulator;
	}

	/**
	 * @param paymentTransactionPopulator the paymentTransactionPopulator to set
	 */
	public void setPaymentTransactionPopulator(
			AlipayPaymentTransactionPopulator paymentTransactionPopulator) {
		this.paymentTransactionPopulator = paymentTransactionPopulator;
	}



	/**
	 * @return the uiExperienceService
	 */
	public UiExperienceService getUiExperienceService() {
		return uiExperienceService;
	}

	/**
	 * @param uiExperienceService the uiExperienceService to set
	 */
	public void setUiExperienceService(UiExperienceService uiExperienceService) {
		this.uiExperienceService = uiExperienceService;
	}


	// TODO: check if ok 
//	/**
//	 * @return the siteAgentServiceTypeMap
//	 */
//	public HashMap<UiExperienceLevel, List<ServiceType>> getSiteAgentServiceTypeMap() {
//		return siteAgentServiceTypeMap;
//	}
//
//	/**
//	 * @param siteAgentServiceTypeMap the siteAgentServiceTypeMap to set
//	 */
//	public void setSiteAgentServiceTypeMap(
//			HashMap<UiExperienceLevel, List<ServiceType>> siteAgentServiceTypeMap) {
//		this.siteAgentServiceTypeMap = siteAgentServiceTypeMap;
//	}

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
