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
package de.hybris.platform.sap.core.configuration.rfc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for configuration provider.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultRFCDestinationServiceTest-spring.xml" })
public class DefaultRFCDestinationServiceTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "testRFCDestinationService")
	private DefaultRFCDestinationService classUnderTest;

	private static final String destinationName = "testRfcDestinationName";

	@SuppressWarnings("javadoc")
	@Test
	public void testRFCDestinationAvailable()
	{
		assertNotNull(classUnderTest.getRFCDestination(destinationName));
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testRFCDestinationProperties()
	{
		final RFCDestination rfcDestination = classUnderTest.getRFCDestination(destinationName);
		assertEquals("testRfcDestinationName", rfcDestination.getRfcDestinationName());
		assertEquals("testTargetHost", rfcDestination.getTargetHost());
		assertEquals("testUserid", rfcDestination.getUserid());
		assertEquals("testPassword", rfcDestination.getPassword());
	}

}
