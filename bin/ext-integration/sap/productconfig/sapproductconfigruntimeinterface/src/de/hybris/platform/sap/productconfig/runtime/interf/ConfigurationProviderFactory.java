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

/**
 * Retrieves an instance of the configuration provider according to the hybris application configuration.
 */
public interface ConfigurationProviderFactory
{

	/**
	 * Retrieves an instance of the configuration provider according to the hybris application configuration.
	 * 
	 * @return instance of the configuration provider
	 */
	public ConfigurationProvider getProvider();
}
