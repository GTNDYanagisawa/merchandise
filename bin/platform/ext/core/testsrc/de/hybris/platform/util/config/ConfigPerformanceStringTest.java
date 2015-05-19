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

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.test.TestThreadsHolder;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@PerformanceTest
public class ConfigPerformanceStringTest extends ConfigPerformanceIntTest
{

	private static final String LONG_PROP_PREFIX = "testManyThreadManyProperties(de.hybris.platform.util.config.ConfigPerformanceTest)testOneThreadManyProperties(de.hybris.platform.util.config.ConfigPerformanceTest)";

	private static final String TEST_PROP_STRING = "test.prop.string.with.some.average.key.length";

	private static final int MAX_PROPS = 10000;

	private static final String DEFAULT_PROPERTY = "some blah".intern();

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ConfigPerformanceStringTest.class.getName());

	protected final int WAIT_SECONDS = 40;
	protected final int THREADS = 50;



	@Resource
	private ConfigurationService configurationService;

	@Override
	@Before
	public void prepare()
	{
		for (int i = 0; i < MAX_PROPS; i++)
		{
			randKeys[i] = TEST_PROP_STRING + rand.nextInt(MAX_PROPS);
		}

		for (int i = 0; i < MAX_PROPS; i++)
		{
			configurationService.getConfiguration().addProperty(TEST_PROP_STRING + i, LONG_PROP_PREFIX + Integer.toString(i));
		}
	}



	@Override
	@Test
	public void testManyThreadManyProperties()
	{
		runAccessor(THREADS, "long value gets", new TestThreadsHolder<PropertyAccessor>(THREADS, true)
		{
			@Override
			public PropertyAccessor newRunner(final int threadNumber)
			{
				return new SetPropertyTextAccessor(configurationService.getConfiguration());
			}
		});
	}

	@Override
	@Test
	public void testOneThreadManyProperties()
	{
		runAccessor(2, "long value gets", new TestThreadsHolder<PropertyAccessor>(2, true)
		{
			@Override
			public PropertyAccessor newRunner(final int threadNumber)
			{
				return new ManySetPropertyTextAccessor(configurationService.getConfiguration());
			}
		});
	}


	class ManySetPropertyTextAccessor extends PropertyAccessor
	{
		long count = 0;

		ManySetPropertyTextAccessor(final Configuration cfg)
		{
			super(cfg);
			// YTODO Auto-generated constructor stub
		}

		@Override
		void access(final Configuration cfg)
		{
			for (int i = 0; i < MAX_PROPS / 2; i++)
			{
				final String key = randKeys[(int) (count++ % MAX_PROPS)];
				final String value = cfg.getString(key, DEFAULT_PROPERTY);
				if (value == DEFAULT_PROPERTY)
				{
					throw new IllegalArgumentException("Incorrect value " + value + " for key " + key);
				}
				//cfg.setProperty(TEST_PROP_INT + idx, Integer.toString(value));
			}
		}
	}


	class SetPropertyTextAccessor extends PropertyAccessor
	{
		long count = 0;

		SetPropertyTextAccessor(final Configuration cfg)
		{
			super(cfg);
			// YTODO Auto-generated constructor stub
		}

		@Override
		void access(final Configuration cfg) throws IllegalArgumentException
		{
			final String key = randKeys[(int) (count++ % MAX_PROPS)];
			final String value = cfg.getString(key, DEFAULT_PROPERTY);

			if (value == DEFAULT_PROPERTY)
			{
				throw new IllegalArgumentException("Incorrect value " + value + " for key " + key);
			}
			//cfg.setProperty(TEST_PROP_INT + idx, Integer.toString(value));
		}

	}


}
