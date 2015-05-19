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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.services.config.AdvancedSearchConfiguration;
import de.hybris.platform.cockpit.services.config.SearchFieldConfiguration;
import de.hybris.platform.cockpit.services.config.SearchFieldGroupConfiguration;
import de.hybris.platform.cockpit.services.config.UIComponentConfiguration;

import java.util.List;

import org.junit.Test;


@IntegrationTest
public class AdvancedSearchConfigurationTest extends UIConfigurationTestBase
{

	@Test
	public void testDefaultConfiguration() throws Exception
	{
		final ObjectTemplate objectTemplate = typeService.getObjectTemplate("Product.test");
		assertNotNull("Object template not found", objectTemplate);
		final UIComponentConfiguration componentConfig = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"existiertNicht", AdvancedSearchConfiguration.class);
		assertNotNull("Default configuration not found", componentConfig);
	}


	@Test
	public void testAdvancedSearchConfiguration() throws Exception
	{
		final ObjectTemplate objectTemplate = typeService.getObjectTemplate("Product.test");
		assertNotNull("Object template not found", objectTemplate);
		final UIComponentConfiguration componentConfig = uiConfigurationService.getComponentConfiguration(objectTemplate,
				"advancedSearchProductManager", AdvancedSearchConfiguration.class);
		assertNotNull("UIComponentConfig not found", componentConfig);
		final AdvancedSearchConfiguration advancedSearchConfig = (AdvancedSearchConfiguration) componentConfig;
		final List<ObjectTemplate> relatedTypes = advancedSearchConfig.getRelatedTypes();
		assertNotNull("No supported types", relatedTypes);
		assertEquals("Size of supported types", 1, relatedTypes.size());
		assertEquals("Code of supported type", "Product", relatedTypes.get(0).getCode());
		final SearchFieldGroupConfiguration rootGroup = advancedSearchConfig.getRootGroupConfiguration();
		assertNotNull("No root group found", rootGroup);
		assertEquals("Root group name", "root", rootGroup.getName());
		//		assertEquals("EN label", "Administration", rootGroup.getLabel("en"));
		//		assertEquals("DE label", "Verwaltung", rootGroup.getLabel("de"));
		final List<SearchFieldGroupConfiguration> subGroups = rootGroup.getSearchFieldGroupConfigurations();
		assertEquals("Size of group list", 2, subGroups.size());

		final SearchFieldGroupConfiguration generalGroup = subGroups.get(0);
		assertEquals("Root group name", "general", generalGroup.getName());
		//		assertEquals("EN label", "Basic", generalGroup.getLabel("en"));
		//		assertEquals("DE label", "Stammdaten", generalGroup.getLabel("de"));
		assertEquals("Size of fields", 3, generalGroup.getSearchFieldConfigurations().size());
		assertField(generalGroup.getSearchFieldConfigurations().get(0), "Product.code", true, true);
		final SearchFieldConfiguration nameField = generalGroup.getSearchFieldConfigurations().get(1);
		assertField(nameField, "Product.name", true, true);
		assertEquals("Editor of name field", "nameEditor", nameField.getEditor());
		assertField(generalGroup.getSearchFieldConfigurations().get(2), "Product.description", false, false);
		assertSame("Parent group", rootGroup, generalGroup.getParentSearchFieldGroupConfiguration());

		final SearchFieldGroupConfiguration adminGroup = subGroups.get(1);
		assertEquals("Root group name", "admin", adminGroup.getName());
		//		assertEquals("EN label", "Administration", adminGroup.getLabel("en"));
		//		assertEquals("DE label", "Verwaltung", adminGroup.getLabel("de"));
		assertEquals("Size of fields", 2, adminGroup.getSearchFieldConfigurations().size());
		assertField(adminGroup.getSearchFieldConfigurations().get(0), "Item.pk", false, false);
		assertField(adminGroup.getSearchFieldConfigurations().get(1), "Item.modifiedtime", false, false);
		assertSame("Parent group", rootGroup, adminGroup.getParentSearchFieldGroupConfiguration());

	}

	private void assertField(final SearchFieldConfiguration field, final String code, final boolean visible,
			final boolean sortEnabled)
	{
		assertNotNull("Search field configuration missing", field);
		assertNotNull("Property descriptor missing", field.getPropertyDescriptor());
		assertEquals("Propery code", code, field.getPropertyDescriptor().getQualifier());
		assertEquals("Visible flag", Boolean.valueOf(visible), Boolean.valueOf(field.isVisible()));
		assertEquals("Sort enabled flag", Boolean.valueOf(sortEnabled), Boolean.valueOf(!field.isSortDisabled()));
	}
}
