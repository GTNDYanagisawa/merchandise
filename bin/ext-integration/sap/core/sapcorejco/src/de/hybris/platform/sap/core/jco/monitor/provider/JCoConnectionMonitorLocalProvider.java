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
import de.hybris.platform.sap.core.jco.monitor.JCoConnectionMonitor;
import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
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

import com.sap.conn.jco.monitor.JCoConnectionData;


/**
 * Provider of {@link JCoConnectionMonitor} for local environment.
 */
public class JCoConnectionMonitorLocalProvider extends JCoConnectionMonitorProvider implements JCoConnectionMonitor
{

	private static final Logger LOG = Logger.getLogger(JCoConnectionMonitorLocalProvider.class.getName());

	private JCoConnectionMonitorContext localContext;

	/**
	 * Injection setter for {@link JCoConnectionMonitorContext}.
	 * 
	 * @param localContext
	 *           the localContext to set
	 */
	public void setLocalContext(final JCoConnectionMonitorContext localContext)
	{
		this.localContext = localContext;
	}

	@Override
	public String createSnapshotXML()
	{
		String snapShot = "";
		try
		{
			final Nodes clusters = jaxbHandler.generateNodesFromJCoConnectionData(getClusterId(),
					localContext.getJCoConnectionData());
			snapShot = jaxbHandler.generateSnapshot(clusters);
		}
		catch (final JCoMonitorException e)
		{
			LOG.error("Error when generating overview: " + e.getMessage(), e);
		}
		return snapShot;
	}

	@Override
	public String createSnapshotFile()
	{
		String statusMessage = "";
		try
		{
			final File snapshotFile = createSnapshotFile(SapcorejcoConstants.JCO_CONNECTION_CLUSTER_SNAPSHOT_FILE_NAME, new Date());
			final FileOutputStream os = new FileOutputStream(snapshotFile);
			final Nodes clusters = jaxbHandler.generateNodesFromJCoConnectionData(getClusterId(),
					localContext.getJCoConnectionData());
			jaxbHandler.generateSnapshot(os, clusters);
			os.flush();
			os.close();
			statusMessage = "Local JCo connections snapshot file " + snapshotFile.getAbsolutePath()
					+ " has successfully been generated!";
			LOG.info(statusMessage);
		}
		catch (final JCoMonitorException e)
		{
			statusMessage = "Error when generating the XML snapshot file: " + e.getMessage();
			LOG.error(statusMessage, e);
		}
		catch (final IOException e)
		{
			statusMessage = "Error when creating file output stream for snapshot file: " + e.getMessage();
			LOG.error(statusMessage, e);
		}
		return statusMessage;
	}

	@Override
	public Integer getTotalCount()
	{
		final List<? extends JCoConnectionData> connectionsData = localContext.getJCoConnectionData();
		return connectionsData.size();
	}

	@Override
	public Integer getLongRunnerCount()
	{
		int counter = 0;

		final List<? extends JCoConnectionData> connectionsData = localContext.getJCoConnectionData();
		for (final JCoConnectionData jcoConnection : connectionsData)
		{
			if (isLongLifetimeJCoConnection(localContext.getSnapshotTimestamp(), jcoConnection.getLastActivityTimestamp(),
					longLifetimeThresholdMSecs))
			{
				counter++;
			}
		}
		return counter;
	}

	@Override
	public Integer getPoolLimitReachedCount()
	{
		final List<? extends JCoConnectionData> connectionsData = localContext.getJCoConnectionData();
		int counter = 0;
		final Map<String, List<JCoConnectionData>> rfcDestinationMap = getRFCDestinationMap(connectionsData);
		for (final String rfcDestinationName : rfcDestinationMap.keySet())
		{
			final RFCDestination rfcDestination = rfcDestinationService.getRFCDestination(rfcDestinationName);
			final int poolSize = Integer.parseInt(rfcDestination.getPoolSize());
			final List<JCoConnectionData> jCoconnectionList = rfcDestinationMap.get(rfcDestinationName);
			if (jCoconnectionList.size() >= poolSize)
			{
				counter++;
			}
		}
		return Integer.valueOf(counter);
	}

	/**
	 * 
	 * Calculate a list of related JCo connections for each RFC Destination object.
	 * 
	 * @param connectionsData
	 *           list of current JCo connections
	 * @return map with a list of JCo connections
	 */
	private Map<String, List<JCoConnectionData>> getRFCDestinationMap(final List<? extends JCoConnectionData> connectionsData)
	{
		final Map<String, List<JCoConnectionData>> rfcDestinationMap = new HashMap<String, List<JCoConnectionData>>();
		for (final JCoConnectionData jCoConnectionData : connectionsData)
		{
			final String rfcDestinationName = jaxbHandler.getRFCDestinationNameFromGroupName(jCoConnectionData.getGroupName());
			List<JCoConnectionData> jCoConnectionList = rfcDestinationMap.get(rfcDestinationName);
			if (jCoConnectionList == null)
			{
				jCoConnectionList = new ArrayList<JCoConnectionData>();
			}
			jCoConnectionList.add(jCoConnectionData);
			rfcDestinationMap.put(rfcDestinationName, jCoConnectionList);

		}
		return rfcDestinationMap;
	}

}
