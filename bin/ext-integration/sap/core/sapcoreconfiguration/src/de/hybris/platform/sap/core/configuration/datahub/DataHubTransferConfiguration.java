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
package de.hybris.platform.sap.core.configuration.datahub;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import org.springframework.beans.factory.annotation.Required;



/**
 * This class represents a single Data Hub configuration.
 * 
 */
public class DataHubTransferConfiguration
{
	private String itemCode;
	private String rawType;
	@SuppressWarnings("rawtypes")
	private AbstractPopulatingConverter converter;
	private DataHubTransferConfigurationManager dataHubManager;

	/**
	 * Sets the Data Hub manager.
	 * 
	 * @param dataHubManager
	 *           the Data Hub manager
	 */
	public void setDataHubManager(final DataHubTransferConfigurationManager dataHubManager)
	{
		this.dataHubManager = dataHubManager;
	}

	/**
	 * Adds this Data Hub configuration to the map of Data Hub configurations.
	 */
	public void addToDataHubConfigurations()
	{
		dataHubManager.addToDataHubTransferConfigurations(this);
	}

	/**
	 * Returns the item code (String) of the Data Hub configuration.
	 * 
	 * @return the item code
	 */
	public String getItemCode()
	{
		return itemCode;
	}

	/**
	 * Sets the item code (String) of the Data Hub configuration.
	 * 
	 * @param code
	 *           the given item code
	 */
	@Required
	public void setItemCode(final String code)
	{
		this.itemCode = code;
	}

	/**
	 * Returns the raw type of the Data Hub configuration.
	 * 
	 * @return the given raw type
	 */
	public String getRawType()
	{
		return rawType;
	}

	/**
	 * Sets the raw type of the Data Hub configuration.
	 * 
	 * @param rawType
	 *           the given raw type
	 */
	@Required
	public void setRawType(final String rawType)
	{
		this.rawType = rawType;
	}

	/**
	 * Returns the converter of the Data Hub configuration.
	 * 
	 * @return the converter
	 */
	@SuppressWarnings("rawtypes")
	public AbstractPopulatingConverter getConverter()
	{
		return converter;
	}

	/**
	 * Sets the converter of the Data Hub configuration.
	 * 
	 * @param converter
	 *           the given converter
	 */
	@SuppressWarnings("rawtypes")
	@Required
	public void setConverter(final AbstractPopulatingConverter converter)
	{
		this.converter = converter;
	}

}
