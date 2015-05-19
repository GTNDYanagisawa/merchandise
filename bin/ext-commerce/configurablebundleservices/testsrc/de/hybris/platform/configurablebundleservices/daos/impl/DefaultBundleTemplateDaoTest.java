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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * JUnit test suite for {@link DefaultBundleTemplateDao}
 */
@UnitTest
public class DefaultBundleTemplateDaoTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultBundleTemplateDao bundleTemplateDao;

	@Before
	public void setUp() throws Exception
	{
		bundleTemplateDao = new DefaultBundleTemplateDao();
	}

	@Test
	public void testFindBundleTemplatesByProductWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel can not be null");

		bundleTemplateDao.findBundleTemplatesByProduct(null);
	}

	@Test
	public void testFindTemplatesByMasterOrderAndBundleNoWhenNoOrder()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		bundleTemplateDao.findTemplatesByMasterOrderAndBundleNo(null, 1);
	}
}
