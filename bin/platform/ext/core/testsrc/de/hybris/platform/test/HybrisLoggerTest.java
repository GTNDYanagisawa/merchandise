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
package de.hybris.platform.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.logging.HybrisLogListener;
import de.hybris.platform.util.logging.HybrisLogger;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the {@link HybrisLogger} functionality.
 */
@IntegrationTest
public class HybrisLoggerTest extends HybrisJUnit4Test
{
	private static final Logger LOG = Logger.getLogger(HybrisLoggerTest.class);

	private int originalListenerCount;

	@Before
	public void setUp()
	{
		originalListenerCount = HybrisLogger.getAllAListeners().size();
	}

	@Test
	public void testPLA8501()
	{
		final TestListener listener = new TestListener();
		// put some listeners by default to avoid single listener mode at HybrisLogger
		HybrisLogger.addListener(listener);
		HybrisLogger.addListener(listener);

		final TestLoggerThread thread = new TestLoggerThread();
		thread.start();

		for (int i = 0; i < 1000000; i++)
		{
			HybrisLogger.addListener(listener);
			HybrisLogger.removeListener(listener);
		}
		thread.interrupt();
		HybrisLogger.removeListener(listener);
		HybrisLogger.removeListener(listener);

		assertNull("Caught exception while logging", thread.caught);
	}

	@Test
	public void testListener()
	{
		//CronJobLogListener is already registered because of startup of CronJobManager

		final TestListener listener1 = new TestListener();
		final TestListener listener2 = new TestListener();

		HybrisLogger.addListener(listener1);
		assertEquals("Not exactly two listener registered", originalListenerCount + 1, HybrisLogger.getAllAListeners().size());

		LOG.debug("test");
		assertNotNull("No event received", listener1.event);
		assertEquals("Wrong event received", "test", listener1.event.getMessage());
		listener1.event = null;

		HybrisLogger.addListener(listener2);
		assertEquals("Not exactly three listener registered", originalListenerCount + 2, HybrisLogger.getAllAListeners().size());

		LOG.debug("test");
		assertNotNull("No event received", listener1.event);
		assertEquals("Wrong event received", "test", listener1.event.getMessage());

		assertNotNull("No event received", listener2.event);
		assertEquals("Wrong event received", "test", listener2.event.getMessage());

		HybrisLogger.removeListener(listener1);
		assertEquals("Not exactly two listener registered", originalListenerCount + 1, HybrisLogger.getAllAListeners().size());

		HybrisLogger.removeListener(listener2);
		assertEquals("Not exactly one listener registered", originalListenerCount + 0, HybrisLogger.getAllAListeners().size());
	}

	@After
	public void tearDown()
	{
		final List<HybrisLogListener> listeners = HybrisLogger.getAllAListeners();
		assertEquals("Not same amount of listeners regisered as at startUp: " + listeners, originalListenerCount, listeners.size());
	}

	private static class TestListener implements HybrisLogListener
	{
		public LoggingEvent event;

		@Override
		public boolean isEnabledFor(final Level level)
		{
			return true;
		}

		@Override
		public void log(final LoggingEvent event)
		{
			this.event = event;
		}
	}

	private static class TestLoggerThread extends Thread
	{
		public Exception caught;

		@Override
		public void run()
		{
			boolean running = true;
			while (running)
			{
				try
				{
					LOG.isDebugEnabled();
					LOG.debug("bla");
				}
				catch (final Exception e)
				{
					caught = e;
					running = false;
				}
				try
				{
					Thread.sleep(10);
				}
				catch (final InterruptedException e)
				{
					running = false;
				}
			}
		}
	}
}
