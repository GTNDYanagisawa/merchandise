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

/**
 * Interface for JCoConnection monitoring via JMX MBean.
 */
public interface JCoConnectionMonitor
{

	/**
	 * Returns the total number of JCo connections.
	 * 
	 * @return number of JCo connections
	 */
	Integer getTotalCount();

	/**
	 * Returns the number of JCo connections whose lifetime exceed a specific threshold parameter. The threshold
	 * parameter is defined in the project.properties file. Its default value is the same as the value of the default
	 * session timeout (3600 seconds).
	 * 
	 * @return number of JCo connections whose lifetime exceed the defined threshold
	 */
	Integer getLongRunnerCount();

	/**
	 * Returns number of RFC destinations whose pool size is reached.
	 * 
	 * @return number of RFC destinations whose pool size is reached
	 */
	Integer getPoolLimitReachedCount();

	/**
	 * Returns all current JCo connections as XML String.
	 * 
	 * @return XML String
	 */
	public String createSnapshotXML();

	/**
	 * Create JCo connections snapshot file operation.
	 * 
	 * @return result message
	 */
	public String createSnapshotFile();

}
