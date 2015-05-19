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

package de.hybris.platform.sap.core.common.configurer.impl;

import de.hybris.platform.sap.core.common.configurer.ConfigurerEntitiesList;
import de.hybris.platform.sap.core.constants.SapcoreConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation for {@link de.hybris.platform.sap.core.common.configurer.ConfigurerEntitiesList}.
 * 
 * @param <T>
 */
@SuppressWarnings("javadoc")
public class ConfigurerEntitiesListImpl<T> implements ConfigurerEntitiesList<T>
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(ConfigurerEntitiesListImpl.class.getName());

	private final List<T> entities = new ArrayList<T>();

	@Override
	public void addEntity(final T entity)
	{
		entities.add(entity);
	}

	@Override
	public List<T> getEntities()
	{
		return entities;
	}

	@Override
	public String toString()
	{
		String toString = "";

		toString = super.toString() + SapcoreConstants.CRLF + "Parameters: " + SapcoreConstants.CRLF;

		for (final T entity : entities)
		{
			toString = toString + entity.toString();
		}

		return toString;
	}

}
