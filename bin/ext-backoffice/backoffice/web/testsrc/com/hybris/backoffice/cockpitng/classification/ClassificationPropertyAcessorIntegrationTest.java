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
package com.hybris.backoffice.cockpitng.classification;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import com.google.common.collect.Maps;
import com.hybris.cockpitng.core.model.WidgetModel;


@IntegrationTest
public class ClassificationPropertyAcessorIntegrationTest extends ServicelayerTransactionalTest
{
	public static final Logger LOG = Logger.getLogger(ClassificationPropertyAcessorIntegrationTest.class);

	private static final String PRODUCT_CODE = "HW2120-0341";
	private static final String MODIFIED_FEATURES_MODEL_PARAM = "modifiedProductFeatures";
	private static final String MODIFIED_FEATURES_MODEL_PARAM_PREFIX = "modifiedProductFeatures.pk";

	@Resource
	private ClassificationService classificationService;

	@Resource
	private ProductService productService;

	private FeaturePeristanceHandler featurePeristanceHandler;

	private ProductModel productModel;
	private WidgetModel widgetModel;
	private EvaluationContext evaluationContext;


	private final ClassificationPropertyAccessor classificationPropertyAccessor = new ClassificationPropertyAccessor();



	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createHardwareCatalog();
		evaluationContext = Mockito.mock(EvaluationContext.class);
		widgetModel = Mockito.mock(WidgetModel.class);
		Mockito.when(widgetModel.getValue(MODIFIED_FEATURES_MODEL_PARAM, Map.class)).thenReturn(null);
		final TypedValue rootObject = new TypedValue(widgetModel);
		Mockito.when(evaluationContext.getRootObject()).thenReturn(rootObject);

		classificationPropertyAccessor.setClassificationService(classificationService);

		productModel = productService.getProductForCode(PRODUCT_CODE);

		featurePeristanceHandler = new FeaturePeristanceHandler();
		featurePeristanceHandler.setClassificationService(classificationService);

	}

	@Test
	public void testSimpleReadFsbSpeedForProduct() throws AccessException
	{
		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel,
				"{SampleClassification}/{1\\.0}/{cpu}.{fsbSpeed}");

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(1000.0));
	}

	@Test
	public void testSimpleWriteFsbSpeedForProduct() throws AccessException
	{
		final String qualifier = "{SampleClassification}/{1\\.0}/{cpu}.{fsbSpeed}";

		final FeatureValue fValue = new FeatureValue(Double.valueOf(2000.0));
		final FeatureList featureList = classificationService.getFeatures(productModel);
		final Feature feature = getFeatureByCode(featureList, qualifier);
		final ClassificationInfo classificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), fValue);

		Mockito.reset(widgetModel);

		final Map<String, Feature> modifiedFeatures = Maps.newHashMap();

		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(modifiedFeatures);

		classificationPropertyAccessor.write(evaluationContext, productModel, qualifier, classificationInfo);

		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifier);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(2000.0));

	}

	@Test
	public void testWriteAndPersistFeatureForProduct() throws AccessException
	{
		final String qualifier = "{SampleClassification}/{1\\.0}/{cpu}.{fsbSpeed}";

		final FeatureValue fValue = new FeatureValue(Double.valueOf(666.0));
		final FeatureList featureList = classificationService.getFeatures(productModel);
		final Feature feature = getFeatureByCode(featureList, qualifier);
		final ClassificationInfo classificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), fValue);

		Mockito.reset(widgetModel);

		final Map<String, Feature> modifiedFeatures = Maps.newHashMap();
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(modifiedFeatures);

		classificationPropertyAccessor.write(evaluationContext, productModel, qualifier, classificationInfo);

		featurePeristanceHandler.saveFeatures(productModel, modifiedFeatures);

		//fetch from database
		Mockito.reset(widgetModel);
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(null);

		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifier);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(666.0));

	}


	@Test
	public void testWriteAndPersistTwoFeaturesForProduct() throws AccessException
	{
		final String qualifierFsbSpeed = "{SampleClassification}/{1\\.0}/{cpu}.{fsbSpeed}";


		Mockito.reset(widgetModel);

		final Map<String, Feature> modifiedFeatures = Maps.newHashMap();
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(modifiedFeatures);

		final FeatureList featureList = classificationService.getFeatures(productModel);
		final Feature feature = getFeatureByCode(featureList, qualifierFsbSpeed);

		final FeatureValue fValue = new FeatureValue(Double.valueOf(666.0));
		final ClassificationInfo classificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), fValue);

		classificationPropertyAccessor.write(evaluationContext, productModel, qualifierFsbSpeed, classificationInfo);

		final String qualifierClockSpeed = "{SampleClassification}/{1\\.0}/{cpu}.{socket}";

		final FeatureValue sValue = new FeatureValue(Double.valueOf(777.0));
		final ClassificationInfo secondClassificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), sValue);
		classificationPropertyAccessor.write(evaluationContext, productModel, qualifierClockSpeed, secondClassificationInfo);

		featurePeristanceHandler.saveFeatures(productModel, modifiedFeatures);

		//fetch from database
		Mockito.reset(widgetModel);
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(null);

		TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifierFsbSpeed);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(666.0));

		typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifierClockSpeed);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(777.0));

	}

	private String createFeatureMapForProductKey(final ProductModel productModel)
	{
		return MODIFIED_FEATURES_MODEL_PARAM_PREFIX + productModel.getPk();
	}

	protected Feature getFeatureByCode(final FeatureList featureList, final String code)
	{
		Feature ret = null;
		for (final Feature feature : featureList.getFeatures())
		{
			if (code.equalsIgnoreCase(new FeatureCodeExtractor(feature).getCode()))
			{
				ret = feature;
				break;
			}
		}
		return ret;
	}
}
