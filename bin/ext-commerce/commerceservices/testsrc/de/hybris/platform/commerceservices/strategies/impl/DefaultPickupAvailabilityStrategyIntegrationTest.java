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
package de.hybris.platform.commerceservices.strategies.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.strategies.PickupAvailabilityStrategy;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class DefaultPickupAvailabilityStrategyIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private ProductService productService;
	@Resource
	private BaseStoreService baseStoreService;
	@Resource
	private PickupAvailabilityStrategy pickupAvailabilityStrategy;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/commerceservices/test/defaultPickupAvailabilityStrategyTest.impex", "utf-8");
		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID("testSite");
		baseSiteService.setCurrentBaseSite(baseSiteForUID, false);
	}

	@Test
	public void testIsPickupAvailableForProductInStock()
	{
		Assert.assertTrue(pickupAvailabilityStrategy.isPickupAvailableForProduct(productService.getProductForCode("product1"),
				baseStoreService.getCurrentBaseStore()).booleanValue());
	}

	@Test
	public void testIsPickupAvailableForProductForceOutOfStock()
	{
		Assert.assertFalse(pickupAvailabilityStrategy.isPickupAvailableForProduct(productService.getProductForCode("product2"),
				baseStoreService.getCurrentBaseStore()).booleanValue());
	}

	@Test
	public void testIsPickupAvailableForProductForStockCalculationReserved()
	{
		Assert.assertFalse(pickupAvailabilityStrategy.isPickupAvailableForProduct(productService.getProductForCode("product3"),
				baseStoreService.getCurrentBaseStore()).booleanValue());
	}
}
