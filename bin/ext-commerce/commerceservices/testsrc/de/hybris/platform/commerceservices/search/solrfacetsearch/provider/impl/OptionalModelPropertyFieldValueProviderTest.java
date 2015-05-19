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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;


@UnitTest
public class OptionalModelPropertyFieldValueProviderTest extends PropertyFieldValueProviderTestBase
{
	private static final String TEST_PROP = "style";
	private static final String TEST_PROP_VAL = "white";
	private static final String TEST_PROP_FIELD_NAME = "style_string";
	private static final String TEST_PROP_FIELD_NAME_EN = "style_en_string";
	private static final String TEST_RATING_DOUBLE_VAL = "style_de_string";

	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;
	@Mock
	private VariantTypeModel baseProductVariantType;
	@Mock
	private VariantAttributeDescriptorModel variantAttributeDescriptorModel;
	@Mock
	private VariantsService variantsService;
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
		return TEST_PROP;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new OptionalModelPropertyFieldValueProvider());
		configureBase();

		((OptionalModelPropertyFieldValueProvider) getPropertyFieldValueProvider()).setVariantsService(variantsService);
		((OptionalModelPropertyFieldValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		((OptionalModelPropertyFieldValueProvider) getPropertyFieldValueProvider()).setCommonI18NService(commonI18NService);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		given(modelService.getAttributeValue(model, getPropertyName())).willReturn(TEST_PROP_VAL);
		//	given(Boolean.valueOf(IndexedProperties.isRanged(indexedProperty))).willReturn(Boolean.FALSE);
		given(indexedProperty.getName()).willReturn(getPropertyName());
		given(model.getBaseProduct()).willReturn(baseProduct);

		Assert.assertEquals(((OptionalModelPropertyFieldValueProvider) getPropertyFieldValueProvider()).getVariantsService(),
				variantsService);
		Assert.assertEquals(((OptionalModelPropertyFieldValueProvider) getPropertyFieldValueProvider()).getFieldNameProvider(),
				fieldNameProvider);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidArgs() throws FieldValueProviderException
	{
		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null));
	}

	@Test
	public void testWhenIndexPropertyIsLocalized() throws FieldValueProviderException
	{
		// [model=VariantProductModel][indexedProperty.isLocalized=true][qualifier=style][getAttributeValue=white]
		// Expected [{white, [style_en_string]OR[style_de_string]}]
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.TRUE);
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(TEST_EN_LANG_CODE))).willReturn(
				singletonList(TEST_PROP_FIELD_NAME_EN));
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(TEST_DE_LANG_CODE))).willReturn(
				singletonList(TEST_RATING_DOUBLE_VAL));

		Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 2);
		for (final FieldValue val : result)
		{
			Assert.assertTrue(val.getValue().equals(TEST_PROP_VAL));
			Assert.assertTrue(val.getFieldName().equalsIgnoreCase(TEST_PROP_FIELD_NAME_EN)
					|| val.getFieldName().equals(TEST_RATING_DOUBLE_VAL));
		}

		//Test if value == null
		given(modelService.getAttributeValue(any(), Matchers.<String> any())).willReturn(nullString);
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		//Test NOT instanceof VariantProduct
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, baseProduct);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testWhenIndexPropertyIsNotLocalized() throws FieldValueProviderException
	{
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.FALSE);
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(nullString))).willReturn(
				singletonList(TEST_PROP_FIELD_NAME));

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 1);
		//Test that the results returned matches the expected
		for (final FieldValue val : result)
		{
			Assert.assertTrue(((String) val.getValue()).equalsIgnoreCase(TEST_PROP_VAL));
			Assert.assertTrue(val.getFieldName().equalsIgnoreCase(TEST_PROP_FIELD_NAME));
		}
	}

	@Test
	public void testAttributeNotSupportedException() throws FieldValueProviderException
	{
		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), eq(nullString))).willReturn(
				singletonList(TEST_PROP_FIELD_NAME));

		given(modelService.getAttributeValue(model, getPropertyName())).willReturn(nullString);
		given(modelService.getAttributeValue(baseProduct, getPropertyName())).willThrow(
				new AttributeNotSupportedException("cannot find attribute size", TEST_PROP));
		given(baseProduct.getVariantType()).willReturn(baseProductVariantType);
		given(baseProductVariantType.getVariantAttributes()).willReturn(singletonList(variantAttributeDescriptorModel));
		given(variantAttributeDescriptorModel.getQualifier()).willReturn(getPropertyName());
		given(variantsService.getVariantAttributeValue(model, getPropertyName())).willReturn(null);

		Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		given(variantsService.getVariantAttributeValue(model, getPropertyName())).willReturn(TEST_PROP_VAL);

		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, model);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 1);
		for (final FieldValue val : result)
		{
			Assert.assertTrue(((String) val.getValue()).equalsIgnoreCase(TEST_PROP_VAL));
			Assert.assertTrue(val.getFieldName().equalsIgnoreCase(TEST_PROP_FIELD_NAME));
		}

		//Test instanceof VariantProduct
		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, baseProduct);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}
}
