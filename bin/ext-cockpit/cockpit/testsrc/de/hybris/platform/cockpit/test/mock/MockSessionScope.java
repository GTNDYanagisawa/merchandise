/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cockpit.test.mock;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;


public class MockSessionScope implements Scope
{
	private final Map<String, Object> scopeMap = new HashMap<String, Object>();

	@Override
	public Object get(final String bean, final ObjectFactory<?> factory)
	{
		Object object = scopeMap.get(bean);
		if (object == null)
		{
			object = factory.getObject();
			scopeMap.put(bean, object);
		}
		return object;
	}

	@Override
	public String getConversationId()
	{
		return null; // not supported
	}

	@Override
	public void registerDestructionCallback(final String arg0, final Runnable arg1)
	{
		// not supported
	}

	@Override
	public Object remove(final String bean)
	{
		return scopeMap.remove(bean);
	}

	@Override
	public Object resolveContextualObject(final String arg0)
	{
		//YTODO implement
		return null;
	}
}
