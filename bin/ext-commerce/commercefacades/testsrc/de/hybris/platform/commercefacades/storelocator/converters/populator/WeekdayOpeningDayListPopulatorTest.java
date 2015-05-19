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

import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.WeekDay;
import de.hybris.platform.commercefacades.storelocator.data.TimeData;
import de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class WeekdayOpeningDayListPopulatorTest
{
	private static final Locale CURRENT_LOCALE = Locale.UK;
	private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, CURRENT_LOCALE);
	private final DateFormatSymbols weekDaySymbols = new DateFormatSymbols(CURRENT_LOCALE);

	private WeekdayOpeningDayListPopulator weekdayOpeningDayListPopulator;
	@Mock
	private TimeDataPopulator timeDataPopulator;
	private AbstractPopulatingConverter<Date, TimeData> timeDataConverter;

	private WeekdayOpeningDayPopulator weekdayOpeningDayPopulator = new WeekdayOpeningDayPopulator();
	private AbstractPopulatingConverter<WeekdayOpeningDayModel, WeekdayOpeningDayData> weekDayOpeningDayConverter;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		doReturn(dateFormat).when(timeDataPopulator).getDateFormat();

		timeDataConverter = new ConverterFactory<Date, TimeData, TimeDataPopulator>().create(TimeData.class, timeDataPopulator);

		weekdayOpeningDayPopulator = new WeekdayOpeningDayPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return CURRENT_LOCALE;
			}
		};
		weekdayOpeningDayPopulator.setTimeDataConverter(timeDataConverter);
		weekDayOpeningDayConverter = new ConverterFactory<WeekdayOpeningDayModel, WeekdayOpeningDayData, WeekdayOpeningDayPopulator>()
				.create(WeekdayOpeningDayData.class, weekdayOpeningDayPopulator);
	}

	private WeekdayOpeningDayListPopulator createPopulator(final WeekdayOpeningDayListPopulator populator)
	{
		populator.setWeekDayConverter(weekDayOpeningDayConverter);
		populator.setCommerceCommonI18NService(commerceCommonI18NService);
		populator.setTimeDataConverter(timeDataConverter);

		return populator;
	}

	@Test
	public void testGetFirstDayOfWeek()
	{
		weekdayOpeningDayListPopulator = createPopulator(new WeekdayOpeningDayListPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return Locale.KOREA;
			}
		});

		Assert.assertEquals(WeekDay.SUNDAY, weekdayOpeningDayListPopulator.getWeekFirstDay());
	}

	@Test
	public void testFillsEmptyDaysConvertList()
	{
		weekdayOpeningDayListPopulator = createPopulator(new WeekdayOpeningDayListPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return CURRENT_LOCALE;
			}

			@Override
			protected WeekDay getWeekFirstDay()
			{
				return WeekDay.MONDAY;
			}
		});

		final WeekdayOpeningDayModel sunday = new WeekdayOpeningDayModel();
		sunday.setDayOfWeek(WeekDay.SUNDAY);

		final WeekdayOpeningDayModel saturday = new WeekdayOpeningDayModel();
		saturday.setDayOfWeek(WeekDay.SATURDAY);

		final WeekdayOpeningDayModel monday = new WeekdayOpeningDayModel();
		monday.setDayOfWeek(WeekDay.MONDAY);

		final WeekdayOpeningDayModel thursday = new WeekdayOpeningDayModel();
		thursday.setDayOfWeek(WeekDay.THURSDAY);

		final List<WeekdayOpeningDayData> orderedWeekDays = new ArrayList<WeekdayOpeningDayData>();
		weekdayOpeningDayListPopulator.populate(Arrays.asList(sunday, saturday, monday, thursday), orderedWeekDays);
		Assert.assertEquals("There should be orders for  a whole week filled in ", 7, orderedWeekDays.size());
		assertEqualWeekDay(orderedWeekDays.get(0), weekDaySymbols.getShortWeekdays()[2], false);
		assertEqualWeekDay(orderedWeekDays.get(1), weekDaySymbols.getShortWeekdays()[3], true);
		assertEqualWeekDay(orderedWeekDays.get(2), weekDaySymbols.getShortWeekdays()[4], true);
		assertEqualWeekDay(orderedWeekDays.get(3), weekDaySymbols.getShortWeekdays()[5], false);
		assertEqualWeekDay(orderedWeekDays.get(4), weekDaySymbols.getShortWeekdays()[6], true);
		assertEqualWeekDay(orderedWeekDays.get(5), weekDaySymbols.getShortWeekdays()[7], false);
		assertEqualWeekDay(orderedWeekDays.get(6), weekDaySymbols.getShortWeekdays()[1], false);
	}

	protected void assertEqualWeekDay(final WeekdayOpeningDayData data, final String weekDay, final boolean closed)
	{
		Assert.assertEquals(weekDay, data.getWeekDay());
		Assert.assertTrue(closed == data.isClosed());
	}
}
