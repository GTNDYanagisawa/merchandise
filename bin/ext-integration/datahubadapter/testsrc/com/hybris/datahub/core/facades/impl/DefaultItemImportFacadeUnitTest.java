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
package com.hybris.datahub.core.facades.impl;

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportConfig.ValidationMode;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import com.hybris.datahub.core.facades.ItemImportResult;
import com.hybris.datahub.core.services.ImpExResourceFactory;
import com.hybris.datahub.core.services.impl.DataHubFacade;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * A unit test for <code>DefaultItemImportFacade</code>
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("javadoc")
public class DefaultItemImportFacadeUnitTest
{
	private static final String POOL_NAME = "Test pool";
	private static final Long PUBLICATION_ID = 1L;
	private static final String URL = "http://localhost";
	private static final InputStream impexInput = Mockito.mock(InputStream.class);

	private ItemImportResult itemImportResult;

	@Mock
	private ImportService importService;
	@Mock
	private ImpExResourceFactory resourceFactory;
	@Mock
	private ImportResultConverter resultConverter;
	@Mock
	private EventService eventService;
	@Mock
	private DataHubFacade dataHubFacade;
	private final DefaultItemImportFacade facade = new DefaultItemImportFacade();

	@Before
	public void setUp() throws ImpExException
	{
		setUpResourceFactory();
		final ImportResult importResult = setUpImportService();
		setUpImportResultConverter(importResult);

		// explicit wiring instead of @InjectMocks to provide coverage for the setters 
		facade.setImportService(importService);
		facade.setResourceFactory(resourceFactory);
		facade.setResultConverter(resultConverter);
		facade.setDataHubFacade(dataHubFacade);
		facade.setEventService(eventService);
	}

	private void setUpResourceFactory() throws ImpExException
	{
		final ImpExResource resource = Mockito.mock(ImpExResource.class);
		Mockito.doReturn(resource).when(resourceFactory).createResource(impexInput);
	}

	private ImportResult setUpImportService()
	{
		final ImportResult res = Mockito.mock(ImportResult.class);
		Mockito.doReturn(Boolean.TRUE).when(res).isSuccessful();
		Mockito.doReturn(res).when(importService).importData(any(ImportConfig.class));
		return res;
	}

	private void setUpImportResultConverter(final ImportResult importRes)
	{
		itemImportResult = Mockito.mock(ItemImportResult.class);
		Mockito.doReturn(true).when(itemImportResult).isSuccessful();
		Mockito.doReturn(itemImportResult).when(resultConverter).convert(importRes);
	}

	@Test
	public void testCorrectlyCreatesImportConfigBeforeCallingTheImportService() throws Exception
	{
		final ArgumentCaptor<ImportConfig> capturedConfig = ArgumentCaptor.forClass(ImportConfig.class);

		facade.importItems(POOL_NAME, PUBLICATION_ID, URL, impexInput);

		Mockito.verify(importService).importData(capturedConfig.capture());
		assertCreatedImportConfig(capturedConfig.getValue());
	}

	private void assertCreatedImportConfig(final ImportConfig cfg)
	{
		Assert.assertNotNull("Config not created", cfg);
		Assert.assertNotNull("Script not set", cfg.getScript());
		Assert.assertEquals("Strict validation not set", ValidationMode.STRICT, cfg.getValidationMode());
		Assert.assertTrue("Synchronous processing not set", cfg.isSynchronous());
	}

	@Test
	public void testImportResultIsReturnedSuccessfully() throws Exception
	{
		facade.importItems(POOL_NAME, PUBLICATION_ID, URL, impexInput);

		Mockito.verify(dataHubFacade).returnImportResult(URL, null, itemImportResult);
	}

	@Test
	public void testImportResultIsSuccessfulWhenImportCompletesSuccessfully() throws Exception
	{
		final ItemImportResult res = facade.runImport(impexInput, POOL_NAME);

		Assert.assertNotNull("Result not returned", res);
		Assert.assertTrue("Result not successful", res.isSuccessful());
	}

	@Test
	public void testImportResultIsErrorWhenErrorIsReportedFromTheImportService() throws Exception
	{
		simulateErrorResultFromTheImportService();

		final ItemImportResult res = facade.runImport(impexInput, POOL_NAME);

		Assert.assertFalse(res.isSuccessful());
	}

	private void simulateErrorResultFromTheImportService()
	{
		Mockito.doReturn(Boolean.FALSE).when(itemImportResult).isSuccessful();
	}

	@Test
	public void testImportResultIsErrorWhenImportServiceCrashes() throws Exception
	{
		simulateExceptionOnImport();

		final ItemImportResult res = facade.runImport(impexInput, POOL_NAME);

		Assert.assertFalse(res.isSuccessful());
	}

	private void simulateExceptionOnImport()
	{
		Mockito.doThrow(new RuntimeException()).when(importService).importData(any(ImportConfig.class));
	}

	@Test
	public void testImportResultIsErrorWhenImpExScriptIsInvalid() throws Exception
	{
		simulateExceptionOnReadingImpExScript();

		final ItemImportResult res = facade.runImport(impexInput, POOL_NAME);

		Assert.assertFalse(res.isSuccessful());
	}

	private void simulateExceptionOnReadingImpExScript() throws ImpExException
	{
		Mockito.doThrow(new ImpExException("Invalid script")).when(resourceFactory).createResource(impexInput);
	}

	@Test
	public void testReturnImportResultSuccess() throws Exception
	{
		facade.importItems(POOL_NAME, PUBLICATION_ID, URL, impexInput);

		Mockito.verify(dataHubFacade).returnImportResult(URL, null, itemImportResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReturnImportResultFailure() throws Exception
	{
		Mockito.doThrow(new IllegalArgumentException()).when(dataHubFacade).returnImportResult(URL, null, itemImportResult);
		facade.importItems(POOL_NAME, PUBLICATION_ID, URL, impexInput);
	}
}
