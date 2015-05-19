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
package de.hybris.platform.solrfacetsearch.valueprovider.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.List;

import org.junit.Test;


/**
 * 
 */
public class ClassificationPropertyValueProviderTest extends AbstractSolrIntegrationTest
{

	private static final String compact_en = "Digital compact camera";
	private static final String mirror_en = "Digital SLR";

	private static final String compact_de = "Digitale Kompaktkamera";
	private static final String mirror_de = "Digitale Spiegelreflexkamera";


	/**
	 * This case tests proper localization of classification attributes. Related issue: #GEN-180
	 * 
	 * @throws Exception
	 */
	@Test
	public void testClassificationSystemAttributes() throws Exception
	{
		query.setCatalogVersion(hwOnline);
		query.setLanguage("en");
		query.addFacetValue("categoryName", "Photography_online_en");

		final SearchResult result_en = facetSearchService.search(query);
		List<String> typeNames = result_en.getFacet("type").getFacetValueNames();

		assertTrue("Resulting facets has improper names", typeNames.contains(compact_en));
		assertTrue("Resulting facets has improper names", typeNames.contains(mirror_en));

		query.clearAllFields();
		query.setCatalogVersion(hwOnline);
		query.setLanguage("de");
		query.addFacetValue("categoryName", "Fotografie_online_de");

		final SearchResult result_de = facetSearchService.search(query);
		typeNames = result_de.getFacet("type").getFacetValueNames();

		assertTrue("Resulting facets has improper names", typeNames.contains(compact_de));
		assertTrue("Resulting facets has improper names", typeNames.contains(mirror_de));
	}
}
