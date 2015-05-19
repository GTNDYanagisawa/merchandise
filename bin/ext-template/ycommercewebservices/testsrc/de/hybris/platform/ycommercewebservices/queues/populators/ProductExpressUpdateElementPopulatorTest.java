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
 */
package de.hybris.platform.ycommercewebservices.queues.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ycommercewebservices.queues.data.ProductExpressUpdateElementData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductExpressUpdateElementPopulatorTest
{
	private static final String PRODUCT_CODE = "productCode";
	private static final String CATALOG_VERSION = "Online";
	private static final String CATALOG_ID = "productCatalog";
	@Mock
	CatalogVersionModel catalogVersion;
	@Mock
	CatalogModel catalog;
	private ProductExpressUpdateElementPopulator productExpressUpdateElementPopulator;
	private Converter<ProductModel, ProductExpressUpdateElementData> productExpressUpdateElementConverter;
	@Mock
	private ProductModel product;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productExpressUpdateElementPopulator = new ProductExpressUpdateElementPopulator();
		productExpressUpdateElementConverter = new ConverterFactory<ProductModel, ProductExpressUpdateElementData, ProductExpressUpdateElementPopulator>()
				.create(ProductExpressUpdateElementData.class, productExpressUpdateElementPopulator);

		given(product.getCode()).willReturn(PRODUCT_CODE);
		given(product.getCatalogVersion()).willReturn(catalogVersion);

		given(catalogVersion.getVersion()).willReturn(CATALOG_VERSION);
		given(catalogVersion.getCatalog()).willReturn(catalog);
		given(catalog.getId()).willReturn(CATALOG_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenSourceIsNull()
	{
		productExpressUpdateElementConverter.convert(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenPrototypeIsNull()
	{
		productExpressUpdateElementConverter.convert(mock(ProductModel.class), null);
	}

	@Test
	public void testConvert()
	{
		final ProductExpressUpdateElementData result = productExpressUpdateElementConverter.convert(product);

		Assert.assertEquals(PRODUCT_CODE, result.getCode());
		Assert.assertEquals(CATALOG_VERSION, result.getCatalogVersion());
		Assert.assertEquals(CATALOG_ID, result.getCatalogId());
	}

	@Test
	public void testConvertWithResultCreated()
	{
		final ProductExpressUpdateElementData result = new ProductExpressUpdateElementData();
		productExpressUpdateElementConverter.convert(product, result);

		Assert.assertEquals(PRODUCT_CODE, result.getCode());
		Assert.assertEquals(CATALOG_VERSION, result.getCatalogVersion());
		Assert.assertEquals(CATALOG_ID, result.getCatalogId());
	}

	@Test
	public void testConvertWhenCatalogVersionIsNull()
	{
		given(product.getCatalogVersion()).willReturn(null);

		final ProductExpressUpdateElementData result = productExpressUpdateElementConverter.convert(product);

		Assert.assertEquals(PRODUCT_CODE, result.getCode());
		Assert.assertNull(result.getCatalogVersion());
		Assert.assertNull(result.getCatalogId());
	}

	@Test
	public void testConvertWhenCatalogIsNull()
	{
		given(catalogVersion.getCatalog()).willReturn(null);

		final ProductExpressUpdateElementData result = productExpressUpdateElementConverter.convert(product);

		Assert.assertEquals(PRODUCT_CODE, result.getCode());
		Assert.assertEquals(CATALOG_VERSION, result.getCatalogVersion());
		Assert.assertNull(result.getCatalogId());
	}


}
