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

import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import java.util.Properties;

import com.sap.conn.jco.JCoDestination;



/**
 * Stateless RFC connection. <br>
 * Establishes a stateless RFC connection to the backend. Connection is taken from the connection pool and released
 * automatically to the pool after execution was done.
 */
public class JCoConnectionStateless extends JCoConnectionImpl
{


	/**
	 * Cache for managed functions.
	 */
	protected JCoManagedFunctionCache managedFunctionCache = new JCoManagedFunctionCache(); //NOPMD

	/**
	 * Constructor.
	 * 
	 * @param properties
	 *           properties
	 */
	public JCoConnectionStateless(final Properties properties)
	{
		super(properties);
	}

	@Override
	protected boolean isFunctionCached(final JCoManagedFunction managedFunction)
	{
		return (managedFunctionCache != null && managedFunctionCache.isFunctionCached(managedFunction));
	}

	@Override
	protected void executeCachedFunction(final JCoDestination destination, final JCoManagedFunction managedFunction)
			throws BackendException
	{
		managedFunctionCache.executeCachedFunction(getDestination(), managedFunction);
	}


}
