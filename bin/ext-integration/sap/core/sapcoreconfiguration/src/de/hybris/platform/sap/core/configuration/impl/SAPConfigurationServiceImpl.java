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
package de.hybris.platform.sap.core.configuration.impl;

import de.hybris.platform.sap.core.configuration.ConfigurationConstants;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.impl.RFCDestinationImpl;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.log4j.Logger;


/**
 * Model implementation of {@link SAPConfigurationService}.
 */
public class SAPConfigurationServiceImpl implements SAPConfigurationService
{
	private static final Logger LOG = Logger.getLogger(SAPConfigurationServiceImpl.class.getName());

	private BaseStoreService baseStoreService = null;

	private final ThreadLocal<ConfigurationPropertyAccess> threadLocalBaseStoreConfigurationPropertyAccess = new ThreadLocal<ConfigurationPropertyAccess>()
	{
		@Override
		protected ConfigurationPropertyAccess initialValue()
		{
			final BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
			if (currentBaseStore != null)
			{
				return new ConfigurationPropertyAccessImpl(currentBaseStore);
			}
			return null;
		}
	};

	/**
	 * Injection setter for {@link BaseStoreService}.
	 * 
	 * @param baseStoreService
	 *           {@link BaseStoreService} to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * Returns the {@link BaseStoreService}.
	 * 
	 * @return {@link BaseStoreService}
	 */
	public BaseStoreService getBaseStoreService()
	{
		return this.baseStoreService;
	}

	@Override
	public boolean isSAPConfigurationActive()
	{
		return (isBaseStoreActive() && getSAPConfigurationCPAInternal() != null);
	}

	@Override
	public boolean isBaseStoreActive()
	{
		return (getBaseStoreCPAInternal() != null);
	}

	@Override
	public String getSAPConfigurationName()
	{
		return (String) getProperty(ConfigurationConstants.SAP_CONFIGURATION_NAME_ATTRIBUTE);
	}

	@Override
	public <T> T getProperty(final String propertyName)
	{
		return getSAPConfigurationCPA().getProperty(propertyName);
	}

	@Override
	public Map<String, Object> getAllProperties()
	{
		return getSAPConfigurationCPA().getAllProperties();
	}

	@Override
	public ConfigurationPropertyAccess getPropertyAccess(final String propertyAccessName)
	{
		return getSAPConfigurationCPA().getPropertyAccess(propertyAccessName);
	}

	@Override
	public Map<String, ConfigurationPropertyAccess> getAllPropertyAccesses()
	{
		return getSAPConfigurationCPA().getAllPropertyAccesses();
	}

	@Override
	public Collection<ConfigurationPropertyAccess> getPropertyAccessCollection(final String propertyAccessCollectionName)
	{
		return getSAPConfigurationCPA().getPropertyAccessCollection(propertyAccessCollectionName);
	}

	@Override
	public Map<String, Collection<ConfigurationPropertyAccess>> getAllPropertyAccessCollections()
	{
		return getSAPConfigurationCPA().getAllPropertyAccessCollections();
	}

	@Override
	public Object getBaseStoreProperty(final String propertyName)
	{
		return getBaseStoreCPA().getProperty(propertyName);
	}

	@Override
	public String getBackendType()
	{
		final String backendType = getRFCDestination().getBackendType();
		if (backendType == null || backendType.isEmpty())
		{
			throw new ConfigurationRuntimeException("No Backend type is defined for RFC Destination '"
					+ getRFCDestination().getRfcDestinationName() + "'!");
		}
		return backendType;
	}

	@Override
	public RFCDestination getRFCDestination()
	{
		final ConfigurationPropertyAccessImpl cpaSAPRFCDestination = (ConfigurationPropertyAccessImpl) getSAPConfigurationCPA()
				.getPropertyAccess(SAPConfigurationModel.SAPRFCDESTINATION);
		if (cpaSAPRFCDestination == null || cpaSAPRFCDestination.getConfigurationModel() == null)
		{
			final String errorLog = "SAP Configuration service exception: No RFC Destination is maintained for SAP Base Store Configuration '"
					+ getSAPConfigurationName() + "'!";
			LOG.fatal(errorLog);
			throw new ConfigurationRuntimeException(errorLog);
		}
		return new RFCDestinationImpl((SAPRFCDestinationModel) cpaSAPRFCDestination.getConfigurationModel());
	}

	/**
	 * Returns the base store configuration property access.
	 * 
	 * @return base store configuration property access
	 */
	private ConfigurationPropertyAccess getBaseStoreCPA()
	{
		final ConfigurationPropertyAccess cpa = getBaseStoreCPAInternal();
		if (cpa == null)
		{
			final String errorLog = "SAP Configuration service exception: No Base Store active!";
			LOG.fatal(errorLog);
			throw new ConfigurationRuntimeException(errorLog);
		}
		return cpa;
	}

	/**
	 * Returns the base store configuration property access w/o check.
	 * 
	 * @return base store configuration property access
	 */
	private ConfigurationPropertyAccess getBaseStoreCPAInternal()
	{
		final BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
		ConfigurationPropertyAccessImpl configurationPropertyAccess = (ConfigurationPropertyAccessImpl) threadLocalBaseStoreConfigurationPropertyAccess
				.get();
		if (configurationPropertyAccess != null && configurationPropertyAccess.getConfigurationModel() != currentBaseStore)
		{
			configurationPropertyAccess = null;
		}
		if (configurationPropertyAccess == null && currentBaseStore != null)
		{
			configurationPropertyAccess = new ConfigurationPropertyAccessImpl(currentBaseStore);
		}
		threadLocalBaseStoreConfigurationPropertyAccess.set(configurationPropertyAccess);
		return configurationPropertyAccess;
	}

	/**
	 * Returns the sap configuration configuration property access.
	 * 
	 * @return sap configuration configuration property access
	 */
	private ConfigurationPropertyAccess getSAPConfigurationCPA()
	{
		final ConfigurationPropertyAccess cpa = getSAPConfigurationCPAInternal();
		if (cpa == null)
		{
			final String errorLog = "SAP Configuration service exception: No SAP Base Store Configuration assigned to active Base Store!";
			LOG.fatal(errorLog);
			throw new ConfigurationRuntimeException(errorLog);
		}
		return cpa;
	}

	/**
	 * Returns the sap configuration configuration property access w/o check.
	 * 
	 * @return sap configuration configuration property access
	 */
	private ConfigurationPropertyAccess getSAPConfigurationCPAInternal()
	{
		return getBaseStoreCPA().getPropertyAccess(BaseStoreModel.SAPCONFIGURATION);
	}

	@Override
	public String toString()
	{
		String result = super.toString() + SapcoreconfigurationConstants.CRLF;
		result += "SAP Base Store Configuration: " + SapcoreconfigurationConstants.CRLF;
		ConfigurationPropertyAccess configurationPropertyAccess = getSAPConfigurationCPAInternal();
		if (configurationPropertyAccess != null)
		{
			for (final Entry<String, Object> propertyEntry : configurationPropertyAccess.getAllProperties().entrySet())
			{
				result += "- " + propertyEntry.getKey() + ": " + propertyEntry.getValue() + SapcoreconfigurationConstants.CRLF;
			}
		}
		else
		{
			result += "- No SAP Base Store Configuration found!" + SapcoreconfigurationConstants.CRLF;
		}
		configurationPropertyAccess = getBaseStoreCPAInternal();
		result += "Base Store: " + SapcoreconfigurationConstants.CRLF;
		if (configurationPropertyAccess != null)
		{
			for (final Entry<String, Object> propertyEntry : configurationPropertyAccess.getAllProperties().entrySet())
			{
				result += "- " + propertyEntry.getKey() + ": " + propertyEntry.getValue() + SapcoreconfigurationConstants.CRLF;
			}
		}
		else
		{
			result += "- No Base Store found!" + SapcoreconfigurationConstants.CRLF;
		}
		return result;
	}

}
