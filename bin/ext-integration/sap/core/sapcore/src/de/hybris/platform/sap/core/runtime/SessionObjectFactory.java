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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;


/**
 * Factory for session objects for beans defined with scope <code>sapSession</code>.
 */
public class SessionObjectFactory
{
	private static final Logger log = Logger.getLogger(SessionObjectFactory.class.getName());

	private final Map<String, Object> sessionObjectMap = new ConcurrentHashMap<String, Object>();
	private final Map<String, Runnable> destructionCallbacks = new ConcurrentHashMap<String, Runnable>();

	/**
	 * Gets session object from factory and creates it if necessary.
	 * 
	 * @param beanName
	 *           Id or alias of the bean
	 * @param objectFactory
	 *           Object factory to be used
	 * @return Object Bean reference
	 */
	public synchronized Object getSessionObject(final String beanName, final ObjectFactory<?> objectFactory)
	{
		if (!sessionObjectMap.containsKey(beanName))
		{
			final Object object = objectFactory.getObject();
			sessionObjectMap.put(beanName, object);
		}
		return sessionObjectMap.get(beanName);
	}

	/**
	 * Removes a session bean.
	 * 
	 * @param beanName
	 *           Id or alias of the bean
	 * @return Bean reference which has been removed
	 */
	public Object removeSessionObject(final String beanName)
	{
		return sessionObjectMap.remove(beanName);
	}

	/**
	 * Destroy business object factory and hereby all objects.
	 */
	public synchronized void destroy()
	{
		try
		{
			// Call destroy-methods of all objects registered in
			// destructionCallbacks map
			performDestructionCallbacks();
			sessionObjectMap.clear();
		}
		catch (final Exception e)
		{
			log.error(e.toString());
		}
	}

	/**
	 * Registers bean for destroy-method call if declared with Spring.
	 * 
	 * @param beanName
	 *           Id or alias of the bean
	 * @param callback
	 *           Destruction callback
	 * 
	 * @see org.springframework.beans.factory.config.Scope#registerDestructionCallback (java.lang.String,
	 *      java.lang.Runnable)
	 */
	public void registerDestroyObject(final String beanName, final Runnable callback)
	{
		destructionCallbacks.put(beanName, callback);
	}

	/**
	 * Calls all destroy-methods declared with Spring.
	 */
	private void performDestructionCallbacks()
	{
		for (final Map.Entry<String, Runnable> entry : destructionCallbacks.entrySet())
		{
			try
			{
				entry.getValue().run();
			}
			catch (final Exception ex)
			{
				// Do only log and continue with destruction of remaining beans
				log.fatal("Exceptions occurred during destroy!", ex);
			}
		}
		destructionCallbacks.clear();
	}

}
