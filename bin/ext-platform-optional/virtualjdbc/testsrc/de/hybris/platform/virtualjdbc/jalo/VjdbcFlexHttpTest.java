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

import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Test;


@IntegrationTest(standalone = false)
public class VjdbcFlexHttpTest extends AbstractVjdbcFlexTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(VjdbcFlexHttpTest.class);

	@Test
	public void testSelectAsReadWriteUser() throws Exception
	{
		executeStatement(getHttpFlexConnection(getUserPrincipals(true)));
	}

	@Test
	public void testSelectAsReadOnlyUser() throws Exception
	{
		executeStatement(getHttpFlexConnection(getUserPrincipals(false)));
	}

	@Test(expected = SQLException.class)
	public void testSelectAsAnonymuous() throws Exception
	{
		getHttpFlexConnection(new Properties());
	}

	@Test
	public void testSelectAsReadWriteUserPrepared() throws Exception
	{
		executePreparedStatement(getHttpFlexConnection(getUserPrincipals(true)));
	}

	@Test
	public void testSelectAsReadOnlyUserPrepared() throws Exception
	{
		executePreparedStatement(getHttpFlexConnection(getUserPrincipals(false)));
	}


	@Test
	public void testSelectAsReadOnlyUserPreparedList() throws Exception
	{
		executePreparedStatementForList(getHttpFlexConnection(getUserPrincipals(false)));
	}

	@Test
	public void testSelectAsReadOnlyUserList() throws Exception
	{
		executeStatementForList(getHttpFlexConnection(getUserPrincipals(false)));
	}

	@Test(expected = SQLException.class)
	public void testSelectAsAnonymuousPrepared() throws Exception
	{
		getHttpFlexConnection(new Properties());
	}
}
