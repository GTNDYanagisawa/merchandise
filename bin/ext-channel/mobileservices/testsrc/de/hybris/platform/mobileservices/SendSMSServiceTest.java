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
package de.hybris.platform.mobileservices;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.facade.SendSMSService;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * The Class TestShortUrlService.
 */
public class SendSMSServiceTest extends ServicelayerTest

{

	/** The short url service. */
	@Resource
	private SendSMSService smsService;

	private static final String TESTCOUNTRY = "es";
	private static final String TESTPHONE = "699999999";

	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(SendSMSServiceTest.class.getName()); // NOPMD by willy on 24/03/10 8:45

	@Before
	public void createConfiguration() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata07.csv", "UTF-8");
	}

	/**
	 * Test sends a message to the engineer
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void testSendSms() throws Exception
	{

		final boolean result = smsService.sendSms(TESTCOUNTRY, TESTPHONE, "this is a test sms message");
		assertTrue("SMS could not be sent", result);
		final boolean wrongresult = smsService.sendSms(TESTCOUNTRY, TESTPHONE + "9999999999", "test1");
		assertFalse("SMS should not have be sent", wrongresult);

	}

	/**
	 * Test sends a message link to the engineer
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void testSendSmsLink() throws Exception
	{

		final boolean result = smsService.sendLink(TESTCOUNTRY, TESTPHONE, "this is a test link", "http://www.google.com");
		assertTrue("SMS could not be sent", result);
		final boolean wrongresult = smsService.sendLink(TESTCOUNTRY, TESTPHONE + "9999999999", "go to google",
				"http://www.google.com");
		assertFalse("SMS should not have be sent", wrongresult);

	}
}
