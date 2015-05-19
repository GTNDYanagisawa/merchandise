package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.exceptions.CarConnectionRuntimeException;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.sap.sapcarintegration.services.CarConnectionService;

public abstract class AbstractODataFeedService {
	
	private CarConfigurationService carConfigurationService;
	
	private CarConnectionService carConnectionService;
	
	protected String getSapClient() {
		return getCarConfigurationService().getSapClient();
	}
	
	protected String getRootUrl() {
		return getCarConfigurationService().getRootUrl();
	}
	
	protected String getServiceName(){
		
		return getCarConfigurationService().getServiceName();
	}
	
	protected String getServiceURI()
	{
		return getRootUrl() + getServiceName();
	}
		
	protected CarConfigurationService getCarConfigurationService() {
		return carConfigurationService;
	}

	protected void setCarConfigurationService(
			CarConfigurationService carConfigurationService) {
		this.carConfigurationService = carConfigurationService;
	}

	protected CarConnectionService getCarConnectionService() {
		return carConnectionService;
	}

	protected void setCarConnectionService(CarConnectionService carConnectionService) {
		this.carConnectionService = carConnectionService;
	}

	private InputStream execute(final String relativeUri,
			final String contentType, final String httpMethod)
			throws IOException, MalformedURLException {
		final HttpURLConnection connection = getCarConnectionService()
				.createConnection(relativeUri, contentType, httpMethod);

		final InputStream content = connection.getInputStream();

		return content;
	}

	protected String convertToInternalKey(final String id) {
		if (id.matches("\\d+") ) {
			final Integer in = Integer.valueOf(id);
			final String intKey = String.format("%010d", in);
			return intKey;
		}
		
		return id;
	}

	protected ODataFeed readFeed(final String serviceUri,
			final String contentType, final String entitySetQueryName,
			final String entityResultSetName, final String select,
			final String filter, final String expand) {

		final Edm edm = this.readEdm(serviceUri);

		try {
			final EdmEntityContainer entityContainer = edm
					.getDefaultEntityContainer();
			final String absoluteUri = createUri(serviceUri,
					entitySetQueryName, null, expand, select, filter);
			InputStream content = null;
			content = execute(absoluteUri, contentType,
					SapcarintegrationConstants.HTTP_METHOD_GET);

			return EntityProvider.readFeed(contentType,
					entityContainer.getEntitySet(entityResultSetName), content,
					EntityProviderReadProperties.init().build());
		} catch (final IOException | EntityProviderException | EdmException e) {
			throw new CarConnectionRuntimeException(
					"error while reading feed url : " + serviceUri + e);
		}

	}

	protected Edm readEdm(final String serviceUrl) {
		InputStream content;
		try {
			content = execute(serviceUrl + SapcarintegrationConstants.SEPARATOR + "$metadata",
					SapcarintegrationConstants.APPLICATION_XML, SapcarintegrationConstants.HTTP_METHOD_GET);
			return EntityProvider.readMetadata(content, false);
		} catch (final IOException | EntityProviderException e) {
			throw new CarConnectionRuntimeException(
					"Error while reading service url : " + serviceUrl, e);
		}

	}

	protected String createUri(final String serviceUri,
			final String entitySetName, final String id,
			final String expandRelationName, final String select,
			final String filter) {
		UriBuilder uriBuilder = null;
		if (id == null) {
			uriBuilder = UriBuilder.serviceUri(serviceUri, entitySetName,
					getSapClient());
		} else {
			uriBuilder = UriBuilder.serviceUri(serviceUri, entitySetName, id,
					getSapClient());
		}

		if (!StringUtils.isEmpty(expandRelationName)) {
			uriBuilder = uriBuilder.addQuery("$expand", expandRelationName);
		}

		uriBuilder = uriBuilder.addQuery("$select", select);
		uriBuilder = uriBuilder.addQuery("$filter", filter);

		String absoluteURI;
		try {
			absoluteURI = new URI(null, uriBuilder.build(), null)
					.toASCIIString();
		} catch (URISyntaxException e) {
			throw new CarConnectionRuntimeException(
					"Error while building url : " + serviceUri, e);
		}

		return absoluteURI;
	}

	private static class UriBuilder {
		private StringBuilder uri;
		private StringBuilder query;

		private UriBuilder(final String serviceUri, final String entitySetName,
				final String sapClient) {
			uri = new StringBuilder(serviceUri).append(
					SapcarintegrationConstants.SEPARATOR).append(entitySetName);
			if (!StringUtils.isEmpty(sapClient)) {
				uri = new StringBuilder(uri.append("(P_SAPClient='")
						.append(sapClient).append("')"));
			}
			query = new StringBuilder();
		}

		public static UriBuilder serviceUri(final String serviceUri,
				final String entitySetName, final String id, String sapClient) {
			final UriBuilder b = new UriBuilder(serviceUri, entitySetName,
					sapClient);
			return b.id(id);
		}

		public static UriBuilder serviceUri(final String serviceUri,
				final String entitySetName, String sapClient) {
			return new UriBuilder(serviceUri, entitySetName, sapClient);
		}

		private UriBuilder id(final String id) {
			if (id == null) {
				throw new IllegalArgumentException("Null is not an allowed id");
			}
			uri.append("(").append(id).append(")");
			return this;
		}

		public UriBuilder addQuery(final String queryParameter,
				final String value) {
			if (value != null) {
				if (query.length() == 0) {
					query.append("/Results?");
				} else {
					query.append("&");
				}
				query.append(queryParameter).append("=").append(value);
			}
			return this;
		}

		public String build() {
			return uri.toString() + query.toString();
		}
	}

}
