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
package de.hybris.platform.sap.core.configuration.rfc.impl;

import de.hybris.platform.sap.core.configuration.model.RFCDestinationAttributeModel;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.constants.SapcoreConstants;

import java.util.Collection;


/**
 * 
 */
public class DefaultRFCDestination implements RFCDestination
{
	private String password;

	private String sid;

	private String group;

	private String poolSize;

	private String client;

	private String maxWaitTime;

	private String targetHost;

	private String userid;

	private Boolean pooledConnectionMode;

	private String messageServer;

	private String instance;

	private String maxConnections;

	private String rfcDestinationName;

	private Boolean connectionType;

	private String jcoTraceLevel;

	private String jcoMsServ;

	private String jcoSAPRouter;

	private String jcoTracePath;

	private Boolean jcoRFCTrace;

	private String jcoCPICTrace;

	private Boolean jcoClientDelta;

	private String backendType;

	private String sncQOP;

	private boolean sncMode;

	private String sncPartnerName;

	private boolean offline;

	private Collection<RFCDestinationAttributeModel> additionalAttributes;

	@Override
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

	@Override
	public String getSid()
	{
		return sid;
	}

	/**
	 * Sets the system id.
	 * 
	 * @param sid
	 *           the sid to set
	 */
	public void setSid(final String sid)
	{
		this.sid = sid;
	}

	@Override
	public String getGroup()
	{
		return group;
	}

	/**
	 * Sets the logon group name.
	 * 
	 * @param group
	 *           the group to set
	 */
	public void setGroup(final String group)
	{
		this.group = group;
	}

	@Override
	public String getPoolSize()
	{
		return poolSize;
	}

	/**
	 * Sets the connection pool size.
	 * 
	 * @param poolSize
	 *           the poolSize to set
	 */
	public void setPoolSize(final String poolSize)
	{
		this.poolSize = poolSize;
	}

	@Override
	public String getClient()
	{
		return client;
	}

	/**
	 * Sets the client.
	 * 
	 * @param client
	 *           the client to set
	 */
	public void setClient(final String client)
	{
		this.client = client;
	}

	@Override
	public String getMaxWaitTime()
	{
		return maxWaitTime;
	}

	/**
	 * Sets the maximum waiting time.
	 * 
	 * @param maxWaitTime
	 *           the maxWaitTime to set
	 */
	public void setMaxWaitTime(final String maxWaitTime)
	{
		this.maxWaitTime = maxWaitTime;
	}

	@Override
	public String getTargetHost()
	{
		return targetHost;
	}

	/**
	 * Sets the target host.
	 * 
	 * @param targetHost
	 *           the targetHost to set
	 */
	public void setTargetHost(final String targetHost)
	{
		this.targetHost = targetHost;
	}

	@Override
	public String getUserid()
	{
		return userid;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param userid
	 *           the userid to set
	 */
	public void setUserid(final String userid)
	{
		this.userid = userid;
	}

	@Override
	public Boolean getPooledConnectionMode()
	{
		return pooledConnectionMode;
	}

	/**
	 * Sets the pooled connection activation indicator.
	 * 
	 * @param pooledConnectionMode
	 *           the pooledConnectionMode to set
	 */
	public void setPooledConnectionMode(final Boolean pooledConnectionMode)
	{
		this.pooledConnectionMode = pooledConnectionMode;
	}

	@Override
	public String getMessageServer()
	{
		return messageServer;
	}

	/**
	 * Sets the message server (for group connection).
	 * 
	 * @param messageServer
	 *           the messageServer to set
	 */
	public void setMessageServer(final String messageServer)
	{
		this.messageServer = messageServer;
	}

	@Override
	public String getInstance()
	{
		return instance;
	}

	/**
	 * Sets the instance number.
	 * 
	 * @param instance
	 *           the instance to set
	 */
	public void setInstance(final String instance)
	{
		this.instance = instance;
	}

	@Override
	public String getMaxConnections()
	{
		return maxConnections;
	}

	/**
	 * Sets the maximum number of connections.
	 * 
	 * @param maxConnections
	 *           the maxConnections to set
	 */
	public void setMaxConnections(final String maxConnections)
	{
		this.maxConnections = maxConnections;
	}

	@Override
	public String getRfcDestinationName()
	{
		return rfcDestinationName;
	}

	/**
	 * Sets the RFC destination name.
	 * 
	 * @param rfcDestinationName
	 *           the rfcDestinationName to set
	 */
	public void setRfcDestinationName(final String rfcDestinationName)
	{
		this.rfcDestinationName = rfcDestinationName;
	}

	@Override
	public Boolean getConnectionType()
	{
		return connectionType;
	}

	/**
	 * Sets the connection type.
	 * 
	 * @param connectionType
	 *           the connectionType to set
	 */
	public void setConnectionType(final Boolean connectionType)
	{
		this.connectionType = connectionType;
	}

	@Override
	public String getJcoTraceLevel()
	{
		return jcoTraceLevel;
	}

	/**
	 * Sets the JCo trace level.
	 * 
	 * @param jcoTraceLevel
	 *           the jcoTraceLevel to set
	 */
	public void setJcoTraceLevel(final String jcoTraceLevel)
	{
		this.jcoTraceLevel = jcoTraceLevel;
	}

	@Override
	public String getJcoTracePath()
	{
		return jcoTracePath;
	}

	/**
	 * Sets the JCo trace path.
	 * 
	 * @param jcoTracePath
	 *           the jcoTracePath to set
	 */
	public void setJcoTracePath(final String jcoTracePath)
	{
		this.jcoTracePath = jcoTracePath;
	}

	@Override
	public Boolean isJcoRFCTraceEnabled()
	{
		return jcoRFCTrace;
	}

	/**
	 * Sets the JCo trace.
	 * 
	 * @param jcoRFCTrace
	 *           the jcoRFCTrace to set
	 */
	public void setJcoRFCTrace(final String jcoRFCTrace)
	{
		if (jcoRFCTrace.equals("1"))
		{
			this.jcoRFCTrace = true;
		}
		else
		{
			this.jcoRFCTrace = true;
		}

	}

	@Override
	public String getJcoCPICTrace()
	{
		return jcoCPICTrace;
	}

	/**
	 * Sets the JCo CPIC trace.
	 * 
	 * @param jcoCPICTrace
	 *           the jcoCPICTrace to set
	 */
	public void setJcoCPICTrace(final String jcoCPICTrace)
	{
		this.jcoCPICTrace = jcoCPICTrace;
	}

	@Override
	public String getJcoMsServ()
	{
		return jcoMsServ;
	}

	/**
	 * Sets the JCo message server.
	 * 
	 * @param jcoMsServ
	 *           the jcoMsServ to set
	 */
	public void setJcoMsServ(final String jcoMsServ)
	{
		this.jcoMsServ = jcoMsServ;
	}

	@Override
	public String getJcoSAPRouter()
	{
		return jcoSAPRouter;
	}

	/**
	 * Sets the SAP router.
	 * 
	 * @param jcoSAPRouter
	 *           the jcoSAPRouter to set
	 */
	public void setJcoSAPRouter(final String jcoSAPRouter)
	{
		this.jcoSAPRouter = jcoSAPRouter;
	}

	@Override
	public Boolean getJcoClientDelta()
	{
		return jcoClientDelta;
	}

	/**
	 * Sets the client delta switch.
	 * 
	 * @param jcoClientDelta
	 *           the jcoClientDelta to set
	 */
	public void setJcoClientDelta(final Boolean jcoClientDelta)
	{
		this.jcoClientDelta = jcoClientDelta;
	}

	@Override
	public String getBackendType()
	{
		return this.backendType;
	}

	/**
	 * Sets the backend type.
	 * 
	 * @param backendType
	 *           the backend type
	 */
	public void setBackendType(final String backendType)
	{
		this.backendType = backendType;
	}

	@Override
	public Boolean isSncEnabled()
	{

		return sncMode;
	}

	/**
	 * Sets the SNC activation indicator.
	 * 
	 * @param sncMode
	 *           SNC activation indicator
	 */
	public void setSncMode(final boolean sncMode)
	{
		this.sncMode = sncMode;
	}

	@Override
	public String getSncQOP()
	{
		return sncQOP;
	}

	/**
	 * Sets the SNC security level.
	 * 
	 * @param sncQOP
	 *           SNC security level
	 */
	public void setSncQOP(final String sncQOP)
	{
		this.sncQOP = sncQOP;
	}

	@Override
	public String getSncPartnerName()
	{
		return sncPartnerName;
	}

	/**
	 * Sets the SNC communication partner name (application server).
	 * 
	 * @param sncPartnerName
	 *           SNC partner name
	 */
	public void setSncPartnerName(final String sncPartnerName)
	{
		this.sncPartnerName = sncPartnerName;
	}

	@Override
	public boolean isOffline()
	{
		return offline;
	}

	/**
	 * Sets the offline indicator.
	 * 
	 * @param offline
	 *           offline indicator
	 */
	public void setOffline(final boolean offline)
	{
		this.offline = offline;
	}

	@Override
	public Collection<RFCDestinationAttributeModel> getRFCDestinationAttributes()
	{
		return additionalAttributes;
	}

	/**
	 * Sets the additional attributes.
	 * 
	 * @param additionalAttributes
	 *           the additional attributes to set
	 */
	public void setAdditionalAttributes(final Collection<RFCDestinationAttributeModel> additionalAttributes)
	{
		this.additionalAttributes = additionalAttributes;
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreConstants.CRLF + "- RFC Destination Name: " + rfcDestinationName + SapcoreConstants.CRLF
				+ "- RFC Backend Type: " + backendType + SapcoreConstants.CRLF + "- RFC SID: " + sid + SapcoreConstants.CRLF
				+ "- RFC Message Server: " + messageServer + SapcoreConstants.CRLF + "- RFC Target Host: " + targetHost
				+ SapcoreConstants.CRLF + "-SNC enabled: " + sncMode + SapcoreConstants.CRLF + "-SNC Quality of Protection: "
				+ sncQOP + SapcoreConstants.CRLF + "-SNC Partner Name: " + sncPartnerName + SapcoreConstants.CRLF + "-is offline: "
				+ offline + SapcoreConstants.CRLF + "-additional attributes: " + additionalAttributes;
	}

}
