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
package com.hybris.instore.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultInStoreSiteSelectorStrategyTest
{
	private static final String INSTORE_TEST_PREFIX = "instoreTest___";
	private DefaultInStoreSiteSelectorStrategy inStoreSiteSelectorStrategy;

	@Before
	public void setUp()
	{
		inStoreSiteSelectorStrategy = new DefaultInStoreSiteSelectorStrategy();
		inStoreSiteSelectorStrategy.setSiteUidPrefix(INSTORE_TEST_PREFIX);
	}

	@Test
	public void testGetDefaultSite()
	{

		// test correct failing
		boolean exceptionThrown = false;
		try
		{
			inStoreSiteSelectorStrategy.getDefaultSite(null);
		}
		catch (final IllegalArgumentException e)
		{
			exceptionThrown = true;
		}
		Assert.assertTrue("Expected exception has not been thrown.", exceptionThrown);

		exceptionThrown = false;
		try
		{
			inStoreSiteSelectorStrategy.getDefaultSite(Collections.EMPTY_LIST);
		}
		catch (final IllegalArgumentException e)
		{
			exceptionThrown = true;
		}
		Assert.assertTrue("Expected exception has not been thrown.", exceptionThrown);



		// test correct functionality
		final List<BaseSiteModel> sites = new ArrayList<BaseSiteModel>();

		final BaseSiteModel site1 = new BaseSiteModel();
		site1.setUid("site1");
		sites.add(site1);
		final BaseSiteModel site2 = new BaseSiteModel();
		site2.setUid("site2");
		sites.add(site2);
		final BaseSiteModel site3 = new BaseSiteModel();
		site3.setUid("site3");
		sites.add(site3);

		// if we have only one element, it should be returned (obviously ;-))
		Assert.assertEquals(site1, inStoreSiteSelectorStrategy.getDefaultSite(Collections.singleton(site1)));

		// if we have more than one element and none does match the prefix, the first one should be returned
		Assert.assertEquals(site1, inStoreSiteSelectorStrategy.getDefaultSite(sites));

		// if we have one single element matching the prefix, this one should be returned
		site3.setUid(INSTORE_TEST_PREFIX + "foobar");
		Assert.assertEquals(site3, inStoreSiteSelectorStrategy.getDefaultSite(sites));

		// if we have multiple elements matching the prefix, the first should be returned
		site2.setUid(INSTORE_TEST_PREFIX + "foobar");
		Assert.assertEquals(site2, inStoreSiteSelectorStrategy.getDefaultSite(sites));
	}
}
