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
package de.hybris.platform.commerceservices.product.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.stock.StockService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultCommerceProductServiceTest}
 */
@UnitTest
public class DefaultCommerceProductServiceTest
{
	private static final String CATALOG_ID = "testCatalogId";

	private DefaultCommerceProductService defaultCommerceProductService;

	@Mock
	private StockService stockService;


	@Mock
	private WarehouseService warehouseService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultCommerceProductService = new DefaultCommerceProductService();
		defaultCommerceProductService.setStockService(stockService);
		defaultCommerceProductService.setWarehouseService(warehouseService);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetSuperCategoriesExceptClassificationClassesWhenProductIsNull()
	{
		defaultCommerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(null);
	}


	@Test
	public void testGetSuperCategoriesExceptClassificationClasses()
	{
		final ProductModel productModel = mock(ProductModel.class);
		final CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
		final CatalogModel catalogModel = mock(CatalogModel.class);
		final CatalogVersionModel catalogVersionModelForCat = mock(CatalogVersionModel.class);
		final CatalogModel catalogModelForCat = mock(CatalogModel.class);
		final CatalogVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		final CatalogModel classificationSystemModel = mock(ClassificationSystemModel.class);
		final CategoryModel includedCat = mock(CategoryModel.class);
		final CategoryModel excludedCat = mock(CategoryModel.class);
		final CategoryModel classificationClass = mock(ClassificationClassModel.class);
		final List<CategoryModel> supercategories = new ArrayList<CategoryModel>();
		supercategories.add(includedCat);
		supercategories.add(excludedCat);
		supercategories.add(classificationClass);


		given(productModel.getCatalogVersion()).willReturn(catalogVersionModel);
		given(catalogVersionModel.getCatalog()).willReturn(catalogModel);
		given(classificationSystemModel.getId()).willReturn(CATALOG_ID);

		given(classificationSystemVersionModel.getCatalog()).willReturn(classificationSystemModel);
		given(catalogModel.getId()).willReturn(CATALOG_ID);

		given(excludedCat.getCatalogVersion()).willReturn(catalogVersionModelForCat);

		given(classificationClass.getCatalogVersion()).willReturn(classificationSystemVersionModel);
		given(includedCat.getCatalogVersion()).willReturn(catalogVersionModel);

		given(productModel.getCatalogVersion()).willReturn(catalogVersionModel);
		given(catalogVersionModelForCat.getCatalog()).willReturn(catalogModelForCat);
		given(catalogModelForCat.getId()).willReturn("dummyId");

		given(productModel.getSupercategories()).willReturn(supercategories);

		final Collection<CategoryModel> result = defaultCommerceProductService
				.getSuperCategoriesExceptClassificationClassesForProduct(productModel);

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(includedCat, result.iterator().next());
	}
}
