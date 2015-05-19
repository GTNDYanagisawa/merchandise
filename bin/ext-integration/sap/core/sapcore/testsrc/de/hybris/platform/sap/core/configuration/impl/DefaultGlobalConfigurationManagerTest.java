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

import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * DefaultGlobalConfigurationManager test.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultGlobalConfigurationManagerTest-spring.xml" })
public class DefaultGlobalConfigurationManagerTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "testDefaultGlobalConfigurationManager")
	private DefaultGlobalConfigurationManager classUnderTest;

	/**
	 * Tests if all module ids are recognized.
	 */
	@Test
	public void testModuleIds()
	{
		final Set<String> moduleIds = classUnderTest.getModuleIds();
		assertTrue(moduleIds.size() == 2);
		assertTrue(moduleIds.contains("sflight"));
		assertTrue(moduleIds.contains("connection"));
	}

	/**
	 * Tests if all extension names per module id are recognized.
	 */
	@Test
	public void testExtensionNames()
	{
		assertTrue(classUnderTest.getExtensionNames("sflight").contains("sapsflight"));
		assertTrue(classUnderTest.getExtensionNames("sflight").contains("mysflight"));
		assertTrue(classUnderTest.getExtensionNames("connection").contains("sapconnection"));
		assertTrue(classUnderTest.getExtensionNames("invalid") == null);
	}

}
