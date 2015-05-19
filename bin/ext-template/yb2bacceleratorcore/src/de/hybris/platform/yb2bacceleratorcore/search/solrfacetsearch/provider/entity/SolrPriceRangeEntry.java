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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.entity;

import java.math.BigDecimal;


/**
 * Entity to hold one entry in the Solr price range.
 */
public class SolrPriceRangeEntry
{
	private final BigDecimal value;
	private final String currencyIso;

	public SolrPriceRangeEntry(final String value, final String currencyIso)
	{
		this.value = new BigDecimal(Double.valueOf(value));
		this.currencyIso = currencyIso;
	}

	public BigDecimal getValue()
	{
		return value;
	}

	public String getCurrencyIso()
	{
		return currencyIso;
	}
}
