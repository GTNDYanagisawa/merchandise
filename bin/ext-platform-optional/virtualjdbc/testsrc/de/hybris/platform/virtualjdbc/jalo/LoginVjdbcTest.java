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
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.virtualjdbc.constants.VjdbcConstants;

import java.sql.SQLException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.simplicit.vjdbc.VJdbcProperties;


/**
 *
 */
@IntegrationTest(standalone = false)
public class LoginVjdbcTest extends AbstractVjdbcFlexTest
{

	/**
	 * a not allowed user because of the group
	 */
	private static final String SOME_NOT_ALLOWED_USER = "SomeUser";

	/**
	 * not existing user
	 */
	private static final String SOME_UNKNOWN_USER = "someUnknownUser";

	/**
	 * existing user , with rights but with bad password
	 */
	private static final String SOME_INCORRECT_PASSWORD_USER = "someIncorrectPasswordUser";

	private UserGroup testOtherGroup;

	private User testOtherUser;

	private User testBadPasswordUser;

	private Properties getUnknownUserPrincipals()
	{
		final Properties props = new Properties();

		props.put(VJdbcProperties.LOGIN_USER, SOME_UNKNOWN_USER);
		props.put(VJdbcProperties.LOGIN_PASSWORD, "*******************");

		return props;
	}

	private Properties getBadPasswordUserPrincipals()
	{
		final Properties props = new Properties();

		props.put(VJdbcProperties.LOGIN_USER, SOME_INCORRECT_PASSWORD_USER);
		props.put(VJdbcProperties.LOGIN_PASSWORD, "*******************");

		return props;
	}

	private String getGeneralLoginIssuesMessage(final String user)
	{
		final String incorrectMessage = Registry.getMasterTenant().getConfig()
				.getString("incorrect.login.pattern", "Choosen user %s does not exist.");
		return String.format(incorrectMessage, user);
	}

	private Properties getUnathorizedUserPrincipals()
	{
		final Properties props = new Properties();

		props.put(VJdbcProperties.LOGIN_USER, SOME_NOT_ALLOWED_USER);
		props.put(VJdbcProperties.LOGIN_PASSWORD, "1234");

		return props;
	}


	@Override
	@Before
	public void setUpTestData() throws ConsistencyCheckException
	{
		checkIntegrationTest();

		testOtherGroup = UserManager.getInstance().createUserGroup(SOME_UNKNOWN_USER);
		testOtherUser = UserManager.getInstance().createUser(SOME_NOT_ALLOWED_USER);
		testOtherUser.setPassword("1234");
		testOtherGroup.addMember(testOtherUser);

		testBadPasswordUser = UserManager.getInstance().createUser(SOME_INCORRECT_PASSWORD_USER);
		testBadPasswordUser.setPassword("1234");

		UserGroup readOnlyGroup = null;
		try
		{
			readOnlyGroup = UserManager.getInstance().getUserGroupByGroupID(
					Registry.getMasterTenant().getConfig().getString(VjdbcConstants.DB.VJDBC_RO_USER_GROUP, null));
		}
		catch (final JaloItemNotFoundException e)
		{
			readOnlyGroup = UserManager.getInstance().createUserGroup(
					Registry.getMasterTenant().getConfig().getString(VjdbcConstants.DB.VJDBC_RO_USER_GROUP, null));
		}

		readOnlyGroup.addMember(testBadPasswordUser);
	}



	@Test
	public void testUserWithoutRights() throws Exception
	{
		try
		{
			executeStatement(getHttpFlexConnection(getUnathorizedUserPrincipals()));
			Assert.fail("Login shouldn't be possible for unauthorized user ");
		}
		catch (final SQLException e)
		{
			if (!e.getMessage().contains(getGeneralLoginIssuesMessage(SOME_NOT_ALLOWED_USER)))
			{
				Assert.fail("Invalid login message exception " + e.getMessage());
			}
		}
	}

	@Test
	public void testUserUnknown() throws Exception
	{
		try
		{
			executeStatement(getHttpFlexConnection(getUnknownUserPrincipals()));
			Assert.fail("Login shouldn't be possible for unknown user ");
		}
		catch (final SQLException e)
		{
			if (!e.getMessage().contains(getGeneralLoginIssuesMessage(SOME_UNKNOWN_USER)))
			{
				Assert.fail("Invalid login message exception " + e.getMessage());
			}
		}
	}


	@Test
	public void testBadPassowrdUser() throws Exception
	{
		try
		{
			executeStatement(getHttpFlexConnection(getBadPasswordUserPrincipals()));
			Assert.fail("Login shouldn't be possible for  user with bad password ");
		}
		catch (final SQLException e)
		{
			if (!e.getMessage().contains(getGeneralLoginIssuesMessage(SOME_INCORRECT_PASSWORD_USER)))
			{
				Assert.fail("Invalid login message exception " + e.getMessage());
			}
		}
	}


	@Test
	public void testEmptyUser() throws Exception
	{
		try
		{
			executeStatement(getHttpFlexConnection(new Properties()));
			Assert.fail("Login shouldn't be possible without user/password ");
		}
		catch (final SQLException e)
		{
			if (!e.getMessage().contains(getGeneralLoginIssuesMessage(null)))
			{
				Assert.fail("Invalid login message exception " + e.getMessage());
			}
		}
	}



}
