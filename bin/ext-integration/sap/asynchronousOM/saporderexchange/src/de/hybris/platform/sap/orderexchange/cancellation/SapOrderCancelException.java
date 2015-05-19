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
package de.hybris.platform.sap.orderexchange.cancellation;

/**
 * Runtime exception which signals an error in the cancel processing
 */
public class SapOrderCancelException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("javadoc")
	public SapOrderCancelException(final String msg, final Throwable throwable)
	{
		super(msg, throwable);
	}

	@SuppressWarnings("javadoc")
	public SapOrderCancelException(final String msg)
	{
		super(msg);
	}

}
