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




public class BadRequestException extends YWebservicesException
{

	public BadRequestException()
	{
		super();
	}

	public BadRequestException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public BadRequestException(final String message)
	{
		super(message);
	}

	public BadRequestException(final Throwable cause)
	{
		super(cause);
	}

	/*
	 * (non-Javadoc)
	 * @see de.hybris.platform.webservices.YWebservicesException#getResponseStatus()
	 */
	@Override
	public int getResponseStatus()
	{
		return Response.Status.BAD_REQUEST.getStatusCode();
	}


}
