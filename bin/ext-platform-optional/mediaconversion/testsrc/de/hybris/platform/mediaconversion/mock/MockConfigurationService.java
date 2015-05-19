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
package de.hybris.platform.mediaconversion.mock;

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * @author pohl
 */
public class MockConfigurationService implements ConfigurationService
{
	private final Configuration config;

	public MockConfigurationService()
	{
		final Properties props=new Properties();
		final PlatformConfig platformConfig=ConfigUtil.getPlatformConfig(this.getClass());
		ConfigUtil.loadRuntimeProperties(props, platformConfig);
		this.config=new MapConfiguration(props);
	}

	@Override
	public Configuration getConfiguration() 
	{
		return this.config;
	}
}
