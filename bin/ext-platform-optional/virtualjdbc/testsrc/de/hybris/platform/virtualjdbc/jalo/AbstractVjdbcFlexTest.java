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
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;

import com.google.common.base.Joiner;


/**
 * Before running this class be aware to have running standalone platform/tomcat or any web wrapper with vjdbc extension
 * 'on board'
 */
@IntegrationTest(standalone = false)
@Ignore
public abstract class AbstractVjdbcFlexTest extends AbstractVjdbcTest
{
	private static final Logger LOG = Logger.getLogger(AbstractVjdbcFlexTest.class.getName());

	private final static String QUERY_FIND_PRODUCTS = "SELECT {code} FROM {Product} ";

	private final static String CONDITION = "WHERE {code} like '" + PRODUCT_PREFIX + "%' ";
	private final static String CONDITION_PREPARED = "WHERE {code} like ?";


	private final static String CONDITION_LIST = "WHERE {" + Item.PK + "} in ( %s ) ";
	private final static String CONDITION_PREPARED_LIST = "WHERE {" + Item.PK + "} in  ( ? , ? , ? , ? , ? )";


	protected void executePreparedStatement(final Connection conn) throws SQLException
	{
		final String realQuery = QUERY_FIND_PRODUCTS + CONDITION_PREPARED;

		Statement stmt = null;
		ResultSet res = null;
		try
		{
			LOG.info("Executing query:: " + realQuery);

			final PreparedStatement pstmt = conn.prepareStatement(realQuery);
			stmt = pstmt;
			pstmt.setString(1, PRODUCT_PREFIX + "%");
			res = pstmt.executeQuery();
			verifyQueryResult(res);
		}
		finally
		{
			Utilities.tryToCloseJDBC(conn, stmt, res);
		}
	}

	protected void executeStatement(final Connection conn) throws SQLException
	{
		final String realQuery = QUERY_FIND_PRODUCTS + CONDITION;

		Statement stmt = null;
		ResultSet res = null;
		try
		{
			LOG.info("Executing query:: " + realQuery);

			stmt = conn.createStatement();
			res = stmt.executeQuery(realQuery);

			verifyQueryResult(res);
		}
		finally
		{
			Utilities.tryToCloseJDBC(conn, stmt, res);
		}
	}


	protected void executePreparedStatementForList(final Connection vjdbcConn) throws Exception
	{
		Assert.assertEquals(PRODUCT_COUNT, productList.size());
		Statement stmt = null;
		ResultSet res = null;
		try
		{
			final String sqlQuery = QUERY_FIND_PRODUCTS + CONDITION_PREPARED_LIST;
			stmt = vjdbcConn.prepareStatement(sqlQuery);
			int paramCount = 1;
			for (final Product singleProduct : productList)
			{
				((PreparedStatement) stmt).setString(paramCount++, singleProduct.getPK().getLongValueAsString());
			}
			LOG.info("Executing prepared statement :: " + sqlQuery);
			res = ((PreparedStatement) stmt).executeQuery();
			verifyQueryResult(res);
		}
		finally
		{
			Utilities.tryToCloseJDBC(vjdbcConn, stmt, res);
		}
	}

	protected void executeStatementForList(final Connection vjdbcConn) throws Exception
	{
		Assert.assertEquals(PRODUCT_COUNT, productList.size());
		final List<String> productListPks = new ArrayList<String>(PRODUCT_COUNT);
		for (final Product singleProduct : productList)
		{
			productListPks.add(singleProduct.getPK().getLongValueAsString());
		}

		Statement stmt = null;
		ResultSet res = null;
		try
		{
			final String sqlQuery = QUERY_FIND_PRODUCTS + String.format(CONDITION_LIST, evaluateQuery(productListPks));
			stmt = vjdbcConn.createStatement();
			LOG.info("Executing  statement :: " + sqlQuery);
			res = stmt.executeQuery(sqlQuery);
			verifyQueryResult(res);
		}
		finally
		{
			Utilities.tryToCloseJDBC(vjdbcConn, stmt, res);
		}
	}


	/**
	 * 
	 */
	private void verifyQueryResult(final ResultSet res) throws SQLException
	{
		int idx = 0;
		if (res != null)
		{
			while (res.next())
			{
				++idx;
				if (LOG.isDebugEnabled())
				{
					LOG.debug("result[" + (idx) + "]:" + res.getString(1));
				}
				Assert.assertTrue("Should get a product with code " + PRODUCT_PREFIX, res.getString(1).startsWith(PRODUCT_PREFIX));
			}
		}
		Assert.assertEquals("Should get " + PRODUCT_COUNT + " instead of " + idx, PRODUCT_COUNT, idx);
	}

	private String evaluateQuery(final List<String> productListPks)
	{
		final Joiner pksJoiner = Joiner.on(",");
		pksJoiner.join(productListPks);
		return pksJoiner.join(productListPks);
	}
}
