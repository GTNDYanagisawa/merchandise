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
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductSummaryPopulator}
 */
@UnitTest
public class ProductSummaryPopulatorTest
{
	private static final String PRODUCT_SUMMARY = "Some product summary...";

	@Mock
	private ModelService modelService;

	private ProductSummaryPopulator productSummaryPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productSummaryPopulator = new ProductSummaryPopulator();
		productSummaryPopulator.setModelService(modelService);
	}


	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);

		given(modelService.getAttributeValue(source, ProductModel.SUMMARY)).willReturn(PRODUCT_SUMMARY);

		final ProductData result = new ProductData();
		productSummaryPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_SUMMARY, result.getSummary());
	}
}
