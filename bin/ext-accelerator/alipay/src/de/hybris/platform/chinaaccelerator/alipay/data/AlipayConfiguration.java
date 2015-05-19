/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.data;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import org.apache.log4j.Logger;
//import org.jfree.util.Log;

public class AlipayConfiguration
{
	Logger LOG = Logger.getLogger(AlipayConfiguration.class);
	private String web_partner;
	private String web_key;
	private String web_seller_email;
	private String web_gateway;
	private String wap_partner;
	private String wap_key;
	private String wap_seller;
	private String wap_rsa_private;
	private String wap_rsa_alipay_public;
	private String wap_gateway;
	private String basepath;
	private String returnBaseUrl;
	
	private String test_amount;
	private String test_refund_amount;
	private String test_mode;
	private String is_supply_returnurl;
	private String request_timeout;
	
	private String request_subject;
	
	private String https_verify_url;
	private String refund_batch_no_timezone;
	private String alipay_timezone;
	
	public String getRequestPrice(double orderPrice){
		validateParameterNotNull(orderPrice, "The given orderPrice is null!");		
		String format = "%.2f";
		String price = String.format(format, orderPrice);
		if(getTest_mode()!=null 
				&& Boolean.valueOf(getTest_mode()).equals(Boolean.TRUE) 
				&& getTest_amount()!=null){
			try{
				price = getTest_amount();
				if (LOG.isDebugEnabled()){
					LOG.debug("Payment - use of the test amount in the properties file: "+ price);
				}
			}catch(NumberFormatException e){
				LOG.warn("Please use float value for test.amount in the alipay/project.properties.");
			}
		}	
		return price;
	}
	
	public String getRefundPrice(double orderPrice){
		validateParameterNotNull(orderPrice, "The given orderPrice is null!");		
		String format = "%.2f";
		String price = String.format(format, orderPrice);
		if(getTest_mode()!=null 
				&& Boolean.valueOf(getTest_mode()).equals(Boolean.TRUE) 
				&& getTest_refund_amount()!=null){
			try{
				price =  getTest_refund_amount();
				if (LOG.isDebugEnabled()){
					LOG.debug("Refund - use of the test amount in the properties file: "+ price);
				}
			}catch(NumberFormatException e){
				LOG.warn("Please use float value for test.amount in the alipay/project.properties.");
			}
		}	
		
		return price;
	}
	
	/**
	 * @return the is_supply_returnurl
	 */
	public String getIs_supply_returnurl() {
		return is_supply_returnurl;
	}

	/**
	 * @param is_supply_returnurl the is_supply_returnurl to set
	 */
	public void setIs_supply_returnurl(String is_supply_returnurl) {
		this.is_supply_returnurl = is_supply_returnurl;
	}

	/**
	 * @return the web_partner
	 */
	public String getWeb_partner()
	{
		return web_partner;
	}

	/**
	 * @param web_partner
	 *           the web_partner to set
	 */
	public void setWeb_partner(final String web_partner)
	{
		this.web_partner = web_partner;
	}

	/**
	 * @return the web_key
	 */
	public String getWeb_key()
	{
		return web_key;
	}

	/**
	 * @param web_key
	 *           the web_key to set
	 */
	public void setWeb_key(final String web_key)
	{
		this.web_key = web_key;
	}

	/**
	 * @return the web_seller_email
	 */
	public String getWeb_seller_email()
	{
		return web_seller_email;
	}

	/**
	 * @param web_seller_email
	 *           the web_seller_email to set
	 */
	public void setWeb_seller_email(final String web_seller_email)
	{
		this.web_seller_email = web_seller_email;
	}

	/**
	 * @return the web_gateway
	 */
	public String getWeb_gateway()
	{
		return web_gateway;
	}

	/**
	 * @param web_gateway
	 *           the web_gateway to set
	 */
	public void setWeb_gateway(final String web_gateway)
	{
		this.web_gateway = web_gateway;
	}

	/**
	 * @return the wap_partner
	 */
	public String getWap_partner()
	{
		return wap_partner;
	}

	/**
	 * @param wap_partner
	 *           the wap_partner to set
	 */
	public void setWap_partner(final String wap_partner)
	{
		this.wap_partner = wap_partner;
	}

	/**
	 * @return the wap_key
	 */
	public String getWap_key()
	{
		return wap_key;
	}

	/**
	 * @param wap_key
	 *           the wap_key to set
	 */
	public void setWap_key(final String wap_key)
	{
		this.wap_key = wap_key;
	}

	/**
	 * @return the wap_seller
	 */
	public String getWap_seller()
	{
		return wap_seller;
	}

	/**
	 * @param wap_seller
	 *           the wap_seller to set
	 */
	public void setWap_seller(final String wap_seller)
	{
		this.wap_seller = wap_seller;
	}

	/**
	 * @return the wap_rsa_private
	 */
	public String getWap_rsa_private()
	{
		return wap_rsa_private;
	}

	/**
	 * @param wap_rsa_private
	 *           the wap_rsa_private to set
	 */
	public void setWap_rsa_private(final String wap_rsa_private)
	{
		this.wap_rsa_private = wap_rsa_private;
	}

	/**
	 * @return the wap_rsa_alipay_public
	 */
	public String getWap_rsa_alipay_public()
	{
		return wap_rsa_alipay_public;
	}

	/**
	 * @param wap_rsa_alipay_public
	 *           the wap_rsa_alipay_public to set
	 */
	public void setWap_rsa_alipay_public(final String wap_rsa_alipay_public)
	{
		this.wap_rsa_alipay_public = wap_rsa_alipay_public;
	}

	/**
	 * @return the wap_gateway
	 */
	public String getWap_gateway()
	{
		return wap_gateway;
	}

	/**
	 * @param wap_gateway
	 *           the wap_gateway to set
	 */
	public void setWap_gateway(final String wap_gateway)
	{
		this.wap_gateway = wap_gateway;
	}

	/**
	 * @return the basepath
	 */
	public String getBasepath()
	{
		return basepath;
	}

	/**
	 * @param basepath
	 *           the basepath to set
	 */
	public void setBasepath(final String basepath)
	{
		this.basepath = basepath;
	}

	/**
	 * @return the test_amount
	 */
	public String getTest_amount() {
		return test_amount;
	}

	/**
	 * @param test_amount the test_amount to set
	 */
	public void setTest_amount(String test_amount) {
		this.test_amount = test_amount;
	}

	/**
	 * @return the test_mode
	 */
	public String getTest_mode() {
		return test_mode;
	}

	/**
	 * @param test_mode the test_mode to set
	 */
	public void setTest_mode(String test_mode) {
		this.test_mode = test_mode;
	}

	/**
	 * @return the request_timeout
	 */
	public String getRequest_timeout() {
		return request_timeout;
	}

	/**
	 * @param request_timeout the request_timeout to set
	 */
	public void setRequest_timeout(String request_timeout) {
		this.request_timeout = request_timeout;
	}

	/**
	 * @return the request_subject
	 */
	public String getRequest_subject() {
		return request_subject;
	}

	/**
	 * @param request_subject the request_subject to set
	 */
	public void setRequest_subject(String request_subject) {
		this.request_subject = request_subject;
	}
	/**
	 * @return the https_verify_url
	 */
	public String getHttps_verify_url() {
		return https_verify_url;
	}
	/**
	 * @param https_verify_url the https_verify_url to set
	 */
	public void setHttps_verify_url(String https_verify_url) {
		this.https_verify_url = https_verify_url;
	}
	/**
	 * @return the test_refund_amount
	 */
	public String getTest_refund_amount() {
		return test_refund_amount;
	}
	/**
	 * @param test_refund_amount the test_refund_amount to set
	 */
	public void setTest_refund_amount(String test_refund_amount) {
		this.test_refund_amount = test_refund_amount;
	}

	/**
	 * @return the refund_batch_no_timezone
	 */
	public String getRefund_batch_no_timezone() {
		return refund_batch_no_timezone;
	}

	/**
	 * @param refund_batch_no_timezone the refund_batch_no_timezone to set
	 */
	public void setRefund_batch_no_timezone(String refund_batch_no_timezone) {
		this.refund_batch_no_timezone = refund_batch_no_timezone;
	}

	/**
	 * @return the alipay_timezone
	 */
	public String getAlipay_timezone() {
		return alipay_timezone;
	}

	/**
	 * @param alipay_timezone the alipay_timezone to set
	 */
	public void setAlipay_timezone(String alipay_timezone) {
		this.alipay_timezone = alipay_timezone;
	}

	/**
	 * @return the returnBaseUrl
	 */
	public String getReturnBaseUrl() {
		return returnBaseUrl;
	}

	/**
	 * @param returnBaseUrl the returnBaseUrl to set
	 */
	public void setReturnBaseUrl(String returnBaseUrl) {
		this.returnBaseUrl = returnBaseUrl;
	}
}
