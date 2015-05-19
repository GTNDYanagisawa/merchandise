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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.services.config.ColumnConfiguration;
import de.hybris.platform.cockpit.services.config.ColumnGroupConfiguration;
import de.hybris.platform.cockpit.services.config.ListViewConfiguration;
import de.hybris.platform.cockpit.services.config.UIComponentConfiguration;
import de.hybris.platform.cockpit.services.config.impl.LineNumberColumn;
import de.hybris.platform.cockpit.services.config.impl.PropertyColumnConfiguration;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;


@IntegrationTest
public class ListViewConfigurationTest extends UIConfigurationTestBase
{

	@Test
	public void testGetListViewForProductManager()
	{
		final ObjectTemplate objectTemplate = typeService.getObjectTemplate("Product.test");
		assertNotNull("Object template not found", objectTemplate);

		final UIComponentConfiguration componentConfig = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"listViewProductManager", ListViewConfiguration.class);
		assertNotNull("UIComponentConfig not found", componentConfig);
		assertTrue("Component config not instance of " + ListViewConfiguration.class.getName(),
				componentConfig instanceof ListViewConfiguration);
		final ListViewConfiguration listViewConfig = (ListViewConfiguration) componentConfig;
		final ColumnGroupConfiguration rootGroup = listViewConfig.getRootColumnGroupConfiguration();
		assertNotNull("Root group must not be null", rootGroup);
		assertEquals("Root group name", "General", rootGroup.getName());
		final List<? extends ColumnConfiguration> columnConfigurations = rootGroup.getColumnConfigurations();
		assertEquals("Count of columns in General group", 5, columnConfigurations.size());
		//		assertEquals("DE label", "Stammdaten", rootGroup.getLabel("de"));
		//		assertEquals("EN label", "Basic", rootGroup.getLabel("en"));
		assertConfigsHaveLanguagesIfLocalized(columnConfigurations);

		final PropertyColumnConfiguration codePropertyColumn = getPropertyColumn(columnConfigurations, "Product.code");
		assertNotNull("Column for property code missing", codePropertyColumn);
		assertTrue("Selectable of code columns", codePropertyColumn.isSelectable());
		assertEquals("Column width", "100px", codePropertyColumn.getWidth());

		final PropertyColumnConfiguration nameColumn = getPropertyColumn(columnConfigurations, "Product.name");
		assertNotNull("Column for property name missing", nameColumn);
		assertEquals("Editor for name column", "nameEditor", nameColumn.getEditor());
		assertEquals("Size of languages of name column", 1, nameColumn.getLanguages().size());
		assertEquals("Languuge of name column", "de", nameColumn.getLanguages().get(0).getIsocode());
		assertEquals("Size of paramaters of name column", 1, nameColumn.getParameters().size());
		assertEquals("Parameter of name column", "testParamValue", nameColumn.getParameter("testParam"));

		final PropertyColumnConfiguration descriptionPropertyColumn = getPropertyColumn(columnConfigurations, "Product.description");
		assertNotNull("Column for property description missing", descriptionPropertyColumn);
		assertEquals("Value of selectable", Boolean.TRUE, Boolean.valueOf(descriptionPropertyColumn.isSelectable()));
		assertEquals("Value of visible", Boolean.TRUE, Boolean.valueOf(descriptionPropertyColumn.isVisible()));

		final LineNumberColumn customColumn = getCustomColumn(columnConfigurations, "testColumn");
		assertFalse("Selectable of custom column", customColumn.isSelectable());
		assertNotNull("Custom column testColumn must not be null", customColumn);
		assertNotNull("Custom column testSpringColumn must not be null", getCustomColumn(columnConfigurations, "springTestColumn"));


		final List<? extends ColumnGroupConfiguration> columnGroupConfigurations = rootGroup.getColumnGroupConfigurations();
		assertEquals("Count of sub groups", 2, columnGroupConfigurations.size());

		final ColumnGroupConfiguration otherGroup = getColumnGroupConfiguration(columnGroupConfigurations, "Other");
		assertNotNull("Uassigned group missing", otherGroup);
		final Set<PropertyDescriptor> remainingPropertyDescriptors = objectTemplate.getPropertyDescriptors();
		removePropertyDescriptorWithQualifier(remainingPropertyDescriptors, "Product.code");
		removePropertyDescriptorWithQualifier(remainingPropertyDescriptors, "Product.name");
		removePropertyDescriptorWithQualifier(remainingPropertyDescriptors, "Product.description");
		removePropertyDescriptorWithQualifier(remainingPropertyDescriptors, "Product.unit");
		final List<? extends ColumnConfiguration> unassignedColumnConfigurations = otherGroup.getColumnConfigurations();
		for (final ColumnConfiguration columnConfiguration : unassignedColumnConfigurations)
		{
			assertTrue("Column configuration not an instance of " + PropertyColumnConfiguration.class.getSimpleName(),
					columnConfiguration instanceof PropertyColumnConfiguration);
			final PropertyColumnConfiguration propColumn = (PropertyColumnConfiguration) columnConfiguration;
			final boolean contained = remainingPropertyDescriptors.remove(propColumn.getPropertyDescriptor());
			assertTrue("No such property descriptor with qualifier " + propColumn.getPropertyDescriptor().getQualifier(), contained);
		}
		assertTrue("Not all property descriptors used", remainingPropertyDescriptors.isEmpty());
	}

	@Test
	public void testFallbackToDefaultComponent()
	{
		final ObjectTemplate objectTemplate = typeService.getObjectTemplate("Product.test");
		assertNotNull("Object template not found", objectTemplate);
		final UIComponentConfiguration componentConfig = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"listViewProductManagerFallback", ListViewConfiguration.class);
		assertNotNull("Component config not found", componentConfig);
		// Test caching
		final UIComponentConfiguration componentConfigCached = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"listViewProductManagerFallback", ListViewConfiguration.class);
		assertNotNull("Component config not found", componentConfigCached);
		assertSame("Cached component", componentConfig, componentConfigCached);
	}

	@Test
	public void testFallbackToDefaultRole()
	{
		final ObjectTemplate objectTemplate = typeService.getObjectTemplate("Product.test");
		assertNotNull("Object template not found", objectTemplate);

		uiConfigurationService.setSessionRole("anyoldrole");

		final UIComponentConfiguration cockpitGroupComponentConfig = uiConfigurationService.getComponentConfiguration(
				objectTemplate, "listViewProductManager", ListViewConfiguration.class);
		assertNotNull("Component config not found", cockpitGroupComponentConfig);

		final UIComponentConfiguration fallbackComponentConfig = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"listViewFallback", ListViewConfiguration.class);
		assertNotNull("Component config not found", fallbackComponentConfig);

		assertNotSame("Component fallback", fallbackComponentConfig, cockpitGroupComponentConfig);
	}

	private ColumnGroupConfiguration getColumnGroupConfiguration(
			final List<? extends ColumnGroupConfiguration> columnGroupConfigurations, final String name)
	{
		for (final ColumnGroupConfiguration group : columnGroupConfigurations)
		{
			if (name.equals(group.getName()))
			{
				return group;
			}
		}
		return null;
	}

	private LineNumberColumn getCustomColumn(final List<? extends ColumnConfiguration> columnConfigurations, final String name)
	{
		for (final ColumnConfiguration columnConfiguration : columnConfigurations)
		{
			if (columnConfiguration instanceof LineNumberColumn)
			{
				final LineNumberColumn customColumn = (LineNumberColumn) columnConfiguration;
				if (name.equals(customColumn.getName()))
				{
					return customColumn;
				}
			}
		}
		return null;
	}

	private void removePropertyDescriptorWithQualifier(final Set<PropertyDescriptor> remainingPropertyDescriptors,
			final String qualifier)
	{
		final Iterator<PropertyDescriptor> itr = remainingPropertyDescriptors.iterator();
		while (itr.hasNext())
		{
			if (itr.next().getQualifier().equals(qualifier))
			{
				itr.remove();
				return;
			}
		}
		fail("No property descriptor for qualifier " + qualifier);
	}

	private PropertyColumnConfiguration getPropertyColumn(final List<? extends ColumnConfiguration> columnConfigurations,
			final String qualifier)
	{
		for (final ColumnConfiguration columnConfiguration : columnConfigurations)
		{
			if (columnConfiguration instanceof PropertyColumnConfiguration)
			{
				final PropertyColumnConfiguration propConfig = (PropertyColumnConfiguration) columnConfiguration;
				if (propConfig.getName().equals(qualifier))
				{
					return propConfig;
				}
			}
		}
		return null;
	}

	private void assertConfigsHaveLanguagesIfLocalized(final Collection<? extends ColumnConfiguration> configs)
	{
		for (final ColumnConfiguration config : configs)
		{
			assertHasLanguagesIfLocalized(config);
		}
	}

	private void assertHasLanguagesIfLocalized(final ColumnConfiguration config)
	{
		if (config instanceof PropertyColumnConfiguration)
		{
			final PropertyColumnConfiguration propertyConfig = (PropertyColumnConfiguration) config;
			final PropertyDescriptor propertyDescriptor = propertyConfig.getPropertyDescriptor();
			if (propertyDescriptor.isLocalized() && propertyDescriptor.getQualifier().endsWith(".name"))
			{
				assertFalse("Property localized but has no languages: " + propertyDescriptor.getQualifier(), config.getLanguages().isEmpty());
			}
			else if (propertyDescriptor.isLocalized() && propertyDescriptor.getQualifier().endsWith(".description"))
			{
				assertTrue("Property configuration is not localized but has languages: " + propertyDescriptor.getQualifier(), config.getLanguages()
						.isEmpty());
			}
			else
			{
				assertTrue("Property not localized but has languages: " + propertyDescriptor.getQualifier(), config.getLanguages().isEmpty());
			}
		}
	}
}
