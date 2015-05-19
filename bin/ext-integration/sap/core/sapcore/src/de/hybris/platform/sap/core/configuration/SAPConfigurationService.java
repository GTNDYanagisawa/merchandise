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

import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;


/**
 * Interface to access the SAP runtime configuration.
 */
public interface SAPConfigurationService extends ConfigurationPropertyAccess
{

	/**
	 * Checks if currently an SAP configuration is active.
	 * 
	 * @return true, if an SAP configuration is active
	 */
	public boolean isSAPConfigurationActive();

	/**
	 * Checks if currently a Base Store is active.
	 * 
	 * @return true, if a Base Store is active
	 */
	public boolean isBaseStoreActive();

	/**
	 * Returns the SAP configuration name.
	 * 
	 * @return Name of the SAPConfiguration
	 */
	public String getSAPConfigurationName();

	/**
	 * Returns the value of the requested Base Store property.
	 * 
	 * @param property
	 *           Property name
	 * @return Property value
	 */
	public Object getBaseStoreProperty(String property);

	/**
	 * Returns the SAP backend type (e.g. ERP).
	 * 
	 * @return backend type
	 */
	public String getBackendType();

	/**
	 * Returns the RFC destination.
	 * 
	 * @return RFC destination
	 */
	public RFCDestination getRFCDestination();

}
