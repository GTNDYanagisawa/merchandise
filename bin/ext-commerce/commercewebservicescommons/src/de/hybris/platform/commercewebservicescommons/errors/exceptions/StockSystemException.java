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
 * Exception thrown if stock system is not enabled
 */
public class StockSystemException extends WebserviceException
{
	public static final String NOT_ENABLED = "notEnabled";
	private static final String TYPE = "StockSystemError";
	private static final String SUBJECT_TYPE = "site";

	public StockSystemException(final String message)
	{
		super(message);
	}

	public StockSystemException(final String message, final String reason)
	{
		super(message, reason);
	}

	public StockSystemException(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public StockSystemException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public StockSystemException(final String message, final String reason, final String subject, final Throwable cause)
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
