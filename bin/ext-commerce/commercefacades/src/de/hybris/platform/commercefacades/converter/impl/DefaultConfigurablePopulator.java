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
package de.hybris.platform.commercefacades.converter.impl;

import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.converters.Populator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Default implementation of the ConfigurablePopulator.
 */
public class DefaultConfigurablePopulator<SOURCE, TARGET, OPTION> implements ConfigurablePopulator<SOURCE, TARGET, OPTION>
{
	private LinkedHashMap<OPTION, Populator<SOURCE, TARGET>> populators;

	protected LinkedHashMap<OPTION, Populator<SOURCE, TARGET>> getPopulators()
	{
		return populators;
	}

	@Required
	public void setPopulators(final LinkedHashMap<OPTION, Populator<SOURCE, TARGET>> populators)
	{
		this.populators = populators;
	}

	@Override
	public void populate(final SOURCE source, final TARGET target, final Collection<OPTION> options)
	{
		Assert.notNull(source, "Converter source must not be null");
		Assert.notNull(target, "Converter target must not be null");

		if (options != null && !options.isEmpty())
		{
			final LinkedHashMap<OPTION, Populator<SOURCE, TARGET>> populatorMap = getPopulators();
			if (populatorMap != null && !populatorMap.isEmpty())
			{
				for (final Map.Entry<OPTION, Populator<SOURCE, TARGET>> entry : populatorMap.entrySet())
				{
					if (options.contains(entry.getKey()))
					{
						entry.getValue().populate(source, target);
					}
				}
			}
		}
	}
}
