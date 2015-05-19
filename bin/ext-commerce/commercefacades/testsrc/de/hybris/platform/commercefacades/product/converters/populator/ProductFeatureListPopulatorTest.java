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
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductFeatureListPopulator}
 */
@UnitTest
public class ProductFeatureListPopulatorTest
{
	private static final String CLASSIFICATION_CLASS_CODE = "classClassCode";

	@Mock
	private Converter<ClassificationClassModel, ClassificationData> classificationConverter;
	@Mock
	private Converter<Feature, FeatureData> featureConverter;

	private ProductFeatureListPopulator productFeatureListPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productFeatureListPopulator = new ProductFeatureListPopulator();
		productFeatureListPopulator.setFeatureConverter(featureConverter);
		productFeatureListPopulator.setClassificationConverter(classificationConverter);
	}


	@Test
	public void testPopulate()
	{
		final FeatureList source = mock(FeatureList.class);
		final Feature feature = mock(Feature.class);
		final Feature featureDup = mock(Feature.class);
		final List<Feature> features = new ArrayList<Feature>();
		features.add(feature);
		features.add(featureDup);
		final FeatureValue featureValue = mock(FeatureValue.class);
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		final ClassificationData classificationData = mock(ClassificationData.class);
		final FeatureData featureData = mock(FeatureData.class);

		given(featureConverter.convert(feature)).willReturn(featureData);
		given(classificationConverter.convert(classificationClassModel)).willReturn(classificationData);
		given(classificationClassModel.getCode()).willReturn(CLASSIFICATION_CLASS_CODE);
		given(feature.getClassAttributeAssignment()).willReturn(assignmentModel);
		given(featureDup.getClassAttributeAssignment()).willReturn(assignmentModel);
		given(assignmentModel.getClassificationClass()).willReturn(classificationClassModel);
		given(feature.getValues()).willReturn(Collections.singletonList(featureValue));
		given(featureDup.getValues()).willReturn(Collections.singletonList(featureValue));
		given(source.getFeatures()).willReturn(features);

		final ProductData result = new ProductData();
		productFeatureListPopulator.populate(source, result);

		Assert.assertEquals(1, result.getClassifications().size());
		Assert.assertEquals(classificationData, result.getClassifications().iterator().next());
	}
}
