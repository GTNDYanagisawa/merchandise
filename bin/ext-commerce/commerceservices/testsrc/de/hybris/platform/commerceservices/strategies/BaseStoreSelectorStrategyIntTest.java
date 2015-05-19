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
package de.hybris.platform.commerceservices.strategies;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link BaseStoreSelectorStrategy}.
 */
@IntegrationTest
public class BaseStoreSelectorStrategyIntTest extends ServicelayerTransactionalTest
{
	@Resource
	private BaseStoreSelectorStrategy baseStoreSelectorStrategy;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private CommerceStrategyTestHelper helper;

	private BaseSiteModel baseSiteModel;

	@Before
	public void setUp()
	{
		helper = new CommerceStrategyTestHelper();
		baseSiteModel = helper.createSite(modelService, baseSiteService);
	}

	@Test
	public void testEmpty()
	{
		Assert.assertNull(baseStoreSelectorStrategy.getCurrentBaseStore());
	}

	@Test
	public void testOrder()
	{
		final BaseStoreModel store1 = helper.createStore("store1", modelService);
		final BaseStoreModel store2 = helper.createStore("store2", modelService);
		final List<BaseStoreModel> stores = new LinkedList<BaseStoreModel>();
		stores.add(store1);
		stores.add(store2);
		baseSiteModel.setStores(stores);
		modelService.saveAll();
		Assert.assertEquals(store1, baseStoreSelectorStrategy.getCurrentBaseStore());
	}
}
