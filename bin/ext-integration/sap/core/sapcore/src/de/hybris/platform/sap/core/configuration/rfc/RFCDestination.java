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
package de.hybris.platform.sap.core.configuration.rfc;

import de.hybris.platform.sap.core.configuration.model.RFCDestinationAttributeModel;

import java.util.Collection;


/**
 * Interface to read RFC destination properties.
 */
public interface RFCDestination
{
	/**
	 * Returns the password.
	 * 
	 * @return the password
	 */
	public String getPassword();

	/**
	 * Returns the system id.
	 * 
	 * @return the sid
	 */
	public String getSid();

	/**
	 * Returns the logon group name.
	 * 
	 * @return the group
	 */
	public String getGroup();

	/**
	 * Returns the pool size.
	 * 
	 * @return the poolSize
	 */
	public String getPoolSize();

	/**
	 * Returns the client.
	 * 
	 * @return the client
	 */
	public String getClient();

	/**
	 * Returns the maximum waiting time in milliseconds.
	 * 
	 * @return the maxWaitTime
	 */
	public String getMaxWaitTime();

	/**
	 * Returns the host name of the target application server (for server connection) or the message server (for group
	 * connection).
	 * 
	 * @return the targetHost
	 */
	public String getTargetHost();

	/**
	 * Returns the user id.
	 * 
	 * @return the userid
	 */
	public String getUserid();

	/**
	 * Returns the indicator whether a pooled connection mode is enabled.
	 * 
	 * @return the pooledConnectionMode
	 */
	public Boolean getPooledConnectionMode();

	/**
	 * Returns the message server (for group connection).
	 * 
	 * @return the messageServer
	 */
	public abstract String getMessageServer();

	/**
	 * Returns the instance number of the application server (for server connection only).
	 * 
	 * @return the instance
	 */
	public String getInstance();

	/**
	 * Returns the maximum number of connections.
	 * 
	 * @return the maxConnections
	 */
	public String getMaxConnections();

	/**
	 * Returns the RFC destination name.
	 * 
	 * @return the rfcDestinationName
	 */
	public String getRfcDestinationName();

	/**
	 * Returns the type of connection (server or group connection).
	 * 
	 * @return the connectionType
	 */
	public Boolean getConnectionType();

	/**
	 * Returns the JCo trace level (0-10).
	 * 
	 * @return the jcoTraceLevel - Turns on the JCo trace. Allowed levels are [0 .. 10]
	 */
	public String getJcoTraceLevel();

	/**
	 * Returns the JCo trace path (null, stdout, stderr or an existing path are allowed).
	 * 
	 * @return the jcoTracePath
	 */
	public String getJcoTracePath();

	/**
	 * Returns the indicator whether JCo RFC trace is enabled (0 or 1).
	 * 
	 * @return the jcoRFCTrace
	 */
	public Boolean isJcoRFCTraceEnabled();

	/**
	 * Returns the CPIC trace (-1 [take over environment value 'CPIC_TRACE'], 0 no trace, 1,2,3 different trace levels).
	 * 
	 * @return the jcoCPICTrace - Enable/disable CPIC trace
	 */
	public String getJcoCPICTrace();

	/**
	 * Returns the SAP message server or port number.
	 * 
	 * @return the jcoMsServ
	 */
	public String getJcoMsServ();

	/**
	 * Returns the SAP router string to use for networks being protected by a firewall.
	 * 
	 * @return the jcoSAPRouter string
	 */
	public String getJcoSAPRouter();

	/**
	 * Returns the indicator for table parameter delta management (1 - enable [default] or 0 - disable).
	 * 
	 * @return the jcoClientDelta indicator
	 */
	public Boolean getJcoClientDelta();

	/**
	 * Returns the backend type.
	 * 
	 * @return the backend Type
	 */
	public String getBackendType();

	/**
	 * Returns the indicator whether SNC is enabled.
	 * 
	 * @return true, if SNC is enabled
	 */
	public Boolean isSncEnabled();

	/**
	 * Returns the SNC security level / quality of service.
	 * 
	 * @return SNC security level / quality of service
	 */
	public String getSncQOP();

	/**
	 * Returns SNC name of the communication partner (application server).
	 * 
	 * @return name of SNC partner
	 */
	public String getSncPartnerName();

	/**
	 * Returns the offline indicator.
	 * 
	 * @return true if offline
	 */
	public boolean isOffline();

	/**
	 * Returns the additional Attributes for RFC Destination.
	 * 
	 * @return the collection of attributes
	 */
	public Collection<RFCDestinationAttributeModel> getRFCDestinationAttributes();

}
