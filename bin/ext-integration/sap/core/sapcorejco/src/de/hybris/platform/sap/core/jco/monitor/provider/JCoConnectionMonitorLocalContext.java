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
import com.sap.conn.jco.monitor.JCoConnectionMonitor;


/**
 * Provides the local JCo connection monitor context.
 */
public class JCoConnectionMonitorLocalContext implements JCoConnectionMonitorContext
{

	@Override
	public List<? extends JCoConnectionData> getJCoConnectionData()
	{
		return JCoConnectionMonitor.getConnectionsData();
	}

	@Override
	public long getSnapshotTimestamp()
	{
		return System.currentTimeMillis();
	}

}
