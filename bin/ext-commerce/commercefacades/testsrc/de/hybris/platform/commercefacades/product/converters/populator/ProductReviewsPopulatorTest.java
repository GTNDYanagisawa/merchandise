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
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductReviewsPopulator}
 */
@UnitTest
public class ProductReviewsPopulatorTest
{
	@Mock
	private CustomerReviewService customerReviewService;
	@Mock
	private Converter<CustomerReviewModel, ReviewData> customerReviewConverter;
	@Mock
	private ModelService modelService;
	@Mock
	private CommonI18NService commonI18NService;

	private ProductReviewsPopulator productReviewsPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productReviewsPopulator = new ProductReviewsPopulator();
		productReviewsPopulator.setModelService(modelService);
		productReviewsPopulator.setCustomerReviewConverter(customerReviewConverter);
		productReviewsPopulator.setCustomerReviewService(customerReviewService);
		productReviewsPopulator.setCommonI18NService(commonI18NService);
	}


	@Test
	public void testPopulator()
	{
		final ProductModel source = mock(ProductModel.class);
		final CustomerReviewModel customerReviewModel = mock(CustomerReviewModel.class);
		final ReviewData reviewData = mock(ReviewData.class);
		final LanguageModel languageModel = mock(LanguageModel.class);

		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(customerReviewService.getReviewsForProductAndLanguage(source, languageModel)).willReturn(Collections.singletonList(customerReviewModel));
		given(customerReviewConverter.convert(customerReviewModel)).willReturn(reviewData);

		final ProductData result = new ProductData();
		productReviewsPopulator.populate(source, result);

		Assert.assertEquals(1, result.getReviews().size());
		Assert.assertTrue(result.getReviews().contains(reviewData));
	}
}
