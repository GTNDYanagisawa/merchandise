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
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class UseCaseMTBS01FreeTextTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(UseCaseMTBS01FreeTextTest.class.getName());

	@Before
	public void createConfiguration() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata04.csv", "UTF-8");
	}

	@Test
	public void testFreeText() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", " Hi Ho");
		assertNotNull("Message could not be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
		assertTrue("Expected text not found", message.getOutgoingText().equals("hello"));
	}

	@Test
	public void testFreeLink() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Ho");
		assertNotNull("Message could not be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
		assertTrue("Expected text not found", message.getOutgoingSubject().equals("subject"));
	}
}
