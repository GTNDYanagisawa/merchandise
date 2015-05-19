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
package de.hybris.platform.b2b.punchout;

/**
 * Exception throw when Cipher has issues to encrypt or decrypt some text. Usually happens because of a bad key (e.g.:
 * wrong size, wrong data, does not apply to desired algorithm).
 */
public class PunchOutCipherException extends PunchOutException
{

	public PunchOutCipherException(final String message)
	{
		super(PunchOutResponseCode.FORBIDDEN, message);
	}

	public PunchOutCipherException(final String message, final Throwable cause)
	{
		super(PunchOutResponseCode.FORBIDDEN, message, cause);
	}

}
