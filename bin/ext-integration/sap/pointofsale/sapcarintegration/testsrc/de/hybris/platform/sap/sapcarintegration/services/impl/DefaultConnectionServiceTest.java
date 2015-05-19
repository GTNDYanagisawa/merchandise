package de.hybris.platform.sap.sapcarintegration.services.impl;


import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;



/**
 * Test for configuration provider.
 */
@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultConnectionServiceTest extends SapcoreSpringJUnitTest
{
	
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	
	
	
	@Resource
	DefaultCarConnectionService carConnectionService; //NOPMD
	
	@Resource
	CarConfigurationService carConfigurationService; //NOPMD
	

	

	
	@Test
	public void testConnection() throws URISyntaxException, MalformedURLException, IOException {

				
		HttpURLConnection connection = carConnectionService.createConnection(carConfigurationService.getRootUrl() + carConfigurationService.getServiceName(), APPLICATION_JSON, HTTP_METHOD_GET);
		assertNotNull(connection);
		
	}

}

