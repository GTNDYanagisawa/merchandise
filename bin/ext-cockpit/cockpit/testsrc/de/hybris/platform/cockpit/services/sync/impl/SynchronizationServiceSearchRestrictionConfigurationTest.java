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
package de.hybris.platform.cockpit.services.sync.impl;

import de.hybris.platform.testframework.HybrisJUnit4Test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Simple test for PLA-12631
 */
public class SynchronizationServiceSearchRestrictionConfigurationTest extends HybrisJUnit4Test
{

	private SynchronizationServiceImpl synchronizationService;

	@Before
	public void setUp() throws Exception
	{
		synchronizationService = new SynchronizationServiceImpl();
	}

	@Test
	public void testSearchRestrictionsConfiguration()
	{
		Assert.assertTrue("SearchRestrictions should be disabled by default", synchronizationService.isSearchRestrictionDisabled()
				.booleanValue());

		synchronizationService.setSearchRestrictionsDisabled(false);
		Assert.assertFalse("By configuration, SearchRestrictions should NOT be disabled", synchronizationService
				.isSearchRestrictionDisabled().booleanValue());

		synchronizationService.setSearchRestrictionsDisabled(true);
		Assert.assertTrue("By configuration, SearchRestrictions should be disabled", synchronizationService
				.isSearchRestrictionDisabled().booleanValue());
	}
}
