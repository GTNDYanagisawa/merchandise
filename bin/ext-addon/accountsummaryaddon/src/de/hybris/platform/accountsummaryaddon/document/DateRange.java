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
package de.hybris.platform.accountsummaryaddon.document;

import java.util.Date;

public class DateRange implements Range
{

	private final Date minDateRange;
	private final Date maxDateRange;

	public DateRange(final Date minDateRange, final Date maxDateRange)
	{
		this.minDateRange = minDateRange;
		this.maxDateRange = maxDateRange;
	}


	@Override
	public Date getMinBoundary()
	{
		return this.minDateRange;
	}


	@Override
	public Date getMaxBoundary()
	{
		return this.maxDateRange;
	}
}
