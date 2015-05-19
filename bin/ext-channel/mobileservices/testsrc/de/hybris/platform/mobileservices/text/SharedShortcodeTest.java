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
import org.junit.Test;


public class SharedShortcodeTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(SharedShortcodeTest.class.getName());

	@Before
	public void createConfiguration() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata09.csv", "UTF-8");
	}

	/*
	 * test cases
	 * *** test shortcode 111 - shared with supersortcode
	 * tests msg received with supershortcode is processed
	 * tests msg received without supershortcode is dresponded as sorry msg
	 * *** test shortcode 222 - shared without supersortcode
	 * tests msg received with supershortcode is responded as sorry msg
	 * tests msg received without supershortcode is processed
	 */

	@Test
	public void testSharedKeyword() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "KEY Hi Ho");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);

	}

	@Test
	public void testSharedKeywordEmpty1() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "222", "34699111222", "Hi Ho");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);

	}

	@Test
	public void testSharedKeywordEmpty2() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "222", "34699111222", "KEY Hi Ho");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);

	}
}
