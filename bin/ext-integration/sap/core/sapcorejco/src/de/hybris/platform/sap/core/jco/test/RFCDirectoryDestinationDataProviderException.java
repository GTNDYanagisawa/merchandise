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
package de.hybris.platform.sap.core.jco.test;

/**
 * Exception class for RFCDirectoryDestinationDataProvider.
 */
@SuppressWarnings("serial")
public class RFCDirectoryDestinationDataProviderException extends RuntimeException
{

	/**
	 * Constructs a new RFCDirectoryDestinationDataProvider exception with the specified detail message.
	 * 
	 * @param message
	 *           message
	 */
	public RFCDirectoryDestinationDataProviderException(final String message)
	{
		super(message);
	}

	/**
	 * Constructs a new RFCDirectoryDestinationDataProvider exception with the specified detail message and cause.
	 * 
	 * @param message
	 *           the detail message (which is saved for later retrieval by the Throwable.getMessage() method).
	 * @param cause
	 *           the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is
	 *           permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public RFCDirectoryDestinationDataProviderException(final String message, final Throwable cause)
	{
		super(message, cause);
	}





}
