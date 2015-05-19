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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populates {@link TriggerData} with {@link TriggerModel}.
 */
public class TriggerReversePopulator implements Populator<TriggerData, TriggerModel>
{
	@Override
	public void populate(final TriggerData data, final TriggerModel model) throws ConversionException
	{
		model.setDay(data.getDay() == null ? Integer.valueOf(-1) : data.getDay());
		model.setDaysOfWeek(data.getDaysOfWeek());
		model.setRelative(data.getRelative() == null ? Boolean.FALSE : data.getRelative());
		model.setWeekInterval(data.getWeekInterval() == null ? Integer.valueOf(-1) : data.getWeekInterval());
		model.setActivationTime(data.getActivationTime());
		model.setHour(data.getHour() == null ? Integer.valueOf(-1) : data.getHour());
		model.setMinute(data.getMinute() == null ? Integer.valueOf(-1) : data.getMinute());
	}
}
