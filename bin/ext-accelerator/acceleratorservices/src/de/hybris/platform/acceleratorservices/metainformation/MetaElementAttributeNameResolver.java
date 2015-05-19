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
package de.hybris.platform.acceleratorservices.metainformation;

import java.util.Map;


public class MetaElementAttributeNameResolver
{
	protected Map<String, String> mappedNames;

	public void setMappedNames(final Map<String, String> mappedNames)
	{
		this.mappedNames = mappedNames;
	}

	public String resolveName(final String fieldName)
	{
		if (mappedNames != null && mappedNames.containsKey(fieldName))
		{
			return mappedNames.get(fieldName);
		}
		return fieldName;
	}
}
