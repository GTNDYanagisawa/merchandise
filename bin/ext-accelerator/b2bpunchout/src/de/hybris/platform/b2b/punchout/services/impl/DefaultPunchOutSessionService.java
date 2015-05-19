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

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionExpired;
import de.hybris.platform.b2b.punchout.PunchOutSessionNotFoundException;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PunchOutSessionService} based on the use of {@link SessionService}.
 */
public class DefaultPunchOutSessionService implements PunchOutSessionService
{
	protected static final String PUNCHOUT_SESSION_KEY = "punchoutSession";

	private SessionService sessionService;

	private ConfigurationService configurationService;

	@Override
	public void activate(final PunchOutSession punchoutSession)
	{
		sessionService.setAttribute(PUNCHOUT_SESSION_KEY, punchoutSession);
	}

	@Override
	public PunchOutSession load(final String punchoutSessionId) throws PunchOutSessionNotFoundException, PunchOutSessionExpired
	{
		Session session = null;
		try
		{
			session = sessionService.getSession(punchoutSessionId);
		}
		catch (final NullPointerException e)
		{
			throw new PunchOutSessionNotFoundException("Session could not be retrieved.");
		}
		if (session == null)
		{
			throw new PunchOutSessionNotFoundException("Session not found");
		}

		final PunchOutSession punchoutSession = session.getAttribute(PUNCHOUT_SESSION_KEY);

		if (punchoutSession == null)
		{
			throw new PunchOutSessionNotFoundException("PunchOut session not found");
		}

		if (new Date().after(calculateCutOutTime(punchoutSession.getTime())))
		{
			throw new PunchOutSessionExpired("PunchOut session has expired");
		}
		sessionService.getCurrentSession().setAttribute(PUNCHOUT_SESSION_KEY, punchoutSession);

		return punchoutSession;
	}

	@Override
	public void setCurrentCartFromPunchOutSetup(final String punchoutSessionId)
	{
		try
		{
			final Session session = sessionService.getSession(punchoutSessionId);
			final Session currentSession = sessionService.getCurrentSession();
			if (!StringUtils.equals(punchoutSessionId, currentSession.getSessionId()))
			{
				final CartModel cart = session.getAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME);
				// add old cart to current session
				currentSession.setAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME, cart);
			}
		}
		catch (final NullPointerException e)
		{
			throw new PunchOutSessionNotFoundException("Session could not be retrieved.");
		}
	}

	/**
	 * @param sessionCreationDate
	 *           the creating time of the punchout session
	 * @return the time the session should have expired
	 */
	private Date calculateCutOutTime(final Date sessionCreationDate)
	{
		final int timeoutDuration = 5;
		return DateUtils.addMilliseconds(sessionCreationDate,
				configurationService.getConfiguration().getInteger("b2bpunchout.timeout", Integer.valueOf(timeoutDuration)));
	}

	@Override
	public String getCurrentPunchOutSessionId()
	{
		return sessionService.getCurrentSession().getSessionId();
	}

	@Override
	public PunchOutSession getCurrentPunchOutSession()
	{
		return sessionService.getCurrentSession().getAttribute(PUNCHOUT_SESSION_KEY);
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

}
