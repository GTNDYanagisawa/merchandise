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
package de.hybris.platform.sap.core.jco.monitor.event;

import de.hybris.platform.cluster.PingBroadcastHandler;
import de.hybris.platform.cluster.PingBroadcastHandler.NodeInfo;
import de.hybris.platform.regioncache.ConcurrentHashSet;
import de.hybris.platform.sap.core.common.DocumentBuilderFactoryUtil;
import de.hybris.platform.sap.core.jco.monitor.JCoConnectionMonitor;
import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.sap.core.jco.monitor.provider.JCoConnectionMonitorClusterProvider;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Initiates the snapshot of all cluster and collects the result if requested.
 */
public class JCoConnectionsSnapshotClusterHandler extends AbstractEventListener<JCoConnectionsSnapshotClusterResultEvent>
		implements JCoConnectionMonitor
{

	private static final Logger LOG = Logger.getLogger(DocumentBuilderFactoryUtil.class.getName());

	private EventService eventService;
	private JCoConnectionMonitorClusterProvider clusterProvider;
	private String currentSnapshotUuid = null;
	private Set<Integer> clusterIdsWaitingForResult = null;
	private Map<Integer, String> nodeResultMap = null;
	private long collectionTimeoutInMSecs;
	private long cacheLifetimeInMSecs;


	/**
	 * Injection setter for {@link EventService}.
	 * 
	 * @param eventService
	 *           the eventService to set
	 */
	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * Injection setter for {@link JCoConnectionMonitorClusterProvider}.
	 * 
	 * @param jcoConnectionMonitorClusterProvider
	 *           the jcoConnectionMonitorClusterProvider to set
	 */
	@Required
	public void setJCoConnectionMonitorClusterProvider(
			final JCoConnectionMonitorClusterProvider jcoConnectionMonitorClusterProvider)
	{
		this.clusterProvider = jcoConnectionMonitorClusterProvider;
	}

	/**
	 * Injection setter for collection timeout.
	 * 
	 * @param collectionTimeoutInSeconds
	 *           collection timeout in seconds
	 */
	@Required
	public void setCollectionTimeoutInSeconds(final long collectionTimeoutInSeconds)
	{
		this.collectionTimeoutInMSecs = collectionTimeoutInSeconds * 1000;
	}

	/**
	 * Injection setter for cache lifetime.
	 * 
	 * @param cacheLifetimeInSeconds
	 *           cache lifetime in seconds
	 */
	@Required
	public void setCacheLifetimeInSeconds(final long cacheLifetimeInSeconds)
	{
		this.cacheLifetimeInMSecs = cacheLifetimeInSeconds * 1000;
	}

	@Override
	public String createSnapshotFile()
	{
		return createClusterSnapshot(true, false);
	}

	@Override
	public String createSnapshotXML()
	{
		return createClusterSnapshot(false, false);
	}

	@Override
	public Integer getTotalCount()
	{
		checkState();
		return clusterProvider.getTotalCount();
	}

	@Override
	public Integer getLongRunnerCount()
	{
		checkState();
		return clusterProvider.getLongRunnerCount();
	}

	@Override
	public Integer getPoolLimitReachedCount()
	{
		checkState();
		return clusterProvider.getPoolLimitReachedCount();
	}

	/**
	 * Returns the total number of nodes of the result.
	 * 
	 * @return number of nodes
	 */
	public Integer getNodesCount()
	{
		checkState();
		return clusterProvider.getNodesCount();
	}

	/**
	 * Returns the total number of nodes with result of the result.
	 * 
	 * @return number of nodes without result
	 */
	public Integer getNodesWithoutResultCount()
	{
		checkState();
		return clusterProvider.getNodesWithoutResultCount();
	}

	/**
	 * Returns the snapshot time stamp.
	 * 
	 * @return snapshot time stamp
	 */
	public Date getCacheTimestamp()
	{
		checkState();
		return new Date(clusterProvider.getCacheTimestamp());
	}

	/**
	 * Returns the snapshot buffer valid to time stamp.
	 * 
	 * @return snapshot valid to time stamp
	 */
	public Date getCacheExpirationTimestamp()
	{
		checkState();
		return new Date(clusterProvider.getCacheTimestamp() + cacheLifetimeInMSecs);
	}

	/**
	 * Resets the snapshot buffer.
	 */
	public void resetCache()
	{
		checkState();
		this.clusterProvider.reset();
	}

	/**
	 * Returns an indicator whether a snapshot is currently in creation.
	 * 
	 * @return snapshot in process indicator
	 */
	public boolean isSnapshotInProcess()
	{
		if (clusterIdsWaitingForResult != null)
		{
			return true;
		}
		return false;
	}

	/**
	 * Check current state of snapshot in buffer and refreshes it if lifetime is outdated.
	 */
	protected void checkState()
	{
		if (clusterProvider.getCacheTimestamp() == null
				|| System.currentTimeMillis() > (clusterProvider.getCacheTimestamp() + cacheLifetimeInMSecs))
		{
			synchronized (this)
			{
				if (clusterProvider.getCacheTimestamp() == null
						|| System.currentTimeMillis() > (clusterProvider.getCacheTimestamp() + cacheLifetimeInMSecs))
				{
					createClusterSnapshot(false, true);
				}
			}
		}
	}


	/**
	 * Initiates the creation of one jco connections snapshot file for all cluster.
	 * 
	 * @param withFile
	 *           create snapshot file
	 * @param updateCache
	 *           update cache indicator
	 * 
	 * @return status message
	 */
	protected synchronized String createClusterSnapshot(final boolean withFile, final boolean updateCache)
	{
		// Check if a cluster snapshot file is already in creation
		if (clusterIdsWaitingForResult != null)
		{
			return "JCo connections cluster snapshot already in creation!";
		}
		clusterIdsWaitingForResult = new ConcurrentHashSet<Integer>();
		nodeResultMap = new HashMap<Integer, String>();

		// Create map for collection result per node
		final PingBroadcastHandler pingHandler = PingBroadcastHandler.getInstance();
		final Collection<NodeInfo> nodes = pingHandler.getNodes();
		for (final NodeInfo nodeInfo : nodes)
		{
			final int nodeID = nodeInfo.getNodeID();
			clusterIdsWaitingForResult.add(nodeID);
		}

		// Initiate snapshot with result
		currentSnapshotUuid = UUID.randomUUID().toString();
		eventService.publishEvent(new JCoConnectionsSnapshotClusterEvent(currentSnapshotUuid));

		// Set snapshot start time in order to check timeout
		final long snapshotStartTimestamp = System.currentTimeMillis();

		// Waiting for result from other clusters
		while ((clusterIdsWaitingForResult != null && clusterIdsWaitingForResult.size() > 0)
				&& (System.currentTimeMillis() < snapshotStartTimestamp + collectionTimeoutInMSecs))
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (final InterruptedException e)
			{
				final String errorMsg = "Error when waiting for cluster result: " + e.getMessage();
				LOG.error(errorMsg, e);
				return errorMsg;
			}
		}
		try
		{
			final Long snapshotTimestamp = System.currentTimeMillis();
			finalizeSnapshot(currentSnapshotUuid);
			if (withFile)
			{
				final File snapshotFile = clusterProvider.createSnapshotFile(nodeResultMap, snapshotTimestamp);
				return "Snapshot file " + snapshotFile.getAbsolutePath() + " has successfully been generated!";
			}
			else
			{
				if (updateCache)
				{
					clusterProvider.initializeCache(nodeResultMap, snapshotTimestamp);
					return null;
				}
				else
				{
					return clusterProvider.getSnapshotXML(nodeResultMap, snapshotTimestamp);
				}
			}
		}
		catch (final JCoMonitorException e)
		{
			final String errorMsg = "Error when generating cluster snapshot: " + e.getMessage();
			LOG.error(errorMsg, e);
			return errorMsg;
		}
		finally
		{
			currentSnapshotUuid = null;
			clusterIdsWaitingForResult = null;
			nodeResultMap = null;
		}
	}

	/**
	 * Finalize snapshot.
	 * 
	 * @param snapshotUuid
	 *           snapshot uuid
	 * @throws JCoMonitorException
	 *            {@link JCoMonitorException}
	 */
	protected void finalizeSnapshot(final String snapshotUuid) throws JCoMonitorException
	{
		if (currentSnapshotUuid == null || !currentSnapshotUuid.equals(snapshotUuid))
		{
			return;
		}
		synchronized (this)
		{
			if (currentSnapshotUuid == null || !currentSnapshotUuid.equals(snapshotUuid))
			{
				return;
			}
			try
			{
				for (final Integer clusterIdWaitingForResult : clusterIdsWaitingForResult)
				{
					nodeResultMap.put(clusterIdWaitingForResult, null);
				}
			}
			finally
			{
				currentSnapshotUuid = null;
				clusterIdsWaitingForResult = null;
			}
		}
	}

	@Override
	protected void onEvent(final JCoConnectionsSnapshotClusterResultEvent event)
	{
		if (event.getSnapshotUuid().equals(currentSnapshotUuid))
		{
			LOG.info("Receiving jco connections snapshot result from node with cluster id " + event.getClusterId());
			final int clusterId = event.getClusterId();
			clusterIdsWaitingForResult.remove(clusterId);
			if (!event.hasFailed())
			{
				nodeResultMap.put(clusterId, event.getSnapshotContent());
			}
			else
			{
				LOG.error("Snapshot result of node with cluster id " + event.getClusterId() + " has failed: "
						+ event.getException().getMessage(), event.getException());
				nodeResultMap.put(clusterId, null);
			}
		}
	}

}
