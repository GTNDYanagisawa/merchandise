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
 * Callback-Interface for manipulating the product configuration data transfer objects after they have been updated from
 * the model.
 */
public interface ConfigConsistenceChecker
{

	/**
	 * This method will be called after the product configuration DAO has been updated from the model.
	 * 
	 * @param configData
	 *           original DAO
	 * @return DAO to use
	 */
	public ConfigurationData checkConfiguration(ConfigurationData configData);
}
