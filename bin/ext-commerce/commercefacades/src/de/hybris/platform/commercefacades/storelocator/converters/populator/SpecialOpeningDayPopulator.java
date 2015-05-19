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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.platform.commercefacades.storelocator.data.SpecialOpeningDayData;
import de.hybris.platform.storelocator.model.SpecialOpeningDayModel;

import java.text.DateFormat;


public class SpecialOpeningDayPopulator extends OpeningDayPopulator<SpecialOpeningDayModel, SpecialOpeningDayData>
{

	@Override
	public void populate(final SpecialOpeningDayModel source, final SpecialOpeningDayData target)
	{
		populateBase(source, target);
		target.setClosed(source.isClosed());
		target.setName(source.getName());
		target.setComment(source.getMessage());
		if (source.getDate() != null)
		{
			target.setDate(source.getDate());
			target.setFormattedDate(DateFormat.getDateInstance(getDateFormatStyle(), getCurrentLocale()).format(source.getDate()));
		}
	}

	protected int getDateFormatStyle()
	{
		return DateFormat.SHORT;
	}
}
