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

import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationService;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionEvent;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameter;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameters;
import de.hybris.platform.sap.core.jco.connection.JCoLogUtil;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionContainer;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionContainerRestricted;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendOfflineException;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.JCoExceptionSpliter;

import java.security.InvalidParameterException;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoCustomDestination;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;



/**
 * Base class for a managed connection.
 */

public class JCoConnectionImpl implements JCoConnection
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoConnectionImpl.class.getName());

	/**
	 * JCO Custom destination.
	 */
	private JCoCustomDestination destination; //NOPMD

	/**
	 * Properties filled by spring definition in constructor. <br>
	 */
	protected Properties properties; //NOPMD

	/**
	 * Connection parameters.
	 */
	protected JCoConnectionParameters connectionParameters = null;//NOPMD

	/**
	 * Indicator that connections has already been destroyed.
	 */
	protected boolean destroyed; //NOPMD

	/**
	 * JCoManagedConnectionContainer.
	 */
	protected JCoManagedConnectionContainer jcoManagedConnectionContainer;//NOPMD

	/**
	 * RFCDestinationService.
	 */
	protected RFCDestinationService rfcDestinationService;//NOPMD

	/**
	 * Caller id.
	 */
	private String callerId;

	/**
	 * Scope type.
	 */
	private String scopeType = null;

	/**
	 * Constructor.
	 * 
	 * @param properties
	 *           properties for this instance with the following keys
	 *           <ul>
	 *           <li>{@link JCoConnection#JCO_DESTINATION} (destination)</li>
	 *           <li>{@link JCoConnection#JCO_SCOPE_TYPE} (jcoScopeType)</li>
	 *           <li>{@link JCoConnection#JCO_LANG} (jco.client.lang)</li>
	 *           </ul>
	 */
	public JCoConnectionImpl(final Properties properties)
	{
		super();
		this.properties = properties;
	}

	/**
	 * Setter for connection parameters.
	 * 
	 * @param connectionParameters
	 *           connection parameters.
	 */
	public void setConnectionParameters(final JCoConnectionParameters connectionParameters)
	{
		this.connectionParameters = connectionParameters;
	}

	/**
	 * Setter for JCo Managed Connection Container.
	 * 
	 * @param jcoManagedConnectionContainer
	 *           JCo Managed Connection Container
	 */
	public void setJcoManagedConnectionContainer(final JCoManagedConnectionContainer jcoManagedConnectionContainer)
	{
		this.jcoManagedConnectionContainer = jcoManagedConnectionContainer;
	}

	/**
	 * Injection setter for {@link RFCDestinationService}.
	 * 
	 * @param rfcDestinationService
	 *           {@link RFCDestinationService}
	 * 
	 */
	@Required
	public void setRfcDestinationService(final RFCDestinationService rfcDestinationService)
	{
		this.rfcDestinationService = rfcDestinationService;
	}


	/**
	 * Init method called by spring init method definition.
	 * 
	 * @throws BackendException
	 *            Exception in case of failure.
	 */
	public void init() throws BackendException
	{
		// can be overwritten
	}

	/**
	 * Destroy method which cleans up the object.
	 */
	public void setDestroyed()
	{
		destination = null;
		destroyed = true;
	}

	@Override
	public JCoFunction getFunction(String functionName) throws BackendException
	{
		checkInternalStatus();
		JCoFunction function = null;
		JCoConnectionParameter connectionParameter = null;

		if (functionName == null)
		{
			throw new InvalidParameterException("Parameter functionName must not be null.");
		}


		if (connectionParameters != null && connectionParameters.isFunctionModuleConfigured(functionName))
		{
			connectionParameter = connectionParameters.getConnectionParameter(functionName);
			functionName = getReplacedFunctionModuleName(functionName);
		}

		try
		{
			function = getDestination().getRepository().getFunction(functionName);
		}
		catch (final JCoException e)
		{
			handleCommunicationExceptionAndDestroyConnection(e);
			JCoExceptionSpliter.splitAndThrowException(e);
		}

		if (function == null)
		{
			throw new BackendException("Cannot get function " + functionName + " from repository for RFC destination "
					+ getDestination().toString());
		}

		return new JCoManagedFunction(function, connectionParameter);
	}

	/**
	 * Replaces origin function name with function name defined in connection parameters.
	 * 
	 * @param functionName
	 *           origin function name
	 * @return replaced function name
	 */
	protected String getReplacedFunctionModuleName(String functionName)
	{
		final JCoConnectionParameter connectionParameter = connectionParameters.getConnectionParameter(functionName);
		if (connectionParameter.getFunctionModuleToBeReplaced() != null)
		{
			log.debug("The function module " + functionName + " will be replaced with function module "
					+ connectionParameter.getFunctionModuleToBeReplaced());
			functionName = connectionParameter.getFunctionModuleToBeReplaced();
		}
		return functionName;
	}

	@Override
	public void execute(final JCoFunction function) throws BackendException
	{
		checkInternalStatus();
		try
		{
			log.debug("The JCoFunction" + function.getName() + " will be executed.");
			if (!(function instanceof JCoManagedFunction))
			{
				function.execute(getDestination());
				return;
			}

			final JCoManagedFunction managedFunction = (JCoManagedFunction) function;

			if (managedFunction.hasConnectionParameters())
			{
				preProcessingExecute((JCoManagedFunction) function);
			}

			if (isFunctionCached(managedFunction))
			{
				executeCachedFunction(getDestination(), managedFunction);
			}
			else
			{
				function.execute(getDestination());
			}

			if (managedFunction.hasConnectionParameters())
			{
				postProcessingExecute((JCoManagedFunction) function);
			}
		}
		catch (final JCoException e)
		{
			handleCommunicationExceptionAndDestroyConnection(e);
			JCoExceptionSpliter.splitAndThrowException(e);
		}
	}

	/**
	 * Checks if function result is cached.
	 * 
	 * @param managedFunction
	 *           requested function
	 * @return true, if result is cached
	 */
	protected boolean isFunctionCached(final JCoManagedFunction managedFunction)
	{
		return false;
	}

	/**
	 * Executes function on cache result.
	 * 
	 * @param destination
	 *           rfc destination
	 * @param function
	 *           rfc function
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	protected void executeCachedFunction(final JCoDestination destination, final JCoManagedFunction function)
			throws BackendException
	{
		return;
	}

	/**
	 * Called before JCO execution. Calls event listeners and tracing. <br>
	 * This method is only called if parameters are defined for that function.
	 * 
	 * @param function
	 *           function which is called.
	 */
	protected void preProcessingExecute(final JCoManagedFunction function)
	{

		final JCoConnectionParameter connectionParameter = function.getConnectionParameter();

		if (connectionParameter.getEventListener() != null)
		{
			connectionParameter.getEventListener().connectionEvent(
					new JCoConnectionEvent(this, function, JCoConnectionEvent.BEFORE_JCO_FUNCTION_CALL));
		}


		if (connectionParameter.getTraceBefore())
		{
			final JCoLogUtil logUtil = new JCoLogUtil();
			logUtil.logBeforeCall(function);
		}

	}

	/**
	 * Called after JCO execution. Calls event listeners and tracing. <br>
	 * This method is only called if parameters are defined for that function.
	 * 
	 * @param function
	 *           function which is called.
	 */
	protected void postProcessingExecute(final JCoManagedFunction function)
	{
		final JCoConnectionParameter connectionParameter = function.getConnectionParameter();

		if (connectionParameter.getEventListener() != null)
		{
			connectionParameter.getEventListener().connectionEvent(
					new JCoConnectionEvent(this, function, JCoConnectionEvent.AFTER_JCO_FUNCTION_CALL));
		}

		if (connectionParameter.getTraceAfter())
		{
			final JCoLogUtil logUtil = new JCoLogUtil();
			logUtil.logAfterCall(function);
		}

	}

	@Override
	public boolean isFunctionAvailable(final String functionName) throws BackendException
	{
		checkInternalStatus();
		boolean returnValue = false;
		try
		{
			returnValue = getDestination().getRepository().getFunction(functionName) != null;
			log.debug("The function module " + functionName + " is available in the connected backend: " + returnValue);
		}
		catch (final JCoException e)
		{
			handleCommunicationExceptionAndDestroyConnection(e);
			JCoExceptionSpliter.splitAndThrowException(e);
		}

		return returnValue;
	}


	/**
	 * Sets language in custom destination. <br>
	 * If properties contains a value with key {@link JCoConnection.JCO_LANG} this language is set.
	 * 
	 * Otherwise language is taken from {@link LocaleUtil}
	 * 
	 * @param userData
	 *           user data of custom destination
	 */
	private void setLanguageInCustomDestination(final JCoCustomDestination.UserData userData)
	{
		if (properties != null && properties.containsKey(JCoConnection.JCO_LANG))
		{
			final String lang = properties.getProperty(JCoConnection.JCO_LANG);
			log.debug("Language for JCo connection in connectionProperties is " + lang);
			userData.setLanguage(lang);
			return;
		}

		final Locale locale = LocaleUtil.getLocale();
		if (locale != null)
		{
			log.debug("Language for JCo connection is taken from LocaleUtil " + locale.getLanguage());
			userData.setLanguage(locale.getLanguage());
			return;
		}
		log.debug("No language for JCo connection is set");
	}


	/**
	 * Check if the destination is stateful.
	 * 
	 * @return true if stateful, false otherwise.
	 * @throws BackendException
	 *            BackendException
	 */
	protected boolean isStateful() throws BackendException
	{
		checkInternalStatus();
		return JCoContext.isStateful(getDestination());
	}

	/**
	 * Getter for destination.
	 * 
	 * @return the destination
	 */
	protected JCoCustomDestination getDestination()
	{
		if (destination == null)
		{
			if (destroyed)
			{
				throw new BackendRuntimeException("Destination '" + properties.getProperty(JCoConnection.JCO_DESTINATION)
						+ "' has already been destroyed");
			}
			try
			{
				if (!properties.containsKey(JCoConnection.JCO_DESTINATION))
				{
					throw new BackendException("" + JCoConnection.JCO_DESTINATION + " not provided");
				}

				final String destinationName = properties.getProperty(JCoConnection.JCO_DESTINATION);

				final String scopeType = properties.getProperty(JCoConnection.JCO_SCOPE_TYPE, getScopeType());

				try
				{
					destination = JCoDestinationManager.getDestination(destinationName, scopeType).createCustomDestination();
					log.debug("JCoCustomDestination has been created for parent destination " + destinationName
							+ " and with scopeType " + scopeType);
					final JCoCustomDestination.UserData userProps = destination.getUserLogonData();
					setLanguageInCustomDestination(userProps);
				}
				catch (final JCoException e)
				{
					this.destination = null;
					JCoExceptionSpliter.splitAndThrowException(e);
				}
			}
			catch (final BackendException e1)
			{
				throw new BackendRuntimeException("Destination '" + properties.getProperty(JCoConnection.JCO_DESTINATION)
						+ "' could not be established. Backend exception: " + e1.getMessage(), e1);
			}
		}
		return destination;
	}

	/**
	 * Checks internal status of the connection. <br>
	 * After the object is destroyed the destination is set to null. No more function execution is possible.
	 * 
	 * Throws BackendRuntimeException in case of object was already destroyed.
	 * 
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	protected void checkInternalStatus() throws BackendException
	{
		if (destroyed)
		{
			throw new BackendRuntimeException("Connection '" + properties.getProperty(JCoConnection.JCO_DESTINATION)
					+ "' already released");
		}
		if (isBackendOffline())
		{
			throw new BackendOfflineException("RFC destination '" + properties.getProperty(JCoConnection.JCO_DESTINATION)
					+ "' is offline!");
		}
		if (getDestination() == null)
		{
			throw new BackendRuntimeException("Connection '" + properties.getProperty(JCoConnection.JCO_DESTINATION)
					+ "' already released");
		}
	}

	@Override
	public void setCallerId(final String callerID)
	{
		this.callerId = callerID;

	}

	/**
	 * Getter for caller id.
	 * 
	 * @return caller id
	 */
	public String getCallerId()
	{
		return callerId;
	}

	/**
	 * Getter for scope type.
	 * 
	 * @return the scopeType
	 */
	public String getScopeType()
	{
		return scopeType;
	}

	/**
	 * Setter for scope type.
	 * 
	 * @param scopeType
	 *           the scopeType to set
	 */
	public void setScopeType(final String scopeType)
	{
		this.scopeType = scopeType;
	}

	@Override
	public boolean isBackendAvailable() throws BackendException
	{
		if (isBackendOffline())
		{
			return false;
		}
		try
		{
			getDestination().ping();
		}
		catch (final JCoException e)
		{
			handleCommunicationExceptionAndDestroyConnection(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean isBackendOffline() throws BackendException
	{
		if (!properties.containsKey(JCoConnection.JCO_DESTINATION))
		{
			throw new BackendException("" + JCoConnection.JCO_DESTINATION + " not provided");
		}

		final String destinationName = properties.getProperty(JCoConnection.JCO_DESTINATION);

		if (getRFCDestination(destinationName).isOffline())
		{
			final BackendOfflineException backendOfflineException = new BackendOfflineException("Backend is offline!");
			handleCommunicationExceptionAndDestroyConnection(backendOfflineException);
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the JCoException group is JCO_ERROR_COMMUNICATION.
	 * 
	 * @param e
	 *           JCo Exception
	 * @return true if group is JCO_ERROR_COMMUNICATION, otherwise false
	 */
	private boolean isCommunicationException(final Exception e)
	{
		if (e instanceof JCoException)
		{
			final JCoException jcoEx = (JCoException) e;
			return (jcoEx.getGroup() == JCoException.JCO_ERROR_COMMUNICATION);
		}
		return false;
	}

	/**
	 * Removes the current connection from the HashMap in JCoManagedConnectionContainer.
	 */
	private void removeConnectionFromContainer()
	{
		if (this.jcoManagedConnectionContainer instanceof JCoManagedConnectionContainerRestricted)
		{
			((JCoManagedConnectionContainerRestricted) this.jcoManagedConnectionContainer).removeConnection(this);
			return;
		}
		throw new BackendRuntimeException(
				"ManagedConnectionContainer does not implement interface JCoManagedConnectionContainerRestricted.");
	}

	/**
	 * Handles the Communication JCoException and destroy Stateful connection.
	 * 
	 * @param e
	 *           JCoException
	 */
	protected void handleCommunicationExceptionAndDestroyConnection(final Exception e)
	{
		if ((isCommunicationException(e) || e instanceof BackendOfflineException) && this instanceof JCoStateful && !destroyed)
		{
			// remove connection from ConnectionContainer
			removeConnectionFromContainer();
			try
			{
				((JCoStateful) this).destroy();
			}
			catch (final BackendException be)
			{
				throw new BackendRuntimeException("Backend Runtime Exception occurred during destroy of Stateful connection '"
						+ properties.getProperty(JCoConnection.JCO_DESTINATION) + "'", be);
			}
		}
	}

	/**
	 * Returns the RFC Destination for the requested name.
	 * 
	 * @param destinationName
	 *           destination name
	 * @return {@link RFCDestination}
	 */
	protected RFCDestination getRFCDestination(final String destinationName)
	{
		log.debug("RFC Destination " + destinationName + " will be read.");
		final RFCDestination rfcDestination = rfcDestinationService.getRFCDestination(destinationName);
		if (rfcDestination == null)
		{
			throw new BackendRuntimeException("Destination '" + properties.getProperty(JCoConnection.JCO_DESTINATION)
					+ "' does not exist! Please check the configuration for RFC Destinations.");
		}
		return rfcDestination;
	}

}
