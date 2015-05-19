/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.data;

public class AlipayErrorNotificationInfo
{
	/*
	 * item_orders_info: It will sends back Error notification back to Hybris if there's error in order or the
	 * products. It will continue sending until Hyrbis responses Success.
	 * 
	 * ALSO, Handles any kind of other Error notifications (in this case return_url will be filled)
	 */
	private String partner; /* Mandatory */

	private String out_trade_no; /* Mandatory */
	private String error_code;/* Mandatory */
	private String return_url; /* Mandatory for general Error Notify than item_order_info. */
	private String buyer_email;
	private String buyer_id;
	private String seller_email;
	private String seller_id;

	/**
	 * @return the partner
	 */
	public String getPartner()
	{
		return partner;
	}

	/**
	 * @param partner
	 *           the partner to set
	 */
	public void setPartner(final String partner)
	{
		this.partner = partner;
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
	 * @return the error_code
	 */
	public String getError_code()
	{
		return error_code;
	}

	/**
	 * @param error_code
	 *           the error_code to set
	 */
	public void setError_code(final String error_code)
	{
		this.error_code = error_code;
	}

	/**
	 * @return the return_url
	 */
	public String getReturn_url()
	{
		return return_url;
	}

	/**
	 * @param return_url
	 *           the return_url to set
	 */
	public void setReturn_url(final String return_url)
	{
		this.return_url = return_url;
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

}
