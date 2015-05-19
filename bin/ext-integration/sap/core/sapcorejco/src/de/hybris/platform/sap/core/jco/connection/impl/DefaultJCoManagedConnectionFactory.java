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
package de.hybris.platform.sap.core.jco.connection.impl;


import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * Default implementation for managed connection factory.
 */
public class DefaultJCoManagedConnectionFactory implements JCoManagedConnectionFactory
{
	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(DefaultJCoManagedConnectionFactory.class.getName());

	/**
	 * Connection definition.
	 */
	protected Map<String, String> connectionDefinitions = new HashMap<String, String>(); //NOPMD

	/**
	 * Generic factory.
	 */
	protected GenericFactory genericFactory; //NOPMD

	/**
	 * Constructor.
	 * 
	 * @param connectionDefinitions
	 *           connection definition.
	 */
	public DefaultJCoManagedConnectionFactory(final Map<String, String> connectionDefinitions)
	{
		this.connectionDefinitions = connectionDefinitions;
	}

	@Override
	public JCoConnection getManagedConnection(final String connectionName, final String callerId)
	{
		return getManagedConnection(connectionName, callerId, null);
	}

	@Override
	public JCoConnection getManagedConnection(final String connectionName, final String callerId, final String destinationName)
	{

		if (!connectionDefinitions.containsKey(connectionName))
		{
			throw new BackendRuntimeException("No managed connection with connection type " + connectionName + "defined");
		}

		if (callerId == null || callerId.isEmpty())
		{
			throw new IllegalArgumentException(
					"Parameter callerId should not be null or empty. Please add a caller id which identifies your compoent.");
		}

		JCoConnection managedConnection;
		if (destinationName != null)
		{
			final Properties properties = new Properties();
			properties.setProperty("destination", destinationName);
			managedConnection = (JCoConnection) getGenericFactory().getBean(connectionDefinitions.get(connectionName), properties);
		}
		else
		{
			managedConnection = (JCoConnection) getGenericFactory().getBean(connectionDefinitions.get(connectionName));
		}
		return managedConnection;
	}

	/**
	 * Getter for generic factory.
	 * 
	 * @return generic factory instance.
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}


	/**
	 * Setter for generic factory.
	 * 
	 * @param genericFactory
	 *           generic factory instance
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

}
