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
package de.hybris.platform.commercefacades.product;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.ProductResultData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductExportFacade;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultProductExportFacade}
 */
@IntegrationTest
public class DefaultProductExportFacadeIntegrationTest extends ServicelayerTest
{

	@Resource
	private ProductExportFacade productExportFacade;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createHardwareCatalog();
	}

	@Test
	public void getAllProductsForOptions()
	{
		final int NUM_PRODUCTS_EXPECTED = 5;
		final ProductResultData result = productExportFacade.getAllProductsForOptions("hwcatalog", "Online", null, 0,
				NUM_PRODUCTS_EXPECTED);
		Assert.assertNotNull(result.getProducts());
		Assert.assertEquals(NUM_PRODUCTS_EXPECTED, result.getRequestedCount());
		Assert.assertEquals(NUM_PRODUCTS_EXPECTED, result.getCount());
	}

}
