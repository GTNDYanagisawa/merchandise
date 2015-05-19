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
package de.hybris.platform.cockpit.services.config.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.services.config.EditorConfiguration;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.config.EditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.jaxb.editor.CustomGroup;
import de.hybris.platform.cockpit.services.config.jaxb.editor.Editor;
import de.hybris.platform.cockpit.services.config.jaxb.editor.Group;
import de.hybris.platform.cockpit.services.config.jaxb.editor.Label;
import de.hybris.platform.cockpit.services.config.jaxb.editor.Parameter;
import de.hybris.platform.cockpit.services.config.jaxb.editor.Property;
import de.hybris.platform.cockpit.util.jaxb.impl.DefaultJAXBContextCache;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests conversion mechanism of the editor persisting strategy. This mechanism is responsible for conversion of the
 * cockpit configuration java class (<code>EditorConfiguration</code>) to the <code>JAXB</code> equivalent (
 * <code>Editor</code>).
 */
@UnitTest
public class EditorConfigurationPersistingStrategyTest extends AbstractConfigurationPersistingStrategyTest
{
	private EditorConfigurationPersistingStrategy strategy;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		this.strategy = new EditorConfigurationPersistingStrategy();
		this.strategy.setJaxbContextCache(new DefaultJAXBContextCache());
	}

	@Test
	public void testUpdateJaxbEditorConfigurationWithoutContext()
	{
		final EditorConfiguration config = createConfig(false);
		final Editor editor = this.strategy.updateJaxb(config);
		compare(editor, config, false);
	}

	@Test
	public void testUpdateJaxbEditorConfigurationWithContext()
	{
		final EditorConfiguration config = createConfig(true);
		final Editor editor = this.strategy.updateJaxb(config);
		compare(editor, config, true);
	}

	private EditorConfiguration createConfig(final boolean withContext)
	{
		JaxbBasedUIComponentConfigurationContext context = null;

		final DefaultEditorConfiguration config = new DefaultEditorConfiguration();
		final List<EditorSectionConfiguration> sections = new ArrayList<EditorSectionConfiguration>();
		config.setSections(sections);
		if (withContext)
		{
			final Editor editor = new Editor();
			context = new JaxbBasedUIComponentConfigurationContext(editor);
			config.setContext(context);
		}

		DefaultEditorSectionConfiguration section = new DefaultEditorSectionConfiguration("general");
		sections.add(section);
		section.setInitiallyOpened(true);
		section.setPosition(1);
		section.setPrintable(true);
		section.setShowIfEmpty(true);
		section.setShowInCreateMode(true);
		section.setTabbed(false);
		section.setVisible(true);
		//section.setXmlDataProvider(xmlDataProvider);
		section.setAllLabel(createLabels("general"));
		if (withContext)
		{
			final Group group = new Group();
			context.registerJaxbElement(section, group);
		}

		List<EditorRowConfiguration> rows = new ArrayList<EditorRowConfiguration>();
		section.setSectionRows(rows);
		PropertyEditorRowConfiguration row = new PropertyEditorRowConfiguration(propCode, true, true);
		rows.add(row);
		if (withContext)
		{
			final Property prop = new Property();
			context.registerJaxbElement(prop, row);
		}

		row = new PropertyEditorRowConfiguration(propCatalogVersion, true, false);
		rows.add(row);
		if (withContext)
		{
			final Property prop = new Property();
			context.registerJaxbElement(prop, row);
		}

		row = new PropertyEditorRowConfiguration(propName, true, true);
		rows.add(row);
		if (withContext)
		{
			final Property prop = new Property();
			context.registerJaxbElement(prop, row);
		}

		section = new DefaultEditorSectionConfiguration("advanced");
		sections.add(section);
		section.setInitiallyOpened(false);
		section.setPosition(2);
		section.setPrintable(true);
		section.setShowIfEmpty(false);
		section.setShowInCreateMode(true);
		section.setTabbed(false);
		section.setVisible(true);
		//section.setXmlDataProvider(xmlDataProvider);
		section.setAllLabel(createLabels("advanced"));
		if (withContext)
		{
			final Group group = new Group();
			context.registerJaxbElement(section, group);
		}

		rows = new ArrayList<EditorRowConfiguration>();
		section.setSectionRows(rows);
		row = new PropertyEditorRowConfiguration(propUnitName, true, true);
		rows.add(row);
		row.setEditor("specialEditor");
		row.setParameter("param1", "value1");
		row.setParameter("param2", "value2");
		if (withContext)
		{
			final Property prop = new Property();
			context.registerJaxbElement(prop, row);
		}

		//custom section:
		section = new UnassignedEditorSectionConfiguration("other");
		sections.add(section);
		section.setInitiallyOpened(false);
		section.setPosition(3);
		section.setPrintable(false);
		section.setShowIfEmpty(false);
		section.setShowInCreateMode(false);
		section.setTabbed(false);
		section.setVisible(false);
		//section.setXmlDataProvider(xmlDataProvider);
		section.setAllLabel(createLabels("other"));
		if (withContext)
		{
			final CustomGroup group = new CustomGroup();
			group.setClazz(UnassignedEditorSectionConfiguration.class.getName());
			context.registerJaxbElement(section, group);
		}

		return config;
	}

	private void compare(final Editor editor, final EditorConfiguration config, final boolean withContext)
	{
		assertNotNull(config);

		final List<Object> groups = editor.getGroupOrCustomGroup();
		assertEquals(groups.size(), config.getSections().size());
		int index = 0;
		for (final EditorSectionConfiguration section : config.getSections())
		{
			compareSection(section, groups.get(index++), withContext);
		}
	}

	private void compareSection(final EditorSectionConfiguration section, final Object groupObject, final boolean withContext)
	{
		if (groupObject instanceof Group)
		{
			final Group group = (Group) groupObject;

			assertEquals(section.getQualifier(), group.getQualifier());
			assertEquals(section.getPosition(), group.getPosition().intValue());
			assertEquals(section.isInitiallyOpened(), group.isInitiallyOpened());
			assertEquals(section.isPrintable(), group.isPrintable());
			assertEquals(section.showIfEmpty(), group.isShowIfEmpty());
			assertEquals(section.showInCreateMode(), group.isShowInCreateMode());
			assertEquals(section.isTabbed(), group.isTabbed());
			assertEquals(section.isVisible(), group.isVisible());

			compareLabel(section.getAllLabel(), group.getLabel());

			final List<Property> properties = group.getProperty();
			final List<EditorRowConfiguration> rows = section.getSectionRows();
			assertEquals(properties.size(), rows.size());
			int index = 0;
			for (final EditorRowConfiguration row : rows)
			{
				compareRow(row, properties.get(index++));
			}
		}
		else if (groupObject instanceof CustomGroup)
		{
			final CustomGroup group = (CustomGroup) groupObject;

			assertEquals(section.getQualifier(), group.getQualifier());
			assertEquals(section.isInitiallyOpened(), group.isInitiallyOpened());
			assertEquals(section.isPrintable(), group.isPrintable());
			assertEquals(section.showIfEmpty(), group.isShowIfEmpty());
			assertEquals(section.isVisible(), group.isVisible());

			compareLabel(section.getAllLabel(), group.getLabel());

			if (withContext)
			{
				assertEquals(group.getClazz(), section.getClass().getName());
			}
		}
	}

	private void compareRow(final EditorRowConfiguration row, final Property property)
	{
		assertEquals(property.getQualifier().toLowerCase(), row.getPropertyDescriptor().getQualifier().toLowerCase());
		assertEquals(property.getEditor(), row.getEditor());
		assertEquals(property.getPrintoutas(), row.getPrintoutAs());
		assertEquals(property.isVisible(), row.isVisible());

		compareParameters(row.getParameters(), property.getParameter());
	}

	private void compareLabel(final Map<LanguageModel, String> configLabels, final List<Label> jaxbLabels)
	{
		assertEquals(configLabels.keySet().size(), jaxbLabels.size());
		if (!jaxbLabels.isEmpty())
		{
			final Map<String, String> labelLangMap = new HashMap<String, String>(jaxbLabels.size());
			for (final Label label : jaxbLabels)
			{
				labelLangMap.put(label.getLang(), label.getValue());
			}

			for (final Map.Entry<LanguageModel, String> entry : configLabels.entrySet())
			{
				assertEquals(entry.getValue(), labelLangMap.get(entry.getKey().getIsocode()));
			}
		}
	}

	private void compareParameters(final Map<String, String> parameters, final List<Parameter> jaxbParams)
	{
		assertEquals(parameters.keySet().size(), jaxbParams.size());
		if (!jaxbParams.isEmpty())
		{
			final Map<String, String> paramNameMap = new HashMap<String, String>(jaxbParams.size());
			for (final Parameter para : jaxbParams)
			{
				paramNameMap.put(para.getName(), para.getValue());
			}

			for (final Map.Entry<String, String> entry : parameters.entrySet())
			{
				assertEquals(entry.getValue(), paramNameMap.get(entry.getKey()));
			}
		}
	}
}
