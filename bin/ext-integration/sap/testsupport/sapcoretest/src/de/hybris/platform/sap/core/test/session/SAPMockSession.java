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
package de.hybris.platform.sap.core.test.session;

import de.hybris.platform.servicelayer.session.impl.DefaultSession;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Copy of MockSession enhanced by removeAttribute method.
 */
public class SAPMockSession extends DefaultSession
{
	private static final long serialVersionUID = 3949657850392397912L;

	private long sessionIdCounter = 1;

	private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
	private final String sessionId;

	/**
	 * Standard constructor.
	 */
	public SAPMockSession()
	{
		super();
		this.sessionId = String.valueOf(sessionIdCounter++);
	}

	@Override
	public String getSessionId()
	{
		return this.sessionId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<String, T> getAllAttributes()
	{
		return (Map<String, T>) Collections.unmodifiableMap(attributes);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(final String name)
	{
		return (T) attributes.get(name);
	}


	@Override
	public void setAttribute(final String name, final Object value)
	{
		attributes.put(name, value);
	}

	@Override
	public void removeAttribute(final String name)
	{
		attributes.remove(name);
	}

}
