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
package de.hybris.platform.basecommerce.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * 
 */
public class DefaultBaseStoreSelectorStrategyTest
{
	private DefaultBaseStoreSelectorStrategy strategy;
	@Mock
	private BaseSiteService siteService;
	@Mock
	private BaseSiteModel siteModel;

	private List<BaseStoreModel> stores;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		stores = new LinkedList<BaseStoreModel>();
		strategy = new DefaultBaseStoreSelectorStrategy();
		strategy.setBaseSiteService(siteService);
		given(siteService.getCurrentBaseSite()).willReturn(siteModel);
		given(siteModel.getStores()).willReturn(stores);
	}

	@Test
	public void testEmpty()
	{
		final BaseStoreModel result = strategy.getCurrentBaseStore();
		Assert.assertNull(result);
	}

	@Test
	public void testFirst()
	{
		final BaseStoreModel store1 = Mockito.mock(BaseStoreModel.class);
		final BaseStoreModel store2 = Mockito.mock(BaseStoreModel.class);
		stores.add(store1);
		stores.add(store2);
		BaseStoreModel result = strategy.getCurrentBaseStore();
		Assert.assertEquals(store1, result);
		stores.remove(0);
		result = strategy.getCurrentBaseStore();
		Assert.assertEquals(store2, result);
	}

}
