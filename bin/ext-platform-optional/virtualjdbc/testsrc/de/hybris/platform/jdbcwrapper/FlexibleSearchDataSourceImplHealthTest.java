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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.virtualjdbc.db.VjdbcDataSourceImplFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.jdbc.support.JdbcUtils;

import junit.framework.Assert;



@IntegrationTest
public class FlexibleSearchDataSourceImplHealthTest extends HybrisJUnit4Test
{
	private static final Logger LOG = Logger.getLogger(FlexibleSearchDataSourceImplHealthTest.class);
	public static final int MAX_CONNECTIONS = 10;

	@Test
	public void testGetAndReleaseConnection() throws SQLException, InterruptedException
	{

		final HybrisDataSource currentDataSource = Registry.getCurrentTenantNoFallback().getDataSource(
				VjdbcDataSourceImplFactory.class.getName());

		final JDBCConnectionPool pool = currentDataSource.getConnectionPool();
		LOG.info("start .... ");
		printPoolInformat(pool);
		for (int i = 0; i < MAX_CONNECTIONS; i++)
		{
			Connection connection = null;
			try
			{
				connection = currentDataSource.getConnection();
				waitInCurrentThread(100);
				Assert.assertTrue(connection.getClass().getName().contains("FlexSyntaxAwareConnection"));
				//final Statement statement = 	connection.createStatement();
				LOG.info("after get connection ... ");
				printPoolInformat(pool);
			}
			finally
			{
				JdbcUtils.closeConnection(connection);
				LOG.info("after close connection ... ");
				printPoolInformat(pool);
			}
		}
		LOG.info("finish ... ");
		printPoolInformat(pool);
	}


	@Test
	public void testGetAllConnectionsReleaseAndRelaseAll() throws SQLException, InterruptedException
	{

		final HybrisDataSource currentDataSource =
				  Registry.getCurrentTenantNoFallback().getDataSource(VjdbcDataSourceImplFactory.class.getName());

		final JDBCConnectionPool pool = currentDataSource.getConnectionPool();
		LOG.info("start .... ");
		printPoolInformat(pool);
		final List<Connection> connections = new ArrayList<>();
		try
		{
			for (int i = 0; i < MAX_CONNECTIONS; i++)
			{
				final Connection connection  = currentDataSource.getConnection();
				Assert.assertTrue(connection.getClass().getName().contains("FlexSyntaxAwareConnection"));
				//final Statement statement = 	connection.createStatement();
				LOG.info("after get connection ... ");
				printPoolInformat(pool);
				connections.add(connection);
			}
		}
		finally
		{
			waitInCurrentThread(100);
			for (final Connection connection : connections)
			{
				JdbcUtils.closeConnection(connection);
				LOG.info("after close connection ... ");
			}
			printPoolInformat(pool);
		}
		LOG.info("finish ... ");
		printPoolInformat(pool);
	}

	private void waitInCurrentThread(final int time) throws InterruptedException
	{
		Thread.sleep(time);
	}

	private void printPoolInformat(final JDBCConnectionPool pool)
	{
		LOG.info(String.format("Pool info %s:%s \n  %s:%s \n  %s:%s \n  %s:%s \n  %s:%s \n",//
				"maxActive", Integer.valueOf(pool.getMaxActive()),//
				"maxPhyOpen", Integer.valueOf(pool.getMaxPhysicalOpen()), //
				"numActive", Integer.valueOf(pool.getNumActive()), //
				"numIdle", Integer.valueOf(pool.getNumIdle()), //
				"numPhyOpen", Integer.valueOf(pool.getNumPhysicalOpen())));
	}
}
