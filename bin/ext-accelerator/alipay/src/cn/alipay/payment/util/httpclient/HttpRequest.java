package cn.alipay.payment.util.httpclient;

import org.apache.commons.httpclient.NameValuePair;


public class HttpRequest
{

	/** HTTP GET method */
	public static final String METHOD_GET = "GET";

	/** HTTP POST method */
	public static final String METHOD_POST = "POST";


	private String url = null;

	private String method = METHOD_POST;

	private int timeout = 0;

	private int connectionTimeout = 0;


	private NameValuePair[] parameters = null;


	private String queryString = null;


	private String charset = "GBK";


	private String clientIp;

	private HttpResultType resultType = HttpResultType.BYTES;

	public HttpRequest(final HttpResultType resultType)
	{
		super();
		this.resultType = resultType;
	}

	/**
	 * @return Returns the clientIp.
	 */
	public String getClientIp()
	{
		return clientIp;
	}

	/**
	 * @param clientIp
	 *           The clientIp to set.
	 */
	public void setClientIp(final String clientIp)
	{
		this.clientIp = clientIp;
	}

	public NameValuePair[] getParameters()
	{
		return parameters;
	}

	public void setParameters(final NameValuePair[] parameters)
	{
		this.parameters = parameters;
	}

	public String getQueryString()
	{
		return queryString;
	}

	public void setQueryString(final String queryString)
	{
		this.queryString = queryString;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(final String method)
	{
		this.method = method;
	}

	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	public void setConnectionTimeout(final int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout(final int timeout)
	{
		this.timeout = timeout;
	}

	/**
	 * @return Returns the charset.
	 */
	public String getCharset()
	{
		return charset;
	}

	/**
	 * @param charset
	 *           The charset to set.
	 */
	public void setCharset(final String charset)
	{
		this.charset = charset;
	}

	public HttpResultType getResultType()
	{
		return resultType;
	}

	public void setResultType(final HttpResultType resultType)
	{
		this.resultType = resultType;
	}

}
