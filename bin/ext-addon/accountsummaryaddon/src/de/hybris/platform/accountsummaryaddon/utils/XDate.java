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
package de.hybris.platform.accountsummaryaddon.utils;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;


/**
 * Used to provide Date utilities
 */
public final class XDate
{
	private static final Logger LOG = Logger.getLogger(XDate.class);

	private XDate()
	{
		// No public constructor for utility class
	}

	public static Date setToEndOfDay(final Date date)
	{
		Date newDate = new Date(date.getTime());
		newDate = DateUtils.setHours(newDate, 23);
		newDate = DateUtils.setMinutes(newDate, 59);
		newDate = DateUtils.setSeconds(newDate, 59);
		newDate = DateUtils.setMilliseconds(newDate, 999);
		return newDate;
	}
}
