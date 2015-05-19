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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.cronjob.model.TriggerModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class TriggerPopulatorTest
{

	private TriggerPopulator triggerPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		triggerPopulator = new TriggerPopulator();
	}

	@Test
	public void testConvert()
	{
		final Date startDate = new Date();
		final Integer day = Integer.valueOf(5);
		final Integer week = Integer.valueOf(2);
		final List<DayOfWeek> days = new ArrayList<DayOfWeek>();
		days.add(DayOfWeek.TUESDAY);
		days.add(DayOfWeek.FRIDAY);
		final String timetable = "Weekly on Tues and Thursdays";

		final TriggerModel triggerModel = Mockito.mock(TriggerModel.class);
		given(triggerModel.getRelative()).willReturn(Boolean.TRUE);
		given(triggerModel.getActivationTime()).willReturn(startDate);
		given(triggerModel.getDay()).willReturn(day);
		given(triggerModel.getWeekInterval()).willReturn(week);
		given(triggerModel.getDaysOfWeek()).willReturn(days);
		given(triggerModel.getTimeTable()).willReturn(timetable);


		final TriggerData triggerData = new TriggerData();
		triggerPopulator.populate(triggerModel, triggerData);

		Assert.assertEquals(Boolean.TRUE, triggerData.getRelative());
		Assert.assertEquals(startDate, triggerData.getActivationTime());
		Assert.assertEquals(day, triggerData.getDay());
		Assert.assertEquals(week, triggerData.getWeekInterval());
		Assert.assertEquals(days, triggerData.getDaysOfWeek());
		Assert.assertEquals(timetable, triggerData.getDisplayTimeTable());
	}
}
