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

package de.hybris.platform.sap.core.jco.jmx;

import de.hybris.platform.sap.core.jco.monitor.JCoConnectionMonitor;

import java.util.Date;


/**
 * Interface for JCoConnection cluster monitoring via JMX MBean.
 */
public interface JCoConnectionClusterMBean extends JCoConnectionMonitor
{

	/**
	 * Returns the total number of nodes of the result.
	 * 
	 * @return number of nodes
	 */
	public Integer getNodesCount();

	/**
	 * Returns the total number of nodes which haven't returned a result.
	 * 
	 * @return number of nodes
	 */
	public Integer getNodesWithoutResultCount();

	/**
	 * Returns the time stamp of the current cluster cache.
	 * 
	 * @return cluster snapshot time stamp
	 */
	Date getCacheTimestamp();

	/**
	 * Returns the time stamp until the current cluster cache is valid.
	 * 
	 * @return cluster snapshot valid to time stamp
	 */
	Date getCacheExpirationTimestamp();

	/**
	 * Resets the JCo connections cache operation.
	 * 
	 * @return result message
	 */
	public String resetCache();

}
