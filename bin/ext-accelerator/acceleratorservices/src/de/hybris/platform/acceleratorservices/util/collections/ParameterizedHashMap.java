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
package de.hybris.platform.acceleratorservices.util.collections;

import java.util.HashMap;


/**
 *
 */
public class ParameterizedHashMap<K, V> extends HashMap<K, V>
{
	public String getMessage(final K key, final V... parameters)
	{
		final Object initValue = get(key);

		String message = String.valueOf(initValue);
		if (parameters != null && parameters.length > 0)
		{
			for (int counter = 0; counter < parameters.length; counter++)
			{
				message = message.replace("{" + counter + "}", String.valueOf(parameters[counter]).trim());
			}
		}

		// Return the property name if no message found so it can be looked up.
		if (message == null || message.equalsIgnoreCase("null"))
		{
			message = String.valueOf("${" + key + "}");
		}
		return message;
	}
}
