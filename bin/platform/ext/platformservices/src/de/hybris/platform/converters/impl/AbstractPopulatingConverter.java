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
package de.hybris.platform.converters.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.PopulatorList;

import java.util.List;


/**
 * Populating converter that uses a list of configured populators to populate the target during conversion.
 */
public abstract class AbstractPopulatingConverter<SOURCE, TARGET> extends AbstractConverter<SOURCE, TARGET> implements
		PopulatorList<SOURCE, TARGET>
{
	private List<Populator<SOURCE, TARGET>> populators;

	@Override
	public List<Populator<SOURCE, TARGET>> getPopulators()
	{
		return populators;
	}

	@Override
	public void setPopulators(final List<Populator<SOURCE, TARGET>> populators)
	{
		this.populators = populators;
	}

	/**
	 * Populate the target instance from the source instance. Calls a list of injected populators to populate the
	 * instance.
	 * 
	 * @param source
	 *           the source item
	 * @param target
	 *           the target item to populate
	 */
	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		final List<Populator<SOURCE, TARGET>> list = getPopulators();
		if (list != null)
		{
			for (final Populator<SOURCE, TARGET> populator : list)
			{
				if (populator != null)
				{
					populator.populate(source, target);
				}
			}
		}
	}
}
