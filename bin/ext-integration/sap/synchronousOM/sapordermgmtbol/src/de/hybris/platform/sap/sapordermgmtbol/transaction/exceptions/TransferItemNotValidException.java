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
package de.hybris.platform.sap.sapordermgmtbol.transaction.exceptions;

import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseException;


/**
 * Exception class for handling invalid exception.
 * 
 */
public class TransferItemNotValidException extends ApplicationBaseException
{

	/**
     * 
     */
	private static final long serialVersionUID = 5726866258031091847L;

	/**
	 * Constructor. <br>
	 * 
	 * @param string
	 *           explaining the problem
	 */
	public TransferItemNotValidException(final String string)
	{
		super(string);
	}

	/**
	 * Constructor. <br>
	 * 
	 * @param msg
	 *           string describing the problem
	 * @param rootCause
	 *           to keep the stack trace
	 */
	public TransferItemNotValidException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}
