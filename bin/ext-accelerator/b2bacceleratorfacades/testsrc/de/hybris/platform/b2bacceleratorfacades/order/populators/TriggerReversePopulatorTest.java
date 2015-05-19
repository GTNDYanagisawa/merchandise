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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.cronjob.model.TriggerModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;


@UnitTest
public class TriggerReversePopulatorTest
{
	private TriggerReversePopulator triggerPopulator;

	@Before
	public void setUp()
	{
		triggerPopulator = new TriggerReversePopulator();
	}

	@Test
	public void testDayInterval()
	{
		final TriggerData triggerData = new TriggerData();
		final TriggerModel triggerModel = new TriggerModel();

		final Integer day = Integer.valueOf(2);
		final Date startDate = new Date();

		triggerData.setActivationTime(startDate);
		triggerData.setDay(day);
		triggerData.setRelative(Boolean.TRUE);

		triggerPopulator.populate(triggerData, triggerModel);


		Assert.assertEquals(Boolean.TRUE, triggerModel.getRelative());
		Assert.assertEquals(startDate, triggerModel.getActivationTime());
		Assert.assertEquals(day, triggerModel.getDay());
		Assert.assertEquals(Integer.valueOf(-1), triggerModel.getWeekInterval());
		Assert.assertEquals(null, triggerModel.getDaysOfWeek());
	}



	@Test
	public void testWeekly()
	{

		final TriggerData triggerData = new TriggerData();
		final TriggerModel triggerModel = new TriggerModel();

		final Date startDate = new Date();
		final Integer weekInterval = Integer.valueOf(4);
		final ArrayList<DayOfWeek> days = new ArrayList<DayOfWeek>();
		days.add(DayOfWeek.MONDAY);
		days.add(DayOfWeek.WEDNESDAY);

		triggerData.setActivationTime(startDate);
		triggerData.setDaysOfWeek(days);
		triggerData.setWeekInterval(weekInterval);

		triggerPopulator.populate(triggerData, triggerModel);


		Assert.assertEquals(startDate, triggerModel.getActivationTime());
		Assert.assertEquals(days, days);
		Assert.assertEquals(weekInterval, triggerModel.getWeekInterval());

		Assert.assertEquals(Boolean.FALSE, triggerModel.getRelative());
		Assert.assertEquals(Integer.valueOf(-1), triggerModel.getDay());

	}


	@Test
	public void testMontly()
	{
		final TriggerData triggerData = new TriggerData();
		final TriggerModel triggerModel = new TriggerModel();

		final Integer day = Integer.valueOf(2);
		final Date startDate = new Date();

		triggerData.setActivationTime(startDate);
		triggerData.setDay(day);
		triggerData.setRelative(Boolean.FALSE);

		triggerPopulator.populate(triggerData, triggerModel);


		Assert.assertEquals(Boolean.FALSE, triggerModel.getRelative());
		Assert.assertEquals(day, triggerModel.getDay());
		Assert.assertEquals(Integer.valueOf(-1), triggerModel.getWeekInterval());
		Assert.assertEquals(null, triggerModel.getDaysOfWeek());
	}

}
