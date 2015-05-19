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
package de.hybris.platform.solrfacetsearch.integration;

import static junit.framework.Assert.assertEquals;

import de.hybris.platform.solrfacetsearch.search.SearchResult;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the Solr search with multi-categories.
 * 
 */
public class SearchInMultiCategoriesTest extends AbstractSolrIntegrationTest
{

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
	}

	/**
	 * Creates the index for the hwcatalog_online, searches for products that belong to category with code "HW1200", and
	 * searches for the products that belong to both "HW1200" and "HW1210".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultiCategoryCodes() throws Exception
	{
		final String queryField = "categoryCode";
		query.setCatalogVersion(hwOnline);
		query.addFacetValue(queryField, "HW1200");
		SearchResult result = facetSearchService.search(query);
		assertEquals(9, result.getTotalNumberOfResults());
		query.addFacetValue(queryField, "HW1210");
		result = facetSearchService.search(query);
		assertEquals(4, result.getTotalNumberOfResults());
	}

	/**
	 * Creates the index for the hwcatalog_online, sets the language to German, searches for products that belong to
	 * category with name "Hardware", searches for the products that belong to both "Hardware" and
	 * "Elektronische Geräte", and at last searches for products that belong to "Hardware", "Elektronische Geräte", and
	 * "Topseller_online_de".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultiCategoryNames() throws Exception
	{
		query.setLanguage("de");
		final String queryField = "categoryName";
		query.setCatalogVersion(hwOnline);
		query.addFacetValue(queryField, "Hardware");
		SearchResult result = facetSearchService.search(query);
		assertEquals(33, result.getTotalNumberOfResults());
		query.addFacetValue(queryField, "\"Elektronische Ger\u00E4te\"");
		result = facetSearchService.search(query);
		assertEquals(33, result.getTotalNumberOfResults());
		query.addFacetValue(queryField, "Topseller_online_de");
		query.addSolrParams("timeAllowed", "5000");
		result = facetSearchService.search(query);
		assertEquals(6, result.getTotalNumberOfResults());
	}

}
