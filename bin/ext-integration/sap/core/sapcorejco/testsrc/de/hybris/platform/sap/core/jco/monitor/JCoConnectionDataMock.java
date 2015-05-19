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

package de.hybris.platform.sap.core.jco.monitor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sap.conn.jco.monitor.JCoConnectionData;


/**
 * JCoConnectionData mock class.
 */
public class JCoConnectionDataMock implements JCoConnectionData
{

	private String abapClient;
	private String abapHost;
	private String abapLanguage;
	private String abapSystemNumber;
	private String abapUser;
	private String applicationName;
	private long connectionHandle;
	private String connectionType;
	private String convId;
	private String dsrPassport;
	private String functionModuleName;
	private String groupName;
	private long lastActivityTimestamp;
	private String protocol;
	private String sessionId;
	private int state;
	private String systemID;
	private long threadId;
	private String threadName;
	private String stateString;

	/**
	 * Setter.
	 * 
	 * @param abapClient
	 *           the abapClient to set
	 */
	public void setAbapClient(final String abapClient)
	{
		this.abapClient = abapClient;
	}

	/**
	 * Setter.
	 * 
	 * @param abapHost
	 *           the abapHost to set
	 */
	public void setAbapHost(final String abapHost)
	{
		this.abapHost = abapHost;
	}

	/**
	 * Setter.
	 * 
	 * @param abapLanguage
	 *           the abapLanguage to set
	 */
	public void setAbapLanguage(final String abapLanguage)
	{
		this.abapLanguage = abapLanguage;
	}

	/**
	 * Setter.
	 * 
	 * @param abapSystemNumber
	 *           the abapSystemNumber to set
	 */
	public void setAbapSystemNumber(final String abapSystemNumber)
	{
		this.abapSystemNumber = abapSystemNumber;
	}

	/**
	 * Setter.
	 * 
	 * @param abapUser
	 *           the abapUser to set
	 */
	public void setAbapUser(final String abapUser)
	{
		this.abapUser = abapUser;
	}

	/**
	 * Setter.
	 * 
	 * @param applicationName
	 *           the applicationName to set
	 */
	public void setApplicationName(final String applicationName)
	{
		this.applicationName = applicationName;
	}

	/**
	 * Setter.
	 * 
	 * @param connectionHandle
	 *           the connectionHandle to set
	 */
	public void setConnectionHandle(final long connectionHandle)
	{
		this.connectionHandle = connectionHandle;
	}

	/**
	 * Setter.
	 * 
	 * @param connectionType
	 *           the connectionType to set
	 */
	public void setConnectionType(final String connectionType)
	{
		this.connectionType = connectionType;
	}

	/**
	 * Setter.
	 * 
	 * @param convId
	 *           the convId to set
	 */
	public void setConvId(final String convId)
	{
		this.convId = convId;
	}

	/**
	 * Setter.
	 * 
	 * @param string
	 *           the dSRPassport to set
	 */
	public void setDSRPassport(final String string)
	{
		dsrPassport = string;
	}

	/**
	 * Setter.
	 * 
	 * @param functionModuleName
	 *           the functionModuleName to set
	 */
	public void setFunctionModuleName(final String functionModuleName)
	{
		this.functionModuleName = functionModuleName;
	}

	/**
	 * Setter.
	 * 
	 * @param groupName
	 *           the groupName to set
	 */
	public void setGroupName(final String groupName)
	{
		this.groupName = groupName;
	}

	/**
	 * Setter.
	 * 
	 * @param lastActivityTimestamp
	 *           the lastActivityTimestamp to set
	 */
	public void setLastActivityTimestamp(final long lastActivityTimestamp)
	{
		this.lastActivityTimestamp = lastActivityTimestamp;
	}

	/**
	 * Setter.
	 * 
	 * @param protocol
	 *           the protocol to set
	 */
	public void setProtocol(final String protocol)
	{
		this.protocol = protocol;
	}

	/**
	 * Setter.
	 * 
	 * @param sessionId
	 *           the sessionId to set
	 */
	public void setSessionId(final String sessionId)
	{
		this.sessionId = sessionId;
	}

	/**
	 * Setter.
	 * 
	 * @param state
	 *           the state to set
	 */
	public void setState(final int state)
	{
		this.state = state;
	}

	/**
	 * Setter.
	 * 
	 * @param stateString
	 *           string the state string to set
	 */
	public void setStateString(final String stateString)
	{
		this.stateString = stateString;
	}

	/**
	 * Setter.
	 * 
	 * @param systemID
	 *           the systemID to set
	 */
	public void setSystemID(final String systemID)
	{
		this.systemID = systemID;
	}

	/**
	 * Setter.
	 * 
	 * @param threadId
	 *           the threadId to set
	 */
	public void setThreadId(final long threadId)
	{
		this.threadId = threadId;
	}

	/**
	 * Setter.
	 * 
	 * @param threadName
	 *           the threadName to set
	 */
	public void setThreadName(final String threadName)
	{
		this.threadName = threadName;
	}

	@Override
	public String getAbapClient()
	{
		return abapClient;
	}

	@Override
	public String getAbapHost()
	{
		return abapHost;
	}

	@Override
	public String getAbapLanguage()
	{
		return abapLanguage;
	}

	@Override
	public String getAbapSystemNumber()
	{
		return abapSystemNumber;
	}

	@Override
	public String getAbapUser()
	{
		return abapUser;
	}

	@Override
	public String getApplicationName()
	{
		return applicationName;
	}

	@Override
	public long getConnectionHandle()
	{
		return connectionHandle;
	}

	@Override
	public String getConnectionHandleAsString()
	{
		return Long.toString(connectionHandle);
	}

	@Override
	public String getConnectionType()
	{
		return connectionType;
	}

	@Override
	public String getConvId()
	{
		return convId;
	}

	@Override
	public byte[] getDSRPassport()
	{
		return dsrPassport.getBytes();
	}

	@Override
	public String getDSRPassportAsString()
	{
		return "";
		//		return Byte.toString(DSRPassport);
	}

	@Override
	public String getFunctionModuleName()
	{
		return functionModuleName;
	}

	@Override
	public String getGroupName()
	{
		return groupName;
	}

	@Override
	public long getLastActivityTimestamp()
	{
		return lastActivityTimestamp;
	}

	@Override
	public String getLastActivityTimestampAsString(final Calendar arg0)
	{
		return new SimpleDateFormat("yyyy-MM-dd|HHmmss").format(new Date(lastActivityTimestamp));
	}

	@Override
	public String getProtocol()
	{
		return protocol;
	}

	@Override
	public String getSessionId()
	{
		return sessionId;
	}

	@Override
	public int getState()
	{
		return state;
	}

	@Override
	public String getStateAsString()
	{
		return stateString;
	}

	@Override
	public String getSystemID()
	{
		return systemID;
	}

	@Override
	public long getThreadId()
	{
		return threadId;
	}

	@Override
	public String getThreadIdAsString()
	{
		return Long.toString(threadId);
	}

	@Override
	public String getThreadName()
	{
		return threadName;
	}

}
