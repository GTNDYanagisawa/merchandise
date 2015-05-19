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
package de.hybris.platform.commerceservices.spring.config;

import java.util.HashMap;
import java.util.Map;


public class MultipleMapMergeBean
{
	private Map<String, String> propertyMap;
	private Map<String, String> fieldMap; // NOPMD: The variable fieldMap is required for the tests.

	public MultipleMapMergeBean()
	{
		propertyMap = new HashMap<String, String>();
		fieldMap = new HashMap<String, String>();
	}

	public Map<String, String> getPropertyMap()
	{
		return propertyMap;
	}

	public void setPropertyMap(final Map<String, String> propertyMap)
	{
		this.propertyMap = propertyMap;
	}

	public void setFieldMap(final Map<String, String> fieldMap)
	{
		this.fieldMap = fieldMap;
	}
}