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
 */
package de.hybris.platform.commercewebservicescommons.mapping.config;

import java.util.Map;


public class FieldSetLevelMapping
{
	private Class dtoClass;
	private Map<String, String> levelMapping;

	/**
	 * @return the dtoClass
	 */
	public Class getDtoClass()
	{
		return dtoClass;
	}

	/**
	 * @param dtoClass
	 *           the dtoClass to set
	 */
	public void setDtoClass(final Class dtoClass)
	{
		this.dtoClass = dtoClass;
	}

	/**
	 * @return the levelMapping
	 */
	public Map<String, String> getLevelMapping()
	{
		return levelMapping;
	}

	/**
	 * @param levelMapping
	 *           the levelMapping to set
	 */
	public void setLevelMapping(final Map<String, String> levelMapping)
	{
		this.levelMapping = levelMapping;
	}
}
