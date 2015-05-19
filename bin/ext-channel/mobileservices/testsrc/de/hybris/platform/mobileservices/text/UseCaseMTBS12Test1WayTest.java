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
import static org.junit.Assert.assertEquals;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class UseCaseMTBS12Test1WayTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(UseCaseMTBS12Test1WayTest.class.getName());

	@Resource
	private TextService textService;

	@Before
	public void createConfiguration() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata07.csv", "UTF-8");
	}

	@Test
	public void testMT() throws Exception
	{
		final MobileMessageContextModel msg = textService.sendMessage("ES", "699111222", " Hi Ho");

		assertNotNull("Message count bot be passed to the reception layer", msg);
		LOG.info("Got msg " + msg);
		final MobileMessageStatus status = blockUsingModel(msg.getPk().toString());
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(msg));
		assertEquals("Send MT failed", status, MobileMessageStatus.SENT);
	}

	@Test
	public void testMTOverMax() throws Exception
	{
		final MobileMessageContextModel msg = textService
				.sendMessage(
						"ES",
						"699111222",
						" Hi Ho 012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");

		assertNotNull("Message count bot be passed to the reception layer", msg);
		LOG.info("Got msg " + msg);
		final MobileMessageStatus status = blockUsingModel(msg.getPk().toString());
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(msg));
		assertFalse("Send MT should have failed", status == MobileMessageStatus.SENT);
	}

	@Test
	public void testMTLink() throws Exception
	{
		final MobileMessageContextModel msg = textService.sendLink("ES", "699111222", " Hi Ho", "http://google.com");

		assertNotNull("Message count bot be passed to the reception layer", msg);
		LOG.info("Got msg " + msg);
		final MobileMessageStatus status = blockUsingModel(msg.getPk().toString());
		LOG.info("Got status " + status);
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(msg));
		assertEquals("Send MT failed", status, MobileMessageStatus.SENT);
	}

	@Test
	public void testMTLinkOverMax() throws Exception
	{
		final MobileMessageContextModel msg = textService
				.sendLink(
						"ES",
						"699111222",
						" Hi Ho 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789",
						"http://google.com");

		assertNotNull("Message count bot be passed to the reception layer", msg);
		LOG.info("Got pk " + msg);
		final MobileMessageStatus status = blockUsingModel(msg.getPk().toString());
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(msg));
		assertFalse("Send MT should have failed", status == MobileMessageStatus.SENT);
	}
}
