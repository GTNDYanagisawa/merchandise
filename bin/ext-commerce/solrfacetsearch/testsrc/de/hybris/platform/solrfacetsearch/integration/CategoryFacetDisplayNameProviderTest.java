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

import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


/**
 * Tests the Solr search with name converter support.
 * 
 */
public class CategoryFacetDisplayNameProviderTest extends AbstractSolrIntegrationTest
{


	@Override
	protected void prepareIndexForTest() throws Exception
	{
		dropIndex();
	}

	/**
	 * Creates the index for the hwcatalog_online, searches for the product with code "HW2300-2356", and tests if the
	 * category names are correctly converted.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCategoryFacetDisplayNameProvider() throws Exception
	{
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(hwOnline, classificationVersion));
		indexerService.performFullIndex(facetSearchConfig);

		query.searchInField("code", "HW2300-2356");
		query.setCatalogVersion(hwOnline);
		query.setLanguage("de");
		SearchResult result = facetSearchService.search(query);
		Facet facet = result.getFacet("categoryCode");
		Map<String, String> categoryNames = createGermanNames();
		for (final FacetValue fv : facet.getFacetValues())
		{
			assertEquals(categoryNames.get(fv.getName()), fv.getDisplayName());
		}

		query.setLanguage("en");
		result = facetSearchService.search(query);
		facet = result.getFacet("categoryCode");
		categoryNames = createEnglishNames();
		for (final FacetValue fv : facet.getFacetValues())
		{
			assertEquals(categoryNames.get(fv.getName()), fv.getDisplayName());
		}
	}

	private Map<String, String> createGermanNames()
	{
		final Map<String, String> result = new HashMap<String, String>();
		result.put("HW2000", "Systemkomponenten_online_de");
		result.put("HW2300", "Grafikkarten_online_de");
		result.put("electronics", "Elektronische Geräte");
		result.put("graphics", "Grafikkarten");
		result.put("hardware", "Hardware");
		result.put("specialoffers", "Sonderangebote_online_de");
		return result;
	}

	private Map<String, String> createEnglishNames()
	{
		final Map<String, String> result = new HashMap<String, String>();
		result.put("HW2000", "System components_online_en");
		result.put("HW2300", "Graphic cards_online_en");
		result.put("electronics", "Electronical Goods");
		result.put("graphics", "Graphic cards");
		result.put("hardware", "Hardware");
		result.put("specialoffers", "Special offers_online_en");
		return result;
	}

}
