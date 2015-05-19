package de.hybris.platform.sap.sapcarintegration.services.impl;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.exceptions.CarConnectionRuntimeException;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.sap.sapcarintegration.services.CarConnectionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;


/**
 *
 */
public class DefaultCarConnectionService implements CarConnectionService
{

	//injected to the service
	private String proxyhost;
	
	//injected to the service
	private Integer proxyport;
	
	private CommonI18NService commonI18NService;
	
	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}
    
	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	private CarConfigurationService carConfigurationService;
	
	
	public CarConfigurationService getCarConfigurationService() {
		return carConfigurationService;
	}

	public void setCarConfigurationService(
			CarConfigurationService carConfigurationService) {
		this.carConfigurationService = carConfigurationService;
	}

	public String getProxyhost() {
		return proxyhost;
	}

	@Required
	public void setProxyhost(String proxyhost) {
		this.proxyhost = proxyhost;
	}

	public Integer getProxyport() {
		return proxyport;
	}

	@Required
	public void setProxyport(Integer proxyport) {
		this.proxyport = proxyport;
	}

	
	/* (non-Javadoc)
	 * @see de.hybris.platform.sap.sapcarintegration.services.impl.CarConnectionService1#createConnection(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public HttpURLConnection createConnection(final String absoluteUri, final String contentType, final String httpMethod)
			throws IOException, MalformedURLException
	{
		
		final URL url = new URL(absoluteUri);

		HttpURLConnection connection = null;
		if (!StringUtils.isEmpty(getProxyhost()))
		{
			final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getProxyhost(), getProxyport()));
			connection = (HttpURLConnection) url.openConnection(proxy);
		}
		else
		{
			connection = (HttpURLConnection) url.openConnection();
		}
		connection.setRequestProperty(SapcarintegrationConstants.HTTP_HEADER_ACCEPT, contentType);
		connection.setRequestMethod(httpMethod);			
		connection.setRequestProperty("Accept-Language", getLanguage() + ";q=0.5" );
		
		if (SapcarintegrationConstants.HTTP_METHOD_POST.equals(httpMethod) || SapcarintegrationConstants.HTTP_METHOD_PUT.equals(httpMethod))
		{
			connection.setDoOutput(true);
			connection.setRequestProperty(SapcarintegrationConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		//authentification required
		if (!StringUtils.isEmpty(getCarConfigurationService().getUsername()))
			
		{
			String authorization = "Basic ";
			authorization += new String(Base64.encodeBase64((getCarConfigurationService().getUsername() + ":" + getCarConfigurationService().getPassword()).getBytes()));
			connection.setRequestProperty("Authorization", authorization);
		}
		
		connection.connect();
		
		checkStatus(connection);
		
		return connection;
	}

	private HttpStatusCodes checkStatus(final HttpURLConnection connection) throws IOException
	{
		final HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599)
		{
			final String msg = "Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
					+ httpStatusCode.toString() + ".\n\tRequest URL was: '" + connection.getURL().toString() + "'.";
			throw new CarConnectionRuntimeException(msg);
		}
		return httpStatusCode;
	}
	
	protected String getLanguage()
	{
		return getCommonI18NService().getCurrentLanguage().getIsocode();
	}

}

