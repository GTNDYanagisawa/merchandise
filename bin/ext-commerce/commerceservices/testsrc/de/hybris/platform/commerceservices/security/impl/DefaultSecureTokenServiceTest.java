/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.commerceservices.security.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.testframework.TestUtils;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;


/**
 * test for {@link DefaultSecureTokenServiceTest}
 */
@UnitTest
public class DefaultSecureTokenServiceTest
{
	private static final String TEST_DATA = "5a402e7b71";
	private static final long TEST_TS = 1234567l;
	private DefaultSecureTokenService service;

	@Before
	public void setUp() throws DecoderException, NoSuchAlgorithmException
	{
		service = new DefaultSecureTokenService();
		service.setEncryptionKeyHex("386566563556563a47795d4950643224692a493f3b6b325d45774d5244");
		service.setSignatureKeyHex("3147353d59706c6d223a3677367c78267b7b7c27275073562e3e5f2863");
		service.afterPropertiesSet();
	}


	@Test
	public void testEncryptLenght()
	{

		final String data = "test.user123412341234@test.com:1397570101997:8796094365700";
		final SecureToken token = new SecureToken(data, System.currentTimeMillis());
		final String encryptedString = service.encryptData(token);
		System.out.println("encrypted data : '" + data + "' of size " + data.length() + " to '" + encryptedString + "'  length: " + encryptedString.length() + " encrypted sting is larger is size by " + (encryptedString.length() - data.length()));

	}


	@Test(expected = IllegalArgumentException.class)
	public void testEncryptNull()
	{
		service.encryptData(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEncryptEmpty()
	{
		final SecureToken token = new SecureToken("", 0l);
		service.encryptData(token);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecryptNull()
	{
		service.decryptData(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecryptEmpty()
	{
		service.decryptData("");
	}

	@Test
	public void testCycle()
	{
		final SecureToken data = new SecureToken(TEST_DATA, TEST_TS);
		final String token = service.encryptData(data);
		Assert.assertNotNull(token);
		final SecureToken result = service.decryptData(token);
		Assert.assertNotSame(result, data);
		Assert.assertEquals(TEST_DATA, result.getData());
		Assert.assertEquals(TEST_TS, result.getTimeStamp());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBlocksize()
	{
		final SecureToken data = new SecureToken(TEST_DATA, TEST_TS);
		final String token = service.encryptData(data);
		Assert.assertNotNull(token);
		TestUtils.disableFileAnalyzer("ACC-864");
		try
		{
			// generates a warning IllegalBlockSizeException in the log
			service.decryptData(token.substring(0, 40));
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testChecksum()
	{
		final SecureToken data = new SecureToken(TEST_DATA, TEST_TS);
		final String token = service.encryptData(data);
		final StringBuilder builder = new StringBuilder(token);
		builder.replace(0, 4, "1234");
		service.decryptData(builder.toString());
	}

}
