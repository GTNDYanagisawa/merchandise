/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.jdbcwrapper;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


public class JUnitJDBCConnectionFactory extends ConnectionErrorCheckingJDBCConnectionFactory
{
	private final AtomicBoolean forceAllConnectionsFail = new AtomicBoolean(false);
	private final Map<Connection, Connection> forceValidationErrorConnections = new ConcurrentHashMap<Connection, Connection>();
	private final ConnectionStatus connectionStatus;

	public JUnitJDBCConnectionFactory(final HybrisDataSource dataSource, final ConnectionStatus connectionStatus)
	{
		super(dataSource, connectionStatus);
		this.connectionStatus = connectionStatus;
	}

	@Override
	public boolean validateObject(final Object obj)
	{
		final boolean result = !forceAllConnectionsFail.get() && !forceValidationErrorConnections.containsKey(obj)
				&& super.validateObject(obj);
		if (!result)
		{
			connectionStatus.notifyConnectionError();
		}
		return result;
	}

	public void setAllConnectionsFail(final boolean allFail)
	{
		forceAllConnectionsFail.set(allFail);
	}

	public void addFailingConnection(final Connection con)
	{
		forceValidationErrorConnections.put(con, con);
	}

	public void removeFailingConnection(final Connection con)
	{
		forceValidationErrorConnections.remove(con);
	}

	public void removeAllFailingConnections()
	{
		forceValidationErrorConnections.clear();
	}
}
