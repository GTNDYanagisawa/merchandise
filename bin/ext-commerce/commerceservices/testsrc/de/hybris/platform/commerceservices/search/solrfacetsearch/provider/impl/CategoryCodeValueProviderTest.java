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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.CategorySource;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;


@UnitTest
public class CategoryCodeValueProviderTest extends PropertyFieldValueProviderTestBase
{
	private static final String TEST_CATEGORY_CODE_PROP = "code";

	private static final String TEST_CAT_IDENTIFIER = "Test Category 1";
	private static final String TEST_CAT_FIELD_NAME = "category_string_mv";
	private static final String TEST_CAT_EN_FIELD_NAME = "category_string_en_mv";
	private static final String TEST_CAT_DE_FIELD_NAME = "category_string_de_mv";

	//Objects used by test class (some as params)
	@Mock
	private CategorySource categorySource;
	@Mock
	private CategoryModel categoryModel;
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
		return TEST_CATEGORY_CODE_PROP;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new CategoryCodeValueProvider());
		configureBase();

		((CategoryCodeValueProvider) getPropertyFieldValueProvider()).setCommonI18NService(commonI18NService);
		((CategoryCodeValueProvider) getPropertyFieldValueProvider()).setCategorySource(categorySource);
		((CategoryCodeValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);
	}

	@Test
	public void testWhenNoCategories() throws FieldValueProviderException
	{
		//Configure getFieldValues() required test vars USING: [0 Category]
		given(
				categorySource.getCategoriesForConfigAndProperty(Matchers.<IndexConfig> any(), Matchers.<IndexedProperty> any(),
						Matchers.<Object> any())).willReturn(Collections.<CategoryModel> emptyList());
		Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, new VariantProductModel());

		//Expected Results (Collections.EMPTY_LIST). Assert we received valid results
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		//Configure getFieldValues() required test vars USING: [null Category]
		given(
				categorySource.getCategoriesForConfigAndProperty(Matchers.<IndexConfig> any(), Matchers.<IndexedProperty> any(),
						Matchers.<Object> any())).willReturn(null);
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty,
				new VariantProductModel());

		//Expected Results (Collections.EMPTY_LIST). Assert we received valid results
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testWhenIndexPropertyIsLocalized() throws FieldValueProviderException
	{
		//Configure getFieldValues() required test vars USING: [1 Category] [isLocalized=TRUE]
		given(
				categorySource.getCategoriesForConfigAndProperty(Matchers.<IndexConfig> any(), Matchers.<IndexedProperty> any(),
						Matchers.<Object> any())).willReturn(singletonList(categoryModel));
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.TRUE);

		//Configure createFieldValue() required test vars
		given(modelService.getAttributeValue(categoryModel, getPropertyName())).willReturn(TEST_CAT_IDENTIFIER);
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(TEST_EN_LANG_CODE))).willReturn(
				singletonList(TEST_CAT_EN_FIELD_NAME));
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(TEST_DE_LANG_CODE))).willReturn(
				singletonList(TEST_CAT_DE_FIELD_NAME));

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, new VariantProductModel());

		//Test we received valid results
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 2);

		//Test that the results returned matches the expected
		for (final FieldValue val : result)
		{
			Assert.assertTrue(((String) val.getValue()).equalsIgnoreCase(TEST_CAT_IDENTIFIER));
			Assert.assertTrue(val.getFieldName().equalsIgnoreCase(TEST_CAT_EN_FIELD_NAME)
					|| val.getFieldName().equalsIgnoreCase(TEST_CAT_DE_FIELD_NAME));
		}
	}

	@Test
	public void testWhenIndexPropertyIsNotLocalized() throws FieldValueProviderException
	{
		//Configure getFieldValues() required test vars USING: [1 Category] [isLocalized=FALSE]
		given(
				categorySource.getCategoriesForConfigAndProperty(Matchers.<IndexConfig> any(), Matchers.<IndexedProperty> any(),
						Matchers.<Object> any())).willReturn(singletonList(categoryModel));
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.FALSE);

		//Configure createFieldValue() required test vars
		given(modelService.getAttributeValue(categoryModel, getPropertyName())).willReturn(TEST_CAT_IDENTIFIER);
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(nullString))).willReturn(
				singletonList(TEST_CAT_FIELD_NAME));

		//Make the call
		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, new VariantProductModel());
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 1);

		//Test that the results returned matches the expected
		for (final FieldValue val : result)
		{
			Assert.assertTrue(((String) val.getValue()).equalsIgnoreCase(TEST_CAT_IDENTIFIER));
			Assert.assertTrue(val.getFieldName().equalsIgnoreCase(TEST_CAT_FIELD_NAME));
		}
	}
}
