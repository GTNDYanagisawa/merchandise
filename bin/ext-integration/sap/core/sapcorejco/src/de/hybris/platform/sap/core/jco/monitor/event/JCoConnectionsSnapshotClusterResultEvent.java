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

import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 * Returns the JCo connections snapshot of the current cluster to the requester.
 */
public class JCoConnectionsSnapshotClusterResultEvent extends AbstractEvent implements ClusterAwareEvent
{

	private static final long serialVersionUID = -5580091916886609299L;
	private final String snapshotUuid;
	private final int clusterId;
	private final String snapshotContent;
	private final JCoMonitorException exception;

	/**
	 * Standard constructor.
	 * 
	 * @param snapshotUuid
	 *           snapshot uuid
	 * @param clusterId
	 *           cluster id
	 * @param snapshotContent
	 *           snapshot content
	 */
	public JCoConnectionsSnapshotClusterResultEvent(final String snapshotUuid, final int clusterId, final String snapshotContent)
	{
		super();
		this.snapshotUuid = snapshotUuid;
		this.clusterId = clusterId;
		this.snapshotContent = snapshotContent;
		this.exception = null;
	}

	/**
	 * Exception constructor.
	 * 
	 * @param snapshotUuid
	 *           snapshot uuid
	 * @param clusterId
	 *           cluster id
	 * @param exception
	 *           {@link JCoMonitorException}
	 */
	public JCoConnectionsSnapshotClusterResultEvent(final String snapshotUuid, final int clusterId,
			final JCoMonitorException exception)
	{
		super();
		this.snapshotUuid = snapshotUuid;
		this.clusterId = clusterId;
		this.snapshotContent = null;
		this.exception = exception;
	}

	/**
	 * Returns the snapshot uuid.
	 * 
	 * @return the snapshotGuid
	 */
	public String getSnapshotUuid()
	{
		return snapshotUuid;
	}

	/**
	 * Returns the cluster id.
	 * 
	 * @return the clusterId
	 */
	public int getClusterId()
	{
		return clusterId;
	}

	/**
	 * Returns the snapshot content.
	 * 
	 * @return the snapshotContent
	 */
	public String getSnapshotContent()
	{
		return snapshotContent;
	}

	/**
	 * Returns the exception which may have occurred during creation of the snapshot.
	 * 
	 * @return the {@link JCoMonitorException}
	 */
	public JCoMonitorException getException()
	{
		return exception;
	}

	/**
	 * Returns if the creation of the snapshot has failed.
	 * 
	 * @return failed indicator
	 */
	public boolean hasFailed()
	{
		return (exception != null);
	}

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return true;
	}

}
