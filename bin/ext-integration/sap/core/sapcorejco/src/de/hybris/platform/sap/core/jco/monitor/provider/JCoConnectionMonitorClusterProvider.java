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

import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.jco.constants.SapcorejcoConstants;
import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.sap.core.jco.monitor.jaxb.JcoConnectionType;
import de.hybris.platform.sap.core.jco.monitor.jaxb.NodeType;
import de.hybris.platform.sap.core.jco.monitor.jaxb.Nodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Provider of {@link JCoConnectionMonitorProvider} for node environment.
 */
public class JCoConnectionMonitorClusterProvider extends JCoConnectionMonitorProvider
{

	private static final Logger LOG = Logger.getLogger(JCoConnectionMonitorClusterProvider.class.getName());

	private long cacheTimestamp;
	private int nodesCount;
	private int nodesWithoutResultCount;
	private int longRunnerCount;
	private int totalCount;
	private int poolLimitReachedCount;

	/**
	 * Initializes the snapshot cache.
	 * 
	 * @param snapshotsPerNode
	 *           snapshot as XML string per node id
	 * @param snapshotTimestamp
	 *           snapshot time stamp
	 * @throws JCoMonitorException
	 *            {@link JCoMonitorException}
	 */
	public void initializeCache(final Map<Integer, String> snapshotsPerNode, final long snapshotTimestamp)
			throws JCoMonitorException
	{
		reset();
		createSnapshotCache(snapshotsPerNode, snapshotTimestamp);
	}

	/**
	 * Resets the snapshot content.
	 */
	public void reset()
	{
		this.cacheTimestamp = 0;
		nodesCount = 0;
		nodesWithoutResultCount = 0;
		longRunnerCount = 0;
		totalCount = 0;
		poolLimitReachedCount = 0;
	}

	/**
	 * Checks if snapshot content is available.
	 * 
	 * @return true, if available
	 */
	public boolean snapshotAvailable()
	{
		return (cacheTimestamp > 0);
	}

	/**
	 * Returns the time stamp.
	 * 
	 * @return time stamp as long
	 */
	public Long getCacheTimestamp()
	{
		if (!snapshotAvailable())
		{
			return null;
		}
		return cacheTimestamp;
	}

	/**
	 * Returns the total number of nodes of the result.
	 * 
	 * @return number of nodes
	 */
	public Integer getNodesCount()
	{
		if (!snapshotAvailable())
		{
			return null;
		}
		return nodesCount;
	}

	/**
	 * Returns the total number of nodes which returned no result.
	 * 
	 * @return number of nodes without result
	 */
	public Integer getNodesWithoutResultCount()
	{
		if (!snapshotAvailable())
		{
			return null;
		}
		return nodesWithoutResultCount;
	}

	/**
	 * Returns cached result of total number of JCo connections.
	 * 
	 * @return number of JCo connections
	 */
	public Integer getTotalCount()
	{
		if (!snapshotAvailable())
		{
			return null;
		}
		return totalCount;
	}

	/**
	 * Returns the cached result for number of JCo connections whose lifetime exceed a specific threshold parameter.
	 * 
	 * @return number of JCo connections whose lifetime exceed the defined threshold
	 */
	public Integer getLongRunnerCount()
	{
		if (!snapshotAvailable())
		{
			return null;
		}
		return longRunnerCount;
	}

	/**
	 * Returns the cached result for number of RFC destinations whose pool size is reached.
	 * 
	 * @return number of RFC destinations whose pool size is reached
	 */
	public Integer getPoolLimitReachedCount()
	{
		if (!snapshotAvailable())
		{
			return null;
		}
		return poolLimitReachedCount;
	}

	/**
	 * Returns all current JCo connections as XML String.
	 * 
	 * @param snapshotsPerNode
	 *           snapshot as XML string per node id
	 * @param snapshotTimestamp
	 *           snapshot time stamp
	 * @return snapshot XML as string
	 * @throws JCoMonitorException
	 *            {@link JCoMonitorException}
	 */
	public String getSnapshotXML(final Map<Integer, String> snapshotsPerNode, final long snapshotTimestamp)
			throws JCoMonitorException
	{
		final Nodes nodes = jaxbHandler.generateNodes(snapshotsPerNode);
		return jaxbHandler.generateSnapshot(nodes);
	}

	/**
	 * Create JCo connections snapshot file operation.
	 * 
	 * @param snapshotsPerNode
	 *           snapshot as XML string per node id
	 * @param snapshotTimestamp
	 *           snapshot time stamp
	 * @return result message
	 * @throws JCoMonitorException
	 *            {@link JCoMonitorException}
	 */
	public File createSnapshotFile(final Map<Integer, String> snapshotsPerNode, final long snapshotTimestamp)
			throws JCoMonitorException
	{
		String statusMessage = "";
		try
		{
			final File snapshotFile = createSnapshotFile(SapcorejcoConstants.JCO_CONNECTION_CLUSTERS_SNAPSHOT_FILE_NAME, new Date(
					snapshotTimestamp));
			final FileOutputStream os = new FileOutputStream(snapshotFile);
			jaxbHandler.generateSnapshot(os, snapshotsPerNode);
			os.flush();
			os.close();
			return snapshotFile;
		}
		catch (final IOException e)
		{
			statusMessage = "Error when creating file output stream for snapshot file: " + e.getMessage();
			LOG.error(statusMessage, e);
			throw new JCoMonitorException(statusMessage, e);
		}
	}

	/**
	 * Creates the snapshot data which is buffered.
	 * 
	 * @param snapshotsPerNode
	 *           snapshot as XML string per node id
	 * @param cacheTimestamp
	 *           cache time stamp
	 * @throws JCoMonitorException
	 *            {@link JCoMonitorException}
	 */
	protected void createSnapshotCache(final Map<Integer, String> snapshotsPerNode, final long cacheTimestamp)
			throws JCoMonitorException
	{
		final Nodes nodes = jaxbHandler.generateNodes(snapshotsPerNode);
		final List<NodeType> nodeList = nodes.getNode();
		for (final NodeType node : nodeList)
		{
			nodesCount++;
			if (!node.isResultAvailable())
			{
				nodesWithoutResultCount++;
			}
			if (node.getJcoConnections() != null)
			{
				totalCount += node.getJcoConnections().getJcoConnection().size();
				final List<JcoConnectionType> jcoConnectionList = node.getJcoConnections().getJcoConnection();
				for (final JcoConnectionType jcoConnection : jcoConnectionList)
				{
					if (isLongLifetimeJCoConnection(cacheTimestamp, jcoConnection.getLastActivityTimestamp().longValue(),
							longLifetimeThresholdMSecs))
					{
						longRunnerCount++;
					}
				}
			}
		}
		final Map<String, List<JcoConnectionType>> rfcDestinationJCoConnectionsMap = getJCoConnectionByRFCDestinationMap(nodes);
		for (final String rfcDestinationName : rfcDestinationJCoConnectionsMap.keySet())
		{
			final RFCDestination rfcDestination = rfcDestinationService.getRFCDestination(rfcDestinationName);
			final int poolSize = Integer.parseInt(rfcDestination.getPoolSize());
			final List<JcoConnectionType> jCoconnectionList = rfcDestinationJCoConnectionsMap.get(rfcDestinationName);
			if (jCoconnectionList.size() >= poolSize)
			{
				poolLimitReachedCount += 1;
			}
		}
		this.cacheTimestamp = cacheTimestamp;
	}

	/**
	 * Returns all JCo connections per RFC destination.
	 * 
	 * @param nodes
	 *           JAXB content
	 * @return RFC destination / JCo Connections map
	 */
	protected Map<String, List<JcoConnectionType>> getJCoConnectionByRFCDestinationMap(final Nodes nodes)
	{
		final Map<String, List<JcoConnectionType>> rfcDestinationMap = new HashMap<String, List<JcoConnectionType>>();
		final List<NodeType> nodeList = nodes.getNode();
		for (final NodeType node : nodeList)
		{
			if (node.getJcoConnections() != null)
			{
				final List<JcoConnectionType> jcoConnectionList = node.getJcoConnections().getJcoConnection();
				for (final JcoConnectionType jcoConnection : jcoConnectionList)
				{
					final String rfcDestinationName = jcoConnection.getRfcDestinationName();
					List<JcoConnectionType> jCoConnectionList = rfcDestinationMap.get(rfcDestinationName);
					if (jCoConnectionList == null)
					{
						jCoConnectionList = new ArrayList<JcoConnectionType>();
					}
					jCoConnectionList.add(jcoConnection);
					rfcDestinationMap.put(rfcDestinationName, jCoConnectionList);
				}
			}
		}
		return rfcDestinationMap;
	}

}
