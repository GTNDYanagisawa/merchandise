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
package de.hybris.platform.commons.renderer.exceptions;

/**
 * Exception thrown where error during rendering occurr
 */
public class RendererException extends RuntimeException
{
	public RendererException(final String message)
	{
		super(message);
	}

	public RendererException(final String message, final Throwable throwable)
	{
		super(message, throwable);
	}
}
