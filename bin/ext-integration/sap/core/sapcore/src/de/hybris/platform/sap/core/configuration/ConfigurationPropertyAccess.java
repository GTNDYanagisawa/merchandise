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
package de.hybris.platform.sap.core.configuration;

import java.util.Collection;
import java.util.Map;


/**
 * Interface to access configuration properties.
 */
public interface ConfigurationPropertyAccess
{

	/**
	 * Returns the value of the requested simple property.
	 * 
	 * @param <T>
	 *           type of the property value
	 * @param propertyName
	 *           name of property
	 * @return value of the requested property
	 */
	public <T> T getProperty(String propertyName);

	/**
	 * Returns all simple properties.
	 * 
	 * @return map of all simple properties (name/value pairs)
	 */
	public Map<String, Object> getAllProperties();

	/**
	 * Returns the requested related property access (1:1 relation).
	 * 
	 * @param propertyAccessName
	 *           name of property related property access
	 * @return value of the requested property
	 */
	public ConfigurationPropertyAccess getPropertyAccess(String propertyAccessName);

	/**
	 * Returns all related property accesses (1:1 relation).
	 * 
	 * @return map of all related property accesses (name/property access pairs)
	 */
	public Map<String, ConfigurationPropertyAccess> getAllPropertyAccesses();

	/**
	 * Returns the requested related property access collection (1:n relation).
	 * 
	 * @param propertyAccessCollectionName
	 *           name of property related property access
	 * @return value of the requested property
	 */
	public Collection<ConfigurationPropertyAccess> getPropertyAccessCollection(String propertyAccessCollectionName);

	/**
	 * Returns all related property access collections (1:n relation).
	 * 
	 * @return map of all related property access collections (name/property access collection pairs)
	 */
	public Map<String, Collection<ConfigurationPropertyAccess>> getAllPropertyAccessCollections();

}
