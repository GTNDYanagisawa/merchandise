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




/**
 * SAP hybris Session container which keeps the session relevant information for the SAP integration.
 */
public class SAPHybrisSession
{

	private SessionObjectFactory sessionObjectFactory;
	private String applicationName;
	private String sessionId;

	/**
	 * Default constructor.
	 */
	public SAPHybrisSession()
	{
		super();
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the sessionId
	 */
	public String getSessionId()
	{
		return sessionId;
	}

	/**
	 * Sets the session id.
	 * 
	 * @param sessionId
	 *           the sessionId to set
	 */
	public void setSessionId(final String sessionId)
	{
		this.sessionId = sessionId;
	}

	/**
	 * Sets the session object factory.
	 * 
	 * @param sessionObjectFactory
	 *           Session object factory
	 */
	public void setSessionObjectFactory(final SessionObjectFactory sessionObjectFactory)
	{
		this.sessionObjectFactory = sessionObjectFactory;
	}

	/**
	 * Gets the session object factory.
	 * 
	 * @return Session object factory
	 */
	public SessionObjectFactory getSessionObjectFactory()
	{
		return sessionObjectFactory;
	}

	/**
	 * Gets the application name.
	 * 
	 * @return Application name
	 */
	public String getApplicationName()
	{
		return applicationName;
	}

	/**
	 * Sets the application name.
	 * 
	 * @param applicationName
	 *           Application name to set
	 */
	public void setApplicationName(final String applicationName)
	{
		this.applicationName = applicationName;
	}

	/**
	 * Frees up session relevant information.
	 */
	public void destroy()
	{
		sessionObjectFactory.destroy();
	}

}
