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

import de.hybris.platform.europe1.model.PriceRowModel;


/**
 * Entity to hold price range property stored in Solr.
 */
public class SolrPriceRange
{
	public static final String SEPARATOR = "_:_";
	public static final int TOKENS = 4;

	private final SolrPriceRangeEntry lower;
	private final SolrPriceRangeEntry higher;

	/**
	 * Constructor for SolrPriceRange.
	 * 
	 * @param lower
	 *           The {@link SolrPriceRangeEntry} with lower value in the price range.
	 * @param higher
	 *           The {@link SolrPriceRangeEntry} with higher value in the price range.
	 */
	private SolrPriceRange(final SolrPriceRangeEntry lower, final SolrPriceRangeEntry higher)
	{
		super();
		this.lower = lower;
		this.higher = higher;
	}

	public SolrPriceRangeEntry getLower()
	{
		return lower;
	}

	public SolrPriceRangeEntry getHigher()
	{
		return higher;
	}

	/**
	 * Builds a new {@link SolrPriceRange} based on a Solr property String that holds the price range.
	 * 
	 * @param solrProperty
	 *           String that contains the price range property in Solr. See
	 *           {@link #buildSolrPropertyFromPriceRows(PriceRowModel, PriceRowModel)}.
	 * @return a new {@link SolrPriceRange}.
	 */
	public static SolrPriceRange buildSolrPriceRangePairFromProperty(final String solrProperty)
	{
		SolrPriceRange priceRange = null;
		final String[] tokens = solrProperty.split(SEPARATOR);
		if (tokens != null && tokens.length == TOKENS)
		{
			final SolrPriceRangeEntry lower = new SolrPriceRangeEntry(tokens[0], tokens[1]);
			final SolrPriceRangeEntry higher = new SolrPriceRangeEntry(tokens[2], tokens[3]);
			priceRange = new SolrPriceRange(lower, higher);
		}
		else
		{
			throw new IllegalArgumentException("The solrProperty [" + solrProperty + "] should have exactly " + (TOKENS - 1)
					+ " separators '" + SEPARATOR + "'");
		}
		return priceRange;
	}

	/**
	 * Builds a String to be used in price range Solr property based on low and high prices.
	 * 
	 * @param minPrice
	 *           The lowest price value.
	 * @param minCurrency
	 *           The currency of the lowest price.
	 * @param maxPrice
	 *           The highest price value.
	 * @param maxCurrency
	 *           The currency of the highest price.
	 * @return The price range property String.
	 */
	public static String buildSolrPropertyFromPriceRows(final double minPrice, final String minCurrency, final double maxPrice,
			final String maxCurrency)
	{
		return minPrice + SEPARATOR + minCurrency + SEPARATOR + maxPrice + SEPARATOR + maxCurrency;
	}
}
