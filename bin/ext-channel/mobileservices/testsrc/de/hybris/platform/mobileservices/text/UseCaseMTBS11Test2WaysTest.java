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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class UseCaseMTBS11Test2WaysTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(UseCaseMTBS11Test2WaysTest.class.getName());

	@Before
	public void createConfiguration() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata07.csv", "UTF-8");
	}

	@Test
	public void testMOMT() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "777", "34699111222", " Hi Ho");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
	}

	@Test
	public void testMOMTOverMax() throws Exception
	{
		final String pk = messageReceived(
						"ES",
						"ES",
						"777",
						"34699111222",
						" Hi Ho 1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageStatus status = blockUsingModel(pk);
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(pk));
		assertFalse("HelloWorld should be discarted as input has more than default 140 characters",
				status == MobileMessageStatus.SENT);

	}

	@Test
	public void testLonelyKeyword() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
	}

	@Test
	public void testEmptyMessage() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "777", "34699111222", "");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}

	@Test
	public void testUnknownProcessor() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "XXX", "34699111222", "");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageStatus status = blockUsingModel(pk);
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(pk));
		assertTrue("HelloWorld shoukd have failed", status != MobileMessageStatus.SENT);
	}

}
