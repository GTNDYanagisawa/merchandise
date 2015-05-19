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

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for module configuration access.
 */
@UnitTest
@ContextConfiguration(locations =
{ "ModuleConfigurationAccessTest-spring.xml" })
public class ModuleConfigurationAccessTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "sapDefaultTestModuleConfigurationAccess")
	private ModuleConfigurationAccess classUnderTest;

	/**
	 * Test module id <property name="moduleId" value="testModuleId" />.
	 */
	@Test
	public void testModuleId()
	{
		assertEquals("testModuleId", classUnderTest.getModuleId());
	}

	/**
	 * Test backend type <property name="backendType" value="testBackendType" />.
	 */
	@Test
	public void testBackendType()
	{
		assertEquals("testBackendType", classUnderTest.getBackendType());
	}

	/**
	 * <entry key="propString" value="valueString" /> <entry key="propInt" value="2" /> <entry key="propFloat".
	 * value="#{new Float(3.14)}" />
	 */
	@Test
	public void testModuleConfiguration()
	{
		assertEquals("valueString", classUnderTest.getProperty("propString"));
		assertEquals(2, classUnderTest.getProperty("propInt"));
		assertEquals(new Float(3.14), classUnderTest.getProperty("propFloat"));
	}

	/**
	 * <entry key="propString" value="valueString" /> <entry key="propInt" value="2" /> <entry key="propFloat"
	 * value="#{new Float(3.14)}" />.
	 */
	@Test
	public void testBaseStoreConfiguration()
	{
		assertEquals("3020", classUnderTest.getBaseStoreProperty("salesOrganization"));
		assertEquals("30", classUnderTest.getBaseStoreProperty("distributionChannel"));
		assertEquals("00", classUnderTest.getBaseStoreProperty("division"));

	}

}
