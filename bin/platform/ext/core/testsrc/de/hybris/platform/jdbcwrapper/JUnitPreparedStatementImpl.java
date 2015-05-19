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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * For testing sql errors while execution.
 */
public class JUnitPreparedStatementImpl extends PreparedStatementImpl
{
	private boolean forceError = false;

	public JUnitPreparedStatementImpl(final ConnectionImpl conn, final PreparedStatement statement, final String query)
	{
		super(conn, statement, query);
	}

	/**
	 * Allows to force each execution to throw a SQLException.
	 * @param hasError
	 */
	public void setError(final boolean hasError)
	{
		this.forceError = hasError;
	}

	@Override
	protected ResultSet wrapResultSet(final ResultSet real) throws SQLException
	{
		// we're actually abusing this method to simulate a SQL error since
		// it is usually called inside execute*() methods!
		if (forceError)
		{
			throw new SQLException("test SQL error");
		}
		else
		{
			return super.wrapResultSet(real);
		}
	}
}
