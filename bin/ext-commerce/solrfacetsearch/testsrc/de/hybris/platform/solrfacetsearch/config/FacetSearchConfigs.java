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

/**
 *
 */
public class FacetSearchConfigs
{
	public static FacetSearchConfig createFacetSearchConfig(final String name, final String description,
			final IndexConfig indexConfig, final SearchConfig searchConfig, final SolrConfig solrConfig)
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setName(name);
		facetSearchConfig.setDescription(description);
		facetSearchConfig.setIndexConfig(indexConfig);
		facetSearchConfig.setSearchConfig(searchConfig);
		facetSearchConfig.setSolrConfig(solrConfig);
		return facetSearchConfig;
	}
}
