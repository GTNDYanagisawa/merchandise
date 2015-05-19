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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class DefaultWarehouseDaoTest extends ServicelayerTest
{

	@Resource
	private DefaultWarehouseDao warehouseDao;

	@Resource
	private ModelService modelService;

	private WarehouseModel warehouse1;
	private final String warehouse1Code = "w1";
	private WarehouseModel warehouse2;
	private final String warehouse2Code = "w2";
	private WarehouseModel warehouse3;
	private final String warehouse3Code = "w3";
	private WarehouseModel warehouse4;


	private VendorModel vendor1;
	private final String vendor1Code = "v1";
	private VendorModel vendor2;
	private final String vendor2Code = "v2";
	private VendorModel vendor3;
	private final String vendor3Code = "v3";

	private CatalogModel catalog;
	private CatalogVersionModel catalogVersion;

	private ProductModel product1;
	private final String product1Code = "P1";
	private ProductModel product2;
	private final String product2Code = "P2";
	private ProductModel product3;
	private final String product3Code = "P3";

	/**
	 * 
	 */
	@Before
	public void setUp() throws Exception
	{
		vendor1 = createVendor(vendor1Code);
		vendor2 = createVendor(vendor2Code);
		vendor3 = createVendor(vendor3Code);
		warehouse1 = createWarehouse(warehouse1Code, vendor1, Boolean.TRUE);
		warehouse2 = createWarehouse(warehouse2Code, vendor2, Boolean.FALSE);
		warehouse3 = createWarehouse(warehouse3Code, vendor3, Boolean.FALSE);
		warehouse4 = createWarehouse(warehouse3Code, vendor3, Boolean.TRUE);

		catalog = modelService.create(CatalogModel.class);
		catalog.setId("id1");
		modelService.save(catalog);

		catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setVersion("v1");
		catalogVersion.setCatalog(catalog);
		modelService.save(catalogVersion);

		product1 = createProduct(product1Code, catalogVersion);
		product2 = createProduct(product2Code, catalogVersion);
		product3 = createProduct(product3Code, catalogVersion);

		createStockLevel(product1, 10, warehouse1);
		createStockLevel(product2, 10, warehouse1);
		createStockLevel(product3, 10, warehouse1);

		createStockLevel(product1, 1, warehouse2);
		createStockLevel(product2, 3, warehouse2);
		createStockLevel(product3, 5, warehouse2);

		createStockLevel(product1, 1, warehouse3);
		createStockLevel(product2, 1, warehouse3);

	}

	private StockLevelModel createStockLevel(final ProductModel product, final int quantity, final WarehouseModel warehouse)
	{
		final StockLevelModel res = modelService.create(StockLevelModel.class);
		res.setProductCode(product.getCode());
		res.setAvailable(quantity);
		res.setWarehouse(warehouse);
		modelService.save(res);
		return res;
	}

	private ProductModel createProduct(final String code, final CatalogVersionModel version)
	{
		final ProductModel res = modelService.create(ProductModel.class);
		res.setCode(code);
		res.setCatalogVersion(version);
		modelService.save(res);
		return res;
	}

	private WarehouseModel createWarehouse(final String code, final VendorModel vendor, final Boolean def)
	{
		final WarehouseModel res = modelService.create(WarehouseModel.class);
		res.setCode(code);
		res.setVendor(vendor);
		res.setDefault(def);
		modelService.save(res);
		return res;
	}

	private VendorModel createVendor(final String code)
	{
		final VendorModel res = modelService.create(VendorModel.class);
		res.setCode(code);
		modelService.save(res);
		return res;
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getWarehouseForCode(java.lang.String)}.
	 */
	@Test
	public void testGetWarehouseForCode()
	{
		Assertions.assertThat(warehouseDao.getWarehouseForCode(warehouse1Code)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehouseForCode(warehouse2Code)).containsOnly(warehouse2);
		Assertions.assertThat(warehouseDao.getWarehouseForCode(warehouse3Code)).containsOnly(warehouse3, warehouse4);
	}

	/**
	 * Test method for {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getDefWarehouse()}.
	 */
	@Test
	public void testGetDefWarehouse()
	{
		Assertions.assertThat(warehouseDao.getDefWarehouse()).containsOnly(warehouse1, warehouse4);
	}

	/**
	 * Test method for {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getWarehouses(String)}.
	 */
	@Test
	public void testGetWarehouses()
	{
		Assertions.assertThat(warehouseDao.getWarehouses(product1Code)).containsOnly(warehouse1, warehouse2, warehouse3);
		Assertions.assertThat(warehouseDao.getWarehouses(product2Code)).containsOnly(warehouse1, warehouse2, warehouse3);
		Assertions.assertThat(warehouseDao.getWarehouses(product3Code)).containsOnly(warehouse1, warehouse2);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getWarehousesWithProductsInStock(String, int, VendorModel)}
	 * .
	 */
	@Test
	public void testGetWarehousesWithProductsInStock()
	{
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(product1Code, 2, null)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(product2Code, 3, null)).containsOnly(warehouse1,
				warehouse2);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(product3Code, 4, null)).containsOnly(warehouse1,
				warehouse2);

		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(product1Code, 2, vendor1)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(product1Code, 2, vendor2)).isEmpty();

		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(product2Code, 3, vendor1)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(product2Code, 3, vendor2)).containsOnly(warehouse2);
	}

}
