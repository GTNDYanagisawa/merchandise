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
package de.hybris.platform.webservices;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;


/**
 * {@link RuntimeException} which provides some additional webservice specific information which should be used to build
 * the client response.
 * 
 */
public class YWebservicesException extends RuntimeException
{
	// default response status for this exception (depends on exception cause)
	private int respStatus = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

	/**
	 * Constructor.
	 */
	public YWebservicesException()
	{
		super();
	}

	public YWebservicesException(final String message)
	{
		super(message);
	}

	public YWebservicesException(final Throwable cause)
	{
		this(null, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public YWebservicesException(final String message, final Throwable cause)
	{
		super(message, cause);

		// decide which response status is used for this exception
		if (cause instanceof BadRequestException)
		{
			this.respStatus = Response.Status.BAD_REQUEST.getStatusCode();
		}

		if (cause instanceof ForbiddenException)
		{
			this.respStatus = Response.Status.FORBIDDEN.getStatusCode();
		}

		if (cause instanceof YWebservicesException)
		{
			this.respStatus = ((YWebservicesException) cause).getResponseStatus();
		}
	}


	/**
	 * Returns a http-response status which should be used when this exception is thrown.
	 * 
	 * @return response status
	 */
	public int getResponseStatus()
	{
		return this.respStatus;
	}

	/**
	 * Builds and returns a short client-friendly stacktrace.
	 * 
	 * @return short stacktrace
	 */
	public String getShortStacktrace()
	{
		final StringBuilder msgBuilder = new StringBuilder();

		if (getMessage() != null)
		{
			msgBuilder.append(getMessage());
		}
		Throwable cause = getCause();
		int max = 5;
		while (cause != null && max > 0)
		{
			msgBuilder.append("\n").append(cause.getMessage() + " [" + cause.getClass().getSimpleName() + "]");
			cause = cause.getCause();
			max--;
		}

		return msgBuilder.toString();
	}

	/**
	 * Notifies logger about this exception. When exception cause is of type {@link BadRequestException} logger only gets
	 * passed the general error message otherwise exception itself gets passed to logger additionally.
	 * 
	 * @param log
	 *           {@link Logger} which shall be notified about this exception
	 */
	public void notifyLogger(final Logger log)
	{
		if (this.respStatus == Response.Status.BAD_REQUEST.getStatusCode())
		{
			log.error(getMessage());
		}
		else
		{
			log.error(getMessage(), this);
		}
	}

}
