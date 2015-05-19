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
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;



@UnitTest
public class FeaturePopulatorTest
{
	private static final String FEATURE_CODE = "featCode";
	private static final String CLASS_ATTR_DESCRIPTION = "classAttrDesc";
	private static final String CLASS_UNIT_NAME = "unitName";

	private final AbstractPopulatingConverter<Feature, FeatureData> featureConverter = new ConverterFactory<Feature, FeatureData, FeaturePopulator>()
			.create(FeatureData.class, new FeaturePopulator());

	private Feature source;

	private ClassAttributeAssignmentModel classAttributeAssignmentModel;

	private ClassificationAttributeUnitModel classificationAttributeUnitModel;

	@Before
	public void setUp()
	{
		source = mock(Feature.class);
		classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		classificationAttributeUnitModel = mock(ClassificationAttributeUnitModel.class);
		given(source.getCode()).willReturn(FEATURE_CODE);
		given(source.getClassAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getComparable()).willReturn(Boolean.TRUE);
		given(classAttributeAssignmentModel.getDescription()).willReturn(CLASS_ATTR_DESCRIPTION);
		given(classAttributeAssignmentModel.getUnit()).willReturn(classificationAttributeUnitModel);
		given(classificationAttributeUnitModel.getName()).willReturn(CLASS_UNIT_NAME);
	}

	@Test
	public void testConvert()
	{
		final FeatureValue featureValue = mock(FeatureValue.class);
		given(source.getValues()).willReturn(Collections.singletonList(featureValue));

		final FeatureData result = featureConverter.convert(source);

		Assert.assertEquals(FEATURE_CODE, result.getCode());
		Assert.assertEquals(CLASS_ATTR_DESCRIPTION, result.getDescription());
		Assert.assertEquals(CLASS_UNIT_NAME, result.getFeatureUnit().getName());
		Assert.assertEquals(1, result.getFeatureValues().size());
	}

	@Test
	public void testConvertWithoutPrecision()
	{
		final FeatureValue featureValue = mock(FeatureValue.class);

		given(featureValue.getValue()).willReturn(new Double("1.000000"));
		given(source.getValues()).willReturn(Collections.singletonList(featureValue));

		final FeatureData result = featureConverter.convert(source);

		Assert.assertEquals(FEATURE_CODE, result.getCode());
		Assert.assertEquals(CLASS_ATTR_DESCRIPTION, result.getDescription());
		Assert.assertEquals(CLASS_UNIT_NAME, result.getFeatureUnit().getName());
		Assert.assertEquals(1, result.getFeatureValues().size());
		Assert.assertEquals("1", result.getFeatureValues().iterator().next().getValue());
	}

	@Test
	public void testConvertWithPrecision()
	{
		final FeatureValue featureValue = mock(FeatureValue.class);

		given(featureValue.getValue()).willReturn(new Double("1.523"));
		given(source.getValues()).willReturn(Collections.singletonList(featureValue));

		final FeatureData result = featureConverter.convert(source);

		Assert.assertEquals(FEATURE_CODE, result.getCode());
		Assert.assertEquals(CLASS_ATTR_DESCRIPTION, result.getDescription());
		Assert.assertEquals(CLASS_UNIT_NAME, result.getFeatureUnit().getName());
		Assert.assertEquals(1, result.getFeatureValues().size());
		Assert.assertEquals("1.523", result.getFeatureValues().iterator().next().getValue());
	}
}
