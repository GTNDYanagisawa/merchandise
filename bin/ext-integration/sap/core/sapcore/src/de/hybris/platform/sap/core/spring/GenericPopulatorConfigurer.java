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
package de.hybris.platform.sap.core.spring;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.sap.core.constants.SapcoreConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Generic populator configurer which allows to add additional populators to an existing converter.
 */
@SuppressWarnings("rawtypes")
public class GenericPopulatorConfigurer
{

	private AbstractPopulatingConverter converter = null;
	private final Collection<Populator> populatorCollection = new ArrayList<Populator>();

	/**
	 * Inject setter for {@link AbstractPopulatingConverter}.
	 * 
	 * @param converter
	 *           {@link AbstractPopulatingConverter} to set
	 */
	public void setConverter(final AbstractPopulatingConverter converter)
	{
		this.converter = converter;
	}

	/**
	 * Inject setter for populator.
	 * 
	 * @param populator
	 *           {@link Populator} to set
	 */
	public void setPopulator(final Populator populator)
	{
		this.populatorCollection.add(populator);
	}

	/**
	 * Inject setter for populator collection.
	 * 
	 * @param populatorCollection
	 *           list {@link Populator} to set
	 */
	public void setPopulators(final Collection<Populator> populatorCollection)
	{
		this.populatorCollection.addAll(populatorCollection);
	}


	/**
	 * Adds the populator to the converter's population list.
	 */
	@SuppressWarnings("unchecked")
	public void addPopulatorsToConverter()
	{
		// Check if Abstract Converter is available
		if (converter == null)
		{
			throw new CoreBaseRuntimeException("Converter exception: Converter not injected!");
		}
		List<Populator> populators = converter.getPopulators();
		if (populators == null)
		{
			populators = new ArrayList<Populator>();
		}

		populators.addAll(populatorCollection);
		converter.setPopulators(populators);
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreConstants.CRLF + "- Converter: " + this.converter.toString() + SapcoreConstants.CRLF
				+ "- Populators: " + this.populatorCollection.toString();
	}

}
