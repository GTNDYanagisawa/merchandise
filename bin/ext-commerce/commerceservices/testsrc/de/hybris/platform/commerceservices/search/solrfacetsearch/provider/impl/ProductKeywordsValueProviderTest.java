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
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;


@Ignore("ACCEL-2 - removing powermock and disabling test")
@UnitTest
public class ProductKeywordsValueProviderTest extends PropertyFieldValueProviderTestBase
{
	private static final String TEST_PRODUCT_KEYWORD_PROP_VAL = "propVal";
	private static final String TEST_PRODUCT_KEYWORD_PROP_EN_FIELD_NAME = "prop_en_string";
	private static final String TEST_PRODUCT_KEYWORD_PROP_DE_FIELD_NAME = "prop_de_string";

	@Mock
	private SessionService sessionService;
	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;
	@Mock
	private KeywordModel keyword;
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
		return TEST_PRODUCT_KEYWORD_PROP_VAL;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new ProductKeywordsValueProvider());
		configureBase();
		((ProductKeywordsValueProvider) getPropertyFieldValueProvider()).setCommonI18NService(commonI18NService);
		((ProductKeywordsValueProvider) getPropertyFieldValueProvider()).setSessionService(sessionService);
		((ProductKeywordsValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.FALSE);
		given(model.getBaseProduct()).willReturn(baseProduct);
		given(keyword.getKeyword()).willReturn(getPropertyName());
		given(model.getKeywords()).willReturn(singletonList(keyword));
		given(baseProduct.getKeywords()).willReturn(singletonList(keyword));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidArgs() throws FieldValueProviderException
	{
		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null));
	}

	@Test
	public void testWhenIndexPropertyIsLocalized() throws FieldValueProviderException
	{
		Collection<FieldValue> result;
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.TRUE);
		//ACCEL-2
		//		final ArgumentCaptor<SessionExecutionBody> argument = ArgumentCaptor.forClass(SessionExecutionBody.class);
		//		when(sessionService.executeInLocalView(argument.capture())).thenAnswer(new Answer()
		//		{
		//			@Override
		//			public Object answer(final InvocationOnMock invocation)
		//			{
		//				return argument.getValue().execute();
		//			}
		//		});
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(TEST_EN_LANG_CODE))).willReturn(
				singletonList(TEST_PRODUCT_KEYWORD_PROP_EN_FIELD_NAME));
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(TEST_DE_LANG_CODE))).willReturn(
				singletonList(TEST_PRODUCT_KEYWORD_PROP_DE_FIELD_NAME));

		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.size() == 2);
		for (final FieldValue val : result)
		{
			Assert.assertTrue("Did not receive expected results.", val.getValue().equals(getPropertyName() + ' '));
			Assert.assertTrue(
					"Did not receive expected results.",
					val.getFieldName().equalsIgnoreCase(TEST_PRODUCT_KEYWORD_PROP_EN_FIELD_NAME)
							|| val.getFieldName().equals(TEST_PRODUCT_KEYWORD_PROP_DE_FIELD_NAME));
		}

		//Test Keywords null
		reset(model, baseProduct);
		given(model.getKeywords()).willReturn(null);
		given(baseProduct.getKeywords()).willReturn(null);
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, baseProduct);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.isEmpty());

		//Test Keywords empty
		reset(model, baseProduct);
		given(model.getKeywords()).willReturn(Collections.<KeywordModel> emptyList());
		given(baseProduct.getKeywords()).willReturn(Collections.<KeywordModel> emptyList());
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, baseProduct);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.isEmpty());

		//Test Base Product is null
		reset(model, baseProduct);
		given(model.getBaseProduct()).willReturn(null);
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.isEmpty());

		//Test NOT instanceof ProductModel
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty,
				new CatalogModel());
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.isEmpty());
	}

	@Test
	public void testWhenIndexPropertyIsNotLocalized() throws FieldValueProviderException
	{
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.FALSE);

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.isEmpty());
	}
}
