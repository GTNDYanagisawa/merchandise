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


/**
 * Before running this class be aware to have running standalone platform/tomcat or any web wrapper with vjdbc extension
 * 'on board'
 */
@IntegrationTest(standalone = false)
public class VjdbcSqlHttpTest extends AbstractVjdbcSqlTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(VjdbcSqlHttpTest.class);

	@Test
	public void testSelectAsReadWriteUser() throws Exception
	{
		selectTest(getHttpSqlConnection(getUserPrincipals(true)));
	}

	@Test
	public void testSelectAsReadOnlyUser() throws Exception
	{
		selectTest(getHttpSqlConnection(getUserPrincipals(false)));
	}

	@Test(expected = SQLException.class)
	public void testSelectAsAnonymuous() throws Exception
	{
		selectTest(getHttpSqlConnection(new Properties()));
	}

	@Test
	public void testUpdateAsReadWriteUser() throws Exception
	{
		updateTest(getHttpSqlConnection(getUserPrincipals(true)));
	}

	@Test(expected = java.sql.SQLException.class)
	public void testUpdateAsReadOnlyUser() throws Exception
	{
		updateTest(getHttpSqlConnection(getUserPrincipals(false)));
	}

	@Test(expected = java.sql.SQLException.class)
	public void testUpdateAsAnonymuous() throws Exception
	{
		updateTest(getHttpSqlConnection(new Properties()));
	}
}
