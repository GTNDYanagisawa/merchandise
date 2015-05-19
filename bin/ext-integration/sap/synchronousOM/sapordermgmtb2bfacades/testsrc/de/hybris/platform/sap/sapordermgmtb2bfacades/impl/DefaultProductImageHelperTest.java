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
package de.hybris.platform.sap.sapordermgmtb2bfacades.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.product.ProductService;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
@UnitTest
public class DefaultProductImageHelperTest
{
	DefaultProductImageHelper classUnderTest = new DefaultProductImageHelper();

	@Before
	public void init()
	{
		final ProductService productService = EasyMock.createMock(ProductService.class);
		final ProductPrimaryImagePopulator productImagePopulator = new ProductPrimaryImagePopulator<>();
		EasyMock.replay(productService);
		classUnderTest.setProductService(productService);
		classUnderTest.setProductPrimaryImagePopulator(productImagePopulator);
	}

	@Test
	public void test()
	{
		final AbstractOrderData order = new AbstractOrderData();
		final List<OrderEntryData> entries = new ArrayList<>();
		order.setEntries(entries);
		final OrderEntryData entry = new OrderEntryData();
		entries.add(entry);
		classUnderTest.enrichWithProductImages(order);
	}

	@Test
	public void testProductService()
	{
		assertNotNull(classUnderTest.getProductService());
	}

	@Test
	public void testProductPrimaryImagePopulator()
	{
		assertNotNull(classUnderTest.getProductPrimaryImagePopulator());
	}
}
