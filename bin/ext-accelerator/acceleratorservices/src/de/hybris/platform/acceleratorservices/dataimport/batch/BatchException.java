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
package de.hybris.platform.acceleratorservices.dataimport.batch;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Batch Exception encapsulating the BatchHeader information.
 */
public class BatchException extends SystemException
{
	private BatchHeader header;

	/**
	 * @param message
	 */
	public BatchException(final String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param header
	 */
	public BatchException(final String message, final BatchHeader header)
	{
		super(message);
		this.header = header;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BatchException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 * @param header
	 * @param cause
	 */
	public BatchException(final String message, final BatchHeader header, final Throwable cause)
	{
		super(message, cause);
		this.header = header;
	}

	/**
	 * @param cause
	 */
	public BatchException(final Throwable cause)
	{
		super(cause);
	}

	/**
	 * @return the header
	 */
	public BatchHeader getHeader()
	{
		return header;
	}
}
