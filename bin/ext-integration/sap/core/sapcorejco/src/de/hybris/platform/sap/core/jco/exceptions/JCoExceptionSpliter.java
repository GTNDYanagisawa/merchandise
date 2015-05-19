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
package de.hybris.platform.sap.core.jco.exceptions;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoException;


/**
 * Splits Exceptions from the JCo Library in exception classes of this package.
 */
public class JCoExceptionSpliter
{
	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoExceptionSpliter.class.getName());

	/**
	 * Splits the general purpose Exception <code>JCoException</code> used by JCo into some more meaningful and JCo
	 * independent exceptions.
	 * 
	 * 
	 * @param jcoEx
	 *           The JCo exception to be split
	 * @throws BackendCommunicationException
	 *            BackendCommunicationException
	 * @throws BackendLogonException
	 *            BackendLogonException
	 * @throws BackendSystemFailureException
	 *            BackendSystemFailureException
	 * @throws BackendServerStartupException
	 *            BackendServerStartupException
	 * @throws BackendException
	 *            BackendException
	 */
	public static void splitAndThrowException(final JCoException jcoEx) throws BackendCommunicationException,
			BackendLogonException, BackendSystemFailureException, BackendServerStartupException, BackendException
	{

		log.debug(jcoEx);

		switch (jcoEx.getGroup())
		{

			case JCoException.JCO_ERROR_COMMUNICATION:
				String msg = "Backend Communication Error: " + jcoEx.getMessage() + " occurs. (Group: JCO_ERROR_COMMUNICATION)";
				log.log(Level.DEBUG, msg);
				final BackendCommunicationException backendCommEx = new BackendCommunicationException(msg, jcoEx);
				throw backendCommEx;

			case JCoException.JCO_ERROR_LOGON_FAILURE:
				msg = "Backend Logon Exception: " + jcoEx.getMessage() + " occurs. (Group: JCO_ERROR_LOGON_FAILURE)";
				log.log(Level.DEBUG, msg);
				final BackendLogonException logonEx = new BackendLogonException(msg, jcoEx);

				throw logonEx;

			case JCoException.JCO_ERROR_SYSTEM_FAILURE:
				msg = "Backend System Failure Exception: " + jcoEx.getMessage() + " occurs. (Group: JCO_ERROR_SYSTEM_FAILURE)";
				log.log(Level.DEBUG, msg);
				final CoreBaseRuntimeException panicExep = new CoreBaseRuntimeException(msg, jcoEx);
				throw panicExep;

			case JCoException.JCO_ERROR_SERVER_STARTUP:
				msg = "Backend Server Startup Exception: " + jcoEx.getMessage() + " occurs. (Group: JCO_ERROR_SERVER_STARTUP)";
				log.log(Level.DEBUG, msg);
				final BackendServerStartupException serverEx = new BackendServerStartupException(msg, jcoEx);
				throw serverEx;

			case JCoException.JCO_ERROR_APPLICATION_EXCEPTION:
			case JCoException.JCO_ERROR_CANCELLED:
			case JCoException.JCO_ERROR_ILLEGAL_TID:
			case JCoException.JCO_ERROR_INTERNAL:
			case JCoException.JCO_ERROR_NOT_SUPPORTED:
			case JCoException.JCO_ERROR_PROGRAM:
			case JCoException.JCO_ERROR_PROTOCOL:
			case JCoException.JCO_ERROR_STATE_BUSY:
				msg = "Backend Exception: " + jcoEx.getMessage() + " occurs. (Group: " + jcoEx.getGroup() + ")";
				log.log(Level.DEBUG, msg);
				final BackendException wrapperEx = new BackendException(msg, jcoEx);
				throw wrapperEx;

			case JCoException.JCO_ERROR_RESOURCE:
				msg = "JCO_ERROR_RESOURCE: " + jcoEx.getMessage() + " occurs. (Group: JCO_ERROR_RESOURCE)";
				log.log(Level.DEBUG, msg);
				final CoreBaseRuntimeException wcfRuntimeEx = new CoreBaseRuntimeException(msg, jcoEx);
				throw wcfRuntimeEx;

			case JCoException.JCO_ERROR_DESTINATION_DATA_INVALID:
				msg = "Destination data was changed at runtime for the currently used destination : " + jcoEx.getMessage();
				throw new DestinationChangedRuntimeException(msg, jcoEx);

			case JCoException.JCO_ERROR_CONVERSION:
			case JCoException.JCO_ERROR_FIELD_NOT_FOUND:
			case JCoException.JCO_ERROR_FUNCTION_NOT_FOUND:
			case JCoException.JCO_ERROR_NULL_HANDLE:
			case JCoException.JCO_ERROR_UNSUPPORTED_CODEPAGE:
			case JCoException.JCO_ERROR_XML_PARSER:
				msg = "Backend Runtime Exception: " + jcoEx.getMessage() + " occurs. (Group: " + jcoEx.getGroup() + ")";
				log.log(Level.DEBUG, msg);
				final BackendRuntimeException runtimeEx = new BackendRuntimeException(msg, jcoEx);
				throw runtimeEx;

			default:
				msg = "Backend Runtime Exception: Unknown exception \"" + jcoEx.getMessage() + "\" occurs. (Key: " + jcoEx.getKey()
						+ ")";
				throw new BackendRuntimeException(msg, jcoEx);

		}
	}
}
