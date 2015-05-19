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
import de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData;
import de.hybris.platform.commercefacades.storelocator.data.SpecialOpeningDayData;
import de.hybris.platform.commercefacades.storelocator.data.TimeData;
import de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.time.impl.DefaultTimeService;
import de.hybris.platform.storelocator.model.OpeningDayModel;
import de.hybris.platform.storelocator.model.OpeningScheduleModel;
import de.hybris.platform.storelocator.model.SpecialOpeningDayModel;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class OpeningSchedulePopulatorTest
{

	private static final Locale CURRENT_LOCALE = Locale.UK;

	private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, CURRENT_LOCALE);
	private final DateFormatSymbols weekDaySymbols = new DateFormatSymbols(CURRENT_LOCALE);

	private static Date baseDate = null;

	private static Date TEN_YEARS_AGO = null;
	private static Date TEN_DAYS_AGO = null;
	private static Date TEN_SECONDS_AGO = null;

	private static Date TEN_SECONDS_IN_FUTURE = null;
	private static Date TEN_DAYS_IN_FUTURE = null;

	//prepare some secure base time in middle of the day
	static
	{
		final Calendar baseCalendar = Calendar.getInstance();
		baseCalendar.setTime(new Date(System.currentTimeMillis()));
		baseCalendar.set(Calendar.MILLISECOND, 0);
		baseCalendar.set(Calendar.SECOND, 0);
		baseCalendar.set(Calendar.MINUTE, 0);
		baseCalendar.set(Calendar.HOUR_OF_DAY, 10);
		baseDate = baseCalendar.getTime();

		TEN_YEARS_AGO = new Date(baseDate.getTime() - 10 * 365 * 24 * 60 * 60 * 1000);
		TEN_DAYS_AGO = new Date(baseDate.getTime() - 10 * 24 * 60 * 60 * 1000);
		TEN_SECONDS_AGO = new Date(baseDate.getTime() - 10 * 1000);
		TEN_SECONDS_IN_FUTURE = new Date(baseDate.getTime() + 10 * 1000);
		TEN_DAYS_IN_FUTURE = new Date(baseDate.getTime() + 10 * 24 * 60 * 60 * 1000);
	}

	private final OpeningSchedulePopulator openingSchedulePopulator = new OpeningSchedulePopulator();

	private SpecialOpeningDayPopulator specialOpeningDayPopulator;
	private AbstractPopulatingConverter<SpecialOpeningDayModel, SpecialOpeningDayData> specialOpeningDayConverter;

	private WeekdayOpeningDayPopulator weekdayOpeningDayPopulator;
	private AbstractPopulatingConverter<WeekdayOpeningDayModel, WeekdayOpeningDayData> weekdayOpeningDayConverter;

	private WeekdayOpeningDayListPopulator weekdayOpeningDayListPopulator;
	private AbstractPopulatingConverter<List<WeekdayOpeningDayModel>, List<WeekdayOpeningDayData>> weekdayOpeningDayListConverter;

	@Spy
	final TimeService timeService = new DefaultTimeService();

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		specialOpeningDayPopulator = new SpecialOpeningDayPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return CURRENT_LOCALE;
			}
		};
		specialOpeningDayPopulator.setTimeDataConverter(new ConverterFactory<Date, TimeData, TimeDataPopulator>().create(
				TimeData.class, new TimeDataPopulator()));
		specialOpeningDayConverter = new ConverterFactory<SpecialOpeningDayModel, SpecialOpeningDayData, SpecialOpeningDayPopulator>()
				.create(SpecialOpeningDayData.class, specialOpeningDayPopulator);

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

		weekdayOpeningDayListPopulator = new WeekdayOpeningDayListPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return CURRENT_LOCALE;
			}

			@Override
			protected WeekDay getWeekFirstDay()
			{
				return WeekDay.SUNDAY;
			}
		};
		weekdayOpeningDayListPopulator.setWeekDayConverter(weekdayOpeningDayConverter);
		weekdayOpeningDayListConverter = new AbstractPopulatingConverter<List<WeekdayOpeningDayModel>, List<WeekdayOpeningDayData>>()
		{
			@Override
			protected List<WeekdayOpeningDayData> createTarget()
			{
				return new ArrayList<WeekdayOpeningDayData>();
			}

			@Override
			public void populate(final List<WeekdayOpeningDayModel> source, final List<WeekdayOpeningDayData> target)
			{
				weekdayOpeningDayListPopulator.populate(source, target);
			}
		};

		doReturn(new Date()).when(timeService).getCurrentTime();

		openingSchedulePopulator.setSpecialDayConverter(specialOpeningDayConverter);
		openingSchedulePopulator.setTimeService(timeService);
		openingSchedulePopulator.setWeekDaysConverter(weekdayOpeningDayListConverter);
	}

	/**
	 * Test for filtering out a special days for current day and any future special day
	 */
	@Test
	public void testSpecialOpenedDaysOnlyForFutureOrder()
	{

		final List<OpeningDayModel> specialOpeningDays = new ArrayList<OpeningDayModel>();

		final SpecialOpeningDayModel summerSellOf = new StubbedSpecialOpeningDayModel();
		summerSellOf.setMessage("Special prizes");
		summerSellOf.setDate(TEN_DAYS_AGO);

		final SpecialOpeningDayModel nearFuture = new StubbedSpecialOpeningDayModel();
		nearFuture.setMessage("Near future");
		nearFuture.setDate(TEN_SECONDS_IN_FUTURE);

		final SpecialOpeningDayModel specialXmasTenYearAgo = new StubbedSpecialOpeningDayModel();
		specialXmasTenYearAgo.setMessage("Xmass in the past");
		specialXmasTenYearAgo.setDate(TEN_YEARS_AGO);

		final SpecialOpeningDayModel holidays = new StubbedSpecialOpeningDayModel();
		holidays.setMessage("Holidays");
		holidays.setDate(TEN_DAYS_IN_FUTURE);

		final SpecialOpeningDayModel lastCommit = new StubbedSpecialOpeningDayModel();
		lastCommit.setMessage("This was a commit");
		lastCommit.setDate(TEN_SECONDS_AGO);

		specialOpeningDays.add(summerSellOf);
		specialOpeningDays.add(nearFuture);
		specialOpeningDays.add(specialXmasTenYearAgo);
		specialOpeningDays.add(holidays);
		specialOpeningDays.add(lastCommit);

		final ItemContextBuilder builder = ItemContextBuilder.createDefaultBuilder(OpeningScheduleModel.class);
		builder.setLocaleProvider(new StubLocaleProvider(Locale.ENGLISH));
		final OpeningScheduleModel allSchedule = new OpeningScheduleModel(builder.build());

		allSchedule.setCode("code");
		allSchedule.setName("name");
		allSchedule.setOpeningDays(specialOpeningDays);

		final OpeningScheduleData data = new OpeningScheduleData();
		openingSchedulePopulator.populate(allSchedule, data);

		Assert.assertEquals("code", data.getCode());
		Assert.assertEquals("name", data.getName());

		Assert.assertNotNull("Special day openinigs list should be not null", data.getSpecialDayOpeningList());

		Assert.assertEquals("There are only 3 special day openinigs starting from today ", 3, data.getSpecialDayOpeningList()
				.size());

		//verify order
		assertEqualSpecialDay(data.getSpecialDayOpeningList().get(0), TEN_SECONDS_AGO, false);
		assertEqualSpecialDay(data.getSpecialDayOpeningList().get(1), TEN_SECONDS_IN_FUTURE, false);
		assertEqualSpecialDay(data.getSpecialDayOpeningList().get(2), TEN_DAYS_IN_FUTURE, false);


		Assert.assertNotNull(data.getWeekDayOpeningList());
		Assert.assertEquals(7, data.getWeekDayOpeningList().size());

		assertEqualWeekDay(data.getWeekDayOpeningList().get(0), weekDaySymbols.getShortWeekdays()[1], true);
		assertEqualWeekDay(data.getWeekDayOpeningList().get(1), weekDaySymbols.getShortWeekdays()[2], true);
		assertEqualWeekDay(data.getWeekDayOpeningList().get(2), weekDaySymbols.getShortWeekdays()[3], true);
		assertEqualWeekDay(data.getWeekDayOpeningList().get(3), weekDaySymbols.getShortWeekdays()[4], true);
		assertEqualWeekDay(data.getWeekDayOpeningList().get(4), weekDaySymbols.getShortWeekdays()[5], true);
		assertEqualWeekDay(data.getWeekDayOpeningList().get(5), weekDaySymbols.getShortWeekdays()[6], true);
		assertEqualWeekDay(data.getWeekDayOpeningList().get(6), weekDaySymbols.getShortWeekdays()[7], true);
	}

	protected void assertEqualWeekDay(final WeekdayOpeningDayData data, final String weekDay, final boolean closed)
	{
		Assert.assertEquals(weekDay, data.getWeekDay());
		Assert.assertTrue(closed == data.isClosed());
	}

	protected void assertEqualSpecialDay(final SpecialOpeningDayData data, final Date date, final boolean closed)
	{
		Assert.assertEquals(date, data.getDate());
		Assert.assertEquals(dateFormat.format(date), data.getFormattedDate());
		Assert.assertTrue(closed == data.isClosed());
	}

	static class StubbedSpecialOpeningDayModel extends SpecialOpeningDayModel
	{
		static ItemModelContext createContext(final Locale loc)
		{
			final ItemContextBuilder builder = ItemContextBuilder.createDefaultBuilder(SpecialOpeningDayModel.class);
			builder.setLocaleProvider(new StubLocaleProvider(loc));
			return builder.build();
		}

		StubbedSpecialOpeningDayModel()
		{
			super(createContext(Locale.ENGLISH));
		}
	}

}
