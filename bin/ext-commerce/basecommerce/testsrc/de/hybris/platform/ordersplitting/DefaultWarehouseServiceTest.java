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
package de.hybris.platform.ordersplitting;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.daos.WarehouseDao;
import de.hybris.platform.ordersplitting.impl.DefaultWarehouseService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.fest.assertions.Assertions;
import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class DefaultWarehouseServiceTest
{
	@InjectMocks
	DefaultWarehouseService defaultWarehouseService = new DefaultWarehouseService();

	@Mock
	WarehouseDao warehouseDao;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetWarehouses()
	{
		final WarehouseModel warehouse1 = new WarehouseModel();
		final WarehouseModel warehouse2 = new WarehouseModel();
		final ProductModel product1 = new ProductModel();
		product1.setCode("pcode1");
		final ProductModel product2 = new ProductModel();
		product2.setCode("pcode2");
		final OrderEntryModel entry1 = new OrderEntryModel();
		entry1.setProduct(product1);
		final OrderEntryModel entry2 = new OrderEntryModel();
		entry2.setProduct(product2);
		final List<? extends AbstractOrderEntryModel> entries = Arrays.asList(entry1, entry2);

		Mockito.when(warehouseDao.getWarehouses(product1.getCode())).thenReturn(Arrays.asList(warehouse1, warehouse2));
		Mockito.when(warehouseDao.getWarehouses(product2.getCode())).thenReturn(Arrays.asList(warehouse2));

		Assertions.assertThat(defaultWarehouseService.getWarehouses(entries)).containsOnly(warehouse2);
	}


	@Test
	public void testGetWarehousesCheckNull()
	{
		try
		{
			defaultWarehouseService.getWarehouses(null);
			Fail.fail();
		}
		catch (final IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testGetWarehousesDefault()
	{
		final WarehouseModel warehouse1 = new WarehouseModel();
		final WarehouseModel warehouse2 = new WarehouseModel();
		final ProductModel product1 = new ProductModel();
		product1.setCode("pcode1");
		final ProductModel product2 = new ProductModel();
		product2.setCode("pcode2");
		final OrderEntryModel entry1 = new OrderEntryModel();
		entry1.setProduct(product1);
		final OrderEntryModel entry2 = new OrderEntryModel();
		entry2.setProduct(product2);
		final List<? extends AbstractOrderEntryModel> entries = Arrays.asList(entry1, entry2);

		Mockito.when(warehouseDao.getWarehouses(product1.getCode())).thenReturn(Arrays.asList(warehouse1));
		Mockito.when(warehouseDao.getWarehouses(product2.getCode())).thenReturn(Collections.EMPTY_LIST);
		Mockito.when(warehouseDao.getDefWarehouse()).thenReturn(Arrays.asList(warehouse1, warehouse2));

		Assertions.assertThat(defaultWarehouseService.getWarehouses(entries)).containsOnly(warehouse1, warehouse2);
	}


	@Test
	public void testGetWarehousesWithProductsInStock()
	{
		final WarehouseModel warehouse1 = new WarehouseModel();
		final WarehouseModel warehouse2 = new WarehouseModel();
		final ProductModel product1 = new ProductModel();
		product1.setCode("pcode1");
		final OrderEntryModel entry = new OrderEntryModel();
		entry.setProduct(product1);
		entry.setQuantity(Long.valueOf(22));
		entry.setChosenVendor(new VendorModel());


		Mockito.when(
				warehouseDao.getWarehousesWithProductsInStock(entry.getProduct().getCode(), entry.getQuantity().longValue(),
						entry.getChosenVendor())).thenReturn(Arrays.asList(warehouse1, warehouse2));

		Assertions.assertThat(defaultWarehouseService.getWarehousesWithProductsInStock(entry)).containsOnly(warehouse1, warehouse2);
	}

	@Test
	public void testGetWarehousesWithProductsInStockCheckNull()
	{
		try
		{
			defaultWarehouseService.getWarehousesWithProductsInStock(null);
			Fail.fail();
		}
		catch (final IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testGetWarehousesWithProductsInStockDefault()
	{
		final WarehouseModel warehouse1 = new WarehouseModel();
		final WarehouseModel warehouse2 = new WarehouseModel();
		final ProductModel product1 = new ProductModel();
		product1.setCode("pcode1");
		final OrderEntryModel entry = new OrderEntryModel();
		entry.setProduct(product1);
		entry.setQuantity(Long.valueOf(22));
		entry.setChosenVendor(new VendorModel());


		Mockito.when(
				warehouseDao.getWarehousesWithProductsInStock(product1.getCode(), entry.getQuantity().longValue(),
						entry.getChosenVendor())).thenReturn(Collections.EMPTY_LIST);
		Mockito.when(warehouseDao.getDefWarehouse()).thenReturn(Arrays.asList(warehouse1, warehouse2));

		Assertions.assertThat(defaultWarehouseService.getWarehousesWithProductsInStock(entry)).containsOnly(warehouse1, warehouse2);
	}

	@Test
	public void testGetDefWarehouse()
	{
		final WarehouseModel warehouse1 = new WarehouseModel();
		final WarehouseModel warehouse2 = new WarehouseModel();

		Mockito.when(warehouseDao.getDefWarehouse()).thenReturn(Arrays.asList(warehouse1, warehouse2));

		Assertions.assertThat(defaultWarehouseService.getDefWarehouse()).containsOnly(warehouse1, warehouse2);

	}

	@Test
	public void testGetWarehouseForId()
	{
		final WarehouseModel warehouse1 = new WarehouseModel();
		final String code = "id";

		Mockito.when(warehouseDao.getWarehouseForCode(code)).thenReturn(Arrays.asList(warehouse1));

		Assertions.assertThat(defaultWarehouseService.getWarehouseForCode(code)).isEqualTo(warehouse1);

	}

	@Test
	public void testGetWarehouseForIdCheckNull()
	{
		try
		{
			defaultWarehouseService.getWarehouseForCode(null);
			Fail.fail();
		}
		catch (final IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testGetWarehouseForIdMoreThen1()
	{
		final WarehouseModel warehouse1 = new WarehouseModel();
		final WarehouseModel warehouse2 = new WarehouseModel();
		final String code = "id";

		Mockito.when(warehouseDao.getWarehouseForCode(code)).thenReturn(Arrays.asList(warehouse1, warehouse2));

		try
		{
			defaultWarehouseService.getWarehouseForCode(code);
		}
		catch (final AmbiguousIdentifierException e)
		{
			//ok
		}

	}

}
