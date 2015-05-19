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
public class SearchConfigs
{
	public static SearchConfig createSearchConfig(final List emptyList, final int i)
	{
		final SearchConfig config = new SearchConfig();
		config.setDefaultSortOrder(emptyList);
		config.setPageSize(i);
		return config;
	}

}
