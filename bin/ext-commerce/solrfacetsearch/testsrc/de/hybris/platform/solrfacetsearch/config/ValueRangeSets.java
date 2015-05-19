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
package de.hybris.platform.solrfacetsearch.config;

import java.util.List;


/**
 *
 */
public class ValueRangeSets
{
	public static ValueRangeSet createValueRangeSet(final String qualifier, final List<ValueRange> valueRanges)
	{
		final ValueRangeSet set = new ValueRangeSet();
		set.setQualifier(qualifier);
		set.setValueRanges(valueRanges);
		return set;
	}
}
