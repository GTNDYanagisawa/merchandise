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
package de.hybris.platform.accountsummaryaddon.document.service.impl;

import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.accountsummaryaddon.document.NumberOfDayRange;
import de.hybris.platform.accountsummaryaddon.document.service.PastDueBalanceDateRangeService;

public class DefaultPastDueBalanceDateRangeService implements PastDueBalanceDateRangeService
{

	private static final String END_SUFFIX = ".end";
	private static final String START_SUFFIX = ".start";
	private static final String ACCOUNTSUMMARY_DATERANGE = "accountsummaryaddon.daterange.";

	@Override
	public List<NumberOfDayRange> getNumberOfDayRange()
	{
		final List<NumberOfDayRange> result = new ArrayList<NumberOfDayRange>();
		boolean dateRangeExist = true;
		int index = 1;
		while (dateRangeExist)
		{
			final Integer dateRangeStartValue = getRangeValue(index, true);
			final Integer dateRangeEndValue = getRangeValue(index, false);

			if (dateRangeStartValue == null)
			{
				dateRangeExist = false;
			}
			else
			{
				result.add(new NumberOfDayRange(dateRangeStartValue, dateRangeEndValue));
			}
			index++;
		}

		return result;
	}

	protected Integer getRangeValue(final int index, final boolean start)
	{
		final String dateRangeValue = Config.getParameter(ACCOUNTSUMMARY_DATERANGE + index + (start ? START_SUFFIX : END_SUFFIX));
		if (StringUtils.isBlank(dateRangeValue))
		{
			return null;
		}
		return Integer.valueOf(dateRangeValue);
	}
}
