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
 * Abstract webservice exception for throwing single errors.
 */
public abstract class WebserviceException extends RuntimeException
{
	protected String reason;
	protected String subject;

	public WebserviceException(final String message)
	{
		super(message);
	}

	public WebserviceException(final String message, final String reason)
	{
		super(message);
		this.reason = reason;
	}

	public WebserviceException(final String message, final String reason, final Throwable cause)
	{
		super(message, cause);
		this.reason = reason;
	}

	public WebserviceException(final String message, final String reason, final String subject)
	{
		super(message);
		this.reason = reason;
		this.subject = subject;
	}

	public WebserviceException(final String message, final String reason, final String subject, final Throwable cause)
	{
		super(message, cause);
		this.reason = reason;
		this.subject = subject;
	}

	public String getReason()
	{
		return reason;
	}

	public String getSubject()
	{
		return subject;
	}

	public abstract String getType();

	public abstract String getSubjectType();
}
