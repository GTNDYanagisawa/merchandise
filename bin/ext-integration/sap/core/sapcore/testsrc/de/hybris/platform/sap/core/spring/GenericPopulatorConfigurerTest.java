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
package de.hybris.platform.sap.core.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for GenericPopulatorConfigurer.
 */
@UnitTest
@ContextConfiguration(locations =
{ "GenericPopulatorConfigurerTest-spring.xml" })
public class GenericPopulatorConfigurerTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "sapCoreDefaultTestGenericPopulatorConfigurer")
	private GenericPopulatorConfigurer classUnderTest;

	@Resource(name = "sapCoreDefaultTestGenericPopulatorConfigurer2")
	private GenericPopulatorConfigurer classUnderTest2;

	/**
	 * Executes {@link GenericPopulatorConfigurer#addPopulatorsToConverter()} to add injected populator to injected
	 * converter and controls if it is contained in the converter afterwards.
	 */
	@Test
	public void testWithPopulator()
	{
		classUnderTest.addPopulatorsToConverter();
		final String s = getPopulatorName(classUnderTest.toString());
		assertEquals(TestConfigurationDataPopulator.class.getCanonicalName(), s);
	}

	/**
	 * Executes {@link GenericPopulatorConfigurer#addPopulatorsToConverter()} to try to add an empty populator-list to
	 * the converter and controls if there is no populator contained in the converted afterwards.
	 */
	@Test
	public void testWithoutPopulator()
	{
		classUnderTest2.addPopulatorsToConverter();
		final String s = getPopulatorName(classUnderTest2.toString());
		assertEquals("", s);
	}

	/**
	 * Extracts the class name of the first populator in the retrieved list from the toString method.
	 * 
	 * @param s
	 *           the String returned by the toString method.
	 * @return if the populator list is not empty, the class name from the list.<br/>
	 *         else the empty string.
	 */
	private String getPopulatorName(String s)
	{
		final String populator = "Populators: \\[";

		String[] arr = s.split(populator);
		assertTrue("toString() does not contain '" + populator + "'.", arr.length > 1);
		s = arr[arr.length - 1];

		if (s.contains("@"))
		{
			arr = s.split("@");
			assertTrue("toString() does not contain a '@'.", arr.length > 1);
			s = arr[0];
		}
		if (s.contains("]"))
		{
			s = s.replace("]", "");
			assertTrue("toString() does still contain a ']'.", !s.contains("]"));
		}
		return s;
	}
}
