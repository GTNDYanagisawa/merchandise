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

package de.hybris.platform.commercewebservicescommons.errors.exceptions;


/**
 * Thrown when there is insufficient stock at a warehouse.
 */
public class LowStockException extends WebserviceException
{
	public static final String NO_STOCK = "noStock";
	public static final String LOW_STOCK = "lowStock";
	private static final String TYPE = "InsufficientStockError";
	private static final String SUBJECT_TYPE = "entry";

	public LowStockException(final String message)
	{
		super(message);
	}

	public LowStockException(final String message, final String reason)
	{
		super(message, reason);
	}

	public LowStockException(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public LowStockException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public LowStockException(final String message, final String reason, final String subject, final Throwable cause)
	{
		super(message, reason, subject, cause);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}
}
