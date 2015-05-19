/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cockpit.services.search.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.util.FeatureField;
import de.hybris.platform.catalog.jalo.classification.util.FeatureValueCondition;
import de.hybris.platform.catalog.jalo.classification.util.TypedFeature.FeatureType;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.cockpit.model.meta.impl.ClassAttributePropertyDescriptor;
import de.hybris.platform.cockpit.model.search.Operator;
import de.hybris.platform.cockpit.model.search.SearchParameterDescriptor;
import de.hybris.platform.cockpit.model.search.SearchParameterValue;
import de.hybris.platform.cockpit.services.search.ConditionTranslatorContext;
import de.hybris.platform.cockpit.services.search.GenericQueryConditionTranslator;
import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericConditionList;
import de.hybris.platform.core.PK;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test verifies if the condition tree is build as expected
 */
@IntegrationTest
public class FeatureGenericQueryConditionTranslatorTest extends HybrisJUnit4Test
{

	private static final String MAIN_UNIT_MODEL = "mainUnitModel";

	private static final String UNIT_CODE = "unitModel";

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(FeatureGenericQueryConditionTranslatorTest.class);

	private GenericQueryConditionTranslator translator;

	@Mock
	private ConditionTranslatorContext ctx;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ClassAttributeAssignment classAttrAssign;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ClassificationClass classClass;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ClassAttributeAssignmentModel classAttrAssignModel;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ClassificationAttributeUnitModel unitModel;

	@Mock
	private ClassificationAttributeUnitModel mainUnitModel;

	private SearchParameterDescriptor descriptor;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		translator = new FeatureGenericQueryConditionTranslator()
		{
			@Override
			protected ClassAttributeAssignment toClassAttributeAssignment(final ClassAttributeAssignmentModel assignmentModel)
			{
				return classAttrAssign;
			}
		};

		Mockito.when(unitModel.getCode()).thenReturn(UNIT_CODE);
		Mockito.when(unitModel.getConversionFactor()).thenReturn(Double.valueOf(100));


		Mockito.when(classAttrAssign.getUnit().getCode()).thenReturn(UNIT_CODE);
		Mockito.when(classAttrAssign.isLocalized()).thenReturn(Boolean.FALSE);
		Mockito.when(classAttrAssign.getPosition()).thenReturn(Integer.valueOf(6));
		Mockito.when(classAttrAssign.getAttributeType().getCode()).thenReturn(
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.NUMBER);

		Mockito.when(classAttrAssign.getPK()).thenReturn(PK.createFixedCounterPK(0, System.currentTimeMillis()));

		//Mockito.when(classAttrAssign.getUnit()).thenReturn(null);

		Mockito.when(classAttrAssign.getSystemVersion().getFullVersionName()).thenReturn("Blah");
		Mockito.when(classAttrAssign.getClassificationClass().getCode()).thenReturn("Noaah");
		Mockito.when(classAttrAssign.getClassificationAttribute().getCode()).thenReturn("Thrah");
		Mockito.when(classAttrAssign.getClassificationClass().getAttributeValues(classAttrAssign)).thenReturn(
				Collections.EMPTY_LIST);


		Mockito.when(classClass.getSystemVersion().getVersion()).thenReturn("1.0");
		Mockito.when(classClass.getSystemVersion().getClassificationSystem().getId()).thenReturn("SampleClassification");
		Mockito.when(classClass.getCode()).thenReturn("photography");

		//
		Mockito.when(classAttrAssignModel.getUnit()).thenReturn(null);

		final ClassAttributePropertyDescriptor classAttrPropertyDescr = new ClassAttributePropertyDescriptor(
				"SampleClassification/1.0/photography.focalDistance")
		{
			@Override
			public ClassAttributeAssignment getAttributeAssignment()
			{
				return classAttrAssign;
			}

			@Override
			public Multiplicity getMultiplicity()
			{
				return Multiplicity.RANGE;
			}

			@Override
			public ClassificationClass getClassificationClass()
			{
				return classClass;
			}
		};

		descriptor = new ClassAttributeSearchDescriptor(classAttrPropertyDescr)
		{
			@Override
			public ClassAttributeAssignmentModel getClassAttributeAssignment()
			{
				return classAttrAssignModel;
			}
		};

	}

	private void setClassificationUnitAssignment(final String code)
	{
		Mockito.when(classAttrAssignModel.getUnit()).thenReturn(mainUnitModel);
		Mockito.when(classAttrAssignModel.getUnit().getCode()).thenReturn(code);
		Mockito.when(classAttrAssignModel.getUnit().getConversionFactor()).thenReturn(Double.valueOf(10));
		Mockito.when(classAttrAssign.getUnit().getCode()).thenReturn(code);
	}


	@Test
	public void testQueryNoLowBoundaryValueForRange()
	{

		final FeatureValue firstValue = null;
		final FeatureValue secondValue = new FeatureValue(Double.valueOf(23.43), null, unitModel);

		final List<FeatureValue> values = Arrays.asList(firstValue, secondValue);

		//multivalue and range
		Mockito.when(Boolean.valueOf(classAttrAssign.isMultiValuedAsPrimitive())).thenReturn(Boolean.TRUE);
		Mockito.when(Boolean.valueOf(classAttrAssign.isRangeAsPrimitive())).thenReturn(Boolean.TRUE);

		setClassificationUnitAssignment(MAIN_UNIT_MODEL);//set custom unit assignment for classification 

		final SearchParameterValue paramValue = new SearchParameterValue(descriptor, values, Operator.BETWEEN);

		final GenericCondition condition = translator.translate(paramValue, ctx);

		//verify condition
		Assert.assertTrue(condition instanceof GenericConditionList);
		verifyCondition((GenericConditionList) condition, de.hybris.platform.core.Operator.AND, 1);

		final GenericConditionList firstLevelCondition = (GenericConditionList) condition;
		Assert.assertTrue(firstLevelCondition.getConditionList().get(0) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(0), de.hybris.platform.core.Operator.OR,
				2);

		final GenericConditionList secondLevelConditionZero = (GenericConditionList) firstLevelCondition.getConditionList().get(0);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(0),
				de.hybris.platform.core.Operator.LESS, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", MAIN_UNIT_MODEL);

		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", MAIN_UNIT_MODEL);

	}


	@Test
	public void testQueryNoLowBoundaryWithUnitValueForRange()
	{

		final FeatureValue firstValue = null;
		final FeatureValue secondValue = new FeatureValue(Double.valueOf(23.43), null, unitModel);

		final List<FeatureValue> values = Arrays.asList(firstValue, secondValue);

		//multivalue and range
		Mockito.when(Boolean.valueOf(classAttrAssign.isMultiValuedAsPrimitive())).thenReturn(Boolean.TRUE);
		Mockito.when(Boolean.valueOf(classAttrAssign.isRangeAsPrimitive())).thenReturn(Boolean.TRUE);

		final SearchParameterValue paramValue = new SearchParameterValue(descriptor, values, Operator.BETWEEN);

		final GenericCondition condition = translator.translate(paramValue, ctx);

		//verify condition
		Assert.assertTrue(condition instanceof GenericConditionList);
		verifyCondition((GenericConditionList) condition, de.hybris.platform.core.Operator.AND, 1);

		final GenericConditionList firstLevelCondition = (GenericConditionList) condition;
		Assert.assertTrue(firstLevelCondition.getConditionList().get(0) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(0), de.hybris.platform.core.Operator.OR,
				2);

		final GenericConditionList secondLevelConditionZero = (GenericConditionList) firstLevelCondition.getConditionList().get(0);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(0),
				de.hybris.platform.core.Operator.LESS, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);

		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);

	}

	@Test
	public void testQueryNoUpperBoundaryValueForRange()
	{
		final FeatureValue firstValue = new FeatureValue(Double.valueOf(12.23), null, unitModel);
		final FeatureValue secondValue = null;

		final List<FeatureValue> values = Arrays.asList(firstValue, secondValue);

		//multivalue and range
		Mockito.when(Boolean.valueOf(classAttrAssign.isMultiValuedAsPrimitive())).thenReturn(Boolean.TRUE);
		Mockito.when(Boolean.valueOf(classAttrAssign.isRangeAsPrimitive())).thenReturn(Boolean.TRUE);

		setClassificationUnitAssignment(MAIN_UNIT_MODEL);//set custom unit assignment for classification 

		final SearchParameterValue paramValue = new SearchParameterValue(descriptor, values, Operator.BETWEEN);

		final GenericCondition condition = translator.translate(paramValue, ctx);

		//verify condition
		Assert.assertTrue(condition instanceof GenericConditionList);
		verifyCondition((GenericConditionList) condition, de.hybris.platform.core.Operator.AND, 1);

		final GenericConditionList firstLevelCondition = (GenericConditionList) condition;
		Assert.assertTrue(firstLevelCondition.getConditionList().get(0) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(0), de.hybris.platform.core.Operator.OR,
				2);

		final GenericConditionList secondLevelConditionZero = (GenericConditionList) firstLevelCondition.getConditionList().get(0);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(0),
				de.hybris.platform.core.Operator.GREATER, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField",
				MAIN_UNIT_MODEL);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", MAIN_UNIT_MODEL);

	}

	@Test
	public void testQueryNoUpperBoundaryWithUnitValueForRange()
	{
		final FeatureValue firstValue = new FeatureValue(Double.valueOf(12.23), null, unitModel);
		final FeatureValue secondValue = null;

		final List<FeatureValue> values = Arrays.asList(firstValue, secondValue);

		//multivalue and range
		Mockito.when(Boolean.valueOf(classAttrAssign.isMultiValuedAsPrimitive())).thenReturn(Boolean.TRUE);
		Mockito.when(Boolean.valueOf(classAttrAssign.isRangeAsPrimitive())).thenReturn(Boolean.TRUE);

		final SearchParameterValue paramValue = new SearchParameterValue(descriptor, values, Operator.BETWEEN);

		final GenericCondition condition = translator.translate(paramValue, ctx);

		//verify condition
		Assert.assertTrue(condition instanceof GenericConditionList);
		verifyCondition((GenericConditionList) condition, de.hybris.platform.core.Operator.AND, 1);

		final GenericConditionList firstLevelCondition = (GenericConditionList) condition;
		Assert.assertTrue(firstLevelCondition.getConditionList().get(0) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(0), de.hybris.platform.core.Operator.OR,
				2);

		final GenericConditionList secondLevelConditionZero = (GenericConditionList) firstLevelCondition.getConditionList().get(0);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(0),
				de.hybris.platform.core.Operator.GREATER, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);

	}

	@Test
	public void testQueryBothNumberValuesWithUnitForRange()
	{
		//
		final FeatureValue firstValue = new FeatureValue(Double.valueOf(12.23), null, unitModel);
		final FeatureValue secondValue = new FeatureValue(Double.valueOf(23.43), null, unitModel);

		final List<FeatureValue> values = Arrays.asList(firstValue, secondValue);

		//multivalue and range
		Mockito.when(Boolean.valueOf(classAttrAssign.isMultiValuedAsPrimitive())).thenReturn(Boolean.TRUE);
		Mockito.when(Boolean.valueOf(classAttrAssign.isRangeAsPrimitive())).thenReturn(Boolean.TRUE);

		final SearchParameterValue paramValue = new SearchParameterValue(descriptor, values, Operator.BETWEEN);

		final GenericCondition condition = translator.translate(paramValue, ctx);

		//verify condition
		Assert.assertTrue(condition instanceof GenericConditionList);
		verifyCondition((GenericConditionList) condition, de.hybris.platform.core.Operator.AND, 2);

		final GenericConditionList firstLevelCondition = (GenericConditionList) condition;
		Assert.assertTrue(firstLevelCondition.getConditionList().get(0) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(0), de.hybris.platform.core.Operator.OR,
				2);
		Assert.assertTrue(firstLevelCondition.getConditionList().get(1) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(1), de.hybris.platform.core.Operator.OR,
				2);

		final GenericConditionList secondLevelConditionZero = (GenericConditionList) firstLevelCondition.getConditionList().get(0);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(0),
				de.hybris.platform.core.Operator.GREATER, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);

		final GenericConditionList secondLevelConditionOne = (GenericConditionList) firstLevelCondition.getConditionList().get(1);
		Assert.assertTrue(secondLevelConditionOne.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionOne.getConditionList().get(0),
				de.hybris.platform.core.Operator.LESS, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);
		Assert.assertTrue(secondLevelConditionOne.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionOne.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", UNIT_CODE);
	}

	@Test
	public void testQueryBothNumberValuesForRange()
	{
		//
		final FeatureValue firstValue = new FeatureValue(Double.valueOf(12.23), null, unitModel);
		final FeatureValue secondValue = new FeatureValue(Double.valueOf(23.43), null, unitModel);

		final List<FeatureValue> values = Arrays.asList(firstValue, secondValue);

		//multivalue and range
		Mockito.when(Boolean.valueOf(classAttrAssign.isMultiValuedAsPrimitive())).thenReturn(Boolean.TRUE);
		Mockito.when(Boolean.valueOf(classAttrAssign.isRangeAsPrimitive())).thenReturn(Boolean.TRUE);

		setClassificationUnitAssignment(MAIN_UNIT_MODEL);//set custom unit assignment for classification

		final SearchParameterValue paramValue = new SearchParameterValue(descriptor, values, Operator.BETWEEN);

		final GenericCondition condition = translator.translate(paramValue, ctx);

		//verify condition
		Assert.assertTrue(condition instanceof GenericConditionList);
		verifyCondition((GenericConditionList) condition, de.hybris.platform.core.Operator.AND, 2);

		final GenericConditionList firstLevelCondition = (GenericConditionList) condition;
		Assert.assertTrue(firstLevelCondition.getConditionList().get(0) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(0), de.hybris.platform.core.Operator.OR,
				2);
		Assert.assertTrue(firstLevelCondition.getConditionList().get(1) instanceof GenericConditionList);
		verifyCondition((GenericConditionList) firstLevelCondition.getConditionList().get(1), de.hybris.platform.core.Operator.OR,
				2);

		final GenericConditionList secondLevelConditionZero = (GenericConditionList) firstLevelCondition.getConditionList().get(0);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(0),
				de.hybris.platform.core.Operator.GREATER, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField",
				MAIN_UNIT_MODEL);
		Assert.assertTrue(secondLevelConditionZero.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionZero.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", MAIN_UNIT_MODEL);

		final GenericConditionList secondLevelConditionOne = (GenericConditionList) firstLevelCondition.getConditionList().get(1);
		Assert.assertTrue(secondLevelConditionOne.getConditionList().get(0) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionOne.getConditionList().get(0),
				de.hybris.platform.core.Operator.LESS, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", MAIN_UNIT_MODEL);
		Assert.assertTrue(secondLevelConditionOne.getConditionList().get(1) instanceof FeatureValueCondition);
		verifyCondition((FeatureValueCondition) secondLevelConditionOne.getConditionList().get(1),
				de.hybris.platform.core.Operator.EQUAL, "Blah/Noaah.thrah", FeatureType.NUMBER, "RangedFeatureField", MAIN_UNIT_MODEL);
	}

	private void verifyCondition(final GenericConditionList condition, final de.hybris.platform.core.Operator expectedOperator,
			final int expectedSize)
	{
		Assert.assertNotNull(condition);
		Assert.assertEquals(condition.getOperator(), expectedOperator);
		//condition   instanceof GenericConditionList);
		Assert.assertEquals(expectedSize, condition.getConditionList().size());
	}

	private void verifyCondition(final FeatureValueCondition condition, final de.hybris.platform.core.Operator expectedOperator,
			final String expectedIdentifier, final FeatureType expectedType, final String expectedFeatureFieldClass,
			final String expectedUnitCode)
	{
		Assert.assertNotNull(condition);
		Assert.assertEquals(condition.getOperator(), expectedOperator);
		//condition   instanceof GenericConditionList);
		Assert.assertNotNull(condition.getFeatureField());
		Assert.assertEquals(expectedIdentifier, condition.getFeatureField().getQualifier());
		Assert.assertEquals(expectedType, condition.getFeatureField().getFeatureType());
		Assert.assertEquals(expectedFeatureFieldClass, condition.getFeatureField().getClass().getSimpleName());
		final FeatureField field = condition.getFeatureField();
		Assert.assertEquals(expectedUnitCode, field.getUnit().getCode());
	}

}
