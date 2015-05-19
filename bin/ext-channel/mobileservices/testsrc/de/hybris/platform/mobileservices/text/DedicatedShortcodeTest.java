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


public class DedicatedShortcodeTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(DedicatedShortcodeTest.class.getName());

	@Before
	public void createConfiguration1() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata09.csv", "UTF-8");
	}

	@Test
	public void testDedicatedKeyword() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "333", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
	}

	@Test
	public void testDedicatedKeywordBlank() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "333", "34699111222", "");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}

	@Test
	public void testDedicatedKeywordUnsupported() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "333", "34699111222", "Blah bleh");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}

	@Test
	public void testDedicatedWithKeywordPrependingDefaultService() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "444", "34699111222", "key");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}

	@Test
	public void testDedicatedWithKeywordPrependingUnsupported() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "444", "34699111222", "bash");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}

	@Test
	public void testDedicatedWithoutKeywordPrepending1() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "555", "34699111222", "Hi Ho");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
	}

	@Test
	public void testDedicatedWithoutKeywordPrependingUnsupported() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "555", "34699111222", "key");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}

	@Test
	public void testDedicatedWithKeywordPrefixAlone() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "key");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}

	@Test
	public void testDedicatedWithKeywordPrependingWithoutDefaultService() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "666", "34699111222", "key");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsDefaultAction);
	}
}
