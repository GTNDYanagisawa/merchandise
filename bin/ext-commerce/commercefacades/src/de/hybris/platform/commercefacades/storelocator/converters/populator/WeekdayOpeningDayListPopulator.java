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

import de.hybris.platform.basecommerce.enums.WeekDay;
import de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converter for a list of {@link de.hybris.platform.storelocator.model.WeekdayOpeningDayModel} 'handles' a day of week
 * order relative to {@link #getWeekFirstDay()}. Does also an insertion of the 'closed'
 * {@link de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData} where there is no given
 * corresponding model in list of {@link de.hybris.platform.storelocator.model.WeekdayOpeningDayModel}.
 */
public class WeekdayOpeningDayListPopulator extends
		OpeningDayPopulator<List<WeekdayOpeningDayModel>, List<WeekdayOpeningDayData>>
{
	private Converter<WeekdayOpeningDayModel, WeekdayOpeningDayData> weekDayConverter;


	@Required
	public void setWeekDayConverter(final Converter<WeekdayOpeningDayModel, WeekdayOpeningDayData> weekDayConverter)
	{
		this.weekDayConverter = weekDayConverter;
	}

	@Override
	public void populate(final List<WeekdayOpeningDayModel> source, final List<WeekdayOpeningDayData> target)
	{
		final Map<WeekDay, WeekdayOpeningDayData> orderedWeekDaysMap = new TreeMap<WeekDay, WeekdayOpeningDayData>(
				new WeekDayComparator(getWeekFirstDay()));
		for (final WeekdayOpeningDayModel weekDay : source)
		{
			final WeekdayOpeningDayData data = weekDayConverter.convert(weekDay);
			orderedWeekDaysMap.put(weekDay.getDayOfWeek(), data);
		}

		for (final WeekDay singleEnum : WeekDay.values())
		{
			if (!orderedWeekDaysMap.containsKey(singleEnum))
			{
				orderedWeekDaysMap.put(singleEnum, populateClosedWeekDay(singleEnum));
			}
		}

		target.addAll(orderedWeekDaysMap.values());
	}

	/**
	 * Specific comparator to deal with week day relatively to given first day of the week.
	 * 
	 */
	static class WeekDayComparator implements Comparator<WeekDay>
	{
		private final WeekDay firstDayOfWeek;

		WeekDayComparator(final WeekDay first)
		{
			firstDayOfWeek = first;
		}

		@Override
		public int compare(final WeekDay one, final WeekDay two)
		{
			if (one == null && two == null)
			{
				return 0;
			}
			else
			{
				if (one != null && two != null)
				{
					int oneRelativeOrder = one.ordinal() - firstDayOfWeek.ordinal();
					if (oneRelativeOrder < 0)
					{
						oneRelativeOrder += WeekDay.values().length;
					}
					int twoRelativeOrder = two.ordinal() - firstDayOfWeek.ordinal();
					if (twoRelativeOrder < 0)
					{
						twoRelativeOrder += WeekDay.values().length;
					}
					return oneRelativeOrder - twoRelativeOrder;
				}
				else
				{
					if (one == null)
					{
						return -1;
					}
					else
					{
						return 1;
					}
				}
			}
		}
	}

	protected WeekdayOpeningDayData populateClosedWeekDay(final WeekDay singleEnum)
	{
		final WeekdayOpeningDayData data = new WeekdayOpeningDayData();
		data.setClosed(true);
		data.setWeekDay(getWeekDaySymbols().get(singleEnum.ordinal()));
		return data;
	}

	protected WeekDay getWeekFirstDay()
	{
		final Locale defaultLocale = getCurrentLocale();
		final Calendar calendar = Calendar.getInstance(defaultLocale);
		return WeekDay.values()[calendar.getFirstDayOfWeek() - 1];//returns a one based index 
	}

	protected Converter<WeekdayOpeningDayModel, WeekdayOpeningDayData> getWeekDayConverter()
	{
		return weekDayConverter;
	}
}
