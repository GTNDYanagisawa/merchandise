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
package de.hybris.platform.sap.core.module.impl;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.sap.core.configuration.ConfigurationConstants;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.constants.SapcoreConstants;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Access to module configuration.
 */
public class ModuleConfigurationAccessImpl implements ModuleConfigurationAccess
{

	private SAPConfigurationService configurationService = null;
	private String moduleId = null;

	private final Map<String, Object> localBaseStoreProperties = new HashMap<String, Object>();
	private Map<String, Object> localProperties = new HashMap<String, Object>();
	private Map<String, ConfigurationPropertyAccess> localPropertyAccesses = new HashMap<String, ConfigurationPropertyAccess>();
	private Map<String, Collection<ConfigurationPropertyAccess>> localPropertyAccessCollections = new HashMap<String, Collection<ConfigurationPropertyAccess>>();

	/**
	 * Injection setter for SAP configuration service.
	 * 
	 * @param configurationService
	 *           SAP Configuration service
	 */
	public void setConfigurationService(final SAPConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Injection setter for module id.
	 * 
	 * @param moduleId
	 *           Module id to be set
	 */
	public void setModuleId(final String moduleId)
	{
		this.moduleId = moduleId;
	}

	/**
	 * Injection setter for Base Store properties.
	 * 
	 * @param baseStoreProperties
	 *           Base Store properties to set
	 */
	public void setBaseStoreProperties(final Map<String, Object> baseStoreProperties)
	{
		this.localBaseStoreProperties.putAll(baseStoreProperties);
	}

	/**
	 * Injection setter for properties.
	 * 
	 * @param properties
	 *           properties
	 */
	public void setProperties(final Map<String, Object> properties)
	{
		this.localProperties = properties;
	}

	/**
	 * Injection setter for property accesses (1:1 relations).
	 * 
	 * @param propertyAccesses
	 *           property accesses
	 */
	public void setPropertyAccesses(final Map<String, ConfigurationPropertyAccess> propertyAccesses)
	{
		this.localPropertyAccesses = propertyAccesses;
	}

	/**
	 * Injection setter for property access collections (1:n relations).
	 * 
	 * @param propertyAccessCollections
	 *           property access collections
	 */
	public void setPropertyAccessCollections(final Map<String, Collection<ConfigurationPropertyAccess>> propertyAccessCollections)
	{
		this.localPropertyAccessCollections = propertyAccessCollections;
	}

	/**
	 * Initializes the module configuration access.
	 */
	public void init()
	{
		if (moduleId == null || moduleId.isEmpty())
		{
			throw new CoreBaseRuntimeException("Missing module id for module configuration access implemented in class "
					+ this.getClass().getName());
		}
	}

	@Override
	public boolean isSAPConfigurationActive()
	{
		return configurationService.isSAPConfigurationActive();
	}

	@Override
	public boolean isBaseStoreActive()
	{
		return configurationService.isBaseStoreActive();
	}

	@Override
	public String getSAPConfigurationName()
	{
		return (String) getProperty(ConfigurationConstants.SAP_CONFIGURATION_NAME_ATTRIBUTE);
	}

	@Override
	public String getModuleId()
	{
		return moduleId;
	}

	@Override
	public String getBackendType()
	{
		return configurationService.getBackendType();
	}

	@Override
	public RFCDestination getRFCDestination()
	{
		return configurationService.getRFCDestination();
	}

	@Override
	public Object getBaseStoreProperty(final String property)
	{
		if (localBaseStoreProperties.get(property) != null)
		{
			return localBaseStoreProperties.get(property);
		}
		return configurationService.getBaseStoreProperty(property);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(final String propertyName)
	{
		if (localProperties.containsKey(propertyName))
		{
			return (T) localProperties.get(propertyName);
		}
		return configurationService.getProperty(propertyName);
	}

	@Override
	public Map<String, Object> getAllProperties()
	{
		final HashMap<String, Object> tmpProperties = new HashMap<String, Object>();
		tmpProperties.putAll(localProperties);
		tmpProperties.putAll(configurationService.getAllProperties());
		return tmpProperties;
	}

	@Override
	public ConfigurationPropertyAccess getPropertyAccess(final String propertyAccessName)
	{
		if (localPropertyAccesses.containsKey(propertyAccessName))
		{
			return localPropertyAccesses.get(propertyAccessName);
		}
		return configurationService.getPropertyAccess(propertyAccessName);
	}

	@Override
	public Map<String, ConfigurationPropertyAccess> getAllPropertyAccesses()
	{
		final HashMap<String, ConfigurationPropertyAccess> tmpPropertyAccesses = new HashMap<String, ConfigurationPropertyAccess>();
		tmpPropertyAccesses.putAll(localPropertyAccesses);
		tmpPropertyAccesses.putAll(configurationService.getAllPropertyAccesses());
		return tmpPropertyAccesses;
	}

	@Override
	public Collection<ConfigurationPropertyAccess> getPropertyAccessCollection(final String propertyAccessCollectionName)
	{
		if (localPropertyAccessCollections.containsKey(propertyAccessCollectionName))
		{
			return localPropertyAccessCollections.get(propertyAccessCollectionName);
		}
		return configurationService.getPropertyAccessCollection(propertyAccessCollectionName);
	}

	@Override
	public Map<String, Collection<ConfigurationPropertyAccess>> getAllPropertyAccessCollections()
	{
		final HashMap<String, Collection<ConfigurationPropertyAccess>> tmpPropertyAccessCollections = new HashMap<String, Collection<ConfigurationPropertyAccess>>();
		tmpPropertyAccessCollections.putAll(localPropertyAccessCollections);
		tmpPropertyAccessCollections.putAll(configurationService.getAllPropertyAccessCollections());
		return tmpPropertyAccessCollections;
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreConstants.CRLF + "- Locale Properties: " + localProperties + SapcoreConstants.CRLF
				+ "- Locale Property Accesses: " + localPropertyAccesses + SapcoreConstants.CRLF
				+ "- Locale Property Access Collections: " + localPropertyAccessCollections + SapcoreConstants.CRLF
				+ "- Configuration Service: " + configurationService;
	}

}
