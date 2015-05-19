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

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.WeekDay;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData;
import de.hybris.platform.commercefacades.storelocator.data.SpecialOpeningDayData;
import de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.storelocator.model.OpeningDayModel;
import de.hybris.platform.storelocator.model.OpeningScheduleModel;
import de.hybris.platform.storelocator.model.SpecialOpeningDayModel;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * 
 */
@IntegrationTest
public class OpeningSchedulePopulatorIntegrationTest extends ServicelayerTest
{
	private final Date openTime = new Date(System.currentTimeMillis() - 1000);

	private final Date closeTime = new Date(System.currentTimeMillis() + 1000);

	private final Date specialDate = new Date(System.currentTimeMillis() + 10000);

	private final Date specialCloseDate = new Date(System.currentTimeMillis() - 10000);

	private static final Locale CURRENT_LOCALE = Locale.FRANCE;

	private final DateFormatSymbols weekDaySymbols = new DateFormatSymbols(CURRENT_LOCALE);

	@Resource
	private AbstractPopulatingConverter<OpeningScheduleModel, OpeningScheduleData> openingScheduleConverter;

	@Resource
	private ModelService modelService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private SessionService sessionService;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private ItemModelContext baseSiteModelContext;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		when(baseSiteModelContext.getItemType()).thenReturn(BaseSiteModel._TYPECODE);
		when(baseSiteModel.getItemModelContext()).thenReturn(baseSiteModelContext);
		when(baseSiteModel.getLocale()).thenReturn(CURRENT_LOCALE.toString());
		when(baseSiteModel.getDefaultLanguage()).thenReturn(commonI18NService.getCurrentLanguage());
		sessionService.setAttribute("currentSite", baseSiteModel);
	}

	@After
	public void unPrepare()
	{
		if (sessionService.getAttribute("currentSite") == baseSiteModel)
		{
			sessionService.setAttribute("currentSite", null);
		}
	}

	@Test
	public void testOpeningDaysOrderInSchedule()
	{

		final OpeningScheduleModel schedule = modelService.create(OpeningScheduleModel.class);
		schedule.setCode("openinig schedule");
		schedule.setName("openinig schedule name");

		final WeekdayOpeningDayModel wednesday = modelService.create(WeekdayOpeningDayModel.class);
		wednesday.setClosingTime(closeTime);
		wednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		wednesday.setOpeningTime(openTime);

		final WeekdayOpeningDayModel monday = modelService.create(WeekdayOpeningDayModel.class);
		monday.setClosingTime(closeTime);
		monday.setDayOfWeek(WeekDay.MONDAY);
		monday.setOpeningTime(openTime);


		final WeekdayOpeningDayModel sunday = modelService.create(WeekdayOpeningDayModel.class);
		sunday.setClosingTime(closeTime);
		sunday.setDayOfWeek(WeekDay.SUNDAY);
		sunday.setOpeningTime(openTime);

		final SpecialOpeningDayModel specialDay = modelService.create(SpecialOpeningDayModel.class);
		specialDay.setClosingTime(closeTime);
		specialDay.setDate(specialDate);
		specialDay.setOpeningTime(openTime);
		specialDay.setMessage("This is a special event");
		specialDay.setName("This is a special event name");

		final SpecialOpeningDayModel specialClosedDay = modelService.create(SpecialOpeningDayModel.class);
		specialClosedDay.setDate(specialCloseDate);
		specialClosedDay.setClosed(true);
		specialClosedDay.setMessage("This is a special close event");
		specialClosedDay.setName("This is a special close event name");

		final Collection<OpeningDayModel> allOpeningDays = new ArrayList<OpeningDayModel>();
		allOpeningDays.add(wednesday);
		allOpeningDays.add(monday);
		allOpeningDays.add(sunday);
		allOpeningDays.add(specialDay);
		allOpeningDays.add(specialClosedDay);

		schedule.setOpeningDays(allOpeningDays);

		final OpeningScheduleData scheduleData = openingScheduleConverter.convert(schedule);

		Assert.assertNotNull(scheduleData);
		Assert.assertEquals("openinig schedule", scheduleData.getCode());
		Assert.assertEquals("openinig schedule name", scheduleData.getName());

		Assert.assertNotNull(scheduleData.getWeekDayOpeningList());
		Assert.assertEquals(7, scheduleData.getWeekDayOpeningList().size());

		//english monday is first
		assertEqualWeekDay(scheduleData.getWeekDayOpeningList().get(0), weekDaySymbols.getShortWeekdays()[2], false);
		assertEqualWeekDay(scheduleData.getWeekDayOpeningList().get(1), weekDaySymbols.getShortWeekdays()[3], true);
		assertEqualWeekDay(scheduleData.getWeekDayOpeningList().get(2), weekDaySymbols.getShortWeekdays()[4], false);
		assertEqualWeekDay(scheduleData.getWeekDayOpeningList().get(3), weekDaySymbols.getShortWeekdays()[5], true);
		assertEqualWeekDay(scheduleData.getWeekDayOpeningList().get(4), weekDaySymbols.getShortWeekdays()[6], true);
		assertEqualWeekDay(scheduleData.getWeekDayOpeningList().get(5), weekDaySymbols.getShortWeekdays()[7], true);
		assertEqualWeekDay(scheduleData.getWeekDayOpeningList().get(6), weekDaySymbols.getShortWeekdays()[1], false);

		Assert.assertNotNull(scheduleData.getSpecialDayOpeningList());
		Assert.assertEquals(2, scheduleData.getSpecialDayOpeningList().size());
		assertEqualSpecialDay(scheduleData.getSpecialDayOpeningList().get(0), specialCloseDate, true,
				"This is a special close event");
		assertEqualSpecialDay(scheduleData.getSpecialDayOpeningList().get(1), specialDate, false, "This is a special event");


	}

	protected void assertEqualWeekDay(final WeekdayOpeningDayData data, final String weekDay, final boolean closed)
	{
		Assert.assertEquals(weekDay, data.getWeekDay());
		Assert.assertTrue(closed == data.isClosed());
	}

	protected void assertEqualSpecialDay(final SpecialOpeningDayData transformedData, final Date date, final boolean closed,
			final String expectedComment)
	{
		Assert.assertEquals("Dates [" + date + "][" + transformedData.getDate() + "] should be equal ", date,
				transformedData.getDate());
		Assert.assertTrue(closed == transformedData.isClosed());
		Assert.assertEquals(expectedComment, transformedData.getComment());
	}
}
