package com.sap.hybris.reco.common.util;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
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
	private static final int READ_TIMEOUT_RT = 5000;

	private static final Logger LOG = Logger.getLogger(ODataClientService.class.getName());

	private final Properties properties = new Properties();
	private String user;
	private String password;
	private String serviceUri;

	/**
	 * @param user
	 * @param password
	 */
	public void createService(final String user, final String password)
	{
		this.user = user;
		this.password = password;
	}

	private HttpURLConnection initializeConnection(final String absoluteUri, final String contentType, final String httpMethod)
			throws IOException
	{
		LOG.debug("Initialize connection for URL: " + absoluteUri);
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
			connection.setRequestMethod(httpMethod); //throws ProtocolException, subclass of IOException
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT_RT);
		}
		catch (final IOException IOe)
		{
			throw new IOException("Error initializing connection to CEI: " + this.serviceUri, IOe);
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
		LOG.debug("End of initialize connection");
		return connection;
	}

	/**
	 * @return PROXY_HOST
	 */
	protected String getProxyHost()
	{
		return properties.getProperty(PROXY_HOST);
	}

	/**
	 * @return PROXY_PORT
	 */
	protected int getProxyPort()
	{
		final String value = properties.getProperty(PROXY_PORT);
		return Integer.parseInt(value);
	}

	private InputStream execute(final String relativeUri, final String contentType, final String httpMethod) throws IOException
	{
		InputStream content = null;
		final HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);
		try
		{
			connection.connect();

			final HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
			if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599)
			{
				final String msg = "Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
						+ httpStatusCode.toString();
				throw new IOException(msg);
			}
			content = connection.getInputStream();
		}
		catch (final IOException e)
		{
			throw new IOException("Failed to connect to CEI backend system: " + this.serviceUri, e);
		}
		return content;
	}

	/**
	 * @param serviceUri
	 * @param contentType
	 * @param entitySetName
	 * @return ODataFeed
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ODataFeed readFeed(final String serviceUri, final String contentType, final String entitySetName)
			throws ODataException, URISyntaxException, IOException
	{
		return readFeed(serviceUri, contentType, entitySetName, null, null, null, null);
	}

	/**
	 * @param serviceUri
	 * @param contentType
	 * @param entitySetName
	 * @param select
	 * @param filter
	 * @param expand
	 * @param orderby
	 * @return OdataFeed
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ODataFeed readFeed(final String serviceUri, final String contentType, final String entitySetName, final String expand,
			final String select, final String filter, final String orderby) throws ODataException, URISyntaxException, IOException
	{
		LOG.debug("Start of read (feed) ");
		this.serviceUri = serviceUri;
		EdmEntityContainer entityContainer = null;
		InputStream content = null;
		ODataFeed oDF = null;
		try
		{
			final String absoluteUri = createUri(serviceUri, entitySetName, null, expand, select, filter, orderby);
			final Edm edm = this.readEdm(serviceUri);
			entityContainer = edm.getDefaultEntityContainer();
			content = execute(absoluteUri, contentType, HTTP_METHOD_GET);
			oDF = EntityProvider.readFeed(contentType, entityContainer.getEntitySet(entitySetName), content,
					EntityProviderReadProperties.init().build());

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
					LOG.error("", e);
				}
			}
		}
		LOG.debug("End of read (feed) ");
		return oDF;
	}


	private Edm readEdm(final String serviceUrl) throws ODataException, IOException
	{
		InputStream content = null;
		Edm edm = null;
		try
		{
			content = execute(serviceUrl + SEPARATOR + "$metadata", APPLICATION_XML, HTTP_METHOD_GET); //throw IOException
			edm = EntityProvider.readMetadata(content, false);//throw EntityProviderException
		}
		catch (final ODataException e)
		{
			throw new ODataException("HTTP Destination is not configured correctly: " + this.serviceUri, e);
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
			final String select, final String filter, final String orderby) throws URISyntaxException
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
		uriBuilder = uriBuilder.addQuery("$orderby", orderby);

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
