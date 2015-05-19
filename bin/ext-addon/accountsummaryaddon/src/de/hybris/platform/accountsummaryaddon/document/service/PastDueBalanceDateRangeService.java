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
package de.hybris.platform.accountsummaryaddon.document.service;

import java.util.List;

import de.hybris.platform.accountsummaryaddon.document.NumberOfDayRange;


public interface PastDueBalanceDateRangeService
{
	/**
	 * Gets a list of number of days ranges.
	 * 
	 * @return date range list
	 */
	List<NumberOfDayRange> getNumberOfDayRange();
}
