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
package de.hybris.platform.sap.core.configuration.global.impl;

import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.impl.ConfigurationPropertyAccessImpl;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.log4j.Logger;


/**
 * Exemplary Implementation to read global configuration properties.
 */
public class SAPGlobalConfigurationServiceImpl implements SAPGlobalConfigurationService
{
	private static final Logger LOG = Logger.getLogger(SAPGlobalConfigurationServiceImpl.class.getName());

	private SAPGlobalConfigurationDAO globalConfigurationDAO = null;

	private final ThreadLocal<ConfigurationPropertyAccess> threadLocalBaseStoreConfigurationPropertyAccess = new ThreadLocal<ConfigurationPropertyAccess>()
	{
		@Override
		protected ConfigurationPropertyAccess initialValue()
		{
			final SAPGlobalConfigurationModel sapGlobalConfiguration = globalConfigurationDAO.getSAPGlobalConfiguration();
			if (sapGlobalConfiguration != null)
			{
				return new ConfigurationPropertyAccessImpl(sapGlobalConfiguration);
			}
			return null;
		}
	};

	/**
	 * Setter for the globalConfigurationDAO variable.
	 * 
	 * @param globalConfigurationDAO
	 *           the new value for the variable.
	 */
	public void setSapGlobalConfigurationDAO(final SAPGlobalConfigurationDAO globalConfigurationDAO)
	{
		this.globalConfigurationDAO = globalConfigurationDAO;
	}

	@Override
	public <T> T getProperty(final String propertyName)
	{
		return getSAPGlobalConfigurationCPA().getProperty(propertyName);
	}

	@Override
	public Map<String, Object> getAllProperties()
	{
		return getSAPGlobalConfigurationCPA().getAllProperties();
	}

	@Override
	public ConfigurationPropertyAccess getPropertyAccess(final String propertyAccessName)
	{
		return getSAPGlobalConfigurationCPA().getPropertyAccess(propertyAccessName);
	}

	@Override
	public Map<String, ConfigurationPropertyAccess> getAllPropertyAccesses()
	{
		return getSAPGlobalConfigurationCPA().getAllPropertyAccesses();
	}

	@Override
	public Collection<ConfigurationPropertyAccess> getPropertyAccessCollection(final String propertyAccessCollectionName)
	{
		return getSAPGlobalConfigurationCPA().getPropertyAccessCollection(propertyAccessCollectionName);
	}

	@Override
	public Map<String, Collection<ConfigurationPropertyAccess>> getAllPropertyAccessCollections()
	{
		return getSAPGlobalConfigurationCPA().getAllPropertyAccessCollections();
	}

	/**
	 * Returns the sap configuration configuration property access.
	 * 
	 * @return sap configuration configuration property access
	 */
	protected ConfigurationPropertyAccess getSAPGlobalConfigurationCPA()
	{
		SAPGlobalConfigurationModel sapGlobalConfiguration = globalConfigurationDAO.getSAPGlobalConfiguration();
		ConfigurationPropertyAccessImpl cpa = (ConfigurationPropertyAccessImpl) threadLocalBaseStoreConfigurationPropertyAccess
				.get();
		if (cpa != null && cpa.getConfigurationModel() != sapGlobalConfiguration
				&& cpa.getConfigurationModel().getCreationtime() != null) // do not reset dummy configuration
		{
			cpa = null;
		}
		if (cpa == null)
		{
			if (sapGlobalConfiguration == null) // create dummy configuration
			{
				sapGlobalConfiguration = new SAPGlobalConfigurationModel();
				LOG.warn("SAP Configuration exception: Access to SAP Global Configuration which does not exist on database yet! Empty configuration is used.");
			}
			cpa = new ConfigurationPropertyAccessImpl(sapGlobalConfiguration);
			threadLocalBaseStoreConfigurationPropertyAccess.set(cpa);
		}
		if (cpa == null || cpa.getConfigurationModel() == null)
		{
			LOG.fatal("SAP Configuration exception: no SAP Global Configuration exists!");
			throw new ConfigurationRuntimeException("SAP Configuration exception: no SAP Global Configuration exists!");
		}
		return cpa;
	}


}
