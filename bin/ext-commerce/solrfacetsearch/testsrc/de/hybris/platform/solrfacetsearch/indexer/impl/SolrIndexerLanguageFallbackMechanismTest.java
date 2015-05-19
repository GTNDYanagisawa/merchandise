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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.platform.solrfacetsearch.jalo.config.SolrFacetSearchConfig;

import org.junit.Test;


/**
 * Test check how solr indexer behave when fallback language mechanism for {@link SolrFacetSearchConfig} is set
 */
public class SolrIndexerLanguageFallbackMechanismTest extends SolrIndexerLanguageFallbackMechanismSetupTest
{

	@Override
	protected boolean isEnabledFallbackMechanismFlag()
	{
		return true;
	}

	@Test
	public void testSearchNameAndDesriptionFor3Languages() throws Exception
	{
		dropIndex();
		indexerService.performFullIndex(facetSearchConfig);
		setLanguageAndCheckSearchResults("en", PRODUCT_SHOULD_BE_FOUND);
		//product should be found because fallback mechanism is set, despite the fact that for DE lang properties are set to true
		setLanguageAndCheckSearchResults("de", PRODUCT_SHOULD_BE_FOUND);
		//product should not be found because JA language has not set any fallback language
		setLanguageAndCheckSearchResults("ja", PRODUCT_SHOULD_NOT_BE_FOUND);

	}
}
