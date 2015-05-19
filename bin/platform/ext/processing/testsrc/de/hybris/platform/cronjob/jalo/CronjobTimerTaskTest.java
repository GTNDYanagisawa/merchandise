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
package de.hybris.platform.cronjob.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.AbstractTenant;
import de.hybris.platform.core.Registry;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.Timer;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;


/**
 * Tests for thread safety of starting and stopping the cronjob timer task.
 */
@IntegrationTest
public class CronjobTimerTaskTest extends HybrisJUnit4Test
{
	@Test
	public void testStopping() throws Exception
	{
		final AtomicBoolean realRunMarker = new AtomicBoolean();
		final CyclicBarrier runJoinPoint = new CyclicBarrier(2);
		final TestTimerTaskUtils utils = new TestTimerTaskUtils(runJoinPoint, realRunMarker);
		assertFalse(utils.isRunning());

		utils.startTimerTask();
		assertTrue(utils.isRunning());

		// let it run once
		runJoinPoint.await(5, TimeUnit.SECONDS);

		// wait for next run to be just starting
		long waitUntil = System.currentTimeMillis() + (5 * 1000);
		while (runJoinPoint.getNumberWaiting() < 1 && System.currentTimeMillis() < waitUntil)
		{
			Thread.sleep(100);
		}
		// next run should be about to start
		assertEquals(1, runJoinPoint.getNumberWaiting());

		// make sure previous run did set the marker
		assertTrue(realRunMarker.get());
		// reset marker
		realRunMarker.set(false);

		// now cancel *before* run has started
		utils.stopTimerTask();

		// let it run now
		runJoinPoint.await();

		waitUntil = System.currentTimeMillis() + (5 * 1000);
		while (!realRunMarker.get() && System.currentTimeMillis() < waitUntil)
		{
			Thread.sleep(100);
		}
		assertFalse("task runner logic was executed after cancel", realRunMarker.get());
	}

	private static class TestTimerTaskUtils extends TimerTaskUtils
	{
		final CyclicBarrier runJoinPoint;
		final AtomicBoolean realRunMarker;

		public TestTimerTaskUtils(final CyclicBarrier runJoinPoint, final AtomicBoolean realRunMarker)
		{
			this.runJoinPoint = runJoinPoint;
			this.realRunMarker = realRunMarker;
		}

		@Override
		protected void schedule(final Timer timer, final CronJobTimerTask timerTask, final int intervalSeconds)
		{
			timer.schedule(timerTask, 0, 10);
		}

		@Override
		protected CronJobTimerTask createTimerTask()
		{
			return new TestCronjobTimerTask((AbstractTenant) Registry.getCurrentTenant(), runJoinPoint, realRunMarker);
		}
	}

	private static class TestCronjobTimerTask extends CronJobTimerTask
	{
		final CyclicBarrier runJoinPoint;
		final AtomicBoolean realRunMarker;

		public TestCronjobTimerTask(final AbstractTenant t, final CyclicBarrier runJoinPoint, final AtomicBoolean realRunMarker)
		{
			super(t);
			this.runJoinPoint = runJoinPoint;
			this.realRunMarker = realRunMarker;
		}

		@Override
		public void run()
		{
			try
			{
				runJoinPoint.await();
				super.run();
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		protected void processTriggers()
		{
			realRunMarker.set(true);
		}
	}
}
