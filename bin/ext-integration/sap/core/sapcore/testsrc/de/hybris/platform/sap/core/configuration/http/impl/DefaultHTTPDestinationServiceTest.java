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
package de.hybris.platform.sap.core.configuration.http.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.http.HTTPDestination;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for configuration provider.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultHTTPDestinationServiceTest-spring.xml" })
public class DefaultHTTPDestinationServiceTest extends SapcoreSpringJUnitTest
{

	/**
	 * The class under test.
	 */
	@Resource(name = "testHTTPDestinationService")
	private DefaultHTTPDestinationService classUnderTest;

	/**
	 * The name of the destination.
	 */
	private static final String destinationName = "testHttpDestinationName";

	@SuppressWarnings("javadoc")
	@Test
	public void testRFCDestinationAvailable()
	{
		assertNotNull(classUnderTest.getHTTPDestination(destinationName));
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testRFCDestinationProperties()
	{
		final HTTPDestination httpDestination = classUnderTest.getHTTPDestination(destinationName);
		assertEquals("testHttpDestinationName", httpDestination.getHttpDestinationName());
		assertEquals("testTargetURL", httpDestination.getTargetURL());
		assertEquals("testAuthenticationType", httpDestination.getAuthenticationType());
		assertEquals("testUserid", httpDestination.getUserid());
		assertEquals("testPassword", httpDestination.getPassword());
	}

}
