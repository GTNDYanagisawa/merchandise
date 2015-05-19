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

import de.hybris.platform.sap.core.jco.monitor.provider.JCoConnectionMonitorLocalProvider;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Initiates the snapshot for the current cluster if requested by {@link JCoConnectionsSnapshotClusterEvent}.
 */
public class JCoConnectionsSnapshotClusterEventListener extends AbstractEventListener<JCoConnectionsSnapshotClusterEvent>
{

	private static final Logger LOG = Logger.getLogger(JCoConnectionsSnapshotClusterEventListener.class.getName());

	private EventService eventService;
	private ClusterService clusterService;
	private JCoConnectionMonitorLocalProvider localProvider;

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
	 * Injection setter for {@link ClusterService}.
	 * 
	 * @param clusterService
	 *           the ClusterService to set
	 */
	@Override
	public void setClusterService(final ClusterService clusterService)
	{
		super.setClusterService(clusterService);
		this.clusterService = clusterService;
	}

	/**
	 * Injection setter for {@link JCoConnectionMonitorLocalProvider}.
	 * 
	 * @param localProvider
	 *           the localProvider to set
	 */
	@Required
	public void setLocalProvider(final JCoConnectionMonitorLocalProvider localProvider)
	{
		this.localProvider = localProvider;
	}

	@Override
	protected void onEvent(final JCoConnectionsSnapshotClusterEvent event)
	{
		String snapshotAsString;
		final int clusterId = clusterService.getClusterId();
		try
		{
			snapshotAsString = localProvider.createSnapshotXML();
			LOG.info("Requested jco connections cluster snapshot for cluster " + clusterId + " successfully created!");
		}
		catch (final Exception e)
		{
			snapshotAsString = "Exception when generating jco connections snapshot for cluster " + clusterId + " : "
					+ e.getMessage();
			LOG.error(snapshotAsString, e);

		}
		eventService
				.publishEvent(new JCoConnectionsSnapshotClusterResultEvent(event.getSnapshotUuid(), clusterId, snapshotAsString));
	}

}
