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

import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationService;
import de.hybris.platform.sap.core.jco.constants.SapcorejcoConstants;
import de.hybris.platform.sap.core.jco.monitor.JCoConnectionMonitor;
import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.sap.core.jco.monitor.jaxb.JCoMonitorJAXBHandler;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;


/**
 * Provider of {@link JCoConnectionMonitor} for cluster environment.
 */
public abstract class JCoConnectionMonitorProvider
{

	/**
	 * RFC destination service.
	 */
	protected RFCDestinationService rfcDestinationService; // NOPMD

	/**
	 * JAXB handler.
	 */
	protected JCoMonitorJAXBHandler jaxbHandler; // NOPMD

	/**
	 * Long lifetime threshold in milliseconds.
	 */
	protected long longLifetimeThresholdMSecs; // NOPMD

	/**
	 * Cluster service.
	 */
	private ClusterService clusterService; // NOPMD

	/**
	 * Injection setter for {@link ClusterService}.
	 * 
	 * @param clusterService
	 *           the clusterService to set
	 */
	@Required
	public void setClusterService(final ClusterService clusterService)
	{
		this.clusterService = clusterService;
	}

	/**
	 * Injection setter for {@link RFCDestinationService}.
	 * 
	 * @param rfcDestinationService
	 *           {@link RFCDestinationService}
	 * 
	 */
	@Required
	public void setRfcDestinationService(final RFCDestinationService rfcDestinationService)
	{
		this.rfcDestinationService = rfcDestinationService;
	}

	/**
	 * Injection setter for {@link JCoMonitorJAXBHandler}.
	 * 
	 * @param jaxbHandler
	 *           the jaxb handler to set
	 */
	@Required
	public void setJaxbHandler(final JCoMonitorJAXBHandler jaxbHandler)
	{
		this.jaxbHandler = jaxbHandler;
	}

	/**
	 * Injection setter for long lifetime threshold.
	 * 
	 * @param longLifetimeThresholdInSeconds
	 *           long lifetime threshold in seconds
	 */
	public void setLongLifetimeThresholdInSeconds(final long longLifetimeThresholdInSeconds)
	{
		this.longLifetimeThresholdMSecs = longLifetimeThresholdInSeconds;
	}

	/**
	 * Calculates if the lifetime of the given JCo connection exceeds the specified threshold. If the threshold is less
	 * than 0, it is set to 0.
	 * 
	 * @param currentTimestamp
	 *           current time stamp
	 * @param lastActivityTimestamp
	 *           last activity time stamp
	 * @param longLifetime
	 *           the defined threshold
	 * @return true if the lifetime exceeds the threshold
	 */
	protected boolean isLongLifetimeJCoConnection(final long currentTimestamp, final long lastActivityTimestamp,
			final long longLifetime)
	{
		long thresholdInMillis = longLifetime * 1000;
		if (thresholdInMillis < 0)
		{
			thresholdInMillis = 0;
		}
		final long nowInMillis = currentTimestamp;
		if (lastActivityTimestamp + thresholdInMillis < nowInMillis)
		{
			return true;
		}
		return false;
	}

	/**
	 * Creates the snapshot file.
	 * 
	 * @param snapshotFileName
	 *           name of the snapshot file
	 * @param snapshotDate
	 *           time stamp of snapshot
	 * @return snapshot file
	 * @throws JCoMonitorException
	 *            {@link JCoMonitorException}
	 */
	protected File createSnapshotFile(final String snapshotFileName, final Date snapshotDate) throws JCoMonitorException
	{
		final File logDir = Utilities.getPlatformConfig().getSystemConfig().getLogDir();

		final String jcoLogDir = logDir.getAbsolutePath() + File.separator + SapcorejcoConstants.JCO_LOG_SUBDIR;
		final File jcoLogDirFile = new File(jcoLogDir);
		jcoLogDirFile.mkdir();

		final String dateFormatted = new SimpleDateFormat("yyyyMMdd-HHmmss").format(snapshotDate);

		final String dumpFileName = jcoLogDirFile + File.separator + snapshotFileName + "_" + dateFormatted
				+ SapcorejcoConstants.XML_FILE_SUFFIX;
		final File snapshotFile = new File(dumpFileName);
		try
		{
			snapshotFile.createNewFile();
		}
		catch (final IOException e)
		{
			throw new JCoMonitorException("Exception when creating snapshot file " + dumpFileName, e);
		}
		return snapshotFile;
	}

	/**
	 * Returns the cluster id.
	 * 
	 * @return cluster id
	 */
	protected Integer getClusterId()
	{
		return clusterService.getClusterId();
	}

}
