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
package de.hybris.platform.cockpit.model.editor.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.meta.EditorFactory;
import de.hybris.platform.cockpit.model.meta.PropertyEditorDescriptor;
import de.hybris.platform.cockpit.model.meta.impl.ItemAttributePropertyDescriptor;
import de.hybris.platform.cockpit.services.meta.PropertyService;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.Listbox;


@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/cockpit/cockpit-spring-editors.xml")
public class MapUIEditorTest
{
	private final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

	@Test
	@Ignore("TCP-124, PROC-204 it's preparing all necessary mocks but doesn't test anything yet :)")
	public void testSomething()
	{
		map.clear();
		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("attributeQualifier", "Product.articleStatus");

		//Welcome to mock hell:

		final ItemAttributePropertyDescriptor propertyDescriptor = Mockito.mock(ItemAttributePropertyDescriptor.class);
		Mockito.when(Boolean.valueOf(propertyDescriptor.isLocalized())).thenReturn(Boolean.TRUE);
		Mockito.when(propertyDescriptor.getTypeCode()).thenReturn("typeCode");
		Mockito.when(propertyDescriptor.getAttributeQualifier()).thenReturn("Product.ArticleStatus");

		final TypeService cockpitTypeService = Mockito.mock(TypeService.class);
		Mockito.when(cockpitTypeService.getPropertyDescriptor("Product.articleStatus")).thenReturn(propertyDescriptor);

		final ComposedTypeModel composedType = Mockito.mock(ComposedTypeModel.class);

		final de.hybris.platform.servicelayer.type.TypeService typeService = Mockito
				.mock(de.hybris.platform.servicelayer.type.TypeService.class);
		Mockito.when(typeService.getComposedTypeForCode("typeCode")).thenReturn(composedType);

		final AttributeDescriptorModel attributeDescriptorModel = Mockito.mock(AttributeDescriptorModel.class);
		Mockito.when(typeService.getAttributeDescriptor(composedType, "Product.ArticleStatus"))
				.thenReturn(attributeDescriptorModel);

		//attribute type model mock and it's key/value type models:
		final MapTypeModel mapTypeModel = Mockito.mock(MapTypeModel.class);
		//key type model mock
		final TypeModel keyTypeModel = Mockito.mock(TypeModel.class);
		Mockito.when(keyTypeModel.getCode()).thenReturn("ArticleStatus");
		Mockito.when(mapTypeModel.getArgumentType()).thenReturn(keyTypeModel);
		//value type model mock
		final MapTypeModel valueTypeModel = Mockito.mock(MapTypeModel.class);
		Mockito.when(valueTypeModel.getCode()).thenReturn("String");
		Mockito.when(valueTypeModel.getArgumentType()).thenReturn(keyTypeModel);

		Mockito.when(attributeDescriptorModel.getAttributeType()).thenReturn(mapTypeModel);
		Mockito.when(mapTypeModel.getReturntype()).thenReturn(valueTypeModel);

		final PropertyService propertyService = Mockito.mock(PropertyService.class);
		Mockito.when(propertyService.getDefaultEditorType("ArticleStatus", false)).thenReturn("keyEditorType");
		Mockito.when(propertyService.getDefaultEditorType("String", false)).thenReturn("valueEditorType");

		final PropertyEditorDescriptor keyEditorDescriptor = Mockito.mock(PropertyEditorDescriptor.class);
		final Collection keyEditorDescriptors = Collections.singletonList(keyEditorDescriptor);

		final EditorFactory editorFactory = Mockito.mock(EditorFactory.class);
		Mockito.when(editorFactory.getMatchingEditorDescriptors("keyEditorType")).thenReturn(keyEditorDescriptors);

		final PropertyEditorDescriptor valueEditorDescriptor = Mockito.mock(PropertyEditorDescriptor.class);
		final Collection valueEditorDescriptors = Collections.singletonList(valueEditorDescriptor);
		Mockito.when(editorFactory.getMatchingEditorDescriptors("valueEditorType")).thenReturn(valueEditorDescriptors);

		final EditorListener editorListener = Mockito.mock(EditorListener.class);

		final Listbox listbox = new Listbox();
		final ListModelMap listModelMap = new ListModelMap(map);
		listbox.setModel(listModelMap);
		new MapUIEditorRowRenderer().init(new MapUIEditor(), parameters, editorListener, listbox);
		//leaving the hell...
		//simulate adding new entry:
		//		listbox.a
		//		mapUIEditor.fireValueChanged(listener);
		//		Events.sendEvent(new Event)
	}

	/**
	 * Tests if for given ordered map, the correct entry key is replaced. The important thing is that entry with new key
	 * should reside in the same position in order.
	 */
	@Test
	public void mapKeyReplacementTest()
	{
		map.clear();
		map.put("k_one", "v_one");
		map.put("k_two", "v_two");
		map.put("k_three", "v_three");
		map.put("k_four", "v_four");

		final Map<String, String> resultingMap = new MapUIEditorRowRenderer().replaceKey(2, "newKey", map);
		//expected map structure:
		final Object[] originalEntries = map.entrySet().toArray();
		final Object[] resultingEntries = resultingMap.entrySet().toArray();
		Assert.assertEquals(originalEntries[0], resultingEntries[0]);
		Assert.assertEquals(originalEntries[1], resultingEntries[1]);
		Assert.assertEquals("newKey", ((Entry) resultingEntries[2]).getKey());
		Assert.assertEquals(((Entry) originalEntries[2]).getValue(), ((Entry) resultingEntries[2]).getValue());
		Assert.assertEquals(originalEntries[3], resultingEntries[3]);
	}

	/**
	 * Tests if for given ordered map, the correct entry value is replaced. The important thing is that entry with new
	 * key should reside in the same position in order.
	 */
	@Test
	public void mapValueReplacementTest()
	{
		map.clear();
		map.put("k_one", "v_one");
		map.put("k_two", "v_two");
		map.put("k_three", "v_three");
		map.put("k_four", "v_four");

		final Map<String, String> resultingMap = new MapUIEditorRowRenderer().replaceValue(2, "newValue", map);
		//expected map structure:
		final Object[] originalEntries = map.entrySet().toArray();
		final Object[] resultingEntries = resultingMap.entrySet().toArray();
		Assert.assertEquals(originalEntries[0], resultingEntries[0]);
		Assert.assertEquals(originalEntries[1], resultingEntries[1]);
		Assert.assertEquals(((Entry) originalEntries[2]).getKey(), ((Entry) resultingEntries[2]).getKey());
		Assert.assertEquals("newValue", ((Entry) resultingEntries[2]).getValue());
		Assert.assertEquals(originalEntries[3], resultingEntries[3]);
	}
}
