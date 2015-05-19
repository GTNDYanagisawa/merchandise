package de.hybris.platform.virtualjdbc.jalo;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;


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

/**
 * Covers corner case of the vjdbc functionality * support for NVARCHAR ({@link java.sql.Types#NVARCHAR})
 */
@IntegrationTest
public class VjdbcMigrationTest extends AbstractVjdbcTest
{
	private static final Logger LOG = Logger.getLogger(VjdbcMigrationTest.class);

	private static final String NUMBER_TYPE = "integer ";
	private static final String NVARCHAR_TYPE = "nvarchar(100)";
	private static final String TEST_TABLE = "test_" + System.currentTimeMillis();
	private static final String SOME_TEXT = "foo_bar_baz";
	private static final int MAX_ID = 100;

	private static final int INT_COLUMN = 1;
	private static final int TEXT_COLUMN = 2;
	private JdbcTemplate jdbcTemplate;

	private DataSource getReadWriteDataSource()
	{
		try
		{
			Registry.getCurrentTenantNoFallback().activateAlternativeMasterDataSource("vjdbcRW");
			return Registry.getCurrentTenantNoFallback().getDataSource();
		}
		finally
		{
			Registry.getCurrentTenantNoFallback().deactivateAlternativeDataSource();
		}
	}

	@Before
	public void prepare()
	{
		Assume.assumeTrue(Config.isSQLServerUsed());
		jdbcTemplate = new JdbcTemplate(getReadWriteDataSource());
	}

	@After
	public void cleanUp()
	{
		if (jdbcTemplate != null)
		{
			jdbcTemplate.execute(" drop table " + TEST_TABLE);
		}
	}

	@Test
	public void testNVarcharViaQuery() throws Exception
	{
		try
		{
			jdbcTemplate.execute(createSql());

			for (int i = 0; i < MAX_ID; i++)
			{
				jdbcTemplate.execute(" insert into " + TEST_TABLE + " (id, text) values (" + i + ", '" + SOME_TEXT + "')");
			}
			assertDataCorrectness(getHttpSqlConnection(getUserPrincipals(true)));
		}
		finally
		{
			//
		}
	}

	@Test
	public void testNVarcharViaPreparedStatement() throws Exception
	{
		PreparedStatement statement = null;
		Connection connection = null;
		try
		{
			jdbcTemplate.execute(createSql());

			connection = getHttpSqlConnection(getUserPrincipals(true));

			statement = connection.prepareStatement(" insert into " + TEST_TABLE + " (id, text) values ( ? , ?)");

			for (int i = 0; i < MAX_ID; i++)
			{
				statement.setInt(INT_COLUMN, i);
				statement.setString(TEXT_COLUMN, SOME_TEXT);

				Assert.assertTrue(statement.executeUpdate() > 0);
			}
			assertDataCorrectness(getHttpSqlConnection(getUserPrincipals(false)));
		}
		finally
		{
			Utilities.tryToCloseJDBC(connection, statement, null);

		}
	}

	private void assertDataCorrectness(Connection connection) throws SQLException
	{

		ResultSet result = null;
		Statement statement = null;
		try
		{


			statement = connection.createStatement();

			result = statement.executeQuery("SELECT * FROM " + TEST_TABLE);

			while (result.next())
			{
				Assert.assertTrue("Any value should be above 0 ", result.getInt(INT_COLUMN) >= 0);
				Assert.assertTrue("Any value should be below (or equal) " + MAX_ID, result.getInt(INT_COLUMN) < MAX_ID);
				Assert.assertEquals(SOME_TEXT, result.getString(TEXT_COLUMN));
				//Assert.assertEquals("foo_bar", result.getNString(2));
			}

		}

		finally
		{
			Utilities.tryToCloseJDBC(connection, statement, result);
		}
	}

	private String createSql()
	{
		return "create table " + TEST_TABLE + " ( id " + NUMBER_TYPE + " , text " + NVARCHAR_TYPE + " )";
	}

}
