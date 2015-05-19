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
 */
package de.hybris.platform.ycommercewebservices.util.ws.impl;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.ycommercewebservices.util.ws.SearchQueryCodec;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class DefaultSearchQueryCodec implements SearchQueryCodec<SolrSearchQueryData>
{
	@Override
	public SolrSearchQueryData decodeQuery(final String queryString)
	{
		final SolrSearchQueryData searchQuery = new SolrSearchQueryData();
		final List<SolrSearchQueryTermData> filters = new ArrayList<SolrSearchQueryTermData>();

		if (queryString == null)
		{
			return searchQuery;
		}

		final String[] parts = queryString.split(":");
		for (int i = 0; i < parts.length; i++)
		{
			if (i == 0)
			{
				searchQuery.setFreeTextSearch(parts[i]);
			}
			else if (i == 1)
			{
				searchQuery.setSort(parts[i]);
			}
			else
			{
				final SolrSearchQueryTermData term = new SolrSearchQueryTermData();
				term.setKey(parts[i]);
				term.setValue(parts[++i]);
				filters.add(term);
			}
		}

		searchQuery.setFilterTerms(filters);

		return searchQuery;
	}

	@Override
	public String encodeQuery(final SolrSearchQueryData searchQueryData)
	{
		if (searchQueryData == null)
		{
			return null;
		}

		final StringBuilder builder = new StringBuilder();
		builder.append((searchQueryData.getFreeTextSearch() == null) ? "" : searchQueryData.getFreeTextSearch());


		if (searchQueryData.getSort() != null
				|| (searchQueryData.getFilterTerms() != null && !searchQueryData.getFilterTerms().isEmpty()))
		{
			builder.append(":");
			builder.append((searchQueryData.getSort() == null) ? "" : searchQueryData.getSort());
		}

		final List<SolrSearchQueryTermData> terms = searchQueryData.getFilterTerms();
		if (terms != null && !terms.isEmpty())
		{
			for (final SolrSearchQueryTermData term : searchQueryData.getFilterTerms())
			{
				builder.append(":");
				builder.append(term.getKey());
				builder.append(":");
				builder.append(term.getValue());
			}
		}

		//URLEncode?
		return builder.toString();
	}
}
