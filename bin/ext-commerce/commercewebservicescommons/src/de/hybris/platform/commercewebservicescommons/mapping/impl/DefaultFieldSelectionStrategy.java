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

package de.hybris.platform.commercewebservicescommons.mapping.impl;

import de.hybris.platform.commercewebservicescommons.mapping.DataMapper;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSelectionStrategy;

import java.util.Set;

import ma.glasnost.orika.MappingContext;


/**
 * Default implementation of {@link de.hybris.platform.commercewebservicescommons.mapping.FieldSelectionStrategy}
 */
public class DefaultFieldSelectionStrategy implements FieldSelectionStrategy
{
	@Override
	public boolean shouldMap(final Object source, final Object dest, final MappingContext mappingContext)
	{
		if (mappingContext.getProperty(DataMapper.MAP_NULLS) != null)
		{
			final boolean mapNulls = (boolean) mappingContext.getProperty(DataMapper.MAP_NULLS);
			if (!mapNulls && source == null)
			{
				return false;
			}
		}
		if (mappingContext.getProperty(DataMapper.FIELD_SET_NAME) != null)
		{
			final Set<String> config = (Set<String>) mappingContext.getProperty(DataMapper.FIELD_SET_NAME);
			if (config.contains(mappingContext.getFullyQualifiedDestinationPath()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
}
