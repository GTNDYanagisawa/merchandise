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
package de.hybris.platform.servicelayer.security.auth.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.auth.AuthenticationService;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.user.exceptions.PasswordEncoderNotFoundException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultAuthenticationServiceTest extends ServicelayerTransactionalBaseTest
{
	private static final Logger LOG = Logger.getLogger(DefaultAuthenticationServiceTest.class.getName());

	private static final String TEST_USER = "delluriel";

	@Resource
	private AuthenticationService authenticationService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	private UserModel testUser;
	private UserModel anonymous;

	@Before
	public void setUp()
	{
		anonymous = userService.getUserForUID("anonymous");

		testUser = modelService.create(UserModel.class);
		testUser.setUid("testUser");
		modelService.save(testUser);
		userService.setPassword(testUser.getUid(), "pwd");

		assertEquals(anonymous, userService.getCurrentUser());
	}

	@Test
	public void testLoginLogout() throws InvalidCredentialsException, JaloSecurityException
	{
		final UserModel result = authenticationService.login(testUser.getUid(), userService.getPassword(testUser.getUid()));

		assertEquals(testUser, result);
		assertEquals(testUser, userService.getCurrentUser());

		authenticationService.logout();

		assertEquals(anonymous, userService.getCurrentUser());

		// create session for test tear down
		jaloSession = JaloConnection.getInstance().createAnonymousCustomerSession();
	}

	@Test(expected = InvalidCredentialsException.class)
	public void testLoginWrongPwd() throws InvalidCredentialsException
	{
		authenticationService.login(testUser.getUid(), "bla");
	}

	@Test(expected = InvalidCredentialsException.class)
	public void testLoginWrongUser() throws InvalidCredentialsException
	{
		authenticationService.login("bla", userService.getPassword(testUser.getUid()));
	}

	@Test
	public void shouldThrowInvalidCredentialsExceptionWhenLoginIsDisabled()
	{
		// given
		testUser.setLoginDisabled(true);
		modelService.save(testUser);

		try
		{
			// when
			authenticationService.login(testUser.getUid(), userService.getPassword(testUser.getUid()));
			fail("should throw InvalidCredentialsException");
		}
		catch (final InvalidCredentialsException e)
		{
			// then
			assertThat(e.getMessage()).isEqualTo("invalid credentials");
		}
	}


	@Test
	public void shouldThrowInvalidCredentialsExceptionWhenPasswordEncodingIsWrongOrNotKnown()
	{
		// given
		final String password = userService.getPassword(testUser.getUid());
		testUser.setPasswordEncoding("someNonExistend");

		try
		{
			// when
			authenticationService.login(testUser.getUid(), password);
			fail("should throw InvalidCredentialsException");
		}
		catch (final InvalidCredentialsException e)
		{
			// then
			assertThat(e.getMessage()).isEqualTo("invalid credentials");
		}
	}

	@Test
	public void testCheckCredentials() throws InvalidCredentialsException
	{
		final UserModel result = authenticationService.checkCredentials(testUser.getUid(),
				  userService.getPassword(testUser.getUid()));

		assertEquals(testUser, result);
		assertEquals(anonymous, userService.getCurrentUser());
	}

	@Test(expected = InvalidCredentialsException.class)
	public void testCheckCredentialsWrongPwd() throws InvalidCredentialsException
	{
		authenticationService.checkCredentials(testUser.getUid(), "bla");
	}

	@Test(expected = InvalidCredentialsException.class)
	public void testCheckCredentialsWrongUser() throws InvalidCredentialsException
	{
		authenticationService.checkCredentials("bla", userService.getPassword(testUser.getUid()));
	}

	@Test
	public void testEncodedPasswords() throws ConsistencyCheckException
	{
		final UserModel foo = new UserModel();
		modelService.initDefaults(foo);
		foo.setUid("testuser.encoded");
		modelService.save(foo);
		userService.setPassword("testuser.encoded", "xxxxxx", "md5");
		try
		{
			authenticationService.checkCredentials(foo.getUid(), "xxxxxx");
			LOG.info("Authetication successful!");
		}
		catch (final InvalidCredentialsException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * small test for getting the encoder not found exception
	 */
	@Test(expected = PasswordEncoderNotFoundException.class)
	public void testFalseEncoding()
	{
		userService.setPassword(testUser.getUid(), "xxxxxx", "blub");
		fail("there should be no 'blub' encoding - so expected an exception here");
	}


	@Test
	public void testChangePassword() throws Exception
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(TEST_USER);
		user.setName(TEST_USER);

		modelService.save(user);

		userService.setPassword(TEST_USER, "1234");

		final UserModel userAgain = userService.getUserForUID(TEST_USER);
		assertNotNull(userAgain);
		assertEquals("1234", userAgain.getEncodedPassword());
		assertEquals("*", userAgain.getPasswordEncoding());

		authenticationService.checkCredentials(TEST_USER, "1234");

		userService.setPassword(TEST_USER, "testPw1", "md5");
		modelService.refresh(userAgain);
		assertEquals("md5", userAgain.getPasswordEncoding());


		authenticationService.checkCredentials(TEST_USER, "testPw1");

		userService.setPassword(TEST_USER, "testPw2", "md5");
		assertEquals("md5", userAgain.getPasswordEncoding()); // THIS ASSERT WILL FAIL WITH THE REPORTED VALUE BEING "*"

		authenticationService.checkCredentials(TEST_USER, "testPw2");
	}
}
