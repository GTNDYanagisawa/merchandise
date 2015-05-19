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

import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Central class that manages all the Data Hub Transfer configurations. Example to add a Data Hub Transfer configuration
 * via the corresponding spring.xml file:
 * 
 * <pre>
 * {@code 
 * 	<bean id="sapCoreSAPBaseStoreDataHubConfiguration" parent="sapCoreDataHubTransferConfiguration">
 *  		<property name="itemCode"  value="SAPConfiguration" />
 * 		<property name="rawType"   value="RawSAPBaseStoreConfiguration" />
 * 		<property name="converter" ref="sapCoreSAPBaseStoreConfigurationConverter" />
 * 	}
 * </pre>
 */
@SuppressWarnings("deprecation")
public class DataHubTransferConfigurationManager
{
	private static final Logger LOG = Logger.getLogger(DataHubTransferConfigurationManager.class.getName());
	private final Map<String, List<DataHubTransferConfiguration>> dataHubTransferConfigurationByCode = new HashMap<String, List<DataHubTransferConfiguration>>();
	private Map<Integer, String> code2TypeCodeMapping = null;

	/**
	 * Returns the map of Data Hub Transfer configurations.
	 * 
	 * @return the Map of Data Hub Transfer configurations
	 */
	public Map<String, List<DataHubTransferConfiguration>> getAllDataHubConfigurations()
	{
		return dataHubTransferConfigurationByCode;
	}

	/**
	 * Determines the Data Hub configuration for the given type code.
	 * 
	 * @param code
	 *           the given code
	 * @return the Data Hub configuration
	 */
	public List<DataHubTransferConfiguration> getDataHubTransferConfigurations(final String code)
	{
		return dataHubTransferConfigurationByCode.get(code);
	}

	/**
	 * Adds the Data Hub Transfer configuration to list of existing configurations.
	 * 
	 * @param dataHubTransferConfiguration
	 *           the Data Hub Transfer configuration
	 */
	public void addToDataHubTransferConfigurations(final DataHubTransferConfiguration dataHubTransferConfiguration)
	{
		final String code = dataHubTransferConfiguration.getItemCode();
		if (code != null && !code.isEmpty())
		{
			if (!dataHubTransferConfigurationByCode.containsKey(code))
			{
				final List<DataHubTransferConfiguration> dataHubTransferConfigurations = new ArrayList<DataHubTransferConfiguration>();
				dataHubTransferConfigurationByCode.put(code, dataHubTransferConfigurations);
			}
			dataHubTransferConfigurationByCode.get(code).add(dataHubTransferConfiguration);
			LOG.debug("Datahub transfer configuration added to manager: " + dataHubTransferConfiguration);
		}
	}

	/**
	 * Translates the integer type code to string code.
	 * 
	 * @param typeCode
	 *           integer type code
	 * @return string code
	 */
	public String getItemCode(final int typeCode)
	{
		if (code2TypeCodeMapping == null)
		{
			code2TypeCodeMapping = new HashMap<Integer, String>();
			final Set<String> codeSet = dataHubTransferConfigurationByCode.keySet();
			for (final String code : codeSet)
			{
				final int intTypeCode = getItemTypeCode(code);
				if (intTypeCode == -1)
				{
					LOG.warn("No item type code found for item code '" + code + "'! Data won't be replicated to data hub after save.");
				}
				else
				{
					code2TypeCodeMapping.put(intTypeCode, code);
				}
			}
		}
		return code2TypeCodeMapping.get(typeCode);
	}

	/**
	 * Gets the integer type code for string code.
	 * 
	 * @param code
	 *           item code as string
	 * @return item type code as integer
	 */
	public int getItemTypeCode(final String code)
	{
		try
		{
			final ComposedType composedType = TypeManager.getInstance().getComposedType(code);
			if (composedType != null)
			{
				return composedType.getItemTypeCode();
			}
			LOG.debug("No composed type found for item code '" + code + "'!");
			return -1;
		}
		catch (final JaloItemNotFoundException ex)
		{
			LOG.debug("No composed type found for item code '" + code + "'!");
			return -1;
		}
	}

}
