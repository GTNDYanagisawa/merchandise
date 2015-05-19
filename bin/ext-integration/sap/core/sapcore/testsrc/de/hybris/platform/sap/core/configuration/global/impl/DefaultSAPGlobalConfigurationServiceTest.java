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
package de.hybris.platform.sap.core.configuration.global.impl;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * DefaultSAPGlobalConfigurationServicetest.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultSAPGlobalConfigurationServiceTest-spring.xml" })
public class DefaultSAPGlobalConfigurationServiceTest extends SapcoreSpringJUnitTest
{

	/**
	 * The class under test.
	 */
	@Resource(name = "testDefaultSAPGlobalConfigurationService")
	private DefaultSAPGlobalConfigurationService classUnderTest;

	@SuppressWarnings("javadoc")
	@Test
	public void testGetPropertyValue()
	{
		assertEquals("test Entry", classUnderTest.getProperty("sapsflight.global.test"));
	}

}
