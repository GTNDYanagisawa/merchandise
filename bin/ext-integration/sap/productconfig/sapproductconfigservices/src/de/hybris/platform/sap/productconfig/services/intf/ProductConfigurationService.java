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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;



/**
 * ProductConfigurationService provides access to the configuration engine implementation.
 * 
 */
public interface ProductConfigurationService
{

	/**
	 * Based on the hybris product code, provided via the <code>KBKey.productCode</code>, the configuration engine will
	 * provide a default configuration for the requested product.
	 * 
	 * @param kbKey
	 *           The product code for the configurable product
	 * @return The configurable product with default configuration
	 */
	public ConfigModel createDefaultConfiguration(final KBKey kbKey);

	/**
	 * Update the configuration model within the configuration engine.
	 * 
	 * @param model
	 *           Updated model
	 */
	public void updateConfiguration(final ConfigModel model);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in the <code>ConfigModel</code>
	 * format.
	 * 
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	public ConfigModel retrieveConfigurationModel(String configId);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in a <i>XML</i> format.
	 * 
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration as XML string
	 */
	public String retrieveExternalConfiguration(final String configId);

	/**
	 * Creates a configuration from the external string representation (which contains the configuration in XML format)
	 * 
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param kbKey
	 *           Key attributes needed to create a model
	 * @return Configuration model
	 */
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, String externalConfiguration);

	/**
	 * Create a <code>ConfigModel</code> based on a <code>Configuration</code> for the provided product code.
	 * 
	 * @param extConfig
	 *           Configuration in a data structure
	 * @return Configuration model
	 */
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 * 
	 * @param configId
	 *           session id
	 */
	public void releaseSession(String configId);

}