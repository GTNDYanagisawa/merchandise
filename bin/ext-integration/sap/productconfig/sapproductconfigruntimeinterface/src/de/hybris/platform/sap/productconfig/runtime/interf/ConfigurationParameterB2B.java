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
 * Retrieves hybris data relevant for the configuration and pricing engine in the B2B scenario.
 */
public interface ConfigurationParameterB2B
{
	/**
	 * Retrieves the value of the flag for activating of sending the b2b relevant data to the configuration and pricing
	 * engine. If inactive, no b2b scenario relevant data are sent to the pricing engine.
	 * 
	 * @return the value of the flag for activating of of sending the b2b relevant data to the configuration and pricing
	 *         engine
	 */
	public boolean isSupported();

	/**
	 * Retrieves the customer number for the logged on user.
	 * 
	 * @return the customer number
	 */
	public String getCustomerNumber();

	/**
	 * Retrieves the SAP country code for the logged on user.
	 * 
	 * @return the the SAP country code
	 */
	public String getCountrySapCode();

	/**
	 * Retrieves the customer price group for the logged on user.
	 * 
	 * @return the customer price group
	 */
	public String getCustomerPriceGroup();
}
