/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.solrfacetsearch.integration;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test for soft autoCommit behavior. Makes sure that indexed data is visible after maxTime of soft autoCommit elapsed. The
 * opposite behavior (testing that data is not yet visible before maxTime elapsed) cannot be tested because we don't know
 * when Solr commits indexed documents.
 *
 * @author Igor Rohal
 */
public class DataVisibilitySoftAutoCommitTest extends AbstractSolrIntegrationTest
{

	private static final Logger LOG = Logger.getLogger(DataVisibilitySoftAutoCommitTest.class);

	@Test
	public void canSeeNewProductInSolrAfterSoftAutoCommitMaxTime() throws Exception
	{
		// Given
		// - from AbstractSolrIntegrationTest: data is in DB, indexed and committed
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final SearchResult searchResultBefore = facetSearchService.search(query);
		final long itemsCountBefore = searchResultBefore.getTotalNumberOfResults();

		// When
		// - new product is added in DB
		LOG.info("Adding one more product...");
		importCsv("/test/productForAutoCommitTest.csv", "utf-8");
		// - solr update index
		LOG.info("Updating Solr index...");
		indexerService.updateIndex(facetSearchConfig);
		// - waiting more than softAutoCommit maxTime -> currently set to 5 secs
		Thread.sleep(6000);

		// Then
		// - facet search results count is increased
		final SearchResult searchResultAfter = facetSearchService.search(query);
		final long itemsCountAfter = searchResultAfter.getTotalNumberOfResults();
		assertEquals("New results count must be higher by 1.", itemsCountBefore + 1, itemsCountAfter);
	}

}
