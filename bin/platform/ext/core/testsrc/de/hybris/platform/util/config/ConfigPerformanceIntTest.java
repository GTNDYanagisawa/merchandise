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
package de.hybris.platform.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.jdbcwrapper.JDBCConnectionPoolInterruptedException;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.test.TestThreadsHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.bethecoder.ascii_table.ASCIITable;


/**
 *
 */
@PerformanceTest
public class ConfigPerformanceIntTest extends ServicelayerBaseTest
{

	private static final String TEST_PROP_INT = "test.prop.int.";

	private static final int MAX_PROPS = 10000;
	private static final int DEFAULT_PROPERTY_VALUE = 2 * MAX_PROPS;

	private static final Logger LOG = Logger.getLogger(ConfigPerformanceIntTest.class.getName());

	protected final int WAIT_SECONDS = 40;
	protected final int THREADS = 50;

	protected final String[] randKeys = new String[MAX_PROPS];

	protected final Random rand = new Random(System.nanoTime());

	@Resource
	private ConfigurationService configurationService;

	@Before
	public void prepare()
	{
		for (int i = 0; i < MAX_PROPS; i++)
		{
			randKeys[i] = TEST_PROP_INT + rand.nextInt(MAX_PROPS);
		}


		for (int i = 0; i < MAX_PROPS; i++)
		{
			configurationService.getConfiguration().addProperty(TEST_PROP_INT + i, Integer.toString(i));
		}
	}


	@Test
	public void testOneThreadManyProperties()
	{
		runAccessor(2, "short values gets", new TestThreadsHolder<PropertyAccessor>(2, true)
		{
			@Override
			public PropertyAccessor newRunner(final int threadNumber)
			{
				return new ManySetPropertyAccessor(configurationService.getConfiguration());
			}
		});
	}


	@Test
	public void testManyThreadManyProperties()
	{
		runAccessor(THREADS, "short value gets ", new TestThreadsHolder<PropertyAccessor>(THREADS, true)
		{
			@Override
			public PropertyAccessor newRunner(final int threadNumber)
			{
				return new SetPropertyAccessor(configurationService.getConfiguration());
			}
		});
	}


	protected void runAccessor(final int threads, final String callType,
			final TestThreadsHolder<PropertyAccessor> randomAccessHolder)
	{

		randomAccessHolder.startAll();
		try
		{
			Thread.sleep(WAIT_SECONDS * 1000);
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		assertTrue("not all test threads shut down orderly", randomAccessHolder.stopAndDestroy(30));
		assertEquals("found worker errors", Collections.EMPTY_MAP, randomAccessHolder.getErrors());

		long totalCalls = 0;
		for (final PropertyAccessor r : randomAccessHolder.getRunners())
		{

			totalCalls += r.getAccessCount();
			if (!r.occurredErrors.isEmpty())
			{
				for (final Throwable t : r.occurredErrors)
				{
					LOG.error(t);
					Assert.fail(t.getMessage());
				}
			}

		}


		assertTrue(totalCalls > 0);


		final String[] header =
		{ callType, "duration [sec]", "threads", "opers/sec" };
		ASCIITable.getInstance().printTable(
				header,
				new String[][]
				{
				{//
				String.valueOf(totalCalls), String.valueOf(WAIT_SECONDS), String.valueOf(threads),
						String.valueOf(totalCalls / WAIT_SECONDS), // 
				} //
				});


	}


	class ManySetPropertyAccessor extends PropertyAccessor
	{
		long count = 0;

		ManySetPropertyAccessor(final Configuration cfg)
		{
			super(cfg);
		}

		@Override
		void access(final Configuration cfg)
		{
			for (int i = 0; i < MAX_PROPS / 2; i++)
			{
				final String key = randKeys[(int) (count++ % MAX_PROPS)];
				final int value = cfg.getInt(key, DEFAULT_PROPERTY_VALUE);
				if (value == DEFAULT_PROPERTY_VALUE)
				{
					throw new IllegalArgumentException("Incorrect value " + value + " for key " + key + ", expected ");
				}
				//cfg.setProperty(TEST_PROP_INT + idx, Integer.toString(value));
			}
		}

	}


	class SetPropertyAccessor extends PropertyAccessor
	{
		long count = 0;

		SetPropertyAccessor(final Configuration cfg)
		{
			super(cfg);

		}

		@Override
		void access(final Configuration cfg) throws IllegalArgumentException
		{
			final String key = randKeys[(int) (count++ % MAX_PROPS)];
			final int value = cfg.getInt(key, DEFAULT_PROPERTY_VALUE);
			if (value == DEFAULT_PROPERTY_VALUE)
			{
				throw new IllegalArgumentException("Invalid key value for key " + key);
			}
			//cfg.setProperty(TEST_PROP_INT + idx, Integer.toString(value));
		}
	}


	static protected abstract class PropertyAccessor implements Runnable
	{


		private final Configuration cfg;

		volatile long accessCount = 0;

		protected List<Throwable> occurredErrors = new ArrayList<Throwable>();


		PropertyAccessor(final Configuration cfg)
		{
			this.cfg = cfg;
		}

		@Override
		public void run()
		{
			long localAccessCount = 0;
			try
			{
				while (!Thread.currentThread().isInterrupted())
				{
					access(cfg);
					localAccessCount++;
				}
			}
			catch (final IllegalArgumentException e)
			{
				occurredErrors.add(e);
			}
			catch (final JDBCConnectionPoolInterruptedException e)
			{
				// this is ok
			}
			finally
			{
				accessCount = localAccessCount;
			}
		}

		public long getAccessCount()
		{
			return accessCount;
		}

		abstract void access(Configuration cfg) throws IllegalArgumentException;
	}
}
