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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.WeekDay;
import de.hybris.platform.commercefacades.storelocator.data.TimeData;
import de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class WeekdayOpeningDayPopulatorTest
{
	private final byte startHour = 9;
	private final byte startMinute = 59;

	private final byte endHour = 2;
	private final byte endMinute = 21;

	private Date start;
	private Date end;

	private static final Locale CURRENT_LOCALE = Locale.UK;
	private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, CURRENT_LOCALE);
	private final DateFormatSymbols weekDaySymbols = new DateFormatSymbols(CURRENT_LOCALE);

	private WeekdayOpeningDayPopulator weekdayOpeningDayPopulator;
	private AbstractPopulatingConverter<WeekdayOpeningDayModel, WeekdayOpeningDayData> weekdayOpeningDayConverter;

	private TimeDataPopulator timeDataPopulator = new TimeDataPopulator();
	private AbstractPopulatingConverter<Date, TimeData> timeDataConverter;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		weekdayOpeningDayPopulator = new WeekdayOpeningDayPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return CURRENT_LOCALE;
			}
		};
		weekdayOpeningDayConverter = new ConverterFactory<WeekdayOpeningDayModel, WeekdayOpeningDayData, WeekdayOpeningDayPopulator>()
				.create(WeekdayOpeningDayData.class, weekdayOpeningDayPopulator);

		timeDataPopulator = new TimeDataPopulator()
		{
			@Override
			protected DateFormat getDateFormat()
			{
				return dateFormat;
			}
		};
		timeDataConverter = new ConverterFactory<Date, TimeData, TimeDataPopulator>().create(TimeData.class, timeDataPopulator);

		weekdayOpeningDayPopulator.setTimeDataConverter(timeDataConverter);
		start = initDate(startHour, startMinute);
		end = initDate(endHour, endMinute);
	}

	protected Date initDate(final byte hour, final byte minute)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWeekdayConverterForNullModel()
	{
		weekdayOpeningDayConverter.convert(null);
	}

	@Test
	public void testWeekdayConverterForCorrectModel()
	{
		final WeekdayOpeningDayModel model = new WeekdayOpeningDayModel();
		model.setClosingTime(end);
		model.setDayOfWeek(WeekDay.THURSDAY);
		model.setOpeningTime(start);

		final WeekdayOpeningDayData data = weekdayOpeningDayConverter.convert(model);

		Assert.assertEquals(endHour, data.getClosingTime().getHour());
		Assert.assertEquals(endMinute, data.getClosingTime().getMinute());

		Assert.assertEquals(weekDaySymbols.getShortWeekdays()[5], data.getWeekDay());

		Assert.assertEquals(startHour, data.getOpeningTime().getHour());
		Assert.assertEquals(startMinute, data.getOpeningTime().getMinute());

	}

}
