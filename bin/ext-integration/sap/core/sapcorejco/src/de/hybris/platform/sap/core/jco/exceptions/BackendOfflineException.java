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
package de.hybris.platform.sap.core.jco.exceptions;


/**
 * Represents a the exception which occurs once the backend is switched offline (e.g. in RFC destination hmc
 * maintenance).
 */
public class BackendOfflineException extends BackendCommunicationException
{

	private static final long serialVersionUID = 3596371100453759356L;

	/**
	 * Creates a new exception with a given message.
	 * 
	 * @param msg
	 *           Message to give further information about the exception
	 */
	public BackendOfflineException(final String msg)
	{
		super(msg);
	}

}
