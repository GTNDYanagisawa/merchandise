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
package de.hybris.platform.configurablebundleservices.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * JUnit test suite for {@link DefaultCartEntryDao}
 */
@UnitTest
public class DefaultCartEntryDaoTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultCartEntryDao cartEntryDao;

	@Before
	public void setUp() throws Exception
	{
		cartEntryDao = new DefaultCartEntryDao();
	}

	@Test
	public void testFindEntriesByMasterCartAndBundleNoWhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		cartEntryDao.findEntriesByMasterCartAndBundleNo(null, 1);
	}

	@Test
	public void testFindEntriesByMasterCartAndBundleNoAndTemplateWhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(null, 2, new BundleTemplateModel());
	}

	@Test
	public void testFindEntriesByMasterCartAndBundleNoAndTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(new CartModel(), 2, null);
	}

	@Test
	public void testFindEntriesByMasterCartAndBundleNoAndProductWhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		cartEntryDao.findEntriesByMasterCartAndBundleNoAndProduct(null, 1, new ProductModel());
	}

	@Test
	public void testFindEntriesByMasterCartAndBundleNoAndProductWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("product can not be null");

		cartEntryDao.findEntriesByMasterCartAndBundleNoAndProduct(new CartModel(), 1, null);
	}

}
