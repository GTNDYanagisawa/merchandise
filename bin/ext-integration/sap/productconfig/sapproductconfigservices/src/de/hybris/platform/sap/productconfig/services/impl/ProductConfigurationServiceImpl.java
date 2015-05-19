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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class ProductConfigurationServiceImpl implements ProductConfigurationService
{

	private static class ProviderLock
	{
		//used as lock object
	}

	private static final Logger LOG = Logger.getLogger(ProductConfigurationServiceImpl.class);

	private static int maxLocksPerMap = 1024;

	private ConfigurationProviderFactory configurationProviderFactory;
	private static Map<String, ProviderLock> locks = new HashMap<>(maxLocksPerMap);
	private static Map<String, ProviderLock> oldLocks = new HashMap<>(maxLocksPerMap);

	private SessionService sessionService;

	static final String SESSION_CACHE_KEY_PREFIX = ProductConfigurationServiceImpl.class.getSimpleName() + "_last_"
			+ Configuration.class.getSimpleName() + "_";

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		// no need to synchronize create, because config session (identified by the config ID)
		// is only exposed once the object has been created
		final ConfigModel config = getConfigurationProvider().createDefaultConfiguration(kbKey);
		cacheConfig(config);

		return config;

	}


	@Override
	public void updateConfiguration(final ConfigModel model)
	{
		final String id = model.getId();
		final ProviderLock lock = ProductConfigurationServiceImpl.getLock(id);
		synchronized (lock)
		{

			final boolean updateExecuted = getConfigurationProvider().updateConfiguration(model);
			if (updateExecuted)
			{
				LOG.debug("Config with id '" + model.getId() + "' updated, removing it from cache");
				removeConfigFromCache(id);
			}
		}
	}


	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		final ProviderLock lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{

			ConfigModel cachedModel = sessionService.getAttribute(SESSION_CACHE_KEY_PREFIX + configId);
			if (cachedModel == null)
			{
				cachedModel = getConfigurationProvider().retrieveConfigurationModel(configId);
				cacheConfig(configId, cachedModel);
			}
			else
			{
				LOG.debug("Config with id '" + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}

	@Override
	public String retrieveExternalConfiguration(final String configId)
	{
		final ProviderLock lock = getLock(configId);
		synchronized (lock)
		{
			return getConfigurationProvider().retrieveExternalConfiguration(configId);
		}
	}

	public void setConfigurationProviderFactory(final ConfigurationProviderFactory configurationProviderFactory)
	{
		this.configurationProviderFactory = configurationProviderFactory;
	}

	public static void setMaxLocksPerMap(final int maxLocksPerMap)
	{
		ProductConfigurationServiceImpl.maxLocksPerMap = maxLocksPerMap;
	}

	protected static int getMaxLocksPerMap()
	{
		return ProductConfigurationServiceImpl.maxLocksPerMap;
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		final ConfigurationProvider configurationProvider = configurationProviderFactory.getProvider();
		return configurationProvider;
	}


	protected static ProviderLock getLock(final String configId)
	{
		synchronized (ProviderLock.class)
		{

			ProviderLock lock = locks.get(configId);
			if (lock == null)
			{
				lock = oldLocks.get(configId);
				if (lock == null)
				{
					ensureThatLockMapIsNotTooBig();
					lock = new ProviderLock();
					locks.put(configId, lock);
				}
			}
			return lock;
		}
	}


	protected static void ensureThatLockMapIsNotTooBig()
	{
		if (locks.size() >= maxLocksPerMap)
		{
			oldLocks.clear();
			oldLocks = locks;
			locks = new HashMap<>(maxLocksPerMap);
		}

	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(kbKey, externalConfiguration);
		cacheConfig(config);

		return config;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(extConfig);
		cacheConfig(config);

		return config;
	}


	@Override
	public void releaseSession(final String configId)
	{
		LOG.debug("Releasing config session with id " + configId);
		getConfigurationProvider().releaseSession(configId);
		removeConfigFromCache(configId);
	}

	protected void removeConfigFromCache(final String configId)
	{
		LOG.debug("Removing config with id '" + configId + "' from cache");
		sessionService.removeAttribute(SESSION_CACHE_KEY_PREFIX + configId);
	}

	protected void cacheConfig(final ConfigModel config)
	{
		cacheConfig(config.getId(), config);
	}

	protected void cacheConfig(final String configId, final ConfigModel config)
	{
		sessionService.setAttribute(SESSION_CACHE_KEY_PREFIX + configId, config);
		LOG.debug("Config with id '" + configId + "' read frist time, caching it for further access");
	}

	protected ConfigurationProviderFactory getConfigurationProviderFactory()
	{
		return configurationProviderFactory;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
