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

import static junit.framework.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test suite for {@link DefaultSetupImpexService}
 */
@IntegrationTest
public class DefaultSetupImpexServiceTest extends ServicelayerTest
{
	private static final String TEST_PRODUCT_CATALOG = "productCatalog";

	@Resource
	private SetupImpexService setupImpexService;

	@Resource
	private CatalogService catalogService;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
	}


	@Test
	public void testImportImpexFile() throws Exception
	{
		setupImpexService.importImpexFile("/commerceservices/test/testSystemSetup.impex", true);
		assertNotNull("Catalog was null", catalogService.getCatalogForId(TEST_PRODUCT_CATALOG));
	}
}
