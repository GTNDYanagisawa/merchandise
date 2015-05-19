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

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.features.FeatureValue;

import java.util.Collections;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;

import com.google.common.collect.Lists;
import com.hybris.backoffice.cockpitng.classification.ClassificationInfo;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorDefinition;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


/**
 * @author tomasz.lepiorz
 * 
 */
public class FeatureValueEditorTest
{
	@Mock
	private EditorDefinition definition;
	@Mock
	private EditorListener<FeatureValue> editorListener;
	@Mock
	private transient ClassificationSystemService classificationSystemService;
	@InjectMocks
	private FeatureValueEditor editor = new FeatureValueEditor();

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testValue()
	{
		final Integer value = Integer.valueOf(100);
		final FeatureValue featureValue = Mockito.mock(FeatureValue.class);
		Mockito.when(featureValue.getValue()).thenReturn(value);

		final Div parent = new Div();
		final FeatureValueEditor editor = new FeatureValueEditor();
		editor.render(parent, createEditorContextForTestValue(featureValue), editorListener);

		final Editor internalEditor = (Editor) parent.getChildren().get(0).getChildren().get(0);
		Assert.assertEquals(value, internalEditor.getValue());
	}

	@Test
	public void testUnit()
	{
		final Integer value = Integer.valueOf(100);
		final FeatureValue featureValue = Mockito.mock(FeatureValue.class);
		final ClassificationAttributeUnitModel classificationAttributeUnitModel = Mockito
				.mock(ClassificationAttributeUnitModel.class);
		final ClassificationSystemVersionModel systemVersionModel = Mockito.mock(ClassificationSystemVersionModel.class);
		Mockito.when(classificationAttributeUnitModel.getSystemVersion()).thenReturn(systemVersionModel);

		Mockito.when(featureValue.getValue()).thenReturn(value);
		Mockito.when(featureValue.getUnit()).thenReturn(classificationAttributeUnitModel);

		final Div parent = new Div();
		editor.render(parent, createEditorContextForTestUnit(featureValue, classificationAttributeUnitModel), editorListener);

		final Combobox units = (Combobox) parent.getChildren().get(0).getChildren().get(1).getChildren().get(0);
		Assert.assertEquals(2, units.getModel().getSize());
		Assert.assertTrue(contains(units.getModel(), classificationAttributeUnitModel));
	}

	private EditorContext<FeatureValue> createEditorContextForTestValue(final FeatureValue initialValue)
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = Mockito.mock(ClassAttributeAssignmentModel.class);
		final ClassificationInfo classificationInfo = new ClassificationInfo(classAttributeAssignmentModel, initialValue);

		final EditorContext<FeatureValue> ctx = new EditorContext<FeatureValue>(initialValue, definition,
				Collections.<String, Object> emptyMap(), Collections.<String, Object> emptyMap(), Collections.<Locale> emptySet(),
				Collections.<Locale> emptySet());
		ctx.setValueType(Integer.class.getCanonicalName());
		ctx.setParameter(FeatureEditor.CLASSIFICATION_INFO, classificationInfo);
		return ctx;
	}

	private EditorContext<FeatureValue> createEditorContextForTestUnit(final FeatureValue initialValue,
			final ClassificationAttributeUnitModel initialUnit)
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = Mockito.mock(ClassAttributeAssignmentModel.class);
		final ClassificationInfo classificationInfo = new ClassificationInfo(classAttributeAssignmentModel, initialValue);
		final ClassificationSystemVersionModel classificationSystemVersionModel = Mockito
				.mock(ClassificationSystemVersionModel.class);
		final ClassificationAttributeModel classAttributeModel = Mockito.mock(ClassificationAttributeModel.class);
		Mockito.when(classAttributeAssignmentModel.getUnit()).thenReturn(initialUnit);
		Mockito.when(classAttributeAssignmentModel.getUnit().getUnitType()).thenReturn("utype");
		Mockito.when(classAttributeAssignmentModel.getClassificationAttribute()).thenReturn(classAttributeModel);
		Mockito.when(classAttributeModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
		Mockito.when(classAttributeAssignmentModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
		Mockito.when(classificationSystemService.getUnitsOfTypeForSystemVersion(classificationSystemVersionModel, "utype"))
				.thenReturn(Lists.newArrayList(initialUnit));


		final EditorContext<FeatureValue> ctx = new EditorContext<FeatureValue>(initialValue, definition,
				Collections.<String, Object> emptyMap(), Collections.<String, Object> emptyMap(), Collections.<Locale> emptySet(),
				Collections.<Locale> emptySet());
		ctx.setValueType(Integer.class.getCanonicalName());
		ctx.setParameter(FeatureEditor.CLASSIFICATION_INFO, classificationInfo);
		return ctx;
	}

	private boolean contains(final ListModel<?> model, final Object item)
	{
		for (int i = 0; i < model.getSize(); ++i)
		{
			if (item.equals(model.getElementAt(i)))
			{
				return true;
			}
		}
		return false;
	}
}
