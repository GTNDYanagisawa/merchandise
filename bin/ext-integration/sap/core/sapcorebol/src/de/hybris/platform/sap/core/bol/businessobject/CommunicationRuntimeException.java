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


/**
 * Represents a general superclass for all exceptions of the back end layer caused by communication problems (network
 * failures etc.).
 */
public class CommunicationRuntimeException extends BORuntimeException
{


	private static final long serialVersionUID = -6659050592438056173L;


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public CommunicationRuntimeException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor.
	 */
	public CommunicationRuntimeException()
	{
		super();
	}


	/**
	 * Standard constructor for CommunicationException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public CommunicationRuntimeException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}
