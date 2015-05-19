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
package de.hybris.platform.sap.core.common.exceptions;

import de.hybris.platform.sap.core.common.message.Message;


/**
 * Base exception to be used by the applications running on the SPA integration framework.
 */
public class ApplicationBaseException extends CoreBaseException
{

	private static final long serialVersionUID = 1919045997418256533L;


	/**
	 * Standard constructor for ApplicationBaseException with the specified detail message.
	 * 
	 * @param message
	 *           the detail message.
	 */
	public ApplicationBaseException(final String message)
	{
		super(message);
	}

	/**
	 * Standard constructor for ApplicationBaseException using a simple message text.
	 * 
	 * @param message
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public ApplicationBaseException(final String message, final Throwable rootCause)
	{
		super(message, rootCause);
	}



	/**
	 * Standard constructor for ApplicationBaseException using a message object see
	 * {@link de.hybris.platform.sap.core.common.message.Message} for details.
	 * 
	 * @param message
	 *           message which identifies the error message.
	 */
	public ApplicationBaseException(final Message message)
	{
		super(message);
	}


	/**
	 * 
	 * Standard constructor for ApplicationBaseException using a message object see
	 * {@link de.hybris.platform.sap.core.common.message.Message} for details.
	 * 
	 * @param message
	 *           message object which identifies the error message.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public ApplicationBaseException(final Message message, final Throwable rootCause)
	{
		super(message, rootCause);
	}


}
