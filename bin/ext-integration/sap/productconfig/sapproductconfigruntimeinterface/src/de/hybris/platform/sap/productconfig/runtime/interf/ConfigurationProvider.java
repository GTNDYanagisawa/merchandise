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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * ConfigurationProvider provides access to all required interactions with SSC configuration an pricing engine.
 */
public interface ConfigurationProvider
{

	/**
	 * Creates a default configuration for the required knowledge base. The knowledge base (KB) can be identified e.g.
	 * via the product code or via the KB name, version and logical system.
	 * 
	 * @param kbKey
	 *           Information needed to identify a knowledge base
	 * @return The configurable product with default configuration
	 */
	public ConfigModel createDefaultConfiguration(KBKey kbKey);

	/**
	 * Update the configuration model within the configuration engine.
	 * 
	 * @param model
	 *           Updated model
	 */
	public boolean updateConfiguration(ConfigModel model);

	/**
	 * Retrieve the current state of the configuration model for the requested <code>configId</code>.
	 * 
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	public ConfigModel retrieveConfigurationModel(String configId);

	/**
	 * Retrieve the current state of the configuration for the requested <code>configId</code> as an XML string
	 * containing the configuration in external format.
	 * 
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration as XML string
	 */
	public String retrieveExternalConfiguration(String configId);

	/**
	 * Creates a configuration from the configuration in external format which can be provided from outside, e.g. from
	 * the configuration prepared in the back end
	 * 
	 * @param extConfig
	 *           External configuration in external format
	 * @return Configuration model
	 */
	public ConfigModel createConfigurationFromExternalSource(Configuration extConfig);

	/**
	 * Creates a configuration from an XML string containing the configuration in external format
	 * 
	 * @param kbKey
	 *           Information needed to create a knowledge base
	 * @param extConfig
	 *           External configuration as XML string
	 * @return Configuration model
	 */
	public ConfigModel createConfigurationFromExternalSource(KBKey kbKey, String extConfig);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 * 
	 * @param configId
	 *           session id
	 */
	public void releaseSession(String configId);
}
