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
package de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator;

import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.Populator;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SolrSearchQueryDecoderPopulator implements Populator<SearchQueryData, SolrSearchQueryData>
{

	@Override
	public void populate(final SearchQueryData source, final SolrSearchQueryData target)
	{
		if (source != null && source.getValue() != null && !source.getValue().isEmpty())
		{
			final String[] split = source.getValue().split(":");

			if (split.length > 0)
			{
				target.setFreeTextSearch(split[0]);
			}
			if (split.length > 1)
			{
				target.setSort(split[1]);
			}

			final List<SolrSearchQueryTermData> terms = new ArrayList<SolrSearchQueryTermData>();

			for (int i = 2; (i + 1) < split.length; i += 2)
			{
				final SolrSearchQueryTermData termData = new SolrSearchQueryTermData();
				termData.setKey(split[i]);
				termData.setValue(split[i + 1]);
				terms.add(termData);
			}

			target.setFilterTerms(terms);
		}
	}
}
