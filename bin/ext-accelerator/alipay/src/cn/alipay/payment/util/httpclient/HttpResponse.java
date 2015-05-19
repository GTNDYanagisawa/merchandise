package cn.alipay.payment.util.httpclient;

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Header;

import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;


public class HttpResponse
{

	private Header[] responseHeaders;

	private String stringResult;

	private byte[] byteResult;

	public Header[] getResponseHeaders()
	{
		return responseHeaders;
	}

	public void setResponseHeaders(final Header[] responseHeaders)
	{
		this.responseHeaders = responseHeaders;
	}

	public byte[] getByteResult()
	{
		if (byteResult != null)
		{
			return byteResult;
		}
		if (stringResult != null)
		{
			return stringResult.getBytes();
		}
		return null;
	}

	public void setByteResult(final byte[] byteResult)
	{
		this.byteResult = byteResult;
	}

	public String getStringResult() throws UnsupportedEncodingException
	{
		if (stringResult != null)
		{
			return stringResult;
		}
		if (byteResult != null)
		{
			return new String(byteResult, PaymentConstants.Basic.INPUT_CHARSET);
		}
		return null;
	}

	public void setStringResult(final String stringResult)
	{
		this.stringResult = stringResult;
	}

}
