/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import cn.alipay.payment.util.AlipayCore;
import cn.alipay.payment.util.AlipayUtil;

import com.thoughtworks.xstream.XStream;

import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayCheckTradeResponseData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayCheckTradeStatusData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayCloseTradeData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayRequestData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayTradeResponseData;
import de.hybris.platform.chinaaccelerator.alipay.data.BaseRequestData;
import de.hybris.platform.chinaaccelerator.alipay.data.ProcessingRequestData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.RequestServiceType;

public class AlipayService
{
	
	Logger LOG = Logger.getLogger(AlipayService.class);

	private String wap_auth_service_api_name;
	private String wap_trade_direct_api_name;
	private String direct_pay_service_api_name;
	private String direct_pay_paymethod_name;
	private String express_paymethod_name;
	private String close_trade_service_api_name;
	private String check_trade_service_api_name;
	private String refund_service_api_name;
	private AlipayConfiguration alipayConfiguration;
	
	protected String SERVICE = "service";
	protected String PARTNER = "partner";
	protected String NOTIFY_ID = "notify_id";
	protected String SIGN = "sign";

	
	/**
	 *  Define which service type is it for the Alipay request calls 
	 * 
	 *  MobileAuth and MobileTradePay ServiceTypes are not persisted. They are barely used for identify the services to use.
	 *  */
	protected String getServiceTypeCode(final RequestServiceType type)
	{
		if(type.equals(RequestServiceType.CLOSETRADE))
		{
			return getClose_trade_service_api_name();
		}
		else if(type.equals(RequestServiceType.CHECKTRADE))
		{
			return getCheck_trade_service_api_name();
		}
		else if (type.equals(RequestServiceType.MOBILEAUTH))
		{
			return getWap_auth_service_api_name();
		}
		else if (type.equals(RequestServiceType.MOBILETRADEPAY))
		{
			return getWap_trade_direct_api_name();
		}
		else if (type.equals(RequestServiceType.DIRECTPAY) || type.equals(RequestServiceType.BANKPAY) || type.equals(RequestServiceType.EXPRESSPAY))
		{
			return getDirect_pay_service_api_name();
		}
		else if (type.equals(RequestServiceType.REFUND))
		{
			return getRefund_service_api_name();
		}
		return "";
	}

	/**
	 * Defines the payment method for Alipay to differentiate the request. 
	 * E.g. Direct Pay (or Express pay) services can have refined payment type */
	private String getPaymethodName(final RequestServiceType type)
	{
		if (type.equals(RequestServiceType.DIRECTPAY))
		{
			return getDirect_pay_paymethod_name();
		}
		else if (type.equals(RequestServiceType.EXPRESSPAY))
		{
			return getExpress_paymethod_name();
		}
		return "";
	}

	/**
	 * The return_url that passes to the Alipay to receive Synchronized calls after request.
	 * At moment DirectPay and ExpressPay are actually mapped to the same.
	 * 
	 * MobileAuth and MobileTradePay ServiceTypes are not persisted. They are barely used for identify the services to use.
	 * */
	protected String getReturnUrl(final RequestServiceType type)
	{
		
		if (type.equals(RequestServiceType.DIRECTPAY) || type.equals(RequestServiceType.EXPRESSPAY))
		{
			return alipayConfiguration.getReturnBaseUrl() + PaymentConstants.Controller.DIRECT_AND_EXPRESS_RETURN_URL;
		}
		else if(type.equals(RequestServiceType.MOBILEAUTH)|| type.equals(RequestServiceType.MOBILETRADEPAY)){
			//TODO 
			return alipayConfiguration.getReturnBaseUrl() + PaymentConstants.Controller.WAP_RETURN_URL;
		}
		return "";
	}

	/**
	 * The notify_url passes to the Alipay to receive Asynchronized calls. 
	 * 
	 * MobileAuth and MobileTradePay ServiceTypes are not persisted. They are barely used for identify the services to use.
	 * */
	protected String getNotifyUrl(final RequestServiceType type)
	{
		
		if (type.equals(RequestServiceType.DIRECTPAY) || type.equals(RequestServiceType.EXPRESSPAY))
		{
			return alipayConfiguration.getBasepath() + PaymentConstants.Controller.DIRECT_AND_EXPRESS_NOTIFY_URL;
		}
		else if(type.equals(RequestServiceType.MOBILEAUTH)|| type.equals(RequestServiceType.MOBILETRADEPAY)){
			//TODO 
			return alipayConfiguration.getBasepath() + PaymentConstants.Controller.WAP_NOTIFY_URL;
		}
		else if(type.equals(RequestServiceType.REFUND)){
			//TODO 
			return alipayConfiguration.getBasepath() + PaymentConstants.Controller.REFUND_NOTIFY_URL;
		}
		return "";
	}
	
	/**
	 * Construct the request URL for the payment request. 
	 * */
	public String getRequestUrl(ProcessingRequestData requestData) {
		RequestServiceType serviceType = requestData.getRequestServiceType();
		
		/*Set the parameters for the Request call.*/
		AlipayRequestData alipayRequest = requestData.getAlipayRequestData();
		initialiseRequestData(alipayRequest, serviceType);
		alipayRequest.setPaymethod(getPaymethodName(serviceType));
		if(requestData.isToSupplyReturnUrl()){
			alipayRequest.setReturn_url(getReturnUrl(serviceType));
		}
		alipayRequest.setNotify_url(getNotifyUrl(serviceType));
		alipayRequest.setError_notify_url(alipayConfiguration.getBasepath() + PaymentConstants.Controller.ERROR_NOTIFY_URL);
		alipayRequest.setSeller_email(getAlipayConfiguration().getWeb_seller_email());
		if (!serviceType.equals(RequestServiceType.DIRECTPAY))
		{
			alipayRequest.setDefault_login(PaymentConstants.Basic.DEFAULT_LOGIN);
		}
		
		/* Convert the AlipayRequestData into URL.*/
		Map<String, String> paramMap = AlipayUtil.transBean2Map(alipayRequest);  //REDDRA-39 AlipayServiceUtil.transBean2Map(alipayRequest);
		String requestUrl = "";
		try {
			requestUrl = AlipayUtil.getParameterPath(paramMap, getAlipayConfiguration());
		} catch (Exception e) {
//			e.printStackTrace();
			LOG.error("Problem with AlipayUtil.getParameterPath.", e);
		}
		return requestUrl;
	}
		
	/**
	 * Initiate the Request data for the CheckTrade call. 
	 * */
	public AlipayCheckTradeResponseData initialiseCheckTradeData(String out_trade_no){
		AlipayCheckTradeStatusData checkTradeData = new AlipayCheckTradeStatusData();
		initialiseRequestData(checkTradeData, RequestServiceType.CHECKTRADE);
		checkTradeData.setOut_trade_no(out_trade_no);
		final Map<String, String> sParaTemp = AlipayUtil.transBean2Map(checkTradeData);
		
		String xmlString = "";
		try
		{
			xmlString = AlipayUtil.sendPostInfo(sParaTemp, getAlipayConfiguration());
		}
		catch (final Exception e)
		{
//			e.printStackTrace();
			LOG.error("Problem with AlipayUtil.sendPostInfo.", e);
		}
		if(LOG.isDebugEnabled()){
			LOG.debug("initialiseCheckTradeData xmlString: "+xmlString);
		}
		final XStream xstream = new XStream();
		xstream.alias(AlipayCheckTradeResponseData.RESPONSE_ROOT, AlipayCheckTradeResponseData.class);
		xstream.alias(AlipayCheckTradeResponseData.RESPONSE_ATTR_PARAM, NameValuePair.class);
		AlipayCheckTradeResponseData checkTradeResponse = null;
		try {
			checkTradeResponse = (AlipayCheckTradeResponseData) xstream.fromXML(xmlString);
		} catch (Exception e) {
//			e.printStackTrace();
			LOG.error("Problem with xstream.fromXML.", e);
			LOG.error("initialiseCheckTradeData cannot handle - " + xmlString);
		}
		return checkTradeResponse;
	}
	
	/**
	 * Initiate the Request data for the CloseTrade call. 
	 * */
	public AlipayTradeResponseData initialiseCloseTradeData(String out_trade_no){
		AlipayCloseTradeData closeTradeData = new AlipayCloseTradeData();
		initialiseRequestData(closeTradeData, RequestServiceType.CLOSETRADE);
		closeTradeData.setOut_order_no(out_trade_no);
		final Map<String, String> sParaTemp = AlipayUtil.transBean2Map(closeTradeData);
		
		String xmlString = "";
		try
		{
			xmlString = AlipayUtil.sendPostInfo(sParaTemp, getAlipayConfiguration());
		}
		catch (final Exception e)
		{
			//e.printStackTrace();
			LOG.error("Problem with sendPostInfo execution.", e);
		}
		final XStream xstream = new XStream();
		xstream.alias(AlipayTradeResponseData.RESPONSE_ROOT, AlipayTradeResponseData.class);
		final AlipayTradeResponseData closetrade = (AlipayTradeResponseData) xstream.fromXML(xmlString);
		return closetrade;
	}

	private BaseRequestData initialiseRequestData(BaseRequestData requestData, RequestServiceType serciveType){
		requestData.setService(getServiceTypeCode(serciveType));
		requestData.setPartner(getAlipayConfiguration().getWeb_partner());
		requestData.set_input_charset(PaymentConstants.Basic.INPUT_CHARSET);
		return requestData;
	}

	public boolean verify(final Map<String, String> params, final String partner, final String key)
	{
		final Map<String, String> sParaNew = AlipayCore.paraFilter(params);
		final String mysign = AlipayCore.buildMysign(sParaNew, key);
		Boolean responseTxt = Boolean.TRUE;
		if (params.get(NOTIFY_ID) != null)
		{
			responseTxt = verifyResponse(params.get(NOTIFY_ID), partner);
		}
		String sign = "";
		if (params.get(SIGN) != null)
		{
			sign = params.get(SIGN);
		}

		if (responseTxt.equals(Boolean.TRUE) && mysign.equals(sign))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private Boolean verifyResponse(final String notify_id, final String partner)
	{
		String inputLine = "";
		final String veryfy_url = getAlipayConfiguration().getHttps_verify_url() + PARTNER+ "=" + partner + "&"+NOTIFY_ID+"=" + notify_id;

		BufferedReader in = null;
		try
		{
			int MAX_STR_LEN = 2048;
			final URL url = new URL(veryfy_url);
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			int intC;
			while ((intC = in.read()) != -1) {
				char c = (char) intC;
				if (c == '\n')
				{ break; }
				if (sb.length() >= MAX_STR_LEN)
				{ throw new Exception("input too long"); }
				sb.append(c);
			}
			inputLine = sb.toString();
		}
		catch (final Exception e)
		{
//			e.printStackTrace();
			LOG.error("Problem with verifying response.", e);
			inputLine = "";
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
//					e.printStackTrace();
					LOG.error("Problem with closing BufferedReader instance.", e);
				}
			}
		}

		return Boolean.valueOf(inputLine);
	}

	/**
	 * @return the wap_auth_service_api_name
	 */
	public String getWap_auth_service_api_name() {
		return wap_auth_service_api_name;
	}

	/**
	 * @param wap_auth_service_api_name the wap_auth_service_api_name to set
	 */
	public void setWap_auth_service_api_name(String wap_auth_service_api_name) {
		this.wap_auth_service_api_name = wap_auth_service_api_name;
	}

	/**
	 * @return the wap_trade_direct_api_name
	 */
	public String getWap_trade_direct_api_name() {
		return wap_trade_direct_api_name;
	}

	/**
	 * @param wap_trade_direct_api_name the wap_trade_direct_api_name to set
	 */
	public void setWap_trade_direct_api_name(String wap_trade_direct_api_name) {
		this.wap_trade_direct_api_name = wap_trade_direct_api_name;
	}

	/**
	 * @return the direct_pay_service_api_name
	 */
	public String getDirect_pay_service_api_name() {
		return direct_pay_service_api_name;
	}

	/**
	 * @param direct_pay_service_api_name the direct_pay_service_api_name to set
	 */
	public void setDirect_pay_service_api_name(String direct_pay_service_api_name) {
		this.direct_pay_service_api_name = direct_pay_service_api_name;
	}

	/**
	 * @return the direct_pay_paymethod_name
	 */
	public String getDirect_pay_paymethod_name() {
		return direct_pay_paymethod_name;
	}

	/**
	 * @param direct_pay_paymethod_name the direct_pay_paymethod_name to set
	 */
	public void setDirect_pay_paymethod_name(String direct_pay_paymethod_name) {
		this.direct_pay_paymethod_name = direct_pay_paymethod_name;
	}

	/**
	 * @return the express_paymethod_name
	 */
	public String getExpress_paymethod_name() {
		return express_paymethod_name;
	}

	/**
	 * @param express_paymethod_name the express_paymethod_name to set
	 */
	public void setExpress_paymethod_name(String express_paymethod_name) {
		this.express_paymethod_name = express_paymethod_name;
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
	 * @return the close_trade_service_api_name
	 */
	public String getClose_trade_service_api_name() {
		return close_trade_service_api_name;
	}

	/**
	 * @param close_trade_service_api_name the close_trade_service_api_name to set
	 */
	public void setClose_trade_service_api_name(String close_trade_service_api_name) {
		this.close_trade_service_api_name = close_trade_service_api_name;
	}

	/**
	 * @return the check_trade_service_api_name
	 */
	public String getCheck_trade_service_api_name() {
		return check_trade_service_api_name;
	}

	/**
	 * @param check_trade_service_api_name the check_trade_service_api_name to set
	 */
	public void setCheck_trade_service_api_name(String check_trade_service_api_name) {
		this.check_trade_service_api_name = check_trade_service_api_name;
	}

	/**
	 * @return the refund_service_api_name
	 */
	public String getRefund_service_api_name() {
		return refund_service_api_name;
	}

	/**
	 * @param refund_service_api_name the refund_service_api_name to set
	 */
	public void setRefund_service_api_name(String refund_service_api_name) {
		this.refund_service_api_name = refund_service_api_name;
	}

}
