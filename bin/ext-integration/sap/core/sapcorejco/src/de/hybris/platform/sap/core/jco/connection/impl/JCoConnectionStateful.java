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

import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.JCoExceptionSpliter;
import de.hybris.platform.sap.core.jco.runtime.SAPJCoSessionReferenceProvider;

import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoCustomDestination;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;


/**
 * Stateful RFC connection. <br>
 * Establishes a stateful RFC connection to the backend by calling JCoContext.begin() during initialization and
 * JCoContext.end() during destruction. Destruction is done during destroy() method. After the destroy() method was
 * called, it is no more possible to execute JCoFunctions with that connection. <br>
 * During finalize() this class tests if the RFC connection was ended properly. If not it writes error messages to the
 * log file and tries to end the RFC connection in order to avoid hanging RFC connections.
 */
public class JCoConnectionStateful extends JCoConnectionImpl implements JCoStateful
{
	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoConnectionStateful.class.getName());


	/**
	 * Constructor.
	 * 
	 * @param properties
	 *           properties
	 */
	public JCoConnectionStateful(final Properties properties)
	{
		super(properties);
		setScopeType(UUID.randomUUID().toString().replaceAll("-", ""));
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.bol.jco2.RFCManagedConnectionImpl#init()
	 */
	@Override
	public void init() throws BackendException
	{
		super.init();
		SAPJCoSessionReferenceProvider.getReferenceProvider().registerJCoConnection(getScopeType());
		JCoContext.begin(getDestination());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.bol.jco2.RFCStateful#destroy()
	 */
	@Override
	public void destroy() throws BackendException
	{
		try
		{
			final JCoCustomDestination destinationLocal = getDestination();
			if (destinationLocal != null)
			{
				JCoContext.end(destinationLocal);
				if (JCoContext.isStateful(destinationLocal))
				{
					// In case of JCoContext.begin is called several times the JCo Connections is not 
					// released if JCoContext.end is called only one time. The encapsulation of this 
					// class should avoid this situation.
					// This check is to be on the safe side. 
					throw new BackendException("JCoConnection is still stateful after JCoContext.end()");
				}
				setDestroyed();
				SAPJCoSessionReferenceProvider.getReferenceProvider().unRegisterJCoConnection(this.getScopeType());
			}
		}
		catch (final JCoException e)
		{
			JCoExceptionSpliter.splitAndThrowException(e);
		}
	}

	/**
	 * Tries to terminate a stateful connection several times until the context is ended. <br>
	 * In case of a destination is called several times with {@link JCoContext}.begin() the {@link JCoContext}.end()
	 * needs to be called the equivalent. This method is for preventing hanging connections in the backend and logs an
	 * error if the connection was not destroyed properly. <br>
	 * This circumstance should not happen if just this class is used for connection handling. But if a
	 * {@link JCoDestination} is requested directly through the {@link JCoDestinationManager}, which reuses instances, is
	 * is technically possible to begin a context several times.
	 * 
	 */
	protected void endJCoContextInALoop()
	{
		for (int i = 0; i < 5; i++)
		{
			JCoCustomDestination destinationLocal;
			destinationLocal = getDestination();
			if (!JCoContext.isStateful(destinationLocal))
			{
				return;
			}
			try
			{
				JCoContext.end(destinationLocal);
			}
			catch (final JCoException e)
			{
				log.log(Level.ERROR, "JCoContext.end(destination) fails.", e);
				log.log(Level.ERROR,
						"Could not cleanup stateful connection after retrying 5 times. Open connections RFC connections could remain on the backend");
				return;
			}
		}
		log.log(Level.ERROR, "Could not cleanup stateful connection after retrying 5 times");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.bol.jco2.RFCManagedConnectionImpl#getFunction(java.lang.String)
	 */
	@Override
	public JCoFunction getFunction(final String funcName) throws BackendException
	{
		checkInternalStatus();
		return super.getFunction(funcName);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.bol.jco2.RFCManagedConnectionImpl#execute(com.sap.conn.jco.JCoFunction)
	 */
	@Override
	public void execute(final JCoFunction function) throws BackendException
	{
		checkInternalStatus();
		super.execute(function);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			if (SAPJCoSessionReferenceProvider.getReferenceProvider().isRegistered(getScopeType()))
			{
				SAPJCoSessionReferenceProvider.getReferenceProvider().unRegisterJCoConnection(getScopeType());
			}
			if (getDestination() != null)
			{
				log.log(Level.ERROR, "Statefull connection was not destroyed properly. Caller id  is " + getCallerId());
				endJCoContextInALoop();
			}
		}
		finally
		{
			super.finalize();
		}
	}



}
