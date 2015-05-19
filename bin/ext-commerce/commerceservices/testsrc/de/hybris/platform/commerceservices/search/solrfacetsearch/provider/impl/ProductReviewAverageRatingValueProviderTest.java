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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;


@UnitTest
public class ProductReviewAverageRatingValueProviderTest extends PropertyFieldValueProviderTestBase
{
	private static final String TEST_PROP_VAL = "propVal";
	private static final String TEST_PROP_FIELD_NAME = "prop_double";
	private static final String TEST_PROP_FIELD_NAME_EN = "prop_en_double";
	private static final String TEST_PROP_FIELD_NAME_DE = "prop_de_double";
	private static final Double TEST_RATING1_DOUBLE_VAL = Double.valueOf(3.5);
	private static final Double TEST_RATING2_DOUBLE_VAL = Double.valueOf(5.0);

	@Mock
	private CustomerReviewService customerReviewService;
	@Mock
	private CustomerReviewModel review1;
	@Mock
	private CustomerReviewModel review2;
	@Mock
	private VariantProductModel model;
	@Mock
	private IndexedProperty indexedProperty;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return TEST_PROP_VAL;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new ProductReviewAverageRatingValueProvider());
		configureBase();

		((ProductReviewAverageRatingValueProvider) getPropertyFieldValueProvider()).setCustomerReviewService(customerReviewService);
		((ProductReviewAverageRatingValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.FALSE);

		given(review1.getLanguage()).willReturn(enLanguageModel);
		given(review1.getRating()).willReturn(TEST_RATING1_DOUBLE_VAL);
		given(review1.getApprovalStatus()).willReturn(CustomerReviewApprovalType.APPROVED);

		given(review2.getLanguage()).willReturn(deLanguageModel);
		given(review2.getRating()).willReturn(TEST_RATING2_DOUBLE_VAL);
		given(review2.getApprovalStatus()).willReturn(CustomerReviewApprovalType.APPROVED);

		final List<CustomerReviewModel> allReviews;
		allReviews = new ArrayList<CustomerReviewModel>();
		allReviews.add(review1);
		allReviews.add(review2);
		given(customerReviewService.getReviewsForProduct(model)).willReturn(allReviews);

		Assert.assertEquals(getPropertyName(), TEST_PROP_VAL);
	}

	@Test
	public void testWhenIndexPropertyIsLocalized() throws FieldValueProviderException
	{
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.TRUE);
		given(fieldNameProvider.getFieldNames(indexedProperty, TEST_EN_LANG_CODE)).willReturn(
				singletonList(TEST_PROP_FIELD_NAME_EN));
		given(fieldNameProvider.getFieldNames(indexedProperty, TEST_DE_LANG_CODE)).willReturn(
				singletonList(TEST_PROP_FIELD_NAME_DE));

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertEquals("Did not receive expected result size.", 2, result.size());
		for (final FieldValue val : result)
		{
			if (TEST_PROP_FIELD_NAME_EN.equals(val.getFieldName()))
			{
				Assert.assertEquals("Did not receive expected results.", TEST_RATING1_DOUBLE_VAL, val.getValue());
			}
			else if (TEST_PROP_FIELD_NAME_DE.equals(val.getFieldName()))
			{
				Assert.assertEquals("Did not receive expected results.", TEST_RATING2_DOUBLE_VAL, val.getValue());
			}
			else
			{
				Assert.assertTrue("Unexpected field name", false);
			}
		}
	}

	@Test
	public void testWhenIndexPropertyIsNotLocalized() throws FieldValueProviderException
	{
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.FALSE);
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(nullString))).willReturn(
				singletonList(TEST_PROP_FIELD_NAME));
		given(customerReviewService.getReviewsForProduct(model)).willReturn(singletonList(review1));

		Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertEquals("Did not receive expected result size.", 1, result.size());
		for (final FieldValue val : result)
		{
			Assert.assertEquals("Did not receive expected results.", TEST_PROP_FIELD_NAME, val.getFieldName());
			Assert.assertEquals("Did not receive expected results.", TEST_RATING1_DOUBLE_VAL, val.getValue());
		}

		reset(review1);
		given(review1.getRating()).willReturn(null);
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.isEmpty());
	}

	@Test
	public void testRatingsTotalPerLang() throws FieldValueProviderException
	{
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.TRUE);
		reset(indexConfig);
		given(indexConfig.getLanguages()).willReturn(singletonList(enLanguageModel));
		given(fieldNameProvider.getFieldNames(indexedProperty, TEST_EN_LANG_CODE)).willReturn(
				singletonList(TEST_PROP_FIELD_NAME_EN));

		given(review1.getLanguage()).willReturn(enLanguageModel);
		given(review1.getRating()).willReturn(TEST_RATING1_DOUBLE_VAL);
		reset(review2);
		given(review2.getLanguage()).willReturn(enLanguageModel);
		given(review2.getRating()).willReturn(TEST_RATING2_DOUBLE_VAL);

		final List<CustomerReviewModel> allReviews;
		allReviews = new ArrayList<CustomerReviewModel>();
		allReviews.add(review1);
		allReviews.add(review2);
		reset(customerReviewService);
		given(customerReviewService.getReviewsForProduct(model)).willReturn(allReviews);

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertEquals("Did not receive expected result size.", 1, result.size());
		for (final FieldValue val : result)
		{
			Assert.assertEquals("Did not receive expected results.", TEST_PROP_FIELD_NAME_EN, val.getFieldName());
			Assert.assertEquals("Did not receive expected results.", TEST_RATING1_DOUBLE_VAL, val.getValue());
		}
	}

	@Test(expected = FieldValueProviderException.class)
	public void testInvalidArgs() throws FieldValueProviderException
	{
		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty,
				new CatalogModel()));
	}
}
