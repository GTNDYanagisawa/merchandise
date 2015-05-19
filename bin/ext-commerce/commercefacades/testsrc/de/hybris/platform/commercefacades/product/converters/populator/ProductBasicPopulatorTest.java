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
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductBasicPopulator}
 */
@UnitTest
public class ProductBasicPopulatorTest
{
	private static final String PRODUCT_NAME = "proName";
	private static final String PRODUCT_MANUFACTURER = "proMan";
	private static final Double PRODUCT_AVG_RATING = Double.valueOf(3.45D);
	private static final String VARIANT_TYPE_CODE = "varCode";
	private static final String BASE_PRODUCT_CODE = "baseProduct";

	@Mock
	private ModelService modelService;

	private ProductBasicPopulator productBasicPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productBasicPopulator = new ProductBasicPopulator();
		productBasicPopulator.setModelService(modelService);
	}


	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);

		given(modelService.getAttributeValue(source, ProductModel.NAME)).willReturn(PRODUCT_NAME);
		given(modelService.getAttributeValue(source, ProductModel.MANUFACTURERNAME)).willReturn(PRODUCT_MANUFACTURER);
		given(source.getAverageRating()).willReturn(PRODUCT_AVG_RATING);
		given(source.getVariantType()).willReturn(variantTypeModel);
		given(variantTypeModel.getCode()).willReturn(VARIANT_TYPE_CODE);

		final ProductData result = new ProductData();
		productBasicPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_NAME, result.getName());
		Assert.assertEquals(PRODUCT_MANUFACTURER, result.getManufacturer());
		Assert.assertEquals(PRODUCT_AVG_RATING, result.getAverageRating());
		Assert.assertEquals(VARIANT_TYPE_CODE, result.getVariantType());
	}

	@Test
	public void testPopulateNotVariantTyped()
	{
		final ProductModel source = mock(ProductModel.class);

		given(source.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);
		given(modelService.getAttributeValue(source, ProductModel.NAME)).willReturn(PRODUCT_NAME);
		given(modelService.getAttributeValue(source, ProductModel.MANUFACTURERNAME)).willReturn(PRODUCT_MANUFACTURER);
		given(source.getVariantType()).willReturn(null);
		given(source.getAverageRating()).willReturn(PRODUCT_AVG_RATING);

		final ProductData result = new ProductData();
		productBasicPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_NAME, result.getName());
		Assert.assertEquals(PRODUCT_MANUFACTURER, result.getManufacturer());
		Assert.assertEquals(PRODUCT_AVG_RATING, result.getAverageRating());
	}


	@Test
	public void testPopulateAttributeFallback()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);

		given(source.getBaseProduct()).willReturn(baseProduct);
		given(source.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);
		given(modelService.getAttributeValue(source, ProductModel.NAME)).willReturn(null);
		given(modelService.getAttributeValue(baseProduct, ProductModel.NAME)).willReturn(PRODUCT_NAME);
		given(baseProduct.getCode()).willReturn(BASE_PRODUCT_CODE);

		final ProductData result = new ProductData();
		productBasicPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_NAME, result.getName());
		Assert.assertEquals(BASE_PRODUCT_CODE, result.getBaseProduct());
	}
}
