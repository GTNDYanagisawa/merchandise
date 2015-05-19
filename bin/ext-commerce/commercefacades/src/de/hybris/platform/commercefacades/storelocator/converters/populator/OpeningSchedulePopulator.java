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

import de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData;
import de.hybris.platform.commercefacades.storelocator.data.SpecialOpeningDayData;
import de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.storelocator.model.OpeningDayModel;
import de.hybris.platform.storelocator.model.OpeningScheduleModel;
import de.hybris.platform.storelocator.model.SpecialOpeningDayModel;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Required;


public class OpeningSchedulePopulator implements Populator<OpeningScheduleModel, OpeningScheduleData>
{
	private TimeService timeService;
	private Converter<SpecialOpeningDayModel, SpecialOpeningDayData> specialDayConverter;
	private Converter<List<WeekdayOpeningDayModel>, List<WeekdayOpeningDayData>> weekDaysConverter;

	protected Converter<List<WeekdayOpeningDayModel>, List<WeekdayOpeningDayData>> getWeekDaysConverter()
	{
		return weekDaysConverter;
	}

	@Required
	public void setWeekDaysConverter(final Converter<List<WeekdayOpeningDayModel>, List<WeekdayOpeningDayData>> weekDaysConverter)
	{
		this.weekDaysConverter = weekDaysConverter;
	}

	protected Converter<SpecialOpeningDayModel, SpecialOpeningDayData> getSpecialDayConverter()
	{
		return specialDayConverter;
	}

	@Required
	public void setSpecialDayConverter(final Converter<SpecialOpeningDayModel, SpecialOpeningDayData> specialDayConverter)
	{
		this.specialDayConverter = specialDayConverter;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	@Override
	public void populate(final OpeningScheduleModel source, final OpeningScheduleData target)
	{
		target.setCode(source.getCode());
		target.setName(source.getName());

		final Date currentTime = getTimeService().getCurrentTime();
		final Calendar specialDayCutOff = Calendar.getInstance();
		specialDayCutOff.setTime(currentTime);
		specialDayCutOff.set(Calendar.MILLISECOND, 0);
		specialDayCutOff.set(Calendar.SECOND, 0);
		specialDayCutOff.set(Calendar.MINUTE, 0);
		specialDayCutOff.set(Calendar.HOUR_OF_DAY, 0);
		specialDayCutOff.add(Calendar.DATE, -1);
		final Date specialDayCutOffDate = specialDayCutOff.getTime();

		final List<WeekdayOpeningDayModel> weekDaysOpening = new ArrayList<WeekdayOpeningDayModel>();
		final Map<Date, SpecialOpeningDayData> specialDaysOpening = new TreeMap<Date, SpecialOpeningDayData>();
		for (final OpeningDayModel singleOpeningEntry : source.getOpeningDays())
		{
			if (singleOpeningEntry instanceof WeekdayOpeningDayModel)
			{
				weekDaysOpening.add((WeekdayOpeningDayModel) singleOpeningEntry);
			}

			if (singleOpeningEntry instanceof SpecialOpeningDayModel)
			{
				final SpecialOpeningDayModel singleSpecialOpeningEntry = (SpecialOpeningDayModel) singleOpeningEntry;

				// Filter out any special days before the cut off date
				if (singleSpecialOpeningEntry.getDate() != null && !singleSpecialOpeningEntry.getDate().before(specialDayCutOffDate))
				{
					specialDaysOpening
							.put(singleSpecialOpeningEntry.getDate(), specialDayConverter.convert(singleSpecialOpeningEntry));
				}
			}
		}
		target.setWeekDayOpeningList(weekDaysConverter.convert(weekDaysOpening));
		target.setSpecialDayOpeningList(new ArrayList(specialDaysOpening.values()));
	}
}
