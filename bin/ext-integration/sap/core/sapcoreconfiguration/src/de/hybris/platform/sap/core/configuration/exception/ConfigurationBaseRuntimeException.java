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
package de.hybris.platform.sap.core.configuration.exception;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;


/**
 * Core base runtime exception.
 */
public class ConfigurationBaseRuntimeException extends CoreBaseRuntimeException
{

	private static final long serialVersionUID = 8730135682117510547L;

	/**
	 * Standard constructor for ConfigurationBaseRuntimeException with no detail message.
	 */
	public ConfigurationBaseRuntimeException()
	{
		super();
	}

	/**
	 * Standard constructor for ConfigurationBaseRuntimeException with the specified detail message.
	 * 
	 * @param message
	 *           the detail message.
	 */
	public ConfigurationBaseRuntimeException(final String message)
	{
		super(message);
	}

	/**
	 * Standard constructor for ConfigurationBaseRuntimeException with the specified detail message and root cause.
	 * 
	 * @param message
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public ConfigurationBaseRuntimeException(final String message, final Throwable rootCause)
	{
		super(message, rootCause);
	}

}
