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
package de.hybris.platform.acceleratorservices.payment.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 *
 *
 */
public interface AcceleratorDigestUtils
{
	/**
	 * Utility method used for encrypting data used to secure communication with the Payment Provider's server utilizing HmacSHA1 mac algorithm
	 * 
	 * @param customValues
	 *           - a String representation of all the data that requires securing.
	 * @param key
	 *           - a security key provided by PSP used to ensure each transaction is protected during it's
	 *           transmission across the Internet.
	 * @return - an encrypted String that is deemed secure for communication with PSP
	 * @throws java.security.InvalidKeyException
	 *            if the given key is inappropriate for initializing this MAC.
	 * @throws java.security.NoSuchAlgorithmException
	 *            when attempting to get a Message Authentication Code algorithm.
	 */
	String getPublicDigest(String customValues, String key) throws NoSuchAlgorithmException, InvalidKeyException;
}
