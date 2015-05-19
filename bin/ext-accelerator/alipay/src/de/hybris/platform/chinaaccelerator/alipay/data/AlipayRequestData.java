/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.data;



public class AlipayRequestData extends BaseRequestData
{
//	private String service;
//	private String partner;
//	private String _input_charset;
	private String sign_type;
	private String sign;
	private String notify_url;
	private String return_url;
	private String error_notify_url;
	
	private String out_trade_no; //A unique number to identify a transaction in partner system,eg:6843192280647118
	private String subject; //Goods name/trade name/order name/order key words etc.Eg:Apple
	private String payment_type = "1"; //Default Value: 1 (purchase commodity)
	private String defaultbank; //Bank Code
	protected String paymethod; //DIFFERENT BETWEEN DIRECT and EXPRESS
	private String seller_email;
	private String buyer_email;
	private String seller_id;
	private String buyer_id;
	private String seller_account_name;
	private String buyer_account_name;
	private String price; /* Mutually exclusive price + quantity to total_fee */
	private String total_fee; /* Mutually exclusive price + quantity to total_fee */
	private int quantity; /* Mutually exclusive price + quantity to total_fee */
	private String body;
	private String show_url;
	private String need_ctu_check;
	private String royalty_type;
	private String royalty_parameters;
	private String anti_phishing_key;
	private String exter_invoke_ip;
	private String extra_common_param;
	private String extend_param;
	private String it_b_pay;
	private String default_login;
	private String product_type;
	private String token;
	private String item_orders_info;


	/**
	 * @param paymethod the paymethod to set
	 */
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}

	/**
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @param subject
	 *           the subject to set
	 */
	public void setSubject(final String subject)
	{
		this.subject = subject;
	}

	/**
	 * @return the payment_type
	 */
	public String getPayment_type()
	{
		return payment_type;
	}

	/**
	 * @param payment_type
	 *           the payment_type to set
	 */
	public void setPayment_type(final String payment_type)
	{
		this.payment_type = payment_type;
	}

	/**
	 * @return the defaultbank
	 */
	public String getDefaultbank()
	{
		return defaultbank;
	}

	/**
	 * @param defaultbank
	 *           the defaultbank to set
	 */
	public void setDefaultbank(final String defaultbank)
	{
		this.defaultbank = defaultbank;
	}

	/**
	 * @return the paymethod
	 */
	public String getPaymethod()
	{
		return paymethod;
	}

	/**
	 * @return the seller_email
	 */
	public String getSeller_email()
	{
		return seller_email;
	}

	/**
	 * @param seller_email
	 *           the seller_email to set
	 */
	public void setSeller_email(final String seller_email)
	{
		this.seller_email = seller_email;
	}

	/**
	 * @return the buyer_email
	 */
	public String getBuyer_email()
	{
		return buyer_email;
	}

	/**
	 * @param buyer_email
	 *           the buyer_email to set
	 */
	public void setBuyer_email(final String buyer_email)
	{
		this.buyer_email = buyer_email;
	}

	/**
	 * @return the seller_id
	 */
	public String getSeller_id()
	{
		return seller_id;
	}

	/**
	 * @param seller_id
	 *           the seller_id to set
	 */
	public void setSeller_id(final String seller_id)
	{
		this.seller_id = seller_id;
	}

	/**
	 * @return the buyer_id
	 */
	public String getBuyer_id()
	{
		return buyer_id;
	}

	/**
	 * @param buyer_id
	 *           the buyer_id to set
	 */
	public void setBuyer_id(final String buyer_id)
	{
		this.buyer_id = buyer_id;
	}

	/**
	 * @return the seller_account_name
	 */
	public String getSeller_account_name()
	{
		return seller_account_name;
	}

	/**
	 * @param seller_account_name
	 *           the seller_account_name to set
	 */
	public void setSeller_account_name(final String seller_account_name)
	{
		this.seller_account_name = seller_account_name;
	}

	/**
	 * @return the buyer_account_name
	 */
	public String getBuyer_account_name()
	{
		return buyer_account_name;
	}

	/**
	 * @param buyer_account_name
	 *           the buyer_account_name to set
	 */
	public void setBuyer_account_name(final String buyer_account_name)
	{
		this.buyer_account_name = buyer_account_name;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the total_fee
	 */
	public String getTotal_fee() {
		return total_fee;
	}

	/**
	 * @param total_fee the total_fee to set
	 */
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final int quantity)
	{
		this.quantity = quantity;
	}

	/**
	 * @return the body
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * @param body
	 *           the body to set
	 */
	public void setBody(final String body)
	{
		this.body = body;
	}

	/**
	 * @return the show_url
	 */
	public String getShow_url()
	{
		return show_url;
	}

	/**
	 * @param show_url
	 *           the show_url to set
	 */
	public void setShow_url(final String show_url)
	{
		this.show_url = show_url;
	}

	/**
	 * @return the need_ctu_check
	 */
	public String getNeed_ctu_check()
	{
		return need_ctu_check;
	}

	/**
	 * @param need_ctu_check
	 *           the need_ctu_check to set
	 */
	public void setNeed_ctu_check(final String need_ctu_check)
	{
		this.need_ctu_check = need_ctu_check;
	}

	/**
	 * @return the royalty_type
	 */
	public String getRoyalty_type()
	{
		return royalty_type;
	}

	/**
	 * @param royalty_type
	 *           the royalty_type to set
	 */
	public void setRoyalty_type(final String royalty_type)
	{
		this.royalty_type = royalty_type;
	}

	/**
	 * @return the royalty_parameters
	 */
	public String getRoyalty_parameters()
	{
		return royalty_parameters;
	}

	/**
	 * @param royalty_parameters
	 *           the royalty_parameters to set
	 */
	public void setRoyalty_parameters(final String royalty_parameters)
	{
		this.royalty_parameters = royalty_parameters;
	}

	/**
	 * @return the anti_phishing_key
	 */
	public String getAnti_phishing_key()
	{
		return anti_phishing_key;
	}

	/**
	 * @param anti_phishing_key
	 *           the anti_phishing_key to set
	 */
	public void setAnti_phishing_key(final String anti_phishing_key)
	{
		this.anti_phishing_key = anti_phishing_key;
	}

	/**
	 * @return the exter_invoke_ip
	 */
	public String getExter_invoke_ip()
	{
		return exter_invoke_ip;
	}

	/**
	 * @param exter_invoke_ip
	 *           the exter_invoke_ip to set
	 */
	public void setExter_invoke_ip(final String exter_invoke_ip)
	{
		this.exter_invoke_ip = exter_invoke_ip;
	}

	/**
	 * @return the extra_common_param
	 */
	public String getExtra_common_param()
	{
		return extra_common_param;
	}

	/**
	 * @param extra_common_param
	 *           the extra_common_param to set
	 */
	public void setExtra_common_param(final String extra_common_param)
	{
		this.extra_common_param = extra_common_param;
	}

	/**
	 * @return the extend_param
	 */
	public String getExtend_param()
	{
		return extend_param;
	}

	/**
	 * @param extend_param
	 *           the extend_param to set
	 */
	public void setExtend_param(final String extend_param)
	{
		this.extend_param = extend_param;
	}

	/**
	 * @return the it_b_pay
	 */
	public String getIt_b_pay()
	{
		return it_b_pay;
	}

	/**
	 * @param it_b_pay
	 *           the it_b_pay to set
	 */
	public void setIt_b_pay(final String it_b_pay)
	{
		this.it_b_pay = it_b_pay;
	}

	/**
	 * @return the default_login
	 */
	public String getDefault_login()
	{
		return default_login;
	}

	/**
	 * @param default_login
	 *           the default_login to set
	 */
	public void setDefault_login(final String default_login)
	{
		this.default_login = default_login;
	}

	/**
	 * @return the product_type
	 */
	public String getProduct_type()
	{
		return product_type;
	}

	/**
	 * @param product_type
	 *           the product_type to set
	 */
	public void setProduct_type(final String product_type)
	{
		this.product_type = product_type;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the item_orders_info
	 */
	public String getItem_orders_info() {
		return item_orders_info;
	}
	/**
	 * @param item_orders_info the item_orders_info to set
	 */
	public void setItem_orders_info(String item_orders_info) {
		this.item_orders_info = item_orders_info;
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
	 * @return the notify_url
	 */
	public String getNotify_url() {
		return notify_url;
	}

	/**
	 * @param notify_url the notify_url to set
	 */
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	/**
	 * @return the return_url
	 */
	public String getReturn_url() {
		return return_url;
	}

	/**
	 * @param return_url the return_url to set
	 */
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	/**
	 * @return the error_notify_url
	 */
	public String getError_notify_url() {
		return error_notify_url;
	}

	/**
	 * @param error_notify_url the error_notify_url to set
	 */
	public void setError_notify_url(String error_notify_url) {
		this.error_notify_url = error_notify_url;
	}

	/**
	 * @return the out_trade_no
	 */
	public String getOut_trade_no() {
		return out_trade_no;
	}

	/**
	 * @param out_trade_no the out_trade_no to set
	 */
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	
	
}
