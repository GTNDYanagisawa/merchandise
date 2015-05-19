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
package de.hybris.platform.sap.core.spring;

import de.hybris.platform.sap.core.runtime.SAPHybrisSession;
import de.hybris.platform.sap.core.runtime.SAPHybrisSessionProvider;
import de.hybris.platform.sap.core.runtime.SessionObjectFactory;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;


/**
 * Spring scope handler for custom scope <code>sapSession</code> implementing the Spring {@link Scope} interface.
 * <p>
 * This scope keeps the according beans within the lifetime of the {@link SAPHybrisSession}.
 * </p>
 */
public class SAPSessionScopeHandler implements Scope
{

	private SAPHybrisSessionProvider sapHybrisSessionProvider;

	@Override
	public Object get(final String name, final ObjectFactory<?> objectFactory)
	{
		return getSessionObjectFactory().getSessionObject(name, objectFactory);
	}

	@Override
	public Object remove(final String name)
	{
		return getSessionObjectFactory().removeSessionObject(name);
	}

	@Override
	public void registerDestructionCallback(final String name, final Runnable callback)
	{
		getSessionObjectFactory().registerDestroyObject(name, callback);
	}

	@Override
	public Object resolveContextualObject(final String key)
	{
		return null;
	}

	@Override
	public String getConversationId()
	{
		return sapHybrisSessionProvider.getSAPHybrisSession().getSessionId();
	}

	/**
	 * Sets the session object factory.
	 * 
	 * @param sapHybrisSessionProvider
	 *           the sapHybrisSessionProvider to set
	 */
	public void setSapHybrisSessionProvider(final SAPHybrisSessionProvider sapHybrisSessionProvider)
	{
		this.sapHybrisSessionProvider = sapHybrisSessionProvider;
	}

	/**
	 * Gets the session object factory.
	 * 
	 * @return Session object factory
	 * 
	 */
	private SessionObjectFactory getSessionObjectFactory()
	{
		return sapHybrisSessionProvider.getSAPHybrisSession().getSessionObjectFactory();
	}

}
