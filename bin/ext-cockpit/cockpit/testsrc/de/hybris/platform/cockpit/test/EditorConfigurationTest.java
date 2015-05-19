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
package de.hybris.platform.cockpit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.services.config.EditorConfiguration;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.config.EditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.UIComponentConfiguration;
import de.hybris.platform.cockpit.services.config.impl.ClassAttrEditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.impl.UnassignedEditorSectionConfiguration;

import java.util.List;

import org.junit.Test;


@IntegrationTest
public class EditorConfigurationTest extends UIConfigurationTestBase
{

	@Test
	public void testEditorConfiguration() throws Exception
	{
		final ObjectTemplate objectTemplate = typeService.getObjectTemplate("Product.test");
		assertNotNull("Object template not found", objectTemplate);
		final UIComponentConfiguration componentConfig = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"editorProductManager", EditorConfiguration.class);
		assertNotNull("UIComponentConfig not found", componentConfig);
		assertTrue("Component config not instance of " + EditorConfiguration.class.getName(),
				componentConfig instanceof EditorConfiguration);
		final EditorConfiguration editorConfig = (EditorConfiguration) componentConfig;
		final List<EditorSectionConfiguration> sections = editorConfig.getSections();
		assertNotNull("Sections must not be null", sections);
		assertEquals("Size of sections", 4, sections.size());

		EditorSectionConfiguration section = sections.get(0);
		assertEquals("Qualifier", "general", section.getQualifier());
		//		assertEquals("Label de", "Stammdaten", section.getLabel("de"));
		//		assertEquals("Label en", "Basic", section.getLabel("en"));
		assertTrue("Visible", section.isVisible());
		assertTrue("Initially opened", section.isInitiallyOpened());
		assertFalse("Tabbed", section.isTabbed());
		assertTrue("Show if empty", section.showIfEmpty());
		assertTrue("Show in create mode", section.showInCreateMode());
		assertEquals("Position", 1, section.getPosition());
		assertEquals("Number of rows", 2, section.getSectionRows().size());
		assertTrue(section.isPrintable());

		EditorRowConfiguration row = section.getSectionRows().get(0);
		assertEquals("Row qualifier", "Product.code", row.getPropertyDescriptor().getQualifier());
		assertTrue("Editable", row.isEditable());
		assertTrue("Visible", row.isVisible());
		assertEquals("Editor", "test", row.getEditor());
		assertEquals("Count of parameters", 1, row.getParameters().size());
		assertEquals("Parameter", "testParamValue", row.getParameter("testParam"));

		row = section.getSectionRows().get(1);
		assertEquals("Row qualifier", "Product.description", row.getPropertyDescriptor().getQualifier());
		assertTrue("Editable", row.isEditable());
		assertTrue("Visible", row.isVisible());
		assertNull("Editor", row.getEditor());

		section = sections.get(1);
		assertEquals("Qualifier", "unit", section.getQualifier());
		//		assertEquals("Label de", "Einheit", section.getLabel("de"));
		assertTrue("Visible", section.isVisible());
		assertFalse("Initially opened", section.isInitiallyOpened());
		assertFalse("Tabbed", section.isTabbed());
		assertTrue("Show if empty", section.showIfEmpty());
		assertTrue("Show in create mode", section.showInCreateMode());
		assertEquals("Position", 3, section.getPosition());
		assertEquals("Number of rows", 1, section.getSectionRows().size());

		row = section.getSectionRows().get(0);
		assertEquals("Row qualifier", "Product.unit", row.getPropertyDescriptor().getQualifier());
		assertTrue("Editable", row.isEditable());
		assertFalse("Visible", row.isVisible());

		section = sections.get(2);
		assertTrue("Section expected to be of type: " + ClassAttrEditorSectionConfiguration.class.getSimpleName(),
				section instanceof ClassAttrEditorSectionConfiguration);
		assertEquals("Section qualifier", "classification", section.getQualifier());
		assertFalse("Visible", section.isVisible());

		section = sections.get(3);
		assertTrue("Section expected to be of type: " + UnassignedEditorSectionConfiguration.class.getSimpleName(),
				section instanceof UnassignedEditorSectionConfiguration);
		assertEquals("Section qualifier", "test", section.getQualifier());
		assertTrue("Visible", section.isVisible());

	}

	@Test
	public void testEditorConfigurationFallback() throws Exception
	{
		final ObjectTemplate objectTemplate = typeService.getObjectTemplate("product.test");
		assertNotNull("Object template not found", objectTemplate);
		final UIComponentConfiguration componentConfig = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"editorFallback", EditorConfiguration.class);
		assertNotNull("UIComponentConfig not found", componentConfig);
	}

}
