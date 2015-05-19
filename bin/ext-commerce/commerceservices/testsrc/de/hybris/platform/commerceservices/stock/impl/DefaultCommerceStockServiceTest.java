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
package de.hybris.platform.commerceservices.stock.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.impl.DefaultStockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCommerceStockServiceTest
{

	private static final int QTY_OF_PRODUCT_IN_STORE_A = 10;
	private static final int QTY_OF_PRODUCT_IN_STORE_B = 20;

	@InjectMocks
	private final CommerceStockService commerceStockService = new DefaultCommerceStockService();

	@Mock
	private final StockService stockService = new DefaultStockService();

	@Mock
	private ProductModel productModel;

	@Mock
	private BaseStoreModel baseStore;

	@Mock
	private PointOfServiceModel posOne, posTwo;

	@Mock
	private WarehouseModel warehouseDefault, warehouseOne, warehouseTwo;

	@Mock
	private StockLevelModel stockLevelModelOne, stockLevelModelTwo;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		stubStockLevels();
	}

	protected void stubStockLevels()
	{
		final List<WarehouseModel> warehouseModels = new ArrayList<WarehouseModel>();
		warehouseModels.add(warehouseDefault);
		warehouseModels.add(warehouseOne);
		warehouseModels.add(warehouseTwo);

		final List<PointOfServiceModel> pointOfServiceModels = new ArrayList<PointOfServiceModel>();
		pointOfServiceModels.add(posOne);
		pointOfServiceModels.add(posTwo);

		final List<StockLevelModel> stockLevelModels = new ArrayList<StockLevelModel>();
		stockLevelModels.add(stockLevelModelOne);
		stockLevelModels.add(stockLevelModelTwo);

		given(Integer.valueOf(stockLevelModelOne.getAvailable())).willReturn(Integer.valueOf(QTY_OF_PRODUCT_IN_STORE_A));
		given(Integer.valueOf(stockLevelModelTwo.getAvailable())).willReturn(Integer.valueOf(QTY_OF_PRODUCT_IN_STORE_B));

		given(baseStore.getWarehouses()).willReturn(Collections.singletonList(warehouseDefault));
		given(posOne.getWarehouses()).willReturn(Collections.singletonList(warehouseOne));
		given(posTwo.getWarehouses()).willReturn(Collections.singletonList(warehouseTwo));

		given(stockService.getStockLevels(productModel, warehouseModels)).willReturn(stockLevelModels);

		given(baseStore.getPointsOfService()).willReturn(pointOfServiceModels);
	}

	@Test
	public void testIsStockSystemEnabled()
	{
		Assert.assertEquals(true, commerceStockService.isStockSystemEnabled(baseStore));
	}


}
