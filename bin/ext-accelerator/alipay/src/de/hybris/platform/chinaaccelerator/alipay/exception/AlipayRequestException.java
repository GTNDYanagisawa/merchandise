/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.exception;

import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayRequestResponse;


public class AlipayRequestException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AlipayRequestResponse response;

	public AlipayRequestException(final String description, final AlipayRequestResponse response)
	{
		super(description);
		setResponse(response);
	}

	public AlipayRequestException(final String description)
	{
		super(description);
	}

	public AlipayRequestException(final String description, final Throwable e)
	{
		super(description, e);
	}

	public AlipayRequestException(final Throwable e)
	{
		super(e);
	}

	public AlipayRequestException(final String description, final AlipayRequestResponse response, final Throwable e)
	{
		super(e);
		setResponse(response);
	}

	/**
	 * @return the response
	 */
	public AlipayRequestResponse getResponse()
	{
		return response;
	}

	/**
	 * @param response
	 *           the response to set
	 */
	public void setResponse(final AlipayRequestResponse response)
	{
		this.response = response;
	}
}
