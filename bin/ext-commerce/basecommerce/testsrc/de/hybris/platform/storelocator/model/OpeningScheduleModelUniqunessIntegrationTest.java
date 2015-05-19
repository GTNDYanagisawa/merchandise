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
package de.hybris.platform.storelocator.model;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.WeekDay;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test showing uniqueness rules for {@link WeekdayOpeningDayModel} and {@link SpecialOpeningDayModel}
 */
@IntegrationTest
public class OpeningScheduleModelUniqunessIntegrationTest extends ServicelayerTest
{

	@Resource
	private ModelService modelService;

	private final Date ONE_SPECIAL_DATE = new Date(System.currentTimeMillis() + 10000);
	private final Date OTHER_SPECIAL_DATE = new Date(System.currentTimeMillis() - 10000);

	@Test
	public void testNonUniqueWeekDayOpeningHour()
	{
		final Date someDate = new Date();
		final Date otherDate = new Date(System.currentTimeMillis() + 5000);
		final OpeningScheduleModel schedule = modelService.create(OpeningScheduleModel.class);
		schedule.setCode("testSchedule");

		final WeekdayOpeningDayModel wednesday = modelService.create(WeekdayOpeningDayModel.class);
		wednesday.setClosingTime(someDate); //some date
		wednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		wednesday.setOpeningTime(someDate);
		wednesday.setOpeningSchedule(schedule);

		final WeekdayOpeningDayModel otherwednesday = modelService.create(WeekdayOpeningDayModel.class);
		otherwednesday.setClosingTime(otherDate); //some date
		otherwednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		otherwednesday.setOpeningTime(otherDate);
		otherwednesday.setOpeningSchedule(schedule);

		final List<OpeningDayModel> openings = new ArrayList<OpeningDayModel>(2);
		openings.add(wednesday);
		openings.add(otherwednesday);

		schedule.setOpeningDays(openings);

		try
		{
			modelService.saveAll(wednesday, otherwednesday, schedule);
			Assert.fail("Should not be possible to save");
		}
		catch (final ModelSavingException e)
		{
			//ok here
		}

	}


	@Test
	public void testNonUniqueWeekDayOpeningHourWithNullSchedule()
	{
		final Date someDate = new Date();
		final Date otherDate = new Date(System.currentTimeMillis() + 5000);

		final WeekdayOpeningDayModel wednesday = modelService.create(WeekdayOpeningDayModel.class);
		wednesday.setClosingTime(someDate); //some date
		wednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		wednesday.setOpeningTime(someDate);


		final WeekdayOpeningDayModel otherwednesday = modelService.create(WeekdayOpeningDayModel.class);
		otherwednesday.setClosingTime(otherDate); //some date
		otherwednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		otherwednesday.setOpeningTime(otherDate);


		try
		{
			modelService.saveAll(wednesday, otherwednesday);
			Assert.fail("Should not be possible to save");
		}
		catch (final ModelSavingException e)
		{
			//ok here
		}

	}

	@Test
	public void testNonUniqueWeekDayOpeningHourForDifferentSchedules()
	{
		final Date someDate = new Date();
		final Date otherDate = new Date(System.currentTimeMillis() + 5000);

		final OpeningScheduleModel schedule = modelService.create(OpeningScheduleModel.class);
		schedule.setCode("testSchedule");

		final OpeningScheduleModel otherSchedule = modelService.create(OpeningScheduleModel.class);
		otherSchedule.setCode("testOtherSchedule");

		final WeekdayOpeningDayModel wednesday = modelService.create(WeekdayOpeningDayModel.class);
		wednesday.setClosingTime(someDate); //some date
		wednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		wednesday.setOpeningTime(someDate);
		wednesday.setOpeningSchedule(schedule);

		final WeekdayOpeningDayModel otherwednesday = modelService.create(WeekdayOpeningDayModel.class);
		otherwednesday.setClosingTime(otherDate); //some date
		otherwednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		otherwednesday.setOpeningTime(otherDate);
		otherwednesday.setOpeningSchedule(otherSchedule);

		List<OpeningDayModel> openings = new ArrayList<OpeningDayModel>(1);
		openings.add(wednesday);

		schedule.setOpeningDays(openings);

		openings = new ArrayList<OpeningDayModel>(1);
		openings.add(otherwednesday);

		otherSchedule.setOpeningDays(openings);

		try
		{
			modelService.saveAll(wednesday, otherwednesday, schedule, otherSchedule);

		}
		catch (final ModelSavingException e)
		{
			Assert.fail("Should be possible to save");
		}

	}

	@Test
	public void testUniqueWeekDayOpeningHour()
	{
		final Date someDate = new Date();
		final OpeningScheduleModel schedule = modelService.create(OpeningScheduleModel.class);
		schedule.setCode("testSchedule");

		final WeekdayOpeningDayModel wednesday = modelService.create(WeekdayOpeningDayModel.class);
		wednesday.setClosingTime(someDate); //some date
		wednesday.setDayOfWeek(WeekDay.WEDNESDAY);
		wednesday.setOpeningTime(someDate);
		wednesday.setOpeningSchedule(schedule);

		final WeekdayOpeningDayModel otherwednesday = modelService.create(WeekdayOpeningDayModel.class);
		otherwednesday.setClosingTime(someDate); //some date
		otherwednesday.setDayOfWeek(WeekDay.THURSDAY);
		otherwednesday.setOpeningTime(someDate);
		otherwednesday.setOpeningSchedule(schedule);

		final List<OpeningDayModel> openings = new ArrayList<OpeningDayModel>(2);
		openings.add(wednesday);
		openings.add(otherwednesday);

		schedule.setOpeningDays(openings);
		try
		{
			modelService.saveAll(wednesday, otherwednesday, schedule);
		}
		catch (final ModelSavingException e)
		{
			Assert.fail("Should be possible to save");
		}

	}


	@Test
	public void testNonUniqueSpecialDayOpeningHour()
	{
		final Date someDate = new Date();
		final Date otherDate = new Date(System.currentTimeMillis() + 5000);
		final OpeningScheduleModel schedule = modelService.create(OpeningScheduleModel.class);
		schedule.setCode("testSchedule");

		final SpecialOpeningDayModel specialDay = modelService.create(SpecialOpeningDayModel.class);

		specialDay.setClosingTime(someDate); //some date
		specialDay.setDate(ONE_SPECIAL_DATE);
		specialDay.setOpeningTime(someDate);
		specialDay.setOpeningSchedule(schedule);

		final SpecialOpeningDayModel sameSpecialDay = modelService.create(SpecialOpeningDayModel.class);
		sameSpecialDay.setClosingTime(otherDate); //some date
		sameSpecialDay.setDate(ONE_SPECIAL_DATE);
		sameSpecialDay.setOpeningTime(otherDate);
		sameSpecialDay.setOpeningSchedule(schedule);

		final List<OpeningDayModel> openings = new ArrayList<OpeningDayModel>(2);
		openings.add(specialDay);
		openings.add(sameSpecialDay);

		schedule.setOpeningDays(openings);

		try
		{
			modelService.saveAll(specialDay, sameSpecialDay, schedule);
			Assert.fail("Should not be possible to save");
		}
		catch (final ModelSavingException e)
		{
			//ok here
		}
	}

	@Test
	public void testNonUniqueSpecialDayOpeningHourWithNullSchedule()
	{
		final Date someDate = new Date();
		final Date otherDate = new Date(System.currentTimeMillis() + 5000);

		final SpecialOpeningDayModel specialDay = modelService.create(SpecialOpeningDayModel.class);

		specialDay.setClosingTime(someDate); //some date
		specialDay.setDate(ONE_SPECIAL_DATE);
		specialDay.setOpeningTime(someDate);

		final SpecialOpeningDayModel sameSpecialDay = modelService.create(SpecialOpeningDayModel.class);
		sameSpecialDay.setClosingTime(otherDate); //some date
		sameSpecialDay.setDate(ONE_SPECIAL_DATE);
		sameSpecialDay.setOpeningTime(otherDate);

		final List<OpeningDayModel> openings = new ArrayList<OpeningDayModel>(2);
		openings.add(specialDay);
		openings.add(sameSpecialDay);

		try
		{
			modelService.saveAll(specialDay, sameSpecialDay);
			Assert.fail("Should not be possible to save");
		}
		catch (final ModelSavingException e)
		{
			//ok here
		}
	}

	@Test
	public void testNonUniqueSpecialDayOpeningHourForDifferentSchedules()
	{
		final Date someDate = new Date();
		final Date otherDate = new Date(System.currentTimeMillis() + 5000);

		final OpeningScheduleModel schedule = modelService.create(OpeningScheduleModel.class);
		schedule.setCode("testSchedule");

		final OpeningScheduleModel otherSchedule = modelService.create(OpeningScheduleModel.class);
		otherSchedule.setCode("testOtherSchedule");

		final SpecialOpeningDayModel specialDay = modelService.create(SpecialOpeningDayModel.class);
		specialDay.setClosingTime(someDate); //some date
		specialDay.setDate(ONE_SPECIAL_DATE);
		specialDay.setOpeningTime(someDate);
		specialDay.setOpeningSchedule(schedule);

		final SpecialOpeningDayModel sameSpecialDay = modelService.create(SpecialOpeningDayModel.class);
		sameSpecialDay.setClosingTime(otherDate); //some other date
		sameSpecialDay.setDate(ONE_SPECIAL_DATE);
		sameSpecialDay.setOpeningTime(otherDate);
		sameSpecialDay.setOpeningSchedule(otherSchedule);

		List<OpeningDayModel> openings = new ArrayList<OpeningDayModel>(1);
		openings.add(specialDay);

		schedule.setOpeningDays(openings);

		openings = new ArrayList<OpeningDayModel>(1);
		openings.add(sameSpecialDay);

		otherSchedule.setOpeningDays(openings);

		try
		{
			modelService.saveAll(specialDay, sameSpecialDay, schedule, otherSchedule);

		}
		catch (final ModelSavingException e)
		{
			Assert.fail("Should be possible to save");
		}

	}

	@Test
	public void testUniqueSpecialDayOpeningHour()
	{
		final Date someDate = new Date();
		final OpeningScheduleModel schedule = modelService.create(OpeningScheduleModel.class);
		schedule.setCode("testSchedule");

		final SpecialOpeningDayModel wednesday = modelService.create(SpecialOpeningDayModel.class);
		wednesday.setClosingTime(someDate); //some date
		wednesday.setDate(ONE_SPECIAL_DATE);
		wednesday.setOpeningTime(someDate);
		wednesday.setOpeningSchedule(schedule);

		final SpecialOpeningDayModel otherwednesday = modelService.create(SpecialOpeningDayModel.class);
		otherwednesday.setClosingTime(someDate); //some other date
		otherwednesday.setDate(OTHER_SPECIAL_DATE);
		otherwednesday.setOpeningTime(someDate);
		otherwednesday.setOpeningSchedule(schedule);

		final List<OpeningDayModel> openings = new ArrayList<OpeningDayModel>(2);
		openings.add(wednesday);
		openings.add(otherwednesday);

		schedule.setOpeningDays(openings);
		try
		{
			modelService.saveAll(wednesday, otherwednesday, schedule);
		}
		catch (final ModelSavingException e)
		{
			Assert.fail("Should be possible to save");
		}

	}

}
