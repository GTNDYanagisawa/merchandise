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

import de.hybris.platform.sap.core.configuration.ConfigurationConstants;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationService;
import de.hybris.platform.sap.core.constants.SapcoreConstants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of {@link SAPConfigurationService}.
 */
public class DefaultSAPConfigurationService implements SAPConfigurationService
{

	private RFCDestinationService rfcDestinationService;
	private String backendType = ConfigurationConstants.DEFAULT_SAP_BACKEND_TYPE;
	private RFCDestination rfcDestination = null;
	private String rfcDestinationName = null;
	private String sapConfigurationName = null;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Map<String, Object> baseStoreProperties = new HashMap<String, Object>();
	private final Map<String, ConfigurationPropertyAccess> propertyAccesses = new HashMap<String, ConfigurationPropertyAccess>();
	private final Map<String, Collection<ConfigurationPropertyAccess>> propertyAccessCollections = new HashMap<String, Collection<ConfigurationPropertyAccess>>();

	/**
	 * Injection setter for rfcDestinationService.
	 * 
	 * @param rfcDestinationService
	 *           the rfcDestinationService to set
	 */
	public void setRfcDestinationService(final RFCDestinationService rfcDestinationService)
	{
		this.rfcDestinationService = rfcDestinationService;
	}

	/**
	 * Injection setter for properties.
	 * 
	 * @param properties
	 *           properties
	 */
	public void setProperties(final Map<String, Object> properties)
	{
		this.properties = properties;
	}

	/**
	 * Injection setter for Base Store properties.
	 * 
	 * @param baseStoreProperties
	 *           Base Store properties
	 */
	public void setBaseStoreProperties(final Map<String, Object> baseStoreProperties)
	{
		this.baseStoreProperties = baseStoreProperties;
	}

	/**
	 * Injection Setter for SAP configuration name.
	 * 
	 * @param sapConfigurationName
	 *           SAP configuration name
	 */
	public void setSAPConfigurationName(final String sapConfigurationName)
	{
		this.sapConfigurationName = sapConfigurationName;
	}

	/**
	 * Sets the SAP backend type.
	 * 
	 * @param backendType
	 *           the backendType to set
	 */
	public void setBackendType(final String backendType)
	{
		this.backendType = backendType;
	}

	/**
	 * Only used in JUnit tests to set the RFC destination name.
	 * 
	 * @param rfcDestinationName
	 *           the rfcDestinationName to set
	 */
	public void setRfcDestinationName(final String rfcDestinationName)
	{
		this.rfcDestinationName = rfcDestinationName;
	}

	/**
	 * Sets the RFC destination.
	 * 
	 * @param rfcDestination
	 *           RFC destination
	 * 
	 */
	public void setRFCDestination(final RFCDestination rfcDestination)
	{
		this.rfcDestination = rfcDestination;
	}

	@Override
	public boolean isSAPConfigurationActive()
	{
		return true;
	}

	@Override
	public boolean isBaseStoreActive()
	{
		return true;
	}

	@Override
	public String getSAPConfigurationName()
	{
		return sapConfigurationName;
	}

	@Override
	public Object getBaseStoreProperty(final String property)
	{
		return baseStoreProperties.get(property);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(final String propertyName)
	{
		return (T) properties.get(propertyName);
	}

	@Override
	public Map<String, Object> getAllProperties()
	{
		return properties;
	}

	@Override
	public ConfigurationPropertyAccess getPropertyAccess(final String propertyAccessName)
	{
		return propertyAccesses.get(propertyAccessName);
	}

	@Override
	public Map<String, ConfigurationPropertyAccess> getAllPropertyAccesses()
	{
		return propertyAccesses;
	}

	@Override
	public Collection<ConfigurationPropertyAccess> getPropertyAccessCollection(final String propertyAccessCollectionName)
	{
		return propertyAccessCollections.get(propertyAccessCollectionName);
	}

	@Override
	public Map<String, Collection<ConfigurationPropertyAccess>> getAllPropertyAccessCollections()
	{
		return propertyAccessCollections;
	}

	@Override
	public String getBackendType()
	{
		return backendType;
	}

	@Override
	public RFCDestination getRFCDestination()
	{
		if (rfcDestination == null)
		{
			if (rfcDestinationName != null && !rfcDestinationName.isEmpty())
			{
				rfcDestination = rfcDestinationService.getRFCDestination(rfcDestinationName);
			}
			else
			{
				rfcDestination = rfcDestinationService.getRFCDestination(ConfigurationConstants.DEFAULT_RFC_DESTINATION_NAME);
			}
		}
		return rfcDestination;
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreConstants.CRLF + "- Properties: " + properties + SapcoreConstants.CRLF
				+ "- Base Store Properties: " + baseStoreProperties + SapcoreConstants.CRLF + "- Backend Type: " + getBackendType()
				+ SapcoreConstants.CRLF + "- RFC Destination: " + rfcDestination;
	}

}
