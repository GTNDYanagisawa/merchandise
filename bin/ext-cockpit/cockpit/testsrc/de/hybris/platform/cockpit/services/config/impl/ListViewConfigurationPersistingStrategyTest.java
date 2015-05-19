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
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.listview.DynamicColumnProvider;
import de.hybris.platform.cockpit.services.config.ColumnConfiguration;
import de.hybris.platform.cockpit.services.config.ColumnGroupConfiguration;
import de.hybris.platform.cockpit.services.config.ListViewConfiguration;
import de.hybris.platform.cockpit.services.config.jaxb.listview.Custom;
import de.hybris.platform.cockpit.services.config.jaxb.listview.DynamicColumnProviders;
import de.hybris.platform.cockpit.services.config.jaxb.listview.Group;
import de.hybris.platform.cockpit.services.config.jaxb.listview.Label;
import de.hybris.platform.cockpit.services.config.jaxb.listview.ListView;
import de.hybris.platform.cockpit.services.config.jaxb.listview.Parameter;
import de.hybris.platform.cockpit.services.config.jaxb.listview.Property;
import de.hybris.platform.cockpit.util.jaxb.impl.DefaultJAXBContextCache;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests conversion mechanism of the list view persisting strategy. This mechanism is responsible for conversion of the
 * cockpit configuration java class (<code>ListViewConfiguration</code>) to the <code>JAXB</code> equivalent (
 * <code>ListView</code>).
 */
@UnitTest
public class ListViewConfigurationPersistingStrategyTest extends AbstractConfigurationPersistingStrategyTest
{
	private ListViewConfigurationPersistingStrategy strategy;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		this.strategy = new ListViewConfigurationPersistingStrategy();
		this.strategy.setJaxbContextCache(new DefaultJAXBContextCache());
	}

	@Test
	public void testUpdateJaxbEditorConfigurationWithoutContext()
	{
		final ListViewConfiguration config = createConfig(false);
		final ListView search = this.strategy.updateJaxb(config);
		compare(search, config, false);
	}

	@Test
	public void testUpdateJaxbEditorConfigurationWithContext()
	{
		final ListViewConfiguration config = createConfig(true);
		final ListView search = this.strategy.updateJaxb(config);
		compare(search, config, true);
	}

	private ListViewConfiguration createConfig(final boolean withContext)
	{
		JaxbBasedUIComponentConfigurationContext context = null;

		final DefaultListViewConfiguration config = new DefaultListViewConfiguration()
		{
			@Override
			protected DynamicColumnProvider instantiateDynamicColumnProvider(final String springBean)
			{
				if ("de.hybris.platform.cockpit.model.listview.impl.WorkflowAttachmentItemDynamicColumnProvider".equals(springBean))
				{
					return new de.hybris.platform.cockpit.model.listview.impl.WorkflowAttachmentItemDynamicColumnProvider();
				}
				else
				{
					return null;
				}
			}
		};
		if (withContext)
		{
			final ListView root = new ListView();
			root.setUnassignedGroupName("root");
			context = new JaxbBasedUIComponentConfigurationContext(root);
			config.setContext(context);
		}

		config.setAllowCreateInlineItems(false);
		config.setDynamicColumnProvidersSpringBeans(Arrays.asList(new String[]
		{ "de.hybris.platform.cockpit.model.listview.impl.WorkflowAttachmentItemDynamicColumnProvider" }));
		config.setHeaderPopupBean("hpBean23");
		config.setShowVariantAttrs(false);

		final DefaultColumnGroupConfiguration rootGroup = new DefaultColumnGroupConfiguration("root");
		config.setRootColumnConfigurationGroup(rootGroup);
		if (withContext)
		{
			final Group rootJaxbGroup = new Group();
			context.registerJaxbElement(rootGroup, rootJaxbGroup);
		}
		rootGroup.setAllLabels(createLabels("root"));

		// columns
		List<ColumnConfiguration> columns = new ArrayList<ColumnConfiguration>();
		rootGroup.setColumnConfigurations(columns);
		PropertyColumnConfiguration column = new PropertyColumnConfiguration(propCode);
		columns.add(column);
		column.setEditable(true);
		column.setEditor("TEXT");
		column.setName("Code");
		column.setPosition(1);
		column.setSelectable(true);
		column.setSortable(true);
		column.setVisible(true);
		column.setWidth("200");

		final LanguageModel langEn = new LanguageModel();
		langEn.setIsocode("en");
		final LanguageModel langDe = new LanguageModel();
		langDe.setIsocode("de");
		column.setLanguages(Arrays.asList(new LanguageModel[]
		{ langEn, langDe }));
		column.setParameter("param1", "value1");
		column.setParameter("param2", "value2");

		//subgroups
		final List<ColumnGroupConfiguration> subgroups = new ArrayList<ColumnGroupConfiguration>();
		rootGroup.setColumnGroupConfigurations(subgroups);

		final DefaultColumnGroupConfiguration subgroup = new DefaultColumnGroupConfiguration("additional");
		subgroups.add(subgroup);
		if (withContext)
		{
			final Group subJaxbGroup = new Group();
			context.registerJaxbElement(subgroup, subJaxbGroup);
		}
		subgroup.setAllLabels(createLabels("additional"));

		// columns
		columns = new ArrayList<ColumnConfiguration>();
		subgroup.setColumnConfigurations(columns);
		column = new PropertyColumnConfiguration(propUnitName);
		columns.add(column);
		column.setEditable(true);
		column.setEditor("TEXT");
		column.setName("Name");
		column.setPosition(2);
		column.setSelectable(false);
		column.setSortable(false);
		column.setVisible(true);
		column.setWidth("500");
		column.setLanguages(Arrays.asList(new LanguageModel[]
		{ langEn, langDe }));
		if (withContext)
		{
			final Property property = new Property();
			context.registerJaxbElement(column, property);
		}

		final LineNumberColumn lineNrColumn = new LineNumberColumn();
		columns.add(lineNrColumn);
		lineNrColumn.setEditable(false);
		lineNrColumn.setName("Line Nr");
		lineNrColumn.setPosition(3);
		lineNrColumn.setSelectable(false);
		lineNrColumn.setSortable(false);
		lineNrColumn.setVisible(true);
		lineNrColumn.setLanguages(Arrays.asList(new LanguageModel[]
		{ langEn, langDe }));
		if (withContext)
		{
			final Custom custom = new Custom();
			custom.setClazz(LineNumberColumn.class.getName());
			context.registerJaxbElement(lineNrColumn, custom);
		}

		column = new PropertyColumnConfiguration(propMinimal);
		columns.add(column);
		column.setName("Minimal config column");
		column.setEditable(true);
		column.setPosition(4);
		column.setLanguages(Arrays.asList(new LanguageModel[]
		{ langEn, langDe }));

		return config;
	}

	private void compare(final ListView jaxbListView, final ListViewConfiguration config, final boolean withContext)
	{
		assertNotNull(config);

		assertEquals(config.getHeaderPopupBean(), jaxbListView.getHeaderPopupBean());
		assertEquals(config.isAllowCreateInlineItems(), jaxbListView.isAllowCreateInlineItems());
		if (withContext)
		{
			assertEquals(jaxbListView.getUnassignedGroupName(), "root");
		}

		final DynamicColumnProviders jaxbColumnProviders = jaxbListView.getDynamicColumnProviders();
		int index = 0;
		for (final DynamicColumnProvider provider : config.getDynamicColumnProviders())
		{
			assertEquals(provider.getClass().getName(), jaxbColumnProviders.getSpringBeans().get(index++));
		}

		compareGroup(config.getRootColumnGroupConfiguration(), jaxbListView.getGroup(), withContext);
	}

	private void compareGroup(final ColumnGroupConfiguration group, final Group jaxbGroup, final boolean withContext)
	{
		compareLabel(group.getAllLabels(), jaxbGroup.getLabel());
		assertEquals(group.getName(), jaxbGroup.getName());

		final List<Object> propsAndCustoms = getPropertiesAndCustoms(jaxbGroup);
		final List<? extends ColumnConfiguration> columns = group.getColumnConfigurations();
		assertEquals(propsAndCustoms.size(), columns.size());
		int index = 0;
		for (final ColumnConfiguration column : columns)
		{
			compareColumn(column, propsAndCustoms.get(index++), withContext);
		}

		final List<Group> jaxbSubgroups = getSubgroups(jaxbGroup);
		final List<? extends ColumnGroupConfiguration> subgroups = group.getColumnGroupConfigurations();
		assertEquals(jaxbSubgroups.size(), subgroups.size());
		index = 0;
		for (final ColumnGroupConfiguration subgroup : subgroups)
		{
			compareGroup(subgroup, jaxbSubgroups.get(index++), withContext);
		}
	}

	private void compareColumn(final ColumnConfiguration column, final Object jaxbPropertyOrCustom, final boolean withContext)
	{
		if (jaxbPropertyOrCustom instanceof Property)
		{
			final Property jaxbProperty = (Property) jaxbPropertyOrCustom;
			if (withContext || !(column instanceof LineNumberColumn))
			{
				assertTrue(column instanceof PropertyColumnConfiguration);
				final PropertyColumnConfiguration propertyColumn = (PropertyColumnConfiguration) column;
				assertEquals(propertyColumn.getPropertyDescriptor().getQualifier(), jaxbProperty.getQualifier());
			}

			assertEquals(column.getEditor(), jaxbProperty.getEditor());
			assertEquals(column.getName(), jaxbProperty.getName());
			assertEquals(column.getPosition(), jaxbProperty.getPosition().intValue());
			assertEquals(column.getWidth(), jaxbProperty.getWidth());
			assertEquals(column.getColumnDescriptor().isEditable(), jaxbProperty.isEditable());
			assertEquals(column.isSelectable(), jaxbProperty.isSelectable());
			assertEquals(column.isSortable(), jaxbProperty.isSortable());
			assertEquals(column.isVisible(), jaxbProperty.isVisible());
			compareParameters(column.getParameters(), jaxbProperty.getParameter());

			assertEquals(column.getLanguages().size(), jaxbProperty.getLanguage().size());
			if (!column.getLanguages().isEmpty())
			{
				int index = 0;
				for (final LanguageModel language : column.getLanguages())
				{
					assertEquals(language.getIsocode(), jaxbProperty.getLanguage().get(index++).getIsocode());
				}
			}
		}
		else if (jaxbPropertyOrCustom instanceof Custom)
		{
			final Custom jaxbCustom = (Custom) jaxbPropertyOrCustom;

			assertEquals(column.getName(), jaxbCustom.getName());
			assertEquals(column.getPosition(), jaxbCustom.getPosition().intValue());
			assertEquals(column.getWidth(), jaxbCustom.getWidth());
			assertEquals(column.getColumnDescriptor().isEditable(), jaxbCustom.isEditable());
			assertEquals(column.isSelectable(), jaxbCustom.isSelectable());
			assertEquals(column.isSortable(), jaxbCustom.isSortable());
			assertEquals(column.isVisible(), jaxbCustom.isVisible());

			if (withContext)
			{
				assertEquals(jaxbCustom.getClazz(), LineNumberColumn.class.getName());
			}
		}
		else
		{
			fail("Unexpected element: " + jaxbPropertyOrCustom);
		}
	}

	private List<Object> getPropertiesAndCustoms(final Group jaxbGroup)
	{
		final List<Object> result = new ArrayList<Object>();
		final List<Object> elements = jaxbGroup.getPropertyOrCustomOrGroup();
		for (final Object element : elements)
		{
			if (element instanceof Property || element instanceof Custom)
			{
				result.add(element);
			}
		}

		return result;
	}

	private List<Group> getSubgroups(final Group jaxbGroup)
	{
		final List<Group> result = new ArrayList<Group>();
		final List<Object> elements = jaxbGroup.getPropertyOrCustomOrGroup();
		for (final Object element : elements)
		{
			if (element instanceof Group)
			{
				result.add((Group) element);
			}
		}

		return result;
	}

	private void compareParameters(final Map<String, String> parameters, final List<Parameter> jaxbParams)
	{
		if (parameters == null || parameters.isEmpty())
		{
			assertTrue(jaxbParams == null || jaxbParams.isEmpty());
		}
		else
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

}
