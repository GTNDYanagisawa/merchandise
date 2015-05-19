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
 */
package de.hybris.platform.ycommercewebservices.exceptions;

/**
 * Thrown when an operation is performed that requires a session cart, which was not yet created.
 */
public class NoCheckoutCartException extends Exception
{

	/**
	 * @param message
	 */
	public NoCheckoutCartException(final String message)
	{
		super(message);
	}

}
