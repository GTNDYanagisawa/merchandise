/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.core.configuration.http.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.configuration.enums.HTTPAuthenticationType;
import de.hybris.platform.sap.core.configuration.http.HTTPDestination;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests for the test of the HTTP destination service.
 */
@IntegrationTest
public class HTTPDestinationServiceTest extends ServicelayerTransactionalTest
{

	/**
	 * Constant for HTTP destination.
	 */
	final static String HTTP_DESTINATION = "http_test_destination";
	/**
	 * Constant forHTTP destination.
	 */
	final static String HTTP_DESTINATION2 = "http_test_destination_2";

	/**
	 * Variable for HTTP destination model.
	 */
	private SAPHTTPDestinationModel httpDestinationModel;
	/**
	 * Variable for HTTP destination model.
	 */
	private SAPHTTPDestinationModel httpDestinationModel2;

	/**
	 * Variable for the HTTP destination service.
	 */
	@Resource(name = "sapCoreHTTPDestinationService")
	private HTTPDestinationServiceImpl httpDestinationService;

	/**
	 * Variable for the model service.
	 */
	@Resource
	private ModelService modelService;

	@SuppressWarnings("javadoc")
	@Before
	public void setUp()
	{
		httpDestinationModel = new SAPHTTPDestinationModel();
		httpDestinationModel.setHttpDestinationName(HTTP_DESTINATION);
		httpDestinationModel.setTargetURL("http://test.com");
		httpDestinationModel2 = new SAPHTTPDestinationModel();
		httpDestinationModel2.setHttpDestinationName(HTTP_DESTINATION2);
		httpDestinationModel2.setTargetURL("http://test.com");

	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHTTPDestinationServiceGet()
	{
		setHttpDestinationParameters(httpDestinationModel);
		modelService.save(httpDestinationModel);

		final HTTPDestination httpDestination = httpDestinationService.getHTTPDestination(HTTP_DESTINATION);

		assertEquals(HTTP_DESTINATION, httpDestination.getHttpDestinationName());

		modelService.remove(httpDestinationModel);
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHTTPDestinationServiceGetList()
	{
		setHttpDestinationParameters(httpDestinationModel);
		setHttpDestinationParameters(httpDestinationModel2);
		modelService.saveAll(httpDestinationModel, httpDestinationModel2);

		final List<SAPHTTPDestinationModel> httpDestinations = httpDestinationService.getHttpDestinations();

		assertTrue(httpDestinations.size() == 2);

		modelService.remove(httpDestinationModel);
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHTTPDestinationEmptyTargetURL()
	{

		boolean modelSaved = true;
		try
		{
			modelService.save(httpDestinationModel);
		}
		catch (final ModelSavingException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(InterceptorException.class);
			modelSaved = false;
		}

		if (modelSaved)
		{
			modelService.remove(httpDestinationModel);
		}

	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHTTPDestinationIncompleteAuthenticationData()
	{

		boolean modelSaved = true;

		httpDestinationModel.setAuthenticationType(HTTPAuthenticationType.BASIC_AUTHENTICATION);
		try
		{
			modelService.save(httpDestinationModel);
		}
		catch (final ModelSavingException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(InterceptorException.class);
			modelSaved = false;
		}

		if (modelSaved)
		{
			modelService.remove(httpDestinationModel);
		}

	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHTTPDestinationCompleteData()
	{

		setHttpDestinationParameters(httpDestinationModel);

		try
		{
			modelService.save(httpDestinationModel);
		}
		catch (final ModelSavingException e)
		{
			assertNull(e);
		}

		assertTrue(true);
		modelService.remove(httpDestinationModel);

	}

	@SuppressWarnings("javadoc")
	private void setHttpDestinationParameters(final SAPHTTPDestinationModel httpDestinationModel)
	{
		httpDestinationModel.setAuthenticationType(HTTPAuthenticationType.BASIC_AUTHENTICATION);
		httpDestinationModel.setPassword("test");
		httpDestinationModel.setUserid("test");
		httpDestinationModel.setTargetURL("http://test.com");
	}
}
