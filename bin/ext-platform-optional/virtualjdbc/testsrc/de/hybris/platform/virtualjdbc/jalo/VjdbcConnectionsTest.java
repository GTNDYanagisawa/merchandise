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
package de.hybris.platform.virtualjdbc.jalo;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest(standalone = false)
public class VjdbcConnectionsTest extends AbstractVjdbcTest
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(VjdbcFlexHttpTest.class);

	private int iterations;

	@Before
	public void setUpIterations()
	{
		iterations = Config.getInt("vjdbc.db.pool.maxActive", Config.getInt("db.pool.maxActive", 50)) * 2;
	}


	private void defaultQuery(final Connection con, final boolean closeConnection)
	{
		ResultSet rs = null;
		Statement stmt = null;

		try
		{
			stmt = con.createStatement();
			rs = stmt.executeQuery("select count({PK}) AS msg_count from {Product} " + CONDITION);
			if (rs.next())
			{
				Assert.assertEquals("Query should return " + PRODUCT_COUNT + " items ", PRODUCT_COUNT, rs.getInt("msg_count"));
			}
		}
		catch (final Exception ex)
		{
			LOG.error(ex);
		}
		finally
		{
			if (closeConnection)
			{
				Utilities.tryToCloseJDBC(con, stmt, rs);
			}
		}
	}

	@Test
	public void testSameConnection() throws Exception
	{
		Connection connection = null;
		try
		{
			connection = getHttpSqlConnection(getUserPrincipals(true));
			for (int i = 0; i < iterations; i++)
			{
				defaultQuery(connection, false);
				LOG.info("Test same Connection [" + i + "]");
				Assert.assertFalse("Connection should remain opened ", connection.isClosed());
			}

		}
		catch (final Exception e)
		{
			LOG.error(e);
			Assert.fail(e.getMessage());
		}
		finally
		{
			if (connection != null)
			{
				Utilities.tryToCloseJDBC(connection, null, null);
			}
		}
	}

	@Test
	public void testNewConnection() throws Exception
	{
		for (int i = 0; i < iterations; i++)
		{
			final Connection connection = getHttpSqlConnection(getUserPrincipals(true));
			defaultQuery(connection, true);
			LOG.info("Test new Connections [" + i + "]");
			Assert.assertTrue("Connection should remain closed ", connection.isClosed());
			try
			{
				if (!connection.isClosed())
				{
					connection.close();
				}
			}
			catch (final Exception e)
			{
				LOG.error(e);
				Assert.fail(e.getMessage());
			}
		}
	}

}
