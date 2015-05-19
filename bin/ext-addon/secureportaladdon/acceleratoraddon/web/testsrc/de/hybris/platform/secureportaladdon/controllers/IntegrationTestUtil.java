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
package de.hybris.platform.secureportaladdon.controllers;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;


public class IntegrationTestUtil
{
	public static byte[] convertObjectToFormUrlEncodedBytes(final Object object)
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

		final Map<String, Object> propertyValues = mapper.convertValue(object, Map.class);

		final Set<String> propertyNames = propertyValues.keySet();
		final Iterator<String> nameIter = propertyNames.iterator();

		final StringBuilder formUrlEncoded = new StringBuilder();

		for (int index = 0; index < propertyNames.size(); index++)
		{
			final String currentKey = nameIter.next();
			final Object currentValue = propertyValues.get(currentKey);

			formUrlEncoded.append(currentKey);
			formUrlEncoded.append("=");
			formUrlEncoded.append(currentValue);

			if (nameIter.hasNext())
			{
				formUrlEncoded.append("&");
			}
		}

		return formUrlEncoded.toString().getBytes();
	}
}