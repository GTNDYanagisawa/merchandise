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
package de.hybris.platform.sap.core.jco;


import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationPingEvent;
import de.hybris.platform.sap.core.jco.service.impl.DefaultSAPRFCDestinationService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Tests for the Sapcorejco extension.
 *
 * Test prerequisites:
 * - SAP Cryptographic Library is installed and configured
 * - Environment Variable SECUDIR set to SNC sec directory
 * - Environment Variable SNC_LIB set to SNC directory
 * - SNC_TEST_DEST.jcoDestination exist with the parameters of the AS ABAP used for SNC test.
 *
 */
@IntegrationTest
public class SapcorejcoSNCTest extends ServicelayerTransactionalTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	private static final Logger LOG = Logger.getLogger(SapcorejcoSNCTest.class.getName());

	@Before
	@SuppressWarnings("javadoc")
	public void setUp()
	{
		// implement here code executed before each test
	}

	@After
	@SuppressWarnings("javadoc")
	public void tearDown()
	{
		// implement here code executed after each test
	}

	@Test
	@SuppressWarnings("javadoc")
	public void testPingDestWithSnc() {

		LOG.info("The test uses local destination file SNC_TEST_DEST.jcoDestination. The SNC library path is specified there.");

		// Destination SNC_TEST_DEST must be provided as file SNC_TEST_DEST.jcoDestination!!!
		final SAPRFCDestinationPingEvent event = new SAPRFCDestinationPingEvent("SNC_TEST_DEST");
		getEventService().publishEvent(event);

	    if (event.getResultIndicator() == 0) {
	    	LOG.info("SAP RFC SNC Destination Ping Test Result: " + event.getMessage());
	    } else {
	        LOG.error("SAP RFC SNC Destination Ping Test Result: " + event.getMessage());
	    }

		assertTrue(event.getResultIndicator()!= 1);

	}

	@Test
	@SuppressWarnings("javadoc")
	public void testSetSncLibPath2EnvVar() {

		final DefaultSAPRFCDestinationService classUnderTest = getDestinationService();

		LOG.info("Value of the SncLibraryPath of the destination service: "
				+ classUnderTest.getSncLibraryPath());
		LOG.info("Value of environment variable SNC_LIB: " + System.getenv("SNC_LIB"));

		if (System.getenv("SNC_LIB") == null)
		{
			// skip the test; test environment is not quite well
			LOG.info("Check the setup of enviroment variable SNC_LIB");
			return;
		}

		if (classUnderTest.getSncLibraryPath() != null)
		{
			assertTrue(classUnderTest.getSncLibraryPath().equals(System.getenv("SNC_LIB")));
		}
		else
		{
			assertTrue(false);
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void testSetSncLibPath2SpecificValue() {

		final String sncLibraryPath = "D:\\tmp";

		final DefaultSAPRFCDestinationService classUnderTest = getDestinationService();

		// Remind the current assignment of the SNC library path
		final String actualSncLibraryPath = classUnderTest.getSncLibraryPath();

		// Perform the test, set new library path and check
		classUnderTest.setSncLibraryPath(sncLibraryPath);

		assertTrue(classUnderTest.getSncLibraryPath().equals(sncLibraryPath));

		// reset to old reminded value
		classUnderTest.setSncLibraryPath(actualSncLibraryPath);

	}

	@SuppressWarnings("javadoc")
	private EventService getEventService()
	{
		return (EventService) ServicelayerUtils.getApplicationContext().getBean("eventService");
	}

	@SuppressWarnings("javadoc")
	private DefaultSAPRFCDestinationService getDestinationService() {

		return (DefaultSAPRFCDestinationService) ServicelayerUtils.getApplicationContext().getBean("sapCoreDefaultSAPRFCDestinationService");
	}

}
