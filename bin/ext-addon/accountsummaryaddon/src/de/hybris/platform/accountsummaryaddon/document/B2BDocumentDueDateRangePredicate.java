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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.time.DateUtils;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.utils.XDate;


/**
 * Predicate uses to filter all B2B Documents for a specific range of due dates.
 * 
 */
public class B2BDocumentDueDateRangePredicate implements Predicate
{

	private final NumberOfDayRange dateRange;

	public B2BDocumentDueDateRangePredicate(final NumberOfDayRange dateRange)
	{
		this.dateRange = dateRange;
	}

	@Override
	public boolean evaluate(final Object doc)
	{
		if (!(doc instanceof B2BDocumentModel))
		{
			return false;
		}

		final B2BDocumentModel document = (B2BDocumentModel) doc;

		final Date min = XDate.setToEndOfDay(DateUtils.addDays(new Date(), -dateRange.getMinBoundary().intValue()));

		Date max = null;
		if (dateRange.getMaxBoundary() != null)
		{
			max = DateUtils.truncate(DateUtils.addDays(new Date(), -dateRange.getMaxBoundary().intValue()), Calendar.DAY_OF_MONTH);
		}

		return (dateRange.getMaxBoundary() == null || (document.getDueDate() != null && document.getDueDate().getTime() >= max
				.getTime())) && (document.getDueDate() != null && document.getDueDate().getTime() <= min.getTime());
	}
}
