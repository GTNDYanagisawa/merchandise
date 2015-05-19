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
package de.hybris.platform.sap.core.jco.test;

import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;


/**
 * Destination data provider looking for .jcoDestintaion files in the file directory. <br>
 * There can be several places to define destination files. The search strategy is as follows:
 * <ol>
 * <li>/config directory of the hybris Multichannel Suite, where custom configuration files are stored</li>
 * <li>Directory paths defined in spring bean sapCoreRFCDirectoryDestinationProviderPaths (attribute directoryPaths)</li>
 * <li>Extensions defined in spring bean sapCoreRFCDirectoryDestinationProviderExtensions (attribute extensionNames)</li>
 * </ol>
 *
 * The directory path and extension spring beans can be overwritten in order to define search locations.
 *
 */
/**
 *
 */
public class RFCDirectoryDestinationDataProvider implements DestinationDataProvider
{

	/**
	 * file suffix for JCo Destination defintions.
	 */
	private static final String JCO_DESTINATION_FILE_SUFFIX = ".jcoDestination";

	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	private static final Logger log = Logger.getLogger(RFCDirectoryDestinationDataProvider.class.getName());

	/**
	 * Cached connection properties.
	 */
	private final ConcurrentHashMap<String, Properties> connectionPropertiesMap = new ConcurrentHashMap<String, Properties>();

	/**
	 * Directory paths searching for .jcoDestination files.
	 */
	private List<String> directoryPaths;


	/**
	 * Extension names searching for .jcoDestination files.
	 */
	private List<String> extensionNames;


	/**
	 * Getter for extensionNames.
	 *
	 * @return extension names
	 */
	public List<String> getExtensionNames()
	{
		return extensionNames;
	}

	/**
	 * Setter for extensionNames.
	 *
	 * @param extensionNames
	 *           extension names
	 */
	public void setExtensionNames(final List<String> extensionNames)
	{
		this.extensionNames = extensionNames;
	}

	/**
	 * Setter for directoryPaths.
	 *
	 * @param directoryPaths
	 *           directory paths
	 */
	public void setDirectoryPaths(final List<String> directoryPaths)
	{
		this.directoryPaths = directoryPaths;
	}

	/**
	 * Getter for directoryPath.
	 *
	 * @return directory path
	 */
	public List<String> getDirectoryPaths()
	{
		return directoryPaths;
	}


	/**
	 * Init method. Register the destination provider in JCO Environment.
	 */
	public void init()
	{
		if (!Environment.isDestinationDataProviderRegistered())
		{
			Environment.registerDestinationDataProvider(this);
		}
	}


	/**
	 * Searches for JCo Destination files in the file directory.
	 *
	 * In case of the JCo Destination file could not be found a {@link RFCDirectoryDestinationDataProviderException} is
	 * raised.
	 *
	 * @param destination
	 *           destination
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#getDestinationProperties(java.lang.String)
	 *
	 * @return JCo Destination properties
	 */
	@Override
	public Properties getDestinationProperties(final String destination)
	{

		Properties properties = null;

		if (connectionPropertiesMap.containsKey(destination))
		{
			log.debug("Destination property for destination " + destination + " found in cache.");
			return connectionPropertiesMap.get(destination);
		}

		log.debug("Get destination property file from config folder.");
		properties = readPropertyFileFromConfigFolder(destination);
		if (properties != null)
		{
			connectionPropertiesMap.put(destination, properties);
			return properties;
		}

		log.debug("Get destination property file from extensions.");
		properties = readPropertyFileFromExtensions(destination);
		if (properties != null)
		{
			connectionPropertiesMap.put(destination, properties);
			return properties;
		}

		log.debug("Get destination property file from directories.");
		properties = readPropertyFileFromDirectoryPaths(destination);
		if (properties != null)
		{
			connectionPropertiesMap.put(destination, properties);
			return properties;
		}

		log.debug("Destination property file for destination " + destination + "  was not found");
		throw new RFCDirectoryDestinationDataProviderException("Could not find destination " + destination);

	}

	/**
	 * Reads property file from given extensions.
	 *
	 * @param destination
	 *           destination
	 * @return Properties or null if destination could not be found.
	 */
	private Properties readPropertyFileFromExtensions(final String destination)
	{
		for (final Object extensionName : extensionNames)
		{
			final ExtensionInfo extensionInfo = Utilities.getPlatformConfig().getExtensionInfo((String) extensionName);

			if (extensionInfo == null)
			{
				log.debug("Could not get extension info for extension " + extensionName);
				continue;
			}

			try
			{
				final String filePath = extensionInfo.getExtensionDirectory().getCanonicalPath() + File.separator + destination
						+ JCO_DESTINATION_FILE_SUFFIX;
				final Properties properties = readPropertyFile(filePath);
				if (properties != null)
				{
					return properties;
				}
			}
			catch (final IOException e)
			{
				throw new RFCDirectoryDestinationDataProviderException(
						"IOException occured during accessing extension directory for extension " + extensionName, e);
			}

		}
		return null;
	}

	/**
	 * Reads property file from given file directories.
	 *
	 * @param destination
	 *           destination
	 * @return Properties or null if destination could not be found.
	 */
	private Properties readPropertyFileFromDirectoryPaths(final String destination)
	{
		for (final Object path : directoryPaths)
		{
			final String filePath = path + destination + JCO_DESTINATION_FILE_SUFFIX;
			final Properties properties = readPropertyFile(filePath);
			if (properties != null)
			{
				return properties;
			}
		}
		return null;
	}

	/**
	 * Reads property file from config folder.
	 *
	 * @param destination
	 *           destination
	 * @return Properties or null if destination could not be found.
	 */
	private Properties readPropertyFileFromConfigFolder(final String destination)
	{
		final File configDir = Utilities.getPlatformConfig().getSystemConfig().getConfigDir();

		final String destinationFilePath = configDir.getAbsolutePath() + File.separator + destination + JCO_DESTINATION_FILE_SUFFIX;
		return readPropertyFile(destinationFilePath);
	}

	/**
	 * Reads property file from file path.
	 *
	 * @param filePath
	 *           relative or absolute file path.
	 * @return Properties or null if file does not exists.
	 */
	private Properties readPropertyFile(final String filePath)
	{

		FileInputStream is = null;

		try
		{
			log.debug("Try to read property file " + filePath);
			final File f = new File(filePath);

			if (!f.exists())
			{
				log.debug("Cannot find property file with canonical path " + f.getCanonicalPath());
				return null;
			}

			is = new FileInputStream(f);
			final Properties p = new Properties();
			p.load(is);
			is.close();
			log.debug("Found property file " + f.getCanonicalPath());
			log.debug("Content is " + p.toString());
			return p;
		}
		catch (final IOException e)
		{
			throw new RFCDirectoryDestinationDataProviderException("Could not read file " + filePath, e);
		}
		finally
		{
			if (is != null)
			{
				safeClose(is);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#setDestinationDataEventListener(com.sap.conn.jco.ext.
	 * DestinationDataEventListener)
	 */
	@Override
	public void setDestinationDataEventListener(final DestinationDataEventListener eventListener)
	{

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#supportsEvents()
	 */
	@Override
	public boolean supportsEvents()
	{
		return false;
	}


	/**
	 * Closes the given file input stream. The method should be called in finally block to close source safe.
	 *
	 * @param fis
	 *           the file input stream to close
	 */
	private static void safeClose(final FileInputStream fis)
	{
		if (fis != null)
		{
			try
			{
				fis.close();
			}
			catch (final IOException e)
			{
				log.debug(e);
			}
		}
	}

}
