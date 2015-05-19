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
import de.hybris.platform.core.Registry;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;


/**
 * Before runing this class be aware to have runinnig standalone platform/tomcat or any web wrapper with vjdbc extension
 * 'on board'
 */
@IntegrationTest(standalone = false)
@Ignore
public abstract class AbstractVjdbcSqlTest extends AbstractVjdbcTest
{

	private static final Logger LOG = Logger.getLogger(VjdbcSqlHttpTest.class.getName());

	private final String QUERY_FIND_PRODUCTS = " SELECT item_t0.Code FROM %sproducts item_t0 ";
	private final String CONDITION = " WHERE  item_t0.Code like '" + PRODUCT_PREFIX + "%' ";

	private final static String UPDATE_PRODUCTS = "UPDATE %sproducts SET code='code'";

	protected void selectTest(final Connection vjdbcCon) throws Exception
	{
		final String tablePrefix = Registry.getCurrentTenant().equals(Registry.getMasterTenant()) ? "" : Registry
				.getCurrentTenant().getTenantID() + "_";
		String realQuery = String.format(QUERY_FIND_PRODUCTS, StringUtils.isEmpty(tablePrefix) ? "" : tablePrefix);
		realQuery = realQuery + CONDITION;

		Statement stmt = null;
		ResultSet res = null;
		try
		{
			verifyUnderlyingConnection(vjdbcCon);
			stmt = vjdbcCon.createStatement();

			LOG.info("Underlying data base url:: " + vjdbcCon.getMetaData().getURL());
			LOG.info("Executing query:: " + realQuery);

			res = stmt.executeQuery(realQuery);//stmt.getResultSet();
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
					Assert.assertTrue(res.getString(1).startsWith(PRODUCT_PREFIX));
				}
			}
			Assert.assertTrue("Should get " + PRODUCT_COUNT + " instead of " + idx, idx == PRODUCT_COUNT);
		}
		finally
		{
			Utilities.tryToCloseJDBC(vjdbcCon, stmt, res);
		}
	}

	protected void updateTest(final Connection vjdbcConn) throws Exception
	{
		PreparedStatement stmt = null;

		try
		{
			final String tablePrefix = Registry.getCurrentTenant().equals(Registry.getMasterTenant()) ? "" : Registry
					.getCurrentTenant().getTenantID() + "_";
			final String realQuery = String.format(UPDATE_PRODUCTS, StringUtils.isEmpty(tablePrefix) ? "" : tablePrefix);

			stmt = vjdbcConn.prepareStatement(realQuery);

			LOG.info("Underlying data base url:: " + vjdbcConn.getMetaData().getURL());
			LOG.info("Executing query:: " + UPDATE_PRODUCTS);

			stmt.executeUpdate();
		}
		finally
		{
			Utilities.tryToCloseJDBC(vjdbcConn, stmt, null);
		}
	}
}
