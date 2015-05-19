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
package de.hybris.platform.sap.core.jco.runtime;

import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionException;
import com.sap.conn.jco.ext.SessionReferenceProvider;


/**
 * The SAPJCoSessionReferenceProvider is a implementation of the SessionReferenceProvider supporting client sessions
 * running in separate threads (all operation in a thread belongs to the same client session) and server sessions. JCo
 * API allows stateful communication using JCoContext.begin() and JCoContext.end() API. At runtime JCoConnections
 * registers themselves with a scope id, which is a UUID. As soon as the JCoConnection object is destroyed or at least
 * at finalization the entry is removed from the list.
 */
public class SAPJCoSessionReferenceProvider implements SessionReferenceProvider
{

	/**
	 * Marker for reference id with default scope type (means if scope type was null).
	 */
	private static final String SAP_JCO_REFERENCE_DEFAULT = "default";

	private static final Logger loc = Logger.getLogger(SAPJCoSessionReferenceProvider.class.getName());

	/**
	 * Singleton by static definition, since server can run with multiple tenants and JCo Runtime supports only one
	 * reference provider.
	 */
	private static SAPJCoSessionReferenceProvider referenceProvider = new SAPJCoSessionReferenceProvider();

	/**
	 * Map of alive connections.
	 */
	private final Map<String, SAPJCoSessionReference> aliveConnections = new ConcurrentHashMap<String, SAPJCoSessionReference>();

	/**
	 * @return the referenceProvider
	 */
	public static SAPJCoSessionReferenceProvider getReferenceProvider()
	{
		return referenceProvider;
	}


	/**
	 * Register the SAPJCoSessionReferenceProvider.
	 */
	static public void init()
	{
		if (!Environment.isSessionReferenceProviderRegistered())
		{
			loc.debug("SAPJCoSessionReferenceProvider provider gets registered.");
			Environment.registerSessionReferenceProvider(referenceProvider);
		}
	}

	@Override
	public JCoSessionReference getCurrentSessionReference(final String jcoScopeType)
	{
		if (jcoScopeType == null)
		{
			//For scope type null generate a random UUID and do not register it. isSessionAlive() will return false. 
			return new SAPJCoSessionReference(SAP_JCO_REFERENCE_DEFAULT + "@" + UUID.randomUUID().toString().replaceAll("-", ""));
		}
		return new SAPJCoSessionReference(jcoScopeType);
	}

	/**
	 * Registers a JCo reference id.
	 * 
	 * @param jcoReferenceID
	 *           reference id.
	 */
	public void registerJCoConnection(final String jcoReferenceID)
	{
		if (isRegistered(jcoReferenceID))
		{
			throw new BackendRuntimeException("" + jcoReferenceID + " already in map");
		}
		loc.debug("Register jcoReferenceId " + jcoReferenceID);
		aliveConnections.put(jcoReferenceID, new SAPJCoSessionReference(jcoReferenceID));
	}

	/**
	 * Unregisters a JCo reference id.
	 * 
	 * @param jcoReferenceID
	 *           reference id
	 */
	public void unRegisterJCoConnection(final String jcoReferenceID)
	{
		if (!isRegistered(jcoReferenceID))
		{
			throw new BackendRuntimeException("Cannot unregister  " + jcoReferenceID);
		}
		loc.debug("Unregister jcoReferenceId " + jcoReferenceID);
		aliveConnections.remove(jcoReferenceID);
	}


	/**
	 * Check if a reference id is registered.
	 * 
	 * @param jcoReferenceID
	 *           reference id.
	 * @return true if registered.
	 */
	public boolean isRegistered(final String jcoReferenceID)
	{
		return aliveConnections.containsKey(jcoReferenceID);
	}

	@Override
	public boolean isSessionAlive(final String jcoReferenceID)
	{
		final boolean registered = isRegistered(jcoReferenceID);

		if (loc.isDebugEnabled())
		{
			loc.debug("isSessionAlive returns " + registered + " for jcoReferenceId " + jcoReferenceID + ". Map contains "
					+ aliveConnections.size() + " values.");
		}

		return registered;
	}

	@Override
	public void jcoServerSessionContinued(final String sessionID) throws SessionException
	{
		// only needed for JCo server
	}

	@Override
	public void jcoServerSessionFinished(final String sessionID) throws SessionException
	{
		// only needed for JCo server
	}

	@Override
	public void jcoServerSessionPassivated(final String sessionID) throws SessionException
	{
		// only needed for JCo server
	}

	@Override
	public JCoSessionReference jcoServerSessionStarted() throws SessionException
	{
		// only needed for JCo server
		return null;
	}

}
