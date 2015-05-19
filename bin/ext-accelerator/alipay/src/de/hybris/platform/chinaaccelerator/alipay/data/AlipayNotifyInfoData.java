/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.data;

import de.hybris.platform.payment.enums.PaymentTransactionType;



public class AlipayNotifyInfoData
{
	/**
	 * Basic parameters
	 */
	private String notify_time;
	private String notify_type;
	private String notify_id;
	private String sign_type;
	private String sign;
	/**
	 * Business parameters
	 */
	private String out_trade_no;
	private String subject;
	private String payment_type;
	private String trade_no;
	private String trade_status;
	private String gmt_create;
	private String gmt_payment;
	private String gmt_close;
	private String refund_status;
	private String gmt_refund;
	private String seller_email;
	private String buyer_email;
	private String seller_id;
	private String buyer_id;
	private Float price;
	private Float total_fee;
	private Integer quantity;
	private String body;
	private Float discount;
	private String is_total_fee_adjust;
	private String use_coupon;
	private String extra_common_param;
	private String out_channel_type;
	private String out_channel_amount;
	
	private String refund_agent_pay_fee;
	private String refund_cash_fee;
	private String refund_coupon_fee;
	private String refund_fee;
	private String refund_flow_type;
	private String refund_id;
	
	/**
	 * Extra attributes from Checktrade
	 * */
	private String flag_trade_locked;
	private String gmt_last_modified_time;
	private String operator_role;
	private String time_out;
	private String time_out_type;
	private String to_buyer_fee;
	private String to_seller_fee;
	private String additional_trade_status;
	
	/**
	 * Operation attributes
	 * */
	private String transactionType;
	
	/**
	 * @return the notify_type
	 */
	public String getNotify_type()
	{
		return notify_type;
	}

	/**
	 * @param notify_type
	 *           the notify_type to set
	 */
	public void setNotify_type(final String notify_type)
	{
		this.notify_type = notify_type;
	}

	/**
	 * @return the notify_id
	 */
	public String getNotify_id()
	{
		return notify_id;
	}

	/**
	 * @param notify_id
	 *           the notify_id to set
	 */
	public void setNotify_id(final String notify_id)
	{
		this.notify_id = notify_id;
	}

	/**
	 * @return the sign_type
	 */
	public String getSign_type()
	{
		return sign_type;
	}

	/**
	 * @param sign_type
	 *           the sign_type to set
	 */
	public void setSign_type(final String sign_type)
	{
		this.sign_type = sign_type;
	}

	/**
	 * @return the sign
	 */
	public String getSign()
	{
		return sign;
	}

	/**
	 * @param sign
	 *           the sign to set
	 */
	public void setSign(final String sign)
	{
		this.sign = sign;
	}

	/**
	 * @return the out_trade_no
	 */
	public String getOut_trade_no()
	{
		return out_trade_no;
	}

	/**
	 * @param out_trade_no
	 *           the out_trade_no to set
	 */
	public void setOut_trade_no(final String out_trade_no)
	{
		this.out_trade_no = out_trade_no;
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
	 * @return the trade_no
	 */
	public String getTrade_no()
	{
		return trade_no;
	}

	/**
	 * @param trade_no
	 *           the trade_no to set
	 */
	public void setTrade_no(final String trade_no)
	{
		this.trade_no = trade_no;
	}

	/**
	 * @return the trade_status
	 */
	public String getTrade_status()
	{
		return trade_status;
	}

	/**
	 * @param trade_status
	 *           the trade_status to set
	 */
	public void setTrade_status(final String trade_status)
	{
		this.trade_status = trade_status;
	}

	/**
	 * @return the refund_status
	 */
	public String getRefund_status()
	{
		return refund_status;
	}

	/**
	 * @param refund_status
	 *           the refund_status to set
	 */
	public void setRefund_status(final String refund_status)
	{
		this.refund_status = refund_status;
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
	 * @return the price
	 */
	public Float getPrice()
	{
		return price;
	}

	/**
	 * @param price
	 *           the price to set
	 */
	public void setPrice(final Float price)
	{
		this.price = price;
	}

	/**
	 * @return the total_fee
	 */
	public Float getTotal_fee()
	{
		return total_fee;
	}

	/**
	 * @param total_fee
	 *           the total_fee to set
	 */
	public void setTotal_fee(final Float total_fee)
	{
		this.total_fee = total_fee;
	}

	/**
	 * @return the quantity
	 */
	public Integer getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final Integer quantity)
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
	 * @return the discount
	 */
	public Float getDiscount()
	{
		return discount;
	}

	/**
	 * @param discount
	 *           the discount to set
	 */
	public void setDiscount(final Float discount)
	{
		this.discount = discount;
	}

	/**
	 * @return the is_total_fee_adjust
	 */
	public String getIs_total_fee_adjust()
	{
		return is_total_fee_adjust;
	}

	/**
	 * @param is_total_fee_adjust
	 *           the is_total_fee_adjust to set
	 */
	public void setIs_total_fee_adjust(final String is_total_fee_adjust)
	{
		this.is_total_fee_adjust = is_total_fee_adjust;
	}

	/**
	 * @return the use_coupon
	 */
	public String getUse_coupon()
	{
		return use_coupon;
	}

	/**
	 * @param use_coupon
	 *           the use_coupon to set
	 */
	public void setUse_coupon(final String use_coupon)
	{
		this.use_coupon = use_coupon;
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
	 * @return the out_channel_type
	 */
	public String getOut_channel_type()
	{
		return out_channel_type;
	}

	/**
	 * @param out_channel_type
	 *           the out_channel_type to set
	 */
	public void setOut_channel_type(final String out_channel_type)
	{
		this.out_channel_type = out_channel_type;
	}

	/**
	 * @return the out_channel_amount
	 */
	public String getOut_channel_amount()
	{
		return out_channel_amount;
	}

	/**
	 * @param out_channel_amount
	 *           the out_channel_amount to set
	 */
	public void setOut_channel_amount(final String out_channel_amount)
	{
		this.out_channel_amount = out_channel_amount;
	}

	/**
	 * @return the notify_time
	 */
	public String getNotify_time() {
		return notify_time;
	}

	/**
	 * @param notify_time the notify_time to set
	 */
	public void setNotify_time(String notify_time) {
		this.notify_time = notify_time;
	}

	/**
	 * @return the gmt_create
	 */
	public String getGmt_create() {
		return gmt_create;
	}

	/**
	 * @param gmt_create the gmt_create to set
	 */
	public void setGmt_create(String gmt_create) {
		this.gmt_create = gmt_create;
	}

	/**
	 * @return the gmt_payment
	 */
	public String getGmt_payment() {
		return gmt_payment;
	}

	/**
	 * @param gmt_payment the gmt_payment to set
	 */
	public void setGmt_payment(String gmt_payment) {
		this.gmt_payment = gmt_payment;
	}

	/**
	 * @return the gmt_close
	 */
	public String getGmt_close() {
		return gmt_close;
	}

	/**
	 * @param gmt_close the gmt_close to set
	 */
	public void setGmt_close(String gmt_close) {
		this.gmt_close = gmt_close;
	}

	/**
	 * @return the gmt_refund
	 */
	public String getGmt_refund() {
		return gmt_refund;
	}

	/**
	 * @param gmt_refund the gmt_refund to set
	 */
	public void setGmt_refund(String gmt_refund) {
		this.gmt_refund = gmt_refund;
	}

	/**
	 * @return the flag_trade_locked
	 */
	public String getFlag_trade_locked() {
		return flag_trade_locked;
	}

	/**
	 * @param flag_trade_locked the flag_trade_locked to set
	 */
	public void setFlag_trade_locked(String flag_trade_locked) {
		this.flag_trade_locked = flag_trade_locked;
	}

	/**
	 * @return the gmt_last_modified_time
	 */
	public String getGmt_last_modified_time() {
		return gmt_last_modified_time;
	}

	/**
	 * @param gmt_last_modified_time the gmt_last_modified_time to set
	 */
	public void setGmt_last_modified_time(String gmt_last_modified_time) {
		this.gmt_last_modified_time = gmt_last_modified_time;
	}

	/**
	 * @return the operator_role
	 */
	public String getOperator_role() {
		return operator_role;
	}

	/**
	 * @param operator_role the operator_role to set
	 */
	public void setOperator_role(String operator_role) {
		this.operator_role = operator_role;
	}

	/**
	 * @return the time_out
	 */
	public String getTime_out() {
		return time_out;
	}

	/**
	 * @param time_out the time_out to set
	 */
	public void setTime_out(String time_out) {
		this.time_out = time_out;
	}

	/**
	 * @return the time_out_type
	 */
	public String getTime_out_type() {
		return time_out_type;
	}

	/**
	 * @param time_out_type the time_out_type to set
	 */
	public void setTime_out_type(String time_out_type) {
		this.time_out_type = time_out_type;
	}

	/**
	 * @return the to_buyer_fee
	 */
	public String getTo_buyer_fee() {
		return to_buyer_fee;
	}

	/**
	 * @param to_buyer_fee the to_buyer_fee to set
	 */
	public void setTo_buyer_fee(String to_buyer_fee) {
		this.to_buyer_fee = to_buyer_fee;
	}

	/**
	 * @return the to_seller_fee
	 */
	public String getTo_seller_fee() {
		return to_seller_fee;
	}

	/**
	 * @param to_seller_fee the to_seller_fee to set
	 */
	public void setTo_seller_fee(String to_seller_fee) {
		this.to_seller_fee = to_seller_fee;
	}

	/**
	 * @return the additional_trade_status
	 */
	public String getAdditional_trade_status() {
		return additional_trade_status;
	}

	/**
	 * @param additional_trade_status the additional_trade_status to set
	 */
	public void setAdditional_trade_status(String additional_trade_status) {
		this.additional_trade_status = additional_trade_status;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the refund_agent_pay_fee
	 */
	public String getRefund_agent_pay_fee() {
		return refund_agent_pay_fee;
	}

	/**
	 * @param refund_agent_pay_fee the refund_agent_pay_fee to set
	 */
	public void setRefund_agent_pay_fee(String refund_agent_pay_fee) {
		this.refund_agent_pay_fee = refund_agent_pay_fee;
	}

	/**
	 * @return the refund_cash_fee
	 */
	public String getRefund_cash_fee() {
		return refund_cash_fee;
	}

	/**
	 * @param refund_cash_fee the refund_cash_fee to set
	 */
	public void setRefund_cash_fee(String refund_cash_fee) {
		this.refund_cash_fee = refund_cash_fee;
	}

	/**
	 * @return the refund_coupon_fee
	 */
	public String getRefund_coupon_fee() {
		return refund_coupon_fee;
	}

	/**
	 * @param refund_coupon_fee the refund_coupon_fee to set
	 */
	public void setRefund_coupon_fee(String refund_coupon_fee) {
		this.refund_coupon_fee = refund_coupon_fee;
	}

	/**
	 * @return the refund_fee
	 */
	public String getRefund_fee() {
		return refund_fee;
	}

	/**
	 * @param refund_fee the refund_fee to set
	 */
	public void setRefund_fee(String refund_fee) {
		this.refund_fee = refund_fee;
	}

	/**
	 * @return the refund_flow_type
	 */
	public String getRefund_flow_type() {
		return refund_flow_type;
	}

	/**
	 * @param refund_flow_type the refund_flow_type to set
	 */
	public void setRefund_flow_type(String refund_flow_type) {
		this.refund_flow_type = refund_flow_type;
	}

	/**
	 * @return the refund_id
	 */
	public String getRefund_id() {
		return refund_id;
	}

	/**
	 * @param refund_id the refund_id to set
	 */
	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}

}
