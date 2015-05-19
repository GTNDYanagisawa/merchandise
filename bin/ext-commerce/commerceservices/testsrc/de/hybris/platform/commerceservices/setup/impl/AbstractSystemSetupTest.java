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
package de.hybris.platform.commerceservices.setup.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.testframework.TestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
@IntegrationTest
public class AbstractSystemSetupTest extends ServicelayerTransactionalTest
{

	private static String TEST_PRODUCT_CATALOG = "productCatalog";
	private static String TEST_CONTENT_CATALOG = "contentCatalog";

	private SystemSetupContext systemSetupContext;
	private TestSystemSetup testSystemSetup;


	@Resource
	private CatalogService catalogService;

	@Resource
	private SetupSyncJobService setupSyncJobService;

	@Resource
	private SetupImpexService setupImpexService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private SetupSolrIndexerService setupSolrIndexerService;

	//
	@Before
	public void setUp() throws Exception
	{
		final Map parameter = new HashMap();
		parameter.put("test", new String[]
		{ "value1", "value2" });
		parameter.put("initmethod", new String[]
		{ "init" });
		systemSetupContext = new SystemSetupContext(parameter, SystemSetup.Type.ALL, SystemSetup.Process.INIT, "testextension");

		testSystemSetup = new TestSystemSetup();
		testSystemSetup.setSetupImpexService(setupImpexService);
		testSystemSetup.setSetupSyncJobService(setupSyncJobService);
		testSystemSetup.setSetupSolrIndexerService(setupSolrIndexerService);
		testSystemSetup.setCatalogVersionService(catalogVersionService);

		if (MediaManager.getInstance().getMediaFolderByQualifier("root").isEmpty())
		{
			MediaManager.getInstance().createMediaFolder("root", null);
		}
	}

	@Test
	public void testInitOptions() throws Exception
	{
		final TestSystemSetup testSystemSetup = new TestSystemSetup();
		assertEquals("Options not empty", Collections.EMPTY_LIST, testSystemSetup.getInitializationOptions());
	}


	@Test
	public void testImportCsv() throws Exception
	{
		testSystemSetup.importImpexFile(systemSetupContext, "/commerceservices/test/testSystemSetup.impex");

		assertNotNull("Catalog was null", catalogService.getCatalogForId(TEST_PRODUCT_CATALOG));
	}

	@Test
	public void testCreateProductCatalogSyncJob() throws Exception
	{
		try
		{
			testSystemSetup.getCatalogSyncJob(TEST_PRODUCT_CATALOG);
			fail("Catalog was not null");
		}
		catch (final UnknownIdentifierException e)
		{
			//that's fine - catalog doesn't exist yet
		}

		testSystemSetup.importImpexFile(systemSetupContext, "/commerceservices/test/testSystemSetup.impex");
		assertNotNull("Catalog was null", catalogService.getCatalogForId(TEST_PRODUCT_CATALOG));

		testSystemSetup.createProductCatalogSyncJob(systemSetupContext, TEST_PRODUCT_CATALOG);
		assertTrue("No catalog sync jobs created",
				CollectionUtils.isNotEmpty(testSystemSetup.getCatalogSyncJob(TEST_PRODUCT_CATALOG)));
	}

	@Test
	public void testCreateContentCatalogSyncJob() throws Exception
	{
		try
		{
			testSystemSetup.getCatalogSyncJob(TEST_CONTENT_CATALOG);
			fail("Catalog was not null");
		}
		catch (final UnknownIdentifierException e)
		{
			//that's fine - catalog doesn't exist yet
		}
		testSystemSetup.importImpexFile(systemSetupContext, "/commerceservices/test/testSystemSetup.impex");

		testSystemSetup.createContentCatalogSyncJob(systemSetupContext, TEST_CONTENT_CATALOG);
		assertNotNull("Catalog was null", catalogService.getCatalogForId(TEST_CONTENT_CATALOG));

		assertTrue("No catalog sync jobs created",
				CollectionUtils.isNotEmpty(testSystemSetup.getCatalogSyncJob(TEST_CONTENT_CATALOG)));
	}

	@Test
	public void testLogError() throws Exception
	{
		TestUtils.disableFileAnalyzer("Testing log error");
		testSystemSetup.logError(systemSetupContext, "Test Error", null);
		TestUtils.enableFileAnalyzer();
	}

	public class TestSystemSetup extends AbstractSystemSetup
	{
		@Override
		public List<SystemSetupParameter> getInitializationOptions()
		{
			return Collections.EMPTY_LIST;
		}
	}
}
