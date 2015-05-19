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
package de.hybris.platform.mobileservices.text;

import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class FailureRecoveryTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(FailureRecoveryTest.class.getName());

	@Before
	public void createConfiguration() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata03.csv", "UTF-8");
	}

	public MobileMessageContextModel hellowWorldTest(final String shortcode, final String msg)
	{

		final String pk = messageReceived("ES", "ES", shortcode, "34699111222", msg);
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
		return message;
	}

	@Test
	public void testErroInDelivery() throws Exception
	{
		hellowWorldTest("111", "hi");
	}

	@Test
	// AG: since we do not wait for actions there is not 'retry' when 
	//receiving except that a action may do this on its own !!!
	@Ignore("MOBILE-59")
	public void testErrorInProcessing() throws Exception
	{
		hellowWorldTest("222", "Ho");
	}
}
