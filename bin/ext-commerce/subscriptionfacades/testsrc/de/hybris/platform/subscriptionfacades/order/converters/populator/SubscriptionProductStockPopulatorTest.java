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
package de.hybris.platform.subscriptionfacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionfacades.converters.populator.SubscriptionProductStockPopulator;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link SubscriptionProductStockPopulator}
 */
@UnitTest
public class SubscriptionProductStockPopulatorTest
{
	private static final Long AVAILABLE_STOCK = Long.valueOf(99);
	private static final int SUBSCRIPTION_PRODUCT_STOCK_QUANTITY = 1000;

	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private BaseStoreService baseStoreService;

	private SubscriptionProductStockPopulator productStockPopulator;
	private BaseStoreModel baseStore;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productStockPopulator = new SubscriptionProductStockPopulator();
		productStockPopulator.setBaseStoreService(baseStoreService);
		productStockPopulator.setCommerceStockService(commerceStockService);
		productStockPopulator.setSubscriptionProductStockQuantity(SUBSCRIPTION_PRODUCT_STOCK_QUANTITY);

		baseStore = mock(BaseStoreModel.class);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		given(Boolean.valueOf(commerceStockService.isStockSystemEnabled(baseStore))).willReturn(Boolean.TRUE);
	}


	@Test
	public void testPopulateNoBaseStore()
	{
		final ProductModel source = mock(ProductModel.class);

		given(baseStoreService.getCurrentBaseStore()).willReturn(null);
		given(Boolean.valueOf(commerceStockService.isStockSystemEnabled(null))).willReturn(Boolean.FALSE);

		final StockData result = new StockData();
		productStockPopulator.populate(source, result);

		Assert.assertEquals(StockLevelStatus.INSTOCK, result.getStockLevelStatus());
		Assert.assertEquals(Long.valueOf(0), result.getStockLevel());
	}


	@Test
	public void testPopulateForceOutOfStock()
	{
		final ProductModel source = mock(ProductModel.class);

		given(commerceStockService.getStockLevelForProductAndBaseStore(source, baseStore)).willReturn(Long.valueOf(0));
		given(commerceStockService.getStockLevelStatusForProductAndBaseStore(source, baseStore)).willReturn(
				StockLevelStatus.OUTOFSTOCK);

		final StockData result = new StockData();
		productStockPopulator.populate(source, result);

		Assert.assertEquals(Long.valueOf(0), result.getStockLevel());
		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK, result.getStockLevelStatus());
		verify(commerceStockService, times(1)).getStockLevelForProductAndBaseStore(source, baseStore);
		verify(commerceStockService, times(1)).getStockLevelStatusForProductAndBaseStore(source, baseStore);
	}

	@Test
	public void testPopulateInStock()
	{
		final ProductModel source = mock(ProductModel.class);

		given(commerceStockService.getStockLevelForProductAndBaseStore(source, baseStore)).willReturn(AVAILABLE_STOCK);
		given(commerceStockService.getStockLevelStatusForProductAndBaseStore(source, baseStore)).willReturn(
				StockLevelStatus.INSTOCK);

		final StockData result = new StockData();
		productStockPopulator.populate(source, result);

		Assert.assertEquals(AVAILABLE_STOCK, result.getStockLevel());
		Assert.assertEquals(StockLevelStatus.INSTOCK, result.getStockLevelStatus());
		verify(commerceStockService, times(1)).getStockLevelForProductAndBaseStore(source, baseStore);
		verify(commerceStockService, times(1)).getStockLevelStatusForProductAndBaseStore(source, baseStore);
	}

	@Test
	public void testPopulateSubscriptionProductNoStock()
	{
		final SubscriptionProductModel source = mock(SubscriptionProductModel.class);

		final StockData result = new StockData();
		productStockPopulator.populate(source, result);

		Assert.assertEquals(Long.valueOf(SUBSCRIPTION_PRODUCT_STOCK_QUANTITY), result.getStockLevel());
		Assert.assertEquals(StockLevelStatus.INSTOCK, result.getStockLevelStatus());
		// make sure commerceStockService is not used for subscription products
		verify(commerceStockService, times(0)).getStockLevelForProductAndBaseStore(source, baseStore);
		verify(commerceStockService, times(0)).getStockLevelStatusForProductAndBaseStore(source, baseStore);
	}

}
