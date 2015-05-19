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
import static org.junit.Assert.assertFalse;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class ActivationOfAggregatorsAndShortcodesTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(ActivationOfAggregatorsAndShortcodesTest.class.getName());

	@Before
	public void createConfiguration1() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata14.csv", "UTF-8");
	}

	@Test
	public void testAggregatorActiveShortcodeActive() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
	}

	@Test
	public void testAggregatorActiveShortcodeOff() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "222", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageStatus status = blockUsingModel(pk);
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(pk));
		assertFalse("Message should not be sent", status == MobileMessageStatus.SENT);
	}

	@Test
	public void testAggregatorOffShortcodeOn() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "333", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageStatus status = blockUsingModel(pk);
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(pk));
		assertFalse("Message should not be sent", status == MobileMessageStatus.SENT);
	}

	@Test
	public void testAggregatorOffShortcodeOff() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "444", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageStatus status = blockUsingModel(pk);
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(pk));
		assertFalse("Message should not be sent", status == MobileMessageStatus.SENT);
	}

}
