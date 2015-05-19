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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.catalog.jalo.classification.util.FeatureValue;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;


//@RunWith(PowerMockRunner.class)
//@SuppressStaticInitializationFor(
//		{
//				"de.hybris.platform.catalog.jalo.classification.util.FeatureContainer",
//				"de.hybris.platform.catalog.jalo.classification.util.TypedFeature",
//				"de.hybris.platform.catalog.jalo.classification.util.Feature"
//		}
//)
//@PrepareForTest({Feature.class, TypedFeature.class})
@SuppressWarnings("deprecation")
@Ignore("ACCEL-2 - removing powermock and disabling test")
@UnitTest
public class CommerceClassificationPropertyValueProviderTest extends PropertyFieldValueProviderTestBase
{
	private static final String TEST_CLASS_FEATURE_PROP = "features";
	private static final String TEST_FEATURE_VAL1 = "TFT";

	@Mock
	private ProductModel model;
	@Mock
	private IndexedProperty indexedProperty;

	@Mock
	private ClassAttributeAssignmentModel classAttributeAssignmentModel;
	@Mock
	private ClassAttributeAssignment classAttributeAssignment;
	@Mock
	private Product product;

	@Mock
	private FeatureValue<Object> featureValue;
	private List<FeatureValue<Object>> featureValues;
	private CommerceClassificationPropertyValueProvider classUnderTest;

	@Mock
	private FeatureContainer cont;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return TEST_CLASS_FEATURE_PROP;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new CommerceClassificationPropertyValueProvider());
		configureBase();

		((CommerceClassificationPropertyValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);
		Assert.assertTrue(getPropertyName().equals(TEST_CLASS_FEATURE_PROP));

		featureValues = new ArrayList<FeatureValue<Object>>();

		given(indexedProperty.getClassAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(modelService.getSource(classAttributeAssignmentModel)).willReturn(classAttributeAssignment);
		given(modelService.getSource(model)).willReturn(product);
		given(featureValue.getValue()).willReturn(TEST_FEATURE_VAL1);
		featureValues.add(featureValue);
		//ACCEL-2
		//		PowerMockito.suppress(PowerMockito.methods(Feature.class, "isEmpty"));
		//		PowerMockito.suppress(PowerMockito.methods(TypedFeature.class, "isEmpty"));
		//		PowerMockito.suppress(PowerMockito.methods(Feature.class, "getName"));
		//		PowerMockito.suppress(PowerMockito.methods(TypedFeature.class, "getName"));
		//
		//		PowerMockito.mockStatic(Feature.class);
		//		PowerMockito.mockStatic(TypedFeature.class);
		//		PowerMockito.mockStatic(FeatureContainer.class);

		//		given(FeatureContainer.loadTyped(product, classAttributeAssignment)).willReturn(cont);
		//		classUnderTest = PowerMockito.spy(((CommerceClassificationPropertyValueProvider) getPropertyFieldValueProvider()));
	}

	@Test
	public void testGetFieldValuesContainerWithNoFeature() throws Exception
	{
		//Expected:  [Empty List]
		given(Boolean.valueOf(cont.hasFeature(classAttributeAssignment))).willReturn(Boolean.FALSE);
		final Collection<FieldValue> result = classUnderTest.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testGetFieldValuesContainerWithNullFeature() throws Exception
	{
		//Expected:  [Empty List]
		given(Boolean.valueOf(cont.hasFeature(classAttributeAssignment))).willReturn(Boolean.TRUE);
		given(cont.getFeature(classAttributeAssignment)).willReturn(null);
		final Collection<FieldValue> result = classUnderTest.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testGetFieldValuesWithFeatures() throws Exception
	{
		//Expected:  [Empty List]
		given(Boolean.valueOf(cont.hasFeature(classAttributeAssignment))).willReturn(Boolean.TRUE);

		//ACCEL-2
		//		final TypedFeature feature = PowerMockito.mock(TypedFeature.class, RETURNS_MOCKS);
		//		given(cont.getFeature(classAttributeAssignment)).willReturn(feature);
		//		PowerMockito.doReturn(featureValues).when(classUnderTest, "getFeaturesValues", any(), any(), any());

		final Collection<FieldValue> result = classUnderTest.getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertTrue(result.size() == 1);
	}

	@Test(expected = FieldValueProviderException.class)
	public void testWrongInstanceOfProductModel() throws Exception
	{
		verify(classUnderTest.getFieldValues(indexConfig, indexedProperty, new CatalogModel()));
	}

	@Test(expected = FieldValueProviderException.class)
	public void testWrongInstanceOfIndexProperty() throws Exception
	{
		final IndexedProperty failedIndexedProperty = mock(IndexedProperty.class);
		verify(classUnderTest.getFieldValues(indexConfig, failedIndexedProperty, model));
	}
}
