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
package de.hybris.platform.solrfacetsearch.search;

import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;

import java.util.Map;

import junit.framework.Assert;

import org.apache.solr.common.params.FacetParams;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class FacetParamsTest extends AbstractSolrIntegrationTest
{

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
	}

	@Test
	public void testFacetLimitParam() throws Exception
	{
		query.setLanguage("de");
		query.setCatalogVersion(hwOnline);
		query.addSolrParams(FacetParams.FACET_LIMIT, "2");
		final Map<String, String[]> params = query.getSolrParams();
		final String[] strParams = params.get(FacetParams.FACET_LIMIT);
		Assert.assertEquals(1, strParams.length);
		Assert.assertEquals("2", strParams[0]);
		final SearchResult result = facetSearchService.search(query);
		for (final Facet facet : result.getFacets())
		{
			Assert.assertTrue("Number of facet values should be less than or equal 2 due to applied limitation", facet
					.getFacetValues().size() <= 2);
		}
	}

	@Test
	public void testFacetMinCountParam() throws Exception
	{
		query.setLanguage("de");
		query.searchInField("manufacturerName", "EIZO");
		query.setCatalogVersion(hwOnline);
		query.addSolrParams(FacetParams.FACET_MINCOUNT, "0");
		final Map<String, String[]> params = query.getSolrParams();
		final String[] strParams = params.get(FacetParams.FACET_MINCOUNT);
		Assert.assertEquals(1, strParams.length);
		Assert.assertEquals("0", strParams[0]);
		final SearchResult result = facetSearchService.search(query);
		final Facet facet = result.getFacet("manufacturerName");
		final FacetValue HP = facet.getFacetValue("Hewlett-Packard");
		Assert.assertNotNull(HP);
		Assert.assertEquals(0, HP.getCount());
	}

}
