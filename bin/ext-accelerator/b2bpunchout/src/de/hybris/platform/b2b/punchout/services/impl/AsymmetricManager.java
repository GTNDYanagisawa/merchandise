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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;


/**
 * Utility class used for asymmetric encryption.
 */
public class AsymmetricManager
{
	private static final String ALGORITHM = "SHA-256";
	private static final String CHAR_SET = "UTF-8";
	private static final int SALT_LEN = 16;
	private static final Logger LOG = Logger.getLogger(AsymmetricManager.class);

	/**
	 * Generates a hash using asymmetric encryption.
	 * 
	 * @param unsecureText
	 *           The text to be hashed.
	 * @param salt
	 *           The salt used to defend against dictionary and rainbow table attacks.
	 * @return The hash.
	 */
	public static String getHash(final String unsecureText, final String salt)
	{
		try
		{
			final MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
			digest.update(unsecureText.getBytes(CHAR_SET));
			digest.update(salt.getBytes(CHAR_SET));
			final byte[] byteData = digest.digest();

			//convert the byte to hex format method 1
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++)
			{
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			// should never happen
			LOG.error("System was unable to generate the hash.", e);
		}
		return null;
	}

	/**
	 * Generates a random salt.
	 * 
	 * @return The salt.
	 */
	public static String getSalt()
	{
		final byte[] salt = new byte[SALT_LEN];
		final SecureRandom rnd = new SecureRandom();
		rnd.nextBytes(salt);
		return Hex.encodeHexString(salt);
	}
}
