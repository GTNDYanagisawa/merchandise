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
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.CipherService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link CipherService}.
 */
public class DefaultCipherService implements CipherService
{
	private static final Logger LOG = Logger.getLogger(DefaultCipherService.class);
	private static final String SEPARATOR = "-$-";
	protected static final String CHARACTER_ENCODING = "UTF-8";

	private ConfigurationService configurationService;

	@Override
	public String encrypt(final String userId, final PunchOutSession punchoutSession) throws PunchOutCipherException,
			IllegalArgumentException
	{
		if (StringUtils.isNotBlank(userId) && (punchoutSession != null) && (punchoutSession.getTime() != null))
		{
			final String salt = AsymmetricManager.getSalt();
			final String key = SymmetricManager.getKey();
			punchoutSession.setSalt(salt);
			punchoutSession.setKey(key);

			final String unsecureText = getUnsecureText(userId, punchoutSession);
			final String hash = AsymmetricManager.getHash(unsecureText, salt);
			final String encrypted = SymmetricManager.encrypt(userId + SEPARATOR + hash, key);

			return encode(encrypted);
		}
		else
		{
			final String msg = "Unable to encrypt user due empty or to missing arguments: userId=[" + userId + "] punchoutSession=["
					+ punchoutSession + "]";
			LOG.info(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	public String retrieveUserId(final String encryptedText, final PunchOutSession punchoutSession)
			throws PunchOutCipherException, IllegalArgumentException
	{
		String retrievedUserId = null;
		verifyPunchOutSession(punchoutSession);
		final String decryptedText = SymmetricManager.decrypt(encryptedText, punchoutSession.getKey());
		if (StringUtils.isNotBlank(decryptedText))
		{
			final int index = decryptedText.indexOf(SEPARATOR);
			final String userId = decryptedText.substring(0, index);
			final String hash = decryptedText.substring(index + SEPARATOR.length());
			if (verify(hash, userId, punchoutSession))
			{
				retrievedUserId = userId;
			}
		}

		return retrievedUserId;
	}

	private void verifyPunchOutSession(final PunchOutSession punchoutSession) throws IllegalArgumentException
	{
		if ((punchoutSession == null) || StringUtils.isBlank(punchoutSession.getKey()) || (punchoutSession.getTime() == null)
				|| StringUtils.isBlank(punchoutSession.getSalt()))
		{
			final String msg = "Current punchoutSession is null or has missing attributes: " + punchoutSession;
			LOG.info(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Used to grant correct character encoding through web.
	 * 
	 * @param notEncoded
	 * @return The String encoded with {@link DefaultCipherService#CHARACTER_ENCODING}.
	 */
	private static String encode(final String notEncoded)
	{
		try
		{
			return URLEncoder.encode(notEncoded, CHARACTER_ENCODING);
		}
		catch (final UnsupportedEncodingException e)
		{
			// should never happen
			LOG.error("System was unable to encode text with " + CHARACTER_ENCODING, e);
			return null;
		}
	}

	/**
	 * Generates a hash given the session data and verifies if it matches the hash provided.
	 * 
	 * @param hash
	 *           The hash that should be validated.
	 * @param userId
	 *           The user that was used to create the hash.
	 * @param punchoutSession
	 *           The current punchout session.
	 * @return True if the hash passes the verification.
	 * @throws IllegalArgumentException
	 *            If arguments are null or empty.
	 */
	private boolean verify(final String hash, final String userId, final PunchOutSession punchoutSession)
			throws IllegalArgumentException
	{
		boolean passedVerification = false;
		if (StringUtils.isNotBlank(hash) && StringUtils.isNotBlank(userId))
		{
			final String unsecureText = getUnsecureText(userId, punchoutSession);
			final String generatedHash = AsymmetricManager.getHash(unsecureText, punchoutSession.getSalt());
			if (hash.equals(generatedHash))
			{
				passedVerification = true;
			}
		}
		else
		{
			final String msg = "Unable to verify hash. Some parameters are missing: hash=[" + hash + "] email=[" + userId + "]";
			LOG.info(msg);
			throw new IllegalArgumentException(msg);
		}
		return passedVerification;
	}

	/**
	 * Gets text used for asymmetric encryption.
	 * 
	 * @param userId
	 * @param punchoutSession
	 * @return The composed text that should be hashed.
	 */
	private String getUnsecureText(final String userId, final PunchOutSession punchoutSession)
	{
		final long timestamp = punchoutSession.getTime().getTime();
		return userId + timestamp;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
