/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.data;

public class AlipayCloseTradeData extends BaseRequestData
{
	private String sign;
	private String sign_type;
	private String trade_no;
	private String out_order_no;
	private String ip;
	private String trade_role;

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
	 * @return the ip
	 */
	public String getIp()
	{
		return ip;
	}

	/**
	 * @param ip
	 *           the ip to set
	 */
	public void setIp(final String ip)
	{
		this.ip = ip;
	}

	/**
	 * @return the trade_role
	 */
	public String getTrade_role()
	{
		return trade_role;
	}

	/**
	 * @param trade_role
	 *           the trade_role to set
	 */
	public void setTrade_role(final String trade_role)
	{
		this.trade_role = trade_role;
	}

	/**
	 * @return the out_order_no
	 */
	public String getOut_order_no() {
		return out_order_no;
	}

	/**
	 * @param out_order_no the out_order_no to set
	 */
	public void setOut_order_no(String out_order_no) {
		this.out_order_no = out_order_no;
	}

}
