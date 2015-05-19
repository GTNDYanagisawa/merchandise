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
package de.hybris.platform.sap.core.module.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for module configuration access.
 */
@UnitTest
@ContextConfiguration(locations =
{ "ModuleResourceAccessTest-spring.xml" })
public class ModuleResourceAccessTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "sapCoreModuleResourceAccess")
	private ModuleResourceAccessImpl classUnderTest;

	/**
	 * Test retrieving of text with resource key.
	 */
	@Test
	public void testGetStringWithResourceKey()
	{
		assertEquals("My Module Resource Test", classUnderTest.getString("mymodule.bo.test.get.string.with.resourcekey"));
	}

	/**
	 * Test retrieving of text with resource key and arguments.
	 */
	@Test
	public void testGetStringWithResourceKeyAndArguments()
	{
		assertEquals("My Module Resource Test - arguments a1:arg1, a2: arg2",
				classUnderTest.getString("mymodule.bo.test.get.string.with.resourcekey.and.arguments", new Object[]
				{ "arg1", "arg2" }));
	}

	/**
	 * Test retrieving of text with resource key.
	 */
	@Test
	public void testGetStringWithResourceKeyAndLocale()
	{
		assertEquals("Mein Modul Resource Test Deutsch",
				classUnderTest.getString("mymodule.bo.test.get.string.with.resourcekey", Locale.GERMAN));
	}

	/**
	 * Test retrieving of text with resource key.
	 */
	@Test
	public void testGetStringWithResourceKeyAndLocaleAndArguments()
	{
		assertEquals("Mein Modul Resource Test Deutsch - Argumente a1:arg1, a2: arg2",
				classUnderTest.getString("mymodule.bo.test.get.string.with.resourcekey.and.arguments", Locale.GERMAN, new Object[]
				{ "arg1", "arg2" }));
	}

	/**
	 * Test retrieving of text with resource key.
	 */
	@Test
	public void testGetStringWithNotExistingResourceKey()
	{
		assertEquals("text for key not found: [myModule.notExistingKey]", classUnderTest.getString("myModule.notExistingKey"));
	}

	/**
	 * Test retrieving of text with resource key.
	 */
	@Test
	public void testGetStringWithNotExistingModuleId()
	{
		assertEquals("text for key not found: [notExistingModule.notExistingKey]",
				classUnderTest.getString("notExistingModule.notExistingKey"));
	}

}
