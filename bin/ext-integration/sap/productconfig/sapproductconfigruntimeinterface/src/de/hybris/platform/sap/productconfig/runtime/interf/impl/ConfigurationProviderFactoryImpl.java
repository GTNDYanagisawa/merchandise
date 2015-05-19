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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.session.SessionService;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;


public class ConfigurationProviderFactoryImpl implements ConfigurationProviderFactory
{

	private final static Logger LOG = Logger.getLogger(ConfigurationProviderFactoryImpl.class);

	private SessionService sessionService;
	private ApplicationContext applicationContext;

	/**
	 * used for tests
	 */
	void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}


	static final String SESSION_CACHE_KEY = ConfigurationProvider.class.getName();

	@Override
	public ConfigurationProvider getProvider()
	{
		ConfigurationProvider provider = sessionService.getAttribute(SESSION_CACHE_KEY);
		if (provider == null)
		{
			provider = createProviderInstance();
			sessionService.setAttribute(SESSION_CACHE_KEY, provider);
		}

		return provider;
	}

	protected ConfigurationProvider createProviderInstance()
	{
		ConfigurationProvider provider;

		if (applicationContext == null)
		{
			applicationContext = ServicelayerUtils.getApplicationContext();
		}

		if (applicationContext == null)
		{
			throw new IllegalStateException("Application Context not available");
		}

		final String providerBeanName = "sapProductConfigConfigurationProvider";

		provider = (ConfigurationProvider) applicationContext.getBean(providerBeanName);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("created a new configuration provider instance");
		}

		return provider;

	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	protected ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}
}
