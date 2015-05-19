/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sap.wec.adtreco.be;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;



/**
 *
 */
public class ODataClientService
{
	private static final String PROXY_HOST = "proxyhost";
	private static final String PROXY_PORT = "proxyport";
	private static final String SEPARATOR = "/";

	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";

	//Connection times in 1 sec
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int READ_TIMEOUT_DT = 5000;
	private static final int READ_TIMEOUT_RT = 5000;

	private static final Logger LOG = Logger.getLogger(ODataClientService.class.getName());

	private final Properties properties = new Properties();
	private String user;
	private String password;

	public void createService(final String user, final String password)
	{
		this.user = user;
		this.password = password;
	}

	private HttpURLConnection initializeConnection(final String absoluteUri, final String contentType, final String httpMethod)
	{
		LOG.info("Initialize connection for URL: " + absoluteUri);
		HttpURLConnection connection = null;
		try
		{
			final URL url = new URL(absoluteUri);
			if (getProxyHost() != null)
			{
				final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getProxyHost(), getProxyPort()));
				connection = (HttpURLConnection) url.openConnection(proxy);
			}
			else
			{
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
			connection.setRequestMethod(httpMethod);
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT_RT);
		}
		catch (final IOException IOe)
		{
			(new Exception("Error initializing connection to CEI: " + absoluteUri, IOe)).printStackTrace();
		}

		if (HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod))
		{
			connection.setDoOutput(true);
			connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		if (this.user != null)
		{
			String authorization = "Basic ";
			authorization += new String(Base64.encodeBase64((this.user + ":" + this.password).getBytes()));
			connection.setRequestProperty("Authorization", authorization);
		}
		LOG.info("End of initialize connection");
		return connection;
	}

	public String getProxyHost()
	{
		return properties.getProperty(PROXY_HOST);
	}

	public int getProxyPort()
	{
		final String value = properties.getProperty(PROXY_PORT);

		try
		{
			return Integer.parseInt(value);
		}
		catch (final Exception e)
		{
			throw new RuntimeException("Invalid proxy port value '" + value + "'");
		}
	}

	private InputStream execute(final String relativeUri, final String contentType, final String httpMethod) throws IOException,
			MalformedURLException
	{
		final HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);
		connection.connect();
		checkStatus(connection);
		final InputStream content = connection.getInputStream();
		return content;
	}

	private HttpStatusCodes checkStatus(final HttpURLConnection connection) throws IOException
	{
		final HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599)
		{
			final String msg = "Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
					+ httpStatusCode.toString() + ".\n\tRequest URL was: '" + connection.getURL().toString() + "'.";
			throw new RuntimeException(msg);
		}
		return httpStatusCode;
	}

	public ODataFeed readFeed(final String serviceUri, final String contentType, final String entitySetName) throws IOException,
			ODataException, URISyntaxException
	{
		return readFeed(serviceUri, contentType, entitySetName, null, null, null);
	}

	public ODataFeed readFeed(final String serviceUri, final String contentType, final String entitySetName, final String select,
			final String filter, final String expand) throws ODataException, URISyntaxException
	{
		final String absoluteUri = createUri(serviceUri, entitySetName, null, expand, select, filter);

		EdmEntityContainer entityContainer = null;
		InputStream content = null;
		ODataFeed oDF = null;
		try
		{
			LOG.info("Start of read (feed) ");
			final Edm edm = this.readEdm(serviceUri);
			entityContainer = edm.getDefaultEntityContainer();
			content = execute(absoluteUri, contentType, HTTP_METHOD_GET);
			oDF = EntityProvider.readFeed(contentType, entityContainer.getEntitySet(entitySetName), content,
					EntityProviderReadProperties.init().build());
			LOG.info("End of read (feed) ");
		}
		catch (final MalformedURLException e)
		{
			(new Exception("HTTP Destination is not configured correctly: " + absoluteUri, e)).printStackTrace();
		}
		catch (final SocketTimeoutException ex)
		{
			(new Exception("Connection to CEI backend system has timed-out. System not reachable. ", ex)).printStackTrace();
		}
		catch (final IOException e)
		{
			(new Exception("Connection to CEI backend system has failed: " + absoluteUri, e)).printStackTrace();
		}
		finally
		{
			if (content != null)
			{
				try
				{
					content.close();
				}
				catch (final IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return oDF;
	}

	public ODataEntry readEntry(final String serviceUri, final String contentType, final String entitySetName,
			final String select, final String filter, final String keyValue) throws ODataException, URISyntaxException, IOException
	{

		final String absoluteUri = createUri(serviceUri, entitySetName, keyValue, "TargetGroup", select, filter);
		InputStream content = null;
		ODataEntry oDE = null;
		try
		{
			LOG.info("Start of read (Entry) ");
			final Edm edm = this.readEdm(serviceUri);
			final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
			content = execute(absoluteUri, APPLICATION_XML, HTTP_METHOD_GET);
			oDE = EntityProvider.readEntry(contentType, entityContainer.getEntitySet(entitySetName), content,
					EntityProviderReadProperties.init().build());
			LOG.info("End of read (feed) ");
		}
		catch (final MalformedURLException e)
		{
			(new Exception("HTTP Destination is not configured correctly (Malformed URL): " + absoluteUri, e)).printStackTrace();
		}
		catch (final SocketTimeoutException e)
		{
			(new Exception("Backend Connection timed-out. System not reachable: " + absoluteUri, e)).printStackTrace();
		}
		finally
		{
			if (content != null)
			{
				content.close();
			}
		}

		return oDE;
	}

	protected Edm readEdm(final String serviceUrl) throws IOException
	{
		InputStream content = null;
		Edm edm = null;
		try
		{
			content = execute(serviceUrl + SEPARATOR + "$metadata", APPLICATION_XML, HTTP_METHOD_GET);
			edm = EntityProvider.readMetadata(content, false);
		}
		catch (final ODataException e)
		{
			(new Exception("HTTP Destination is not configured correctly: " + serviceUrl, e)).printStackTrace();
		}
		catch (final IOException e)
		{
			(new Exception("Exception connection to CEI backend system: " + serviceUrl, e)).printStackTrace();
		}
		finally
		{
			if (content != null)
			{
				content.close();
			}
		}

		return edm;

	}

	private String createUri(final String serviceUri, final String entitySetName, final String id, final String expand,
			final String select, final String filter) throws URISyntaxException
	{
		UriBuilder uriBuilder = null;
		if (id == null)
		{
			uriBuilder = UriBuilder.serviceUri(serviceUri, entitySetName);
		}
		else
		{
			uriBuilder = UriBuilder.serviceUri(serviceUri, entitySetName, id);
		}

		uriBuilder = uriBuilder.addQuery("$expand", expand);
		uriBuilder = uriBuilder.addQuery("$select", select);
		uriBuilder = uriBuilder.addQuery("$filter", filter);

		final String absoluteURI = new URI(null, uriBuilder.build(), null).toASCIIString();

		return absoluteURI;
	}

	private static class UriBuilder
	{
		private final StringBuilder uri;
		private final StringBuilder query;

		private UriBuilder(final String serviceUri, final String entitySetName)
		{
			uri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
			query = new StringBuilder();
		}

		public static UriBuilder serviceUri(final String serviceUri, final String entitySetName, final String id)
		{
			final UriBuilder b = new UriBuilder(serviceUri, entitySetName);
			return b.id(id);
		}

		public static UriBuilder serviceUri(final String serviceUri, final String entitySetName)
		{
			return new UriBuilder(serviceUri, entitySetName);
		}

		private UriBuilder id(final String id)
		{
			if (id == null)
			{
				throw new IllegalArgumentException("Null is not an allowed id");
			}
			uri.append("(").append(id).append(")");
			return this;
		}

		public UriBuilder addQuery(final String queryParameter, final String value)
		{
			if (value != null)
			{
				if (query.length() == 0)
				{
					query.append("/?");
				}
				else
				{
					query.append("&");
				}
				query.append(queryParameter).append("=").append(value);
			}
			return this;
		}

		public String build()
		{
			return uri.toString() + query.toString();
		}
	}
}
