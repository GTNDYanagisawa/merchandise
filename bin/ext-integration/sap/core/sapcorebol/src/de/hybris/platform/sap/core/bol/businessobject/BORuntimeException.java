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
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;


/**
 * The <code>BORuntimeException</code> is the base class for bo related runtime exceptions. <br>
 * Don't catch this Exception except in the action layer because it represents an error in the implementation and not
 * the underlying backend system.
 */
public class BORuntimeException extends CoreBaseRuntimeException
{

	private static final long serialVersionUID = -2751368261335644855L;

	/**
	 * Creates a new exception without any message.
	 */
	public BORuntimeException()
	{
		super();
	}

	/**
	 * Creates a new exception with a given message.
	 * 
	 * @param msg
	 *           Message to give further information about the exception
	 */
	public BORuntimeException(final String msg)
	{
		super(msg);
	}

	/**
	 * 
	 * Standard constructor for BORuntimeException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public BORuntimeException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}


}
