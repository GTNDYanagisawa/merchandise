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
package de.hybris.platform.sap.productconfig.facades;


/**
 * Facade for Product Configuration.
 */
public interface ConfigurationFacade
{

	/**
	 * Get the default configuration for the given Knowledge Base.
	 * 
	 * @param kbKey
	 *           key of the Knowledge Base
	 * @return default configuration
	 */
	public ConfigurationData getConfiguration(KBKeyData kbKey);

	/**
	 * Update the configuration with the values provided
	 * 
	 * @param configuration
	 *           actual configuration
	 */
	public void updateConfiguration(ConfigurationData configuration);

	/**
	 * Read the actual configuration from the Backend. Current values in the model will be overwritten.
	 * 
	 * @param configuration
	 *           configuration to be refreshed
	 * @return actual configuration
	 */
	public ConfigurationData getConfiguration(ConfigurationData configuration);


}
