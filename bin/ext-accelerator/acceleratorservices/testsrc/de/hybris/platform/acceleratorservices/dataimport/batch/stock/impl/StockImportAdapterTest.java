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
package de.hybris.platform.acceleratorservices.dataimport.batch.stock.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.testframework.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link DefaultStockImportAdapter}
 */
@UnitTest
public class StockImportAdapterTest
{
	private static final String TEST_STOCK = "1234";
	private static final String TEST_WAREHOUSE_CODE = "default";
	private static final String TEST_WAREHOUSE_CODE2 = "warehouse";

	@Mock
	private ModelService modelService;
	@Mock
	private WarehouseService warehouseService;
	@Mock
	private StockService stockService;
	@Mock
	private Item item;
	@Mock
	private ProductModel product;
	@Mock
	private WarehouseModel warehouse;

	private DefaultStockImportAdapter defaultStockImportAdapter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultStockImportAdapter = new DefaultStockImportAdapter();
		defaultStockImportAdapter.setModelService(modelService);
		defaultStockImportAdapter.setStockService(stockService);
		defaultStockImportAdapter.setWarehouseService(warehouseService);
		given(modelService.get(item)).willReturn(product);
		given(warehouseService.getWarehouseForCode(TEST_WAREHOUSE_CODE)).willReturn(warehouse);
	}

	@Test
	public void test()
	{
		defaultStockImportAdapter.performImport(TEST_STOCK, item);
		verify(modelService, BDDMockito.times(1)).get(item);
		verify(warehouseService, BDDMockito.times(1)).getWarehouseForCode(TEST_WAREHOUSE_CODE);
		verify(stockService, BDDMockito.times(1)).updateActualStockLevel(product, warehouse, Integer.parseInt(TEST_STOCK), null);
	}

	@Test
	public void testWarehouse()
	{
		defaultStockImportAdapter.setWarehouseCode(TEST_WAREHOUSE_CODE2);
		given(warehouseService.getWarehouseForCode(TEST_WAREHOUSE_CODE2)).willReturn(warehouse);
		defaultStockImportAdapter.performImport(TEST_STOCK, item);
		verify(warehouseService, BDDMockito.times(1)).getWarehouseForCode(TEST_WAREHOUSE_CODE2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStockNull()
	{
		defaultStockImportAdapter.performImport(null, item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStockEmpty()
	{
		defaultStockImportAdapter.performImport(" ", item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStockInvalid()
	{
		TestUtils.disableFileAnalyzer("IllegalArgumentException is expected in this test");
		try
		{
			defaultStockImportAdapter.performImport("abc", item);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testItemNull()
	{
		defaultStockImportAdapter.performImport(TEST_STOCK, null);
	}

}
