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

import de.hybris.platform.sap.core.configuration.GlobalConfigurationManager;
import de.hybris.platform.util.Utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Manager to access the global configuration information (scope singleton).
 */
public class DefaultGlobalConfigurationManager implements GlobalConfigurationManager
{

	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(DefaultGlobalConfigurationManager.class.getName());

	/**
	 * Map of all properties.
	 */
	private Map<Object, Object> properties = null;

	/**
	 * Map of globally registered module ids with a set of all their extensions.
	 */
	private final Map<String, Set<String>> moduleIds = new HashMap<String, Set<String>>();

	/**
	 * Sets the properties (only considered by injection!).
	 * 
	 * @param properties
	 *           Properties
	 */
	public void setProperties(final Map<Object, Object> properties)
	{
		this.properties = properties;
	}

	/**
	 * Reads the sap specific properties as e.g. module ids.
	 */
	public void init()
	{
		if (properties == null)
		{
			properties = Utilities.loadPlatformProperties();
		}
		for (final Entry<Object, Object> propertyEntry : properties.entrySet())
		{
			final String key = (String) propertyEntry.getKey();
			final int indexOf = key.indexOf(".sap.moduleId");
			if (indexOf > 0)
			{
				final String moduleId = (String) propertyEntry.getValue();
				Set<String> extensionNames = moduleIds.get(moduleId);
				if (extensionNames == null)
				{
					extensionNames = new HashSet<String>();
					moduleIds.put(moduleId, extensionNames);
				}
				final String extensionName = key.substring(0, indexOf);
				extensionNames.add(extensionName);
			}
		}
	}

	@Override
	public Set<String> getModuleIds()
	{
		return Collections.unmodifiableSet(moduleIds.keySet());
	}

	@Override
	public Set<String> getExtensionNames(final String moduleId)
	{
		final Set<String> extensionNames = moduleIds.get(moduleId);
		if (extensionNames == null)
		{
			log.warn("No extensions found for module id '" + moduleId + "'!");
		}
		return extensionNames;
	}

}
