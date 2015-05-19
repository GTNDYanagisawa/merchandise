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

package de.hybris.platform.sap.core.jco.connection;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.DestinationDataProvider;


/**
 * Interface for creating a connection to an ABAP system.
 * 
 */
public interface JCoConnection
{

	/**
	 * Property name for destination parameter.
	 */
	public static final String JCO_DESTINATION = "destination";
	/**
	 * Property name for scope type parameter.
	 */
	public static final String JCO_SCOPE_TYPE = "jcoScopeType";
	/**
	 * Property name for JCo language.
	 */
	public static final String JCO_LANG = DestinationDataProvider.JCO_LANG;

	/**
	 * Returns a JCoFunction object for the given function name.
	 * 
	 * @param functionName
	 *           name of remote callable function module on the SAP system
	 * @return JCoFunction
	 * @throws BackendException
	 *            when something goes wrong while retrieving meta data
	 */
	public JCoFunction getFunction(String functionName) throws BackendException;

	/**
	 * This method is used to execute a JCoFunction on behalf of this connection.
	 * 
	 * @param function
	 *           a JCoFunction which should be executed
	 * @throws BackendException
	 *            thrown if something goes wrong in the EAI layer or if an JCoException is thrown by the SAP Java
	 *            Connector.
	 */
	public void execute(JCoFunction function) throws BackendException;


	/**
	 * Returns <code>true</code> if the JCo Function is available on the SAP system the connection is connected to.
	 * 
	 * @param functionName
	 *           function name
	 * @return <code>true</code> if the JCo Function is available, otherwise <code>false</code>
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public boolean isFunctionAvailable(String functionName) throws BackendException;


	/**
	 * Sets the caller id.
	 * 
	 * @param callerID
	 *           caller id.
	 */
	public void setCallerId(String callerID);

	/**
	 * Returns <code>true</code> if the backend is available the connection is connected to.
	 * 
	 * @return <code>true</code> if the backend is available, otherwise <code>false</code>
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public boolean isBackendAvailable() throws BackendException;

	/**
	 * Returns <code>true</code> if the backend is switched offline the connection is connected to.
	 * 
	 * @return <code>true</code> if the backend is switched offline, otherwise <code>false</code>
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public boolean isBackendOffline() throws BackendException;

}
