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
package de.hybris.platform.sap.core.configuration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * DefaultGlobalConfigurationManager test.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultConfigurationPropertyAccessTest-spring.xml" })
public class DefaultConfigurationPropertyAccessTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "testDefaultConfigurationPropertyAccessMain")
	private DefaultConfigurationPropertyAccess classUnderTest;

	/**
	 * Tests if all properties are available.
	 */
	@Test
	public void testProperties()
	{
		final Map<String, Object> allProperties = classUnderTest.getAllProperties();
		assertTrue(allProperties.size() == 2);
		assertEquals("value1", classUnderTest.getProperty("property1"));
		assertEquals("value1", allProperties.get("property1"));
		assertEquals("value2", classUnderTest.getProperty("property2"));
		assertEquals("value2", allProperties.get("property2"));
	}

	/**
	 * Tests if all property accesses are available with the correct properties.
	 */
	@Test
	public void testPropertyAccesses()
	{
		final Map<String, ConfigurationPropertyAccess> allPropertyAccesses = classUnderTest.getAllPropertyAccesses();
		assertTrue(allPropertyAccesses.size() == 2);
		assertEquals("value1.1", classUnderTest.getPropertyAccess("propertyAccess1").getProperty("property1.1"));
		assertEquals("value1.1", allPropertyAccesses.get("propertyAccess1").getProperty("property1.1"));
		assertEquals("value1.2", classUnderTest.getPropertyAccess("propertyAccess1").getProperty("property1.2"));
		assertEquals("value1.2", allPropertyAccesses.get("propertyAccess1").getProperty("property1.2"));
		assertEquals("value2.1", classUnderTest.getPropertyAccess("propertyAccess2").getProperty("property2.1"));
		assertEquals("value2.1", allPropertyAccesses.get("propertyAccess2").getProperty("property2.1"));
		assertEquals("value2.2", classUnderTest.getPropertyAccess("propertyAccess2").getProperty("property2.2"));
		assertEquals("value2.2", allPropertyAccesses.get("propertyAccess2").getProperty("property2.2"));
	}

	/**
	 * Tests if all property access collections are available with the correct properties.
	 */
	@Test
	public void testPropertyAccessCollections()
	{
		final Map<String, Collection<ConfigurationPropertyAccess>> allPropertyAccessCollections = classUnderTest
				.getAllPropertyAccessCollections();
		assertTrue(allPropertyAccessCollections.size() == 2);
		assertEquals(3, classUnderTest.getPropertyAccessCollection("propertyAccessCollection1").size());
		final Iterator<ConfigurationPropertyAccess> iterator1 = classUnderTest.getPropertyAccessCollection(
				"propertyAccessCollection1").iterator();
		assertEquals("value1.1", iterator1.next().getProperty("property1.1"));
		assertEquals("value2.1", iterator1.next().getProperty("property2.1"));
		assertEquals("value3.1", iterator1.next().getProperty("property3.1"));
		assertEquals(1, classUnderTest.getPropertyAccessCollection("propertyAccessCollection2").size());
		final Iterator<ConfigurationPropertyAccess> iterator2 = classUnderTest.getPropertyAccessCollection(
				"propertyAccessCollection2").iterator();
		assertEquals("value3.1", iterator2.next().getProperty("property3.1"));
	}

}
