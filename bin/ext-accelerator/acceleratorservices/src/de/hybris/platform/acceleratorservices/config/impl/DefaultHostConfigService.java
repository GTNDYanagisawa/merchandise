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
package de.hybris.platform.acceleratorservices.config.impl;

import de.hybris.platform.acceleratorservices.config.ConfigLookup;
import de.hybris.platform.acceleratorservices.config.HostConfigService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of the HostConfigService.
 * Uses the hybris config service to lookup host specific properties.
 */
public class DefaultHostConfigService implements HostConfigService
{
	private ConfigurationService configurationService;
	private UiExperienceService uiExperienceService;

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}


	@Override
	public String getProperty(final String property, final String hostname)
	{
		Assert.notNull(property, "property must not be null");
		Assert.notNull(hostname, "hostname must not be null");

		final Configuration configuration = getConfigurationService().getConfiguration();

		// Try the host and UiExperience
		final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
		if (uiExperienceLevel != null)
		{
			final String key = property + "." + hostname + "." + uiExperienceLevel.getCode();
			final String propertyValue = configuration.getString(key, null);
			if (propertyValue != null)
			{
				return propertyValue;
			}
		}

		// Try the host on its own
		{
			final String key = property + "." + hostname;
			final String propertyValue = configuration.getString(key, null);
			if (propertyValue != null)
			{
				return propertyValue;
			}
		}

		// Try the UiExperience on its own
		if (uiExperienceLevel != null)
		{
			final String key = property + "." + uiExperienceLevel.getCode();
			final String propertyValue = configuration.getString(key, null);
			if (propertyValue != null)
			{
				return propertyValue;
			}
		}

		// Fallback to the property key only
		return configuration.getString(property, null);
	}

	@Override
	public ConfigLookup getConfigForHost(final String hostname)
	{
		return new HostConfigLookup(this, hostname);
	}

	public class HostConfigLookup extends AbstractConfigLookup
	{
		private final HostConfigService hostConfigService;
		private final String hostname;

		public HostConfigLookup(final HostConfigService hostConfigService, final String hostname)
		{
			this.hostConfigService = hostConfigService;
			this.hostname = hostname;
		}

		@Override
		protected String getProperty(final String property)
		{
			return hostConfigService.getProperty(property, hostname);
		}
	}
}
