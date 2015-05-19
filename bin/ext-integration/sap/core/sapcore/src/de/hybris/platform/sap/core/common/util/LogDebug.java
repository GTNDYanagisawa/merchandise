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
package de.hybris.platform.sap.core.common.util;

import java.text.MessageFormat;

import org.apache.log4j.Logger;


/**
 * LogDebug utility.
 */
public class LogDebug
{
	/**
	 * Logs debug information with arguments.
	 * 
	 * @param log
	 *           Logger
	 * @param msg
	 *           Message to be logged. See {@link java.text.MessageFormat} for pattern description
	 * @param objects
	 *           Set of objects to be logged
	 */
	public static void debug(final Logger log, final String msg, final Object... objects)
	{
		if (log.isDebugEnabled())
		{
			log.debug(MessageFormat.format(msg, objects));
		}

	}
}
