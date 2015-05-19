package cn.alipay.payment.util.httpclient;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.log4j.Logger;


public class HttpProtocolHandler
{
	private static Logger LOG = Logger.getLogger(HttpProtocolHandler.class);

	private static String DEFAULT_CHARSET = "GBK";

	private final int defaultConnectionTimeout = 8000;

	private final int defaultSoTimeout = 30000;

	private final int defaultIdleConnTimeout = 60000;

	private final int defaultMaxConnPerHost = 30;

	private final int defaultMaxTotalConn = 80;

	private static final long defaultHttpConnectionManagerTimeout = 3 * 1000;


	private final HttpConnectionManager connectionManager;

	private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();


	public static HttpProtocolHandler getInstance()
	{
		return httpProtocolHandler;
	}


	private HttpProtocolHandler()
	{
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
		connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

		final IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
		ict.addConnectionManager(connectionManager);
		ict.setConnectionTimeout(defaultIdleConnTimeout);

		ict.start();
	}

	public HttpResponse execute(final HttpRequest request)
	{
		final HttpClient httpclient = new HttpClient(connectionManager);

		int connectionTimeout = defaultConnectionTimeout;
		if (request.getConnectionTimeout() > 0)
		{
			connectionTimeout = request.getConnectionTimeout();
		}
		httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);

		int soTimeout = defaultSoTimeout;
		if (request.getTimeout() > 0)
		{
			soTimeout = request.getTimeout();
		}
		httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

		httpclient.getParams().setConnectionManagerTimeout(defaultHttpConnectionManagerTimeout);

		String charset = request.getCharset();
		charset = charset == null ? DEFAULT_CHARSET : charset;
		HttpMethod method = null;

		if (request.getMethod().equals(HttpRequest.METHOD_GET))
		{
			method = new GetMethod(request.getUrl());
			method.getParams().setCredentialCharset(charset);

			method.setQueryString(request.getQueryString());
		}
		else
		{
			method = new PostMethod(request.getUrl());
			((PostMethod) method).addParameters(request.getParameters());
			method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);

		}

		method.addRequestHeader("User-Agent", "Mozilla/4.0");
		final HttpResponse response = new HttpResponse();

		try
		{
			httpclient.executeMethod(method);
			if (request.getResultType().equals(HttpResultType.STRING))
			{
				response.setStringResult(method.getResponseBodyAsString());
			}
			else if (request.getResultType().equals(HttpResultType.BYTES))
			{
				response.setByteResult(method.getResponseBody());
			}
			response.setResponseHeaders(method.getResponseHeaders());
		}
		catch (final UnknownHostException ex)
		{
			LOG.warn("error on execute http request", ex);
			return null;
		}
		catch (final IOException ex)
		{
			LOG.warn("error on execute http request", ex);
			return null;
		}
		catch (final Exception ex)
		{
			LOG.warn("error on execute http request", ex);
			return null;
		}
		finally
		{
			method.releaseConnection();
		}
		return response;
	}

	protected String toString(final NameValuePair[] nameValues)
	{
		if (nameValues == null || nameValues.length == 0)
		{
			return "null";
		}

		final StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < nameValues.length; i++)
		{
			final NameValuePair nameValue = nameValues[i];

			if (i == 0)
			{
				buffer.append(nameValue.getName() + "=" + nameValue.getValue());
			}
			else
			{
				buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
			}
		}

		return buffer.toString();
	}
}
