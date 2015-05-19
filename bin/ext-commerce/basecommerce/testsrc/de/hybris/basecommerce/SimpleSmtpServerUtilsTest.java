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
package de.hybris.basecommerce;

import de.hybris.basecommerce.SimpleSmtpServerUtils.SimpleSmtpServer;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.mail.MailUtils;

import java.util.Arrays;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.junit.Test;


@IntegrationTest
public class SimpleSmtpServerUtilsTest extends HybrisJUnit4Test
{
	private static final Logger LOG = Logger.getLogger(SimpleSmtpServerUtilsTest.class.getName());

	private static final int TEST_START_PORT = 7000;

	@Test
	public void testStartStop()
	{
		SimpleSmtpServer server = null;
		try
		{
			server = SimpleSmtpServerUtils.startServer(TEST_START_PORT);
			Assert.assertFalse(server.isStopped());
			Assert.assertTrue(server.getPort() > 0);

			server.stop();
			Assert.assertTrue(server.isStopped());
		}
		finally
		{
			if (server != null)
			{
				server.stop();
			}
		}
	}

	@Test
	public void testSendSuccess() throws EmailException, AddressException
	{
		final String origMailPortNumber = Config.getParameter(Config.Params.MAIL_SMTP_PORT);
		final String origMailHost = Config.getParameter(Config.Params.MAIL_SMTP_SERVER);

		SimpleSmtpServer server = null;
		try
		{
			server = SimpleSmtpServerUtils.startServer(TEST_START_PORT);
			Assert.assertFalse(server.isStopped());
			Assert.assertTrue(server.getPort() > 0);

			Config.setParameter(Config.Params.MAIL_SMTP_SERVER, "localhost");
			Config.setParameter(Config.Params.MAIL_SMTP_PORT, String.valueOf(server.getPort()));

			final Email email = MailUtils.getPreConfiguredEmail();

			email.setFrom("foo.bar@hybris.com");
			email.setTo(Arrays.asList(InternetAddress.parse("foo.bar@hybris.com")));
			email.setSubject("TEST TEST TEST");
			email.setContent("FOO", Email.TEXT_PLAIN);

			email.send();
		}
		finally
		{
			Config.setParameter(Config.Params.MAIL_SMTP_SERVER, origMailHost);
			Config.setParameter(Config.Params.MAIL_SMTP_PORT, origMailPortNumber);

			if (server != null)
			{
				server.stop();
			}
		}
	}


	@Test
	public void testFindNextPort()
	{
		SimpleSmtpServer server1 = null;
		SimpleSmtpServer server2 = null;
		SimpleSmtpServer server3 = null;
		SimpleSmtpServer server4 = null;
		try
		{
			server1 = SimpleSmtpServerUtils.startServer(TEST_START_PORT, true);
			Assert.assertFalse(server1.isStopped());
			Assert.assertTrue(server1.getPort() > 0);

			server2 = SimpleSmtpServerUtils.startServer(TEST_START_PORT, true);
			Assert.assertFalse(server2.isStopped());
			Assert.assertTrue(server2.getPort() > 0);
			Assert.assertFalse(server1.getPort() == server2.getPort());

			server3 = SimpleSmtpServerUtils.startServer(TEST_START_PORT, true);
			Assert.assertFalse(server3.isStopped());
			Assert.assertTrue(server3.getPort() > 0);
			Assert.assertFalse(server1.getPort() == server3.getPort());
			Assert.assertFalse(server2.getPort() == server3.getPort());

			// stop server 1
			final int server1Port = server1.getPort();
			server1.stop();
			Assert.assertTrue(server1.isStopped());
			server1 = null;

			// now port from server 1 should be free again
			server4 = SimpleSmtpServerUtils.startServer(TEST_START_PORT, true);
			Assert.assertFalse(server4.isStopped());
			if (server1Port != server4.getPort())
			{
				LOG.warn("unable to re-use port server1 port " + server1Port + " even though server1 was stopped");
			}
		}
		finally
		{
			if (server4 != null)
			{
				server4.stop();
			}
			if (server3 != null)
			{
				server3.stop();
			}
			if (server2 != null)
			{
				server2.stop();
			}
			if (server1 != null)
			{
				server1.stop();
			}
		}
	}

}
