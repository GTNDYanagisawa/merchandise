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
package com.hybris.backoffice.cockpitng.classification.editor;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.util.ReflectionTestUtils;

import com.hybris.backoffice.cockpitng.classification.ClassificationInfo;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorDefinition;


@RunWith(Parameterized.class)
public class FeatureEditorTest
{
	final private TestParameter testParameter;

	public FeatureEditorTest(final TestParameter testParameter)
	{
		this.testParameter = testParameter;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> params()
	{
		final String prefixFV = "FeatureValue";
		final String prefixLocalized = "Localized";
		final String prefixMV = "List";
		final String prefixRange = "Range";
		final String prefixEnum = "ClassificationEnum(CLASSIFICATION_CATALOG_ID/CLASSIFICATION_SYSTEM_VERSION/CLASSIFICATION_CLASS.ATTRIBUTE_CODE)";


		final Object[][] data = new Object[][]
		{
				{ new TestParameter(createStringClassAttr(), String.format("%s(java.lang.String)", prefixFV)) },
				{ new TestParameter(createBooleanClassAttr(), String.format("%s(java.lang.Boolean)", prefixFV)) },
				{ new TestParameter(createDateClassAttr(), String.format("%s(java.util.Date)", prefixFV)) },
				{ new TestParameter(createNumberClassAttr(), String.format("%s(java.lang.Double)", prefixFV)) },
				{ new TestParameter(createStringLocalizedAttr(), String.format("%s(%s(java.lang.String))", prefixLocalized, prefixFV)) },
				{ new TestParameter(createMultiValueNumberAttr(), String.format("%s(%s(java.lang.Double))", prefixMV, prefixFV)) },
				{ new TestParameter(createMultiValueLocalizedStringAttr(), String.format("%s(%s(%s(java.lang.String)))",
						prefixLocalized, prefixMV, prefixFV)) },
				{ new TestParameter(createRangeNumberAttr(), String.format("%s(%s(java.lang.Double))", prefixRange, prefixFV)) },
				{ new TestParameter(createMultiValueRangeNumberAttr(), String.format("%s(%s(%s(java.lang.Double)))", prefixMV,
						prefixRange, prefixFV)) },
				{ new TestParameter(createLocalizedRangeAttr(), String.format("%s(%s(%s(java.util.Date)))", prefixLocalized,
						prefixRange, prefixFV)) },
				{ new TestParameter(createLocalizedMultiValueRangeAttr(), String.format("%s(%s(%s(%s(java.util.Date))))",
						prefixLocalized, prefixMV, prefixRange, prefixFV)) },
				{ new TestParameter(createEnumClassAttr(), String.format("%s(%s)", prefixFV, prefixEnum)) },
				{ new TestParameter(createMultiValuedEnum(), String.format("%s(%s(%s))", prefixMV, prefixFV, prefixEnum)) },
				{ new TestParameter(createMultiValuedLocalizedEnum(), String.format("%s(%s(%s(%s)))", prefixLocalized, prefixMV,
						prefixFV, prefixEnum)) }

		};
		return Arrays.asList(data);
	}

	private static class TestParameter
	{
		final private String expectedValue;
		final private ClassAttributeAssignmentModel classAttributeAssignmentModel;

		private TestParameter(final ClassAttributeAssignmentModel classAttributeAssignmentModel, final String expectedValue)
		{
			this.expectedValue = expectedValue;
			this.classAttributeAssignmentModel = classAttributeAssignmentModel;
		}

		public String getExpectedValue()
		{
			return expectedValue;
		}

		public ClassAttributeAssignmentModel getClassAttributeAssignmentModel()
		{
			return classAttributeAssignmentModel;
		}
	}

	private static ClassAttributeAssignmentModel createStringClassAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.STRING);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createNumberClassAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.NUMBER);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createBooleanClassAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.BOOLEAN);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createDateClassAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.DATE);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createStringLocalizedAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.STRING);
		classAttributeAssignmentModel.setLocalized(Boolean.TRUE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		return classAttributeAssignmentModel;
	}



	private static ClassAttributeAssignmentModel createMultiValueNumberAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.NUMBER);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.TRUE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createMultiValueLocalizedStringAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.STRING);
		classAttributeAssignmentModel.setLocalized(Boolean.TRUE);
		classAttributeAssignmentModel.setMultiValued(Boolean.TRUE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createRangeNumberAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.NUMBER);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		classAttributeAssignmentModel.setRange(Boolean.TRUE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createMultiValueRangeNumberAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.NUMBER);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.TRUE);
		classAttributeAssignmentModel.setRange(Boolean.TRUE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createLocalizedRangeAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.DATE);
		classAttributeAssignmentModel.setLocalized(Boolean.TRUE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		classAttributeAssignmentModel.setRange(Boolean.TRUE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createLocalizedMultiValueRangeAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.DATE);
		classAttributeAssignmentModel.setLocalized(Boolean.TRUE);
		classAttributeAssignmentModel.setMultiValued(Boolean.TRUE);
		classAttributeAssignmentModel.setRange(Boolean.TRUE);
		return classAttributeAssignmentModel;
	}



	private static ClassAttributeAssignmentModel createEnumClassAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.ENUM);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		classAttributeAssignmentModel.setRange(Boolean.FALSE);

		final ClassificationSystemModel catalogModel = new ClassificationSystemModel();
		catalogModel.setId("CLASSIFICATION_CATALOG_ID");

		final ClassificationSystemVersionModel systemVerion = new ClassificationSystemVersionModel();
		systemVerion.setVersion("CLASSIFICATION_SYSTEM_VERSION");
		systemVerion.setCatalog(catalogModel);

		final ClassificationAttributeModel cam = new ClassificationAttributeModel();
		cam.setCode("ATTRIBUTE_CODE");

		final ClassificationClassModel ccm = new ClassificationClassModel();
		ccm.setCode("CLASSIFICATION_CLASS");
		ReflectionTestUtils.setField(cam, "_classes", Arrays.asList(ccm));

		classAttributeAssignmentModel.setClassificationAttribute(cam);
		classAttributeAssignmentModel.setSystemVersion(systemVerion);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createMultiValuedEnum()
	{
		final ClassAttributeAssignmentModel cam = createEnumClassAttr();
		cam.setMultiValued(Boolean.TRUE);
		return cam;
	}

	private static ClassAttributeAssignmentModel createMultiValuedLocalizedEnum()
	{
		final ClassAttributeAssignmentModel cam = createEnumClassAttr();
		cam.setMultiValued(Boolean.TRUE);
		cam.setLocalized(Boolean.TRUE);
		return cam;
	}


	@Test
	public void testExtractEmbeddedType()
	{
		final FeatureEditor featureEditor = new FeatureEditor();
		final ClassificationInfo initialValue = new ClassificationInfo(testParameter.getClassAttributeAssignmentModel(), null);
		final EditorDefinition editorDefinition = null;
		final Map<String, Object> parameters = Collections.EMPTY_MAP;
		final Map<String, Object> labels = Collections.EMPTY_MAP;
		final Set<Locale> readableLocales = new HashSet<Locale>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
		final Set<Locale> writableLocales = new HashSet<Locale>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));

		final EditorContext<ClassificationInfo> editorContext = new EditorContext<ClassificationInfo>(initialValue,
				editorDefinition, parameters, labels, readableLocales, writableLocales);

		final String embeddedType = featureEditor.extractEmbeddedType(editorContext);
		Assert.assertEquals(testParameter.getExpectedValue(), embeddedType);
	}
}
