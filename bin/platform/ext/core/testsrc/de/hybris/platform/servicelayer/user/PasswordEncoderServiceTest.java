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
package de.hybris.platform.servicelayer.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.exceptions.CannotDecodePasswordException;
import de.hybris.platform.servicelayer.user.exceptions.PasswordEncoderNotFoundException;
import de.hybris.platform.servicelayer.user.impl.DefaulPasswordEncoderService;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class PasswordEncoderServiceTest extends ServicelayerTransactionalBaseTest
{
	final public static String MD5 = "MD5";

	@Resource
	private DefaulPasswordEncoderService passwordEncoderService;

	@Resource
	private ModelService modelService;

	@Test(expected = PasswordEncoderNotFoundException.class)
	public void testEncodeWithPasswordEncoderNotFoundException()
	{
		assertFalse(passwordEncoderService.getSupportedEncodings().contains("xxx"));
		passwordEncoderService.encode(new UserModel(), "xxx", "xxx");
	}

	@Test
	public void encodeWithPlainEncoding()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid("xxx");

		final String encodedPassword = passwordEncoderService.encode(user, "xxx", PasswordEncoderConstants.DEFAULT_ENCODING);

		assertTrue(modelService.isNew(user));
		assertEquals("xxx", encodedPassword);
	}

	@Test
	public void testEncodeWithNullEncoding()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid("xxx");

		final String encodedPassword = passwordEncoderService.encode(user, "222", null);

		assertEquals("222", encodedPassword);
	}

	@Test
	public void defaultEncodingExists()
	{
		assertTrue(passwordEncoderService.isSupportedEncoding(PasswordEncoderConstants.DEFAULT_ENCODING));
		assertTrue(passwordEncoderService.getSupportedEncodings().contains(PasswordEncoderConstants.DEFAULT_ENCODING));
	}

	@Test
	public void testIsValidAndDecodePassword()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid("xxx");
		user.setPasswordEncoding(PasswordEncoderConstants.DEFAULT_ENCODING);
		user.setEncodedPassword(passwordEncoderService.encode(user, "111", user.getPasswordEncoding()));

		assertTrue(passwordEncoderService.isValid(user, "111"));
		assertEquals("111", passwordEncoderService.decode(user));
	}

	@Test(expected = CannotDecodePasswordException.class)
	public void testCannotDecodeException()
	{
		//this test makes only sence if final we have an final encoding which throws final such exception
		assertTrue(passwordEncoderService.isSupportedEncoding(MD5));
		assertTrue(passwordEncoderService.isSupportedEncoding("md5"));

		final UserModel user = modelService.create(UserModel.class);
		user.setUid("xxx");
		user.setPasswordEncoding("md5");
		passwordEncoderService.decode(user);
	}

	@Test(expected = PasswordEncoderNotFoundException.class)
	public void testDecodeWithPasswordEncoderNotFoundException()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid("xxx");
		user.setPasswordEncoding("xx11");
		passwordEncoderService.decode(user);
	}
}
