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
package de.hybris.platform.servicelayer.config;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.config.impl.DefaultConfigurationService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;



@IntegrationTest
public class ConfigServiceTest extends ServicelayerTransactionalBaseTest
{
	private static final Logger log = Logger.getLogger(ConfigServiceTest.class);

	@Resource
	private final DefaultConfigurationService configurationService = null;

	/**
	 * General testing whether delegating requests to hybris config works well. Test compares configuration values from
	 * jalo with results of configuration values from servicelayer.
	 */
	@Test
	public void testJaloVsCommons()
	{
		//take the jalo configuration as master (expected)
		final Map<String, String> values = Registry.getCurrentTenant().getConfig().getAllParameters();

		//and compare with commons configuration (actual) 
		for (final Map.Entry<String, String> entry : values.entrySet())
		{
			final String key = entry.getKey();
			final String expected = entry.getValue();
			final String actual = this.configurationService.getConfiguration().getString(key);

			//assert as string 
			Assert.assertEquals(expected, actual);

			//assert convenience methods (boolean)
			if ("true".equals(actual) || "false".equals(actual))
			{
				final boolean bExpected = Boolean.parseBoolean(actual);
				final boolean bActual = this.configurationService.getConfiguration().getBoolean(key);
				Assert.assertEquals(Boolean.valueOf(bExpected), Boolean.valueOf(bActual));
			}
		}
	}

	/**
	 * Test firing a {@link ConfigurationListener}
	 */
	@Test
	public void testConfigListeners()
	{
		final Set<Boolean> wasInvoked = new HashSet<Boolean>();
		final ConfigurationListener listener = new ConfigurationListener()
		{
			@Override
			public void configurationChanged(final ConfigurationEvent event)
			{
				wasInvoked.add(Boolean.TRUE);
			}
		};

		((AbstractConfiguration) this.configurationService.getConfiguration()).addConfigurationListener(listener);
		final String oldValue = configurationService.getConfiguration().getString("build.description");
		this.configurationService.getConfiguration().setProperty("build.description", "new description");

		Assert.assertTrue(wasInvoked.contains(Boolean.TRUE));

		wasInvoked.remove(Boolean.TRUE);
		((AbstractConfiguration) this.configurationService.getConfiguration()).removeConfigurationListener(listener);
		this.configurationService.getConfiguration().setProperty("build.description", oldValue);

		Assert.assertTrue(wasInvoked.isEmpty());
	}

	//sandbox code
	public void testOldVsNewPerformance()
	{
		final Map<String, String> all = Registry.getCurrentTenant().getConfig().getAllParameters();

		long count1 = 0;
		final long start1 = System.currentTimeMillis();
		for (final Map.Entry<String, String> entry : all.entrySet())
		{
			final String key = entry.getKey();
			//log.info(key + "=" + Registry.getCurrentTenant().getConfig().getString(key, null));
			Registry.getCurrentTenant().getConfig().getString(key, null);
			count1++;
		}
		final long end1 = System.currentTimeMillis();
		log.info("1)Processed " + count1 + " requests; took " + (end1 - start1) + "ms");

		long count2 = 0;
		final long start2 = System.currentTimeMillis();
		final Configuration config2 = this.configurationService.getConfiguration();
		for (final Iterator<String> iter = config2.getKeys(); iter.hasNext();)
		{
			final String key = iter.next();
			//log.info(key + "=" + Registry.getCurrentTenant().getConfig().getString(key, null));
			config2.getString(key, null);
			count2++;
		}
		final long end2 = System.currentTimeMillis();
		log.info("2)Processed " + count2 + " requests; took " + (end2 - start2) + "ms");
		log.info("1)Processed " + count1 + " requests; took " + (end1 - start1) + "ms");

	}


}
