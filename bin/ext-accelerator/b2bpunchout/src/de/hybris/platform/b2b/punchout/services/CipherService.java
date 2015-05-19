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
package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.punchout.PunchOutCipherException;
import de.hybris.platform.b2b.punchout.PunchOutSession;


/**
 * Service to perform encryption and decryption of values, in order to secure the communication with the PunchOut
 * service.
 */
public interface CipherService
{
	/**
	 * Create a hash for a given user to be used in punchout login.
	 * 
	 * @param userId
	 *           The human readable id of the user that will get an associated hash.
	 * @param punchoutSession
	 *           The session of the current punchout user.
	 * @return The encrypted value.
	 * @throws PunchOutCipherException
	 *            If Cipher has issues encrypting the text.
	 * @throws IllegalArgumentException
	 *            If some of the required arguments is missing or empty.
	 */
	String encrypt(String userId, PunchOutSession punchoutSession) throws PunchOutCipherException, IllegalArgumentException;

	/**
	 * Retrieves the userId for a given encrypted text.
	 * 
	 * @param encryptedText
	 *           The text to be decrypted.
	 * @param punchoutSession
	 *           The session of the current punchout user.
	 * @return The userId, if the information contained in the encryptedText passes the security verification (null
	 *         otherwise).
	 * @throws PunchOutCipherException
	 *            If Cipher has issues decrypting the text.
	 * @throws IllegalArgumentException
	 *            If some of the required arguments is missing or empty.
	 */
	String retrieveUserId(final String encryptedText, final PunchOutSession punchoutSession) throws PunchOutCipherException,
			IllegalArgumentException;
}
