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

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 */
public class SolrSearchQueryEncoderPopulator implements Populator<SolrSearchQueryData, SearchQueryData>
{
	@Override
	public void populate(final SolrSearchQueryData source, final SearchQueryData target)
	{
		final StringBuilder builder = new StringBuilder();

		if (source != null)
		{
			if (StringUtils.isNotBlank(source.getFreeTextSearch()))
			{
				builder.append(source.getFreeTextSearch());
			}

			builder.append(':');

			if (StringUtils.isNotBlank(source.getSort()))
			{
				builder.append(source.getSort());
			}

			final List<SolrSearchQueryTermData> terms = source.getFilterTerms();
			if (terms != null && !terms.isEmpty())
			{
				for (final SolrSearchQueryTermData term : terms)
				{
					if (StringUtils.isNotBlank(term.getKey()) && StringUtils.isNotBlank(term.getValue()))
					{
						builder.append(':').append(term.getKey()).append(':').append(term.getValue());
					}
				}
			}
		}

		final String result = builder.toString();

		// Special case for empty query
		if (":".equals(result))
		{
			target.setValue("");
		}
		else
		{
			target.setValue(result);
		}
	}
}
