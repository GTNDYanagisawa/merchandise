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
package de.hybris.platform.order;

import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * Thrown when creation or modification of cart is not possible.
 */
public class InvalidCartException extends BusinessException
{

	public InvalidCartException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public InvalidCartException(final String message)
	{
		super(message);
	}

	public InvalidCartException(final Throwable cause)
	{
		super(cause);
	}

}
