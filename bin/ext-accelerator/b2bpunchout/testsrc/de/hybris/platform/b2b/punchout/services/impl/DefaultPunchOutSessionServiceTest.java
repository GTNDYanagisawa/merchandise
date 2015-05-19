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
package de.hybris.platform.b2b.punchout.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionExpired;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Test cases for {@link DefaultPunchOutSessionService}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutSessionServiceTest
{

	private static final int TEN_MINUTES = 10 * 60 * 60 * 1000;

	@Mock
	private SessionService sessionService;

	@Mock
	private ConfigurationService configurationService;

	@InjectMocks
	private final DefaultPunchOutSessionService punchoutSessionService = new DefaultPunchOutSessionService();

	@Mock
	private Session session;

	@Mock
	private PunchOutSession punchoutSession;

	@Mock
	private Configuration configuration;

	/**
	 * Tests that the session will be expired when the timeout kicks in, having in mind the session has lived for longer
	 * than the timeout period.
	 * 
	 * @throws Exception
	 *            on error
	 */
	@Test(expected = PunchOutSessionExpired.class)
	public void testLoadExpiredSession() throws Exception
	{
		final Date referenceDate = new Date();
		final Date sessionDate = DateUtils.addMinutes(referenceDate, -2);

		when(sessionService.getSession("testId1")).thenReturn(session);
		when(session.getAttribute(DefaultPunchOutSessionService.PUNCHOUT_SESSION_KEY)).thenReturn(punchoutSession);
		when(punchoutSession.getTime()).thenReturn(sessionDate);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInteger(isA(String.class), isA(Integer.class))).thenReturn(Integer.valueOf(1));

		punchoutSessionService.load("testId1");
	}

	/**
	 * Tests that when the session is created and the timeout is later than the current time the session is still not
	 * expired.
	 * 
	 * @throws Exception
	 *            on error
	 */
	@Test
	public void testLoadNonExpiredSession() throws Exception
	{
		final Date sessionDate = new Date();

		when(sessionService.getSession("testId1")).thenReturn(session);
		when(sessionService.getCurrentSession()).thenReturn(session);
		when(session.getAttribute(DefaultPunchOutSessionService.PUNCHOUT_SESSION_KEY)).thenReturn(punchoutSession);
		when(punchoutSession.getTime()).thenReturn(sessionDate);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInteger(isA(String.class), isA(Integer.class))).thenReturn(Integer.valueOf(TEN_MINUTES));

		assertEquals("The punchout session is expected to be the one we set up in expectations", punchoutSession,
				punchoutSessionService.load("testId1"));
	}

}
