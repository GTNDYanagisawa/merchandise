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
package de.hybris.platform.acceleratorservices.dataimport.batch.aop;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;

import org.apache.commons.lang.ArrayUtils;


/**
 * Utilities for batch aspect implementations.
 */
public final class AspectUtils
{
	private AspectUtils()
	{
		//empty
	}

	/**
	 * Retrieves a header from the given arguments.
	 * 
	 * @param args
	 * @return header
	 */
	public static BatchHeader getHeader(final Object[] args)
	{
		BatchHeader header = null;
		if (!ArrayUtils.isEmpty(args))
		{
			final Object arg = args[0];
			if (arg instanceof BatchHeader)
			{
				header = (BatchHeader) arg;
			}
		}
		return header;
	}
}
