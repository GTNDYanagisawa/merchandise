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
package de.hybris.platform.b2b.punchout.services.impl;

import de.hybris.platform.b2b.punchout.PunchOutCipherException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


/**
 * Utility class used for symmetric encryption.
 */
public class SymmetricManager
{
	private static final String ALGORITHM = "AES";
	private static final int KEY_SIZE = 128;
	private static final Logger LOG = Logger.getLogger(SymmetricManager.class);

	public static String encrypt(final String unsecureText, final String key) throws PunchOutCipherException
	{
		String encrypted = null;
		try
		{
			final Key skeySpec = new SecretKeySpec(new Base64().decode(key), ALGORITHM);
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			final byte[] encryptedValue = cipher.doFinal(unsecureText.getBytes());
			encrypted = new Base64().encodeAsString(encryptedValue);
		}
		catch (final NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			// should never happen
			LOG.error("System was unable instantiate Cipher.", e);
		}
		catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
		{
			final String msg = "Error occured during encryption." + e.getMessage();
			LOG.error(msg);
			throw new PunchOutCipherException(msg, e);
		}

		return encrypted;
	}

	public static String decrypt(final String encrypted, final String key) throws PunchOutCipherException
	{
		String decrypted = null;

		try
		{
			final Key skeySpec = new SecretKeySpec(new Base64().decode(key), ALGORITHM);
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			final byte[] decodedValue = new Base64().decode(encrypted.getBytes());
			final byte[] decryptedValue = cipher.doFinal(decodedValue);
			decrypted = new String(decryptedValue);
		}
		catch (final NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			// should never happen
			LOG.error("System was unable instantiate Cipher.", e);
		}
		catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
		{
			final String msg = "Error occured during decryption." + e.getMessage();
			LOG.info(msg);
			throw new PunchOutCipherException(msg, e);
		}
		return decrypted;
	}

	public static String getKey()
	{
		String key = null;
		try
		{
			final KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
			kgen.init(KEY_SIZE);
			// Generate the secret key specs.
			final SecretKey skey = kgen.generateKey();
			final byte[] raw = skey.getEncoded();
			key = new Base64().encodeAsString(raw);
		}
		catch (final NoSuchAlgorithmException e)
		{
			// should never happen
			LOG.error("System was unable to generate the key.", e);
		}
		return key;
	}
}