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
package de.hybris.platform.sap.core.runtime;

import de.hybris.platform.regioncache.ConcurrentHashSet;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.servicelayer.event.events.BeforeSessionCloseEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * SAP Hybris Session Provider.
 */
public class SAPHybrisSessionProvider extends AbstractEventListener<BeforeSessionCloseEvent>
{

	/**
	 * Alias of the SAP hybris session.
	 */
	public static String SAP_HYBRIS_SESSION_ALIAS = "sapCoreHybrisSession"; //NOPMD


	/**
	 * Session attribute to store the SAP hybris session.
	 */
	public static String SAP_HYBRIS_SESSION_ATTRIBUTE = "SAPHybrisSession"; //NOPMD

	/**
	 * hybris {@link SessionService}.
	 */
	protected SessionService sessionService; //NOPMD

	/**
	 * {@link GenericFactory} reference.
	 */
	protected GenericFactory genericFactory; //NOPMD

	/**
	 * Set of listeners which listens to the {@link SAPHybrisSessionEventListener} interface.
	 */
	protected Set<SAPHybrisSessionEventListener> eventListener = new ConcurrentHashSet<SAPHybrisSessionEventListener>(); //NOPMD

	/**
	 * Session which are about to be deleted.
	 */
	protected final Map<Session, SAPHybrisSession> sessionsToBeDeleted = new ConcurrentHashMap<Session, SAPHybrisSession>(); //NOPMD

	/**
	 * Injection setter for the hybris {@link SessionService}.
	 * 
	 * @param sessionService
	 *           hybris {@link SessionService}
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Injection setter for the {@link GenericFactory}.
	 * 
	 * @param genericFactory
	 *           the {@link GenericFactory} to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * Gets the current {@link SAPHybrisSession}.
	 * 
	 * @return {@link SAPHybrisSession}
	 */
	public SAPHybrisSession getSAPHybrisSession()
	{
		return getSAPHybrisSessionInternal();
	}

	/**
	 * Destroys the current {@link SAPHybrisSession}.
	 */
	public void destroySAPHybrisSession()
	{
		destroySAPHybrisSessionInternal(sessionService.getCurrentSession());
	}


	/**
	 * Checks if a {@link SAPHybrisSession} exists within the hybris {@link Session}.
	 * 
	 * @return true, if session exists
	 */
	public boolean existsSAPHybrisSession()
	{
		final Session defaultSession = sessionService.getCurrentSession();
		SAPHybrisSession sapHybrisSession = defaultSession.getAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		if (sapHybrisSession == null)
		{
			// Check if this is a session to be deleted
			sapHybrisSession = sessionsToBeDeleted.get(defaultSession);
		}
		return (sapHybrisSession != null);
	}

	/**
	 * Registers a {@link SAPHybrisSessionEventListener}.
	 * 
	 * @param listener
	 *           Event listener
	 */
	public void registerEventListener(final SAPHybrisSessionEventListener listener)
	{
		eventListener.add(listener);
	}

	/**
	 * Unregisters a {@link SAPHybrisSessionEventListener}.
	 * 
	 * @param listener
	 *           Event listener
	 */
	public void unregisterEventListener(final SAPHybrisSessionEventListener listener)
	{
		eventListener.remove(listener);
	}

	@Override
	protected void onEvent(final BeforeSessionCloseEvent event)
	{
		// BeforeSessionCloseEvent
		if (event instanceof BeforeSessionCloseEvent)
		{
			final Session toBeClosedSession = sessionService.getBoundSession(event.getSource());
			destroySAPHybrisSessionInternal(toBeClosedSession);
		}
	}

	/**
	 * Creates the {@link SAPHybrisSession} if it does not exist yet and returns it.
	 * 
	 * @return {@link SAPHybrisSession}
	 */
	protected SAPHybrisSession getSAPHybrisSessionInternal()
	{
		final Session defaultSession = sessionService.getCurrentSession();
		SAPHybrisSession sapHybrisSession = defaultSession.getAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		if (sapHybrisSession == null)
		{
			if (sessionsToBeDeleted.containsKey(defaultSession))
			{
				return sessionsToBeDeleted.get(defaultSession);
			}
			sapHybrisSession = (SAPHybrisSession) genericFactory.getBean(SAP_HYBRIS_SESSION_ALIAS);
			sapHybrisSession.setSessionId(defaultSession.getSessionId());
			defaultSession.setAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE, sapHybrisSession);
		}
		return sapHybrisSession;
	}

	/**
	 * Destroys the current {@link SAPHybrisSession} if exists.
	 * 
	 * @param session
	 *           hybris {@link Session}
	 */
	protected void destroySAPHybrisSessionInternal(final Session session)
	{
		final SAPHybrisSession sapHybrisSession = (SAPHybrisSession) session.getAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		if (sapHybrisSession != null)
		{
			sessionsToBeDeleted.put(sessionService.getCurrentSession(), sapHybrisSession);
			sapHybrisSession.destroy();
			sessionsToBeDeleted.remove(sessionService.getCurrentSession());
			for (final SAPHybrisSessionEventListener sapHybrisSessionEventListener : eventListener)
			{
				sapHybrisSessionEventListener.onAfterDestroy(sapHybrisSession);
			}
			session.removeAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		}
	}

}
