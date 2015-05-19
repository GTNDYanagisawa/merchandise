package de.hybris.platform.chinaaccelerator.alipay.data;

import org.apache.commons.httpclient.NameValuePair;

public class AlipayCheckTradeResponseData {
	public static final String RESPONSE_ROOT = "alipay";
	public static final String RESPONSE_ATTR_PARAM = "param";
	
	private String sign;
	private String sign_type;
	private String is_success;
	private String error;
	private NameValuePair[] request;
	private Response response;
	
	public AlipayNotifyInfoData getAlipayNotifyInfoData(){
		if(getResponse()!=null){
			return getResponse().getTrade();
		}
		return null;
	}
	class Response{
		private AlipayNotifyInfoData trade;
		
		/**
		 * @return the trade
		 */
		public AlipayNotifyInfoData getTrade() {
			return trade;
		}

		/**
		 * @param trade the trade to set
		 */
		public void setTrade(AlipayNotifyInfoData trade) {
			this.trade = trade;
		}		
	}

	
	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(Response response) {
		this.response = response;
	}
	
	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}
	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	/**
	 * @return the sign_type
	 */
	public String getSign_type() {
		return sign_type;
	}
	/**
	 * @param sign_type the sign_type to set
	 */
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	/**
	 * @return the is_success
	 */
	public String getIs_success() {
		return is_success;
	}
	/**
	 * @param is_success the is_success to set
	 */
	public void setIs_success(String is_success) {
		this.is_success = is_success;
	}
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
	/**
	 * @return the request
	 */
	public NameValuePair[] getRequest() {
		return request;
	}
	/**
	 * @param request the request to set
	 */
	public void setRequest(NameValuePair[] request) {
		this.request = request;
	}

}
