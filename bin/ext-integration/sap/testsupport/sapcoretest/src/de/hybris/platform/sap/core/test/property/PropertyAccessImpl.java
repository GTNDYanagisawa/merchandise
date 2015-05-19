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
package de.hybris.platform.sap.core.test.property;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


/**
 * This classes handles loading and access to property files. It utilizes the java.util.Property class.<br>
 * 
 */
class PropertyAccessImpl implements PropertyAccess
{

	protected LinkedProperties allProperties; // NOPMD
	protected List<String> propertiesFilePaths; // NOPMD
	protected String propertiesPathPrefix; // NOPMD

	/**
	 * Default Constructor.
	 */
	public PropertyAccessImpl()
	{
		propertiesFilePaths = new ArrayList<String>(4);
		propertiesPathPrefix = "";
		allProperties = new DynamicProperties(null);
	}

	@Override
	public void addPropertyFile(final String path)
	{
		propertiesFilePaths.add(path);
	}

	@Override
	public boolean getBooleanProperty(final String key)
	{
		return Boolean.valueOf(allProperties.getProperty(key)).booleanValue();
	}

	@Override
	public boolean getBooleanProperty(final String key, final boolean defaultValue)
	{
		if (isPropertySet(key))
		{
			return getBooleanProperty(key);
		}
		else
		{
			return defaultValue;
		}
	}

	@Override
	public List<String> getStringList(final String key)
	{
		return getStringList(key, ",");
	}

	@Override
	public List<String> getStringList(final String key, final String separator)
	{
		final String units = getStringProperty(key);
		return Arrays.asList(units.split(separator));
	}

	@Override
	public String getStringProperty(final String key)
	{

		final String value = allProperties.getProperty(key);
		if (value == null)
		{
			throw new IllegalArgumentException("Property with key '" + key + "' not found in files '"
					+ propertiesFilePaths.toString() + "'");
		}
		return value;
	}

	@Override
	public String getStringProperty(final String key, final String defaultValue)
	{
		if (isPropertySet(key))
		{
			return getStringProperty(key);
		}
		else
		{
			return defaultValue;
		}
	}

	@Override
	public boolean isPropertySet(final String key)
	{
		return null != allProperties.getProperty(key);
	}

	/**
	 * Loads additional property files depending on {@link PropertyAccess#PROP_KEY_ENV_ADDITIONAL_PROPS_SUFFIX}.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	protected void loadAdditionalProperties() throws IOException
	{

		if (isPropertySet(PROP_KEY_ENV_ADDITIONAL_PROPS_SUFFIX)
				&& !getStringProperty(PROP_KEY_ENV_ADDITIONAL_PROPS_SUFFIX).isEmpty())
		{
			final String propSuffixes = getStringProperty(PROP_KEY_ENV_ADDITIONAL_PROPS_SUFFIX);

			final List<String> suffixes = Arrays.asList(propSuffixes.split(","));
			final Iterator<String> suffixesItr = suffixes.iterator();

			while (suffixesItr.hasNext())
			{
				final String suffix = suffixesItr.next();
				for (String path : propertiesFilePaths)
				{
					path = propertiesPathPrefix + appendSuffixToFilePath(path, suffix);
					final File propFile = new File(path);
					// system specific properties are optional
					if (propFile.exists())
					{
						loadPropertyFile(path);
					}
				}
			}
		}

	}

	/**
	 * Loads language dependent properties file depending on {@link PropertyAccess#PROP_KEY_ENV_LOCALE}.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	protected void loadLanguageDependentProperties() throws IOException
	{

		if (isPropertySet(PROP_KEY_ENV_LOCALE))
		{
			final String suffix = getStringProperty(PROP_KEY_ENV_LOCALE);

			for (String path : this.propertiesFilePaths)
			{
				path = propertiesPathPrefix + appendSuffixToFilePath(path, suffix);
				final File propFile = new File(path);
				// language specific properties are optional
				if (propFile.exists())
				{
					loadPropertyFile(path);
				}

			}
		}
	}

	@Override
	public void loadProperties() throws IOException
	{

		for (final String path : propertiesFilePaths)
		{
			loadPropertyFile(propertiesPathPrefix + path);
		}

		loadAdditionalProperties();

		loadLanguageDependentProperties();

		// add dynamic property as the last in the linked list
		allProperties = new DynamicProperties(allProperties);
	}

	/**
	 * Load a property file given by a path.
	 * 
	 * @param path
	 *           path of property file
	 * @throws IOException
	 *            {@link IOException}
	 */
	protected void loadPropertyFile(final String path) throws IOException
	{

		final PropertiesWithSource newProperties = new PropertiesWithSource(allProperties, path);
		newProperties.loadFromFile();
		allProperties = newProperties;
	}


	@Override
	public void setPropertyPathPrefix(final String propertiesPathPrefix)
	{
		this.propertiesPathPrefix = propertiesPathPrefix;
	}



	@Override
	public void setStringProperty(final String propKey, final String value) throws FileNotFoundException, IOException
	{
		this.allProperties.setProperty(propKey, value);

	}

	@Override
	public String toString()
	{
		LinkedProperties properties = this.allProperties;

		final StringBuilder output = new StringBuilder(1000);
		while (properties != null)
		{
			if (!properties.isEmpty())
			{
				output.append("*** BEGIN PROPERTY FILE: ");
				output.append(properties.getInfo());
				output.append(" ***\n");

				// add properties as strings in a string array in order to sort them
				final List<String> propertiesAsStrings = new ArrayList<String>(100);
				for (final Entry<Object, Object> entry : properties.entrySet())
				{
					final String propKey = entry.getKey().toString();
					final String propValue = entry.getValue().toString();
					final String propEntryAsString = propKey + " = " + propValue;
					propertiesAsStrings.add(propEntryAsString);
				}

				// sort this properties
				if (!propertiesAsStrings.isEmpty())
				{
					Collections.sort(propertiesAsStrings);
					for (final String prop : propertiesAsStrings)
					{
						output.append(prop);
						output.append("\n");
					}
					output.append("*** END PROPERTY FILE: " + properties.getInfo() + " ***\n");
				}
			}
			properties = properties.getParent();
		}

		output.append("\n");

		return output.toString();

	}

	/**
	 * Appends suffix to file path.
	 * 
	 * @param path
	 *           file path
	 * @param suffix
	 *           suffix
	 * @return file path with suffix
	 */
	private String appendSuffixToFilePath(String path, final String suffix)
	{
		final String[] pathArray = path.split("\\.");
		path = "";
		for (int i = 0; i < pathArray.length - 1; i++)
		{
			path = path + pathArray[i];
		}
		path = path + "_" + suffix + "." + pathArray[pathArray.length - 1];
		return path;
	}

}
