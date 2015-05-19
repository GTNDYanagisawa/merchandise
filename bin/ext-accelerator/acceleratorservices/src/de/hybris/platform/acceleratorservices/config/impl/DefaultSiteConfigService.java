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

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Default implementation of site config service
 */
public class DefaultSiteConfigService extends AbstractConfigLookup implements SiteConfigService
{
	private BaseSiteService baseSiteService;
	private UiExperienceService uiExperienceService;
	private ConfigurationService configurationService;

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
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
	public String getProperty(final String property)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		Assert.notNull(currentBaseSite, "BaseSite should not be null");

		final Configuration configuration = getConfigurationService().getConfiguration();
		final String currentBaseSiteUid = currentBaseSite.getUid();

		// Try the site UID and UiExperience
		final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
		if (uiExperienceLevel != null)
		{
			final String key = property + "." + currentBaseSiteUid + "." + uiExperienceLevel.getCode();
			final String propertyValue = configuration.getString(key, null);
			if (propertyValue != null)
			{
				return propertyValue;
			}
		}

		// Try the site UID on its own
		{
			final String key = property + "." + currentBaseSiteUid;
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
}
