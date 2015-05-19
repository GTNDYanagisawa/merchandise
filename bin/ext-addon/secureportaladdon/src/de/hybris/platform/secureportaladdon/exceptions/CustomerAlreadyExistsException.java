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
package de.hybris.platform.secureportaladdon.exceptions;

import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * Thrown when a customer already exists
 */
public class CustomerAlreadyExistsException extends BusinessException
{

	public CustomerAlreadyExistsException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public CustomerAlreadyExistsException(final String message)
	{
		super(message);
	}

	public CustomerAlreadyExistsException(final Throwable cause)
	{
		super(cause);

	}

}
