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

package de.hybris.platform.sap.core.jco.monitor.provider;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.sap.core.jco.monitor.JCoConnectionDataMock;
import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.sap.core.jco.monitor.jaxb.JCoMonitorJAXBHandler;
import de.hybris.platform.sap.core.jco.monitor.jaxb.JcoConnectionType;
import de.hybris.platform.sap.core.jco.monitor.jaxb.NodeType;
import de.hybris.platform.sap.core.jco.monitor.jaxb.Nodes;
import de.hybris.platform.sap.core.test.TestFileUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sap.conn.jco.monitor.JCoConnectionData;


/**
 * Provides the local JCo connection monitor context.
 */
public class JCoConnectionMonitorTestContext implements JCoConnectionMonitorContext
{

	private JCoMonitorJAXBHandler jaxbHandler;
	private String filename = null;
	private List<JCoConnectionDataMock> jcoConnectionData = null;
	private long snapshotTimestamp;

	/**
	 * Injection setter for JAXB handler.
	 * 
	 * @param jaxbHandler
	 *           the jaxbHandler to set
	 */
	public void setJaxbHandler(final JCoMonitorJAXBHandler jaxbHandler)
	{
		this.jaxbHandler = jaxbHandler;
	}

	/**
	 * @param snapshotTimestamp
	 *           the snapshotTimestamp to set
	 */
	public void setSnapshotTimestamp(final long snapshotTimestamp)
	{
		this.snapshotTimestamp = snapshotTimestamp;
	}

	/**
	 * Injection setter for filename.
	 * 
	 * @param filename
	 *           the filename to set
	 */
	public void setFilename(final String filename)
	{
		this.filename = filename;
	}

	/**
	 * 
	 */
	public void init()
	{
		try
		{
			final File file = TestFileUtility.getFile("sapcorejco", filename);
			final Nodes nodes = jaxbHandler.generateNodes(file);
			createJCoConnectionData(nodes);
		}
		catch (final JCoMonitorException e)
		{
			throw new CoreBaseRuntimeException("Error reading file with name " + filename, e);
		}

	}

	@Override
	public List<? extends JCoConnectionData> getJCoConnectionData()
	{
		return jcoConnectionData;
	}

	@Override
	public long getSnapshotTimestamp()
	{
		return snapshotTimestamp;
	}

	/**
	 * Creates JCoConnectionData from nodes.
	 * 
	 * @param nodes
	 *           JAXB content
	 */
	private void createJCoConnectionData(final Nodes nodes)
	{
		jcoConnectionData = new ArrayList<JCoConnectionDataMock>();
		final List<NodeType> nodeList = nodes.getNode();
		for (final NodeType node : nodeList)
		{
			final List<JcoConnectionType> jcoConnectionList = node.getJcoConnections().getJcoConnection();
			for (final JcoConnectionType jcoConnection : jcoConnectionList)
			{
				final JCoConnectionDataMock jcoConnectionDataMock = new JCoConnectionDataMock();
				jcoConnectionDataMock.setAbapClient(jcoConnection.getAbapClient());
				jcoConnectionDataMock.setAbapHost(jcoConnection.getAbapHost());
				jcoConnectionDataMock.setAbapLanguage(jcoConnection.getAbapLanguage());
				jcoConnectionDataMock.setAbapSystemNumber(jcoConnection.getAbapSystemNumber());
				jcoConnectionDataMock.setAbapUser(jcoConnection.getAbapUser());
				jcoConnectionDataMock.setApplicationName(jcoConnection.getApplicationName());
				jcoConnectionDataMock.setConnectionHandle(jcoConnection.getConnectionHandle());
				jcoConnectionDataMock.setConnectionType(jcoConnection.getConnectionType());
				jcoConnectionDataMock.setConvId(jcoConnection.getConversationId());
				jcoConnectionDataMock.setDSRPassport(jcoConnection.getDsrPassport());
				jcoConnectionDataMock.setFunctionModuleName(jcoConnection.getFunctionModuleName());
				jcoConnectionDataMock.setGroupName(jcoConnection.getGroupName());
				jcoConnectionDataMock.setLastActivityTimestamp(jcoConnection.getLastActivityTimestamp());
				jcoConnectionDataMock.setProtocol(jcoConnection.getProtocol());
				jcoConnectionDataMock.setState(jcoConnection.getState());
				jcoConnectionDataMock.setStateString(jcoConnection.getStateString());
				jcoConnectionDataMock.setSystemID(jcoConnection.getSystemId());
				jcoConnectionDataMock.setThreadId(jcoConnection.getThreadId());
				jcoConnectionDataMock.setThreadName(jcoConnection.getThreadName());
				jcoConnectionData.add(jcoConnectionDataMock);
			}

		}
	}


}
