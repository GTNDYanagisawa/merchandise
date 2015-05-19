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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Default Implementation to read global configuration properties.
 */
public class DefaultSAPGlobalConfigurationService implements SAPGlobalConfigurationService
{

	private Map<String, Object> properties = new HashMap<String, Object>();
	private Map<String, ConfigurationPropertyAccess> propertyAccesses = new HashMap<String, ConfigurationPropertyAccess>();
	private Map<String, Collection<ConfigurationPropertyAccess>> propertyAccessCollections = new HashMap<String, Collection<ConfigurationPropertyAccess>>();


	/**
	 * Injection setter for properties.
	 * 
	 * @param properties
	 *           properties
	 */
	public void setProperties(final Map<String, Object> properties)
	{
		this.properties = properties;
	}

	/**
	 * Injection setter for property accesses (1:1 relations).
	 * 
	 * @param propertyAccesses
	 *           property accesses
	 */
	public void setPropertyAccesses(final Map<String, ConfigurationPropertyAccess> propertyAccesses)
	{
		this.propertyAccesses = propertyAccesses;
	}

	/**
	 * Injection setter for property access collections (1:n relations).
	 * 
	 * @param propertyAccessCollections
	 *           property access collections
	 */
	public void setPropertyAccessCollections(final Map<String, Collection<ConfigurationPropertyAccess>> propertyAccessCollections)
	{
		this.propertyAccessCollections = propertyAccessCollections;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(final String propertyName)
	{
		return (T) properties.get(propertyName);
	}

	@Override
	public Map<String, Object> getAllProperties()
	{
		return properties;
	}

	@Override
	public ConfigurationPropertyAccess getPropertyAccess(final String propertyAccessName)
	{
		return propertyAccesses.get(propertyAccessName);
	}

	@Override
	public Map<String, ConfigurationPropertyAccess> getAllPropertyAccesses()
	{
		return propertyAccesses;
	}

	@Override
	public Collection<ConfigurationPropertyAccess> getPropertyAccessCollection(final String propertyAccessCollectionName)
	{
		return propertyAccessCollections.get(propertyAccessCollectionName);
	}

	@Override
	public Map<String, Collection<ConfigurationPropertyAccess>> getAllPropertyAccessCollections()
	{
		return propertyAccessCollections;
	}

}
