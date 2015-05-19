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

import java.util.List;

import com.sap.conn.jco.monitor.JCoConnectionData;


/**
 * Interface to get JCo connection monitor context data.
 */
public interface JCoConnectionMonitorContext
{

	/**
	 * Returns current JCo connection data.
	 * 
	 * @return JCo connection data list
	 */
	public List<? extends JCoConnectionData> getJCoConnectionData();

	/**
	 * Returns the time stamp of the snapshot.
	 * 
	 * @return snapshot time stamp
	 */
	public long getSnapshotTimestamp();

}
