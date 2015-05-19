/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.servicelayer.cronjob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.DemoTest;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.cronjob.impl.DefaultTriggerService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@DemoTest
public class TriggerServiceDemoTest extends ServicelayerTransactionalBaseTest
{
	private static final Logger LOG = Logger.getLogger(TriggerServiceDemoTest.class);

	protected final static long SECOND = 1000;
	protected final static long MINUTE = 60 * SECOND; //     60000
	protected final static long HOUR = 60 * MINUTE; //   3600000

	@Resource
	private TriggerService triggerService;

	@Resource
	private ModelService modelService;

	@Resource
	private I18NService i18NService;

	private ServicelayerJobModel job;

	@Before
	public void setUp() throws Exception
	{
		job = modelService.create(ServicelayerJobModel.class);
		job.setCode("cleanUpJob");
		job.setSpringId("cleanUpJobPerformable");
		modelService.save(job);
	}

	@Test
	public void testActivationWithCronExpression()
	{
		// create a trigger who triggers every hour at 51 minutes
		final TriggerModel trigger = modelService.create(TriggerModel.class);
		trigger.setCronExpression("0 51 * * * ? *");
		trigger.setJob(job);
		trigger.setActive(Boolean.TRUE);
		// the interceptor will set the activationTime
		modelService.save(trigger);

		long now = System.currentTimeMillis();
		// get activation time already set
		final long activation = trigger.getActivationTime().getTime();
		// simulate activation
		assertFalse("Activation works!", ((DefaultTriggerService) triggerService).activateForTest(trigger, now));
		// get next activation time after activation
		long next = trigger.getActivationTime().getTime();
		// should be the same, because the trigger was not activated
		long delta = next - activation;
		// step 1 hour forward
		assertEquals("Time not equal!", HOUR, delta);

		now = next;
		assertTrue("Activation fails!", ((DefaultTriggerService) triggerService).activateForTest(trigger, now));
		next = trigger.getActivationTime().getTime();

		// test for 5 hours
		// Note: THIS WILL FAIL ON DAYLIGHT SAVING (sommerzeit/winterzeitumstellung)
		for (int i = 0; i < 5; i++)
		{
			delta = next - now;
			if (LOG.isDebugEnabled())
			{
				LOG.debug("time=" + now + " next=" + next + " delta=" + delta);
			}
			// Time increments of 1 hour
			assertEquals("Time not equal!", HOUR, delta);
			now = next;
			assertTrue("Activation fails!", ((DefaultTriggerService) triggerService).activateForTest(trigger, now));
			next = trigger.getActivationTime().getTime();
		}
	}

	/**
	 * Configures a new <code>TriggerModel</code> without saving.
	 * 
	 * @param job
	 *           the <code>JobModel</code> the <code>TriggerModel</code> should be assigned to
	 * @param second
	 *           the second at which the <code>TriggerModel</code> should fire (0-59 or -1 for every second)
	 * @param minute
	 *           the minute at which the <code>TriggerModel</code> should fire (0-59 or -1 for every minute)
	 * @param hour
	 *           hour at which the <code>TriggerModel</code> should fire (0-23 or -1 for every hour)
	 * @param day
	 *           the day of month the <code>TriggerModel</code> should fire (0-31 or -1 for every day)
	 * @param month
	 *           the month at which the <code>TriggerModel</code> should fire (0-11 or -1 for every month)
	 * @param year
	 *           the year when the <code>TriggerModel</code> should fire (-1 for every year)
	 * @param daysOfWeek
	 *           a <code>List</code> of
	 * @param relative
	 *           true if time values should be considered relative to each other or false if not
	 * @return the TriggerModel created
	 */
	private TriggerModel configureNewTrigger(final JobModel job, final int second, final int minute, final int hour,
			final int day, final int month, final int year, final List daysOfWeek, final boolean relative)
	{
		final TriggerModel trigger = modelService.create(TriggerModel.class);

		trigger.setJob(job);
		trigger.setSecond(Integer.valueOf(second));
		trigger.setMinute(Integer.valueOf(minute));
		trigger.setHour(Integer.valueOf(hour));
		trigger.setDay(Integer.valueOf(day));
		trigger.setMonth(Integer.valueOf(month));
		trigger.setYear(Integer.valueOf(year));
		trigger.setDaysOfWeek(daysOfWeek);
		trigger.setRelative(Boolean.valueOf(relative));
		trigger.setActivationTime(triggerService.getNextTime(trigger,
				Calendar.getInstance(i18NService.getCurrentTimeZone(), i18NService.getCurrentLocale())).getTime());
		// Set the default value to -1, which means ignore
		trigger.setMaxAcceptableDelay(Integer.valueOf(-1));
		return trigger;
	}

	@Test
	public void testActivationOldWay()
	{
		// create a trigger who triggers every hour at 51 minutes
		// here we have to set the activationTime by our own
		final TriggerModel trigger = configureNewTrigger(job, -1, 51, -1, -1, -1, -1, null, false);
		trigger.setActive(Boolean.TRUE);
		modelService.save(trigger);

		long now = System.currentTimeMillis();
		// get activation time already set
		final long activation = trigger.getActivationTime().getTime();
		// simulate activation
		assertFalse("Activation works!", ((DefaultTriggerService) triggerService).activateForTest(trigger, now));
		// get next activation time after activation
		long next = trigger.getActivationTime().getTime();
		// should be the same, because the trigger was not activated
		long delta = next - activation;
		// step 1 hour forward
		assertEquals("Time not equal!", HOUR, delta);

		now = next;
		assertTrue("Activation fails!", ((DefaultTriggerService) triggerService).activateForTest(trigger, now));
		next = trigger.getActivationTime().getTime();

		// test for 5 hours
		// Note: THIS WILL FAIL ON DAYLIGHT SAVING (sommerzeit/winterzeitumstellung)
		for (int i = 0; i < 5; i++)
		{
			delta = next - now;
			if (LOG.isDebugEnabled())
			{
				LOG.debug("time=" + now + " next=" + next + " delta=" + delta);
			}
			// Time increments of 1 hour
			assertEquals("Time not equal!", HOUR, delta);
			now = next;
			assertTrue("Activation fails!", ((DefaultTriggerService) triggerService).activateForTest(trigger, now));
			next = trigger.getActivationTime().getTime();
		}
	}
}
