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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;

import org.junit.Test;


/**
 * Class contains set of test cases oriented on fetching search results, where only a few, if any, constraints are given
 * 
 * @author KKW
 */
public class GetAllTest extends AbstractSolrIntegrationTest
{

	/**
	 * Given a set of products then an empty query should return all products and no items of other types
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAllOfType() throws Exception
	{
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		indexerService.performFullIndex(facetSearchConfig);
		final Collection<? extends ItemModel> items = facetSearchService.search(query).getResults();
		for (final ItemModel item : items)
		{
			assertTrue("Only Products are expected in the result", item instanceof ProductModel);
			assertEquals("Only products from session's catalog version are expected in the result", "Online", ((ProductModel) item)
					.getCatalogVersion().getVersion());
		}
	}


	/**
	 * Given a set of products in hwcatalog and catalogVersion : hwcatalog:Staged set then an empty query should return
	 * all products from hwcatalog:Staged and no items of other types
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAllOfTypeInCatalog() throws Exception
	{
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		indexerService.performFullIndex(facetSearchConfig);
		query.setCatalogVersion(hwStaged);
		final Collection<? extends ItemModel> items = facetSearchService.search(query).getResults();
		for (final ItemModel item : items)
		{
			assertTrue("Only Products are expected in the result", item instanceof ProductModel);
			assertEquals("Only products from 'hwcatalog:Staged' are expected in the result", "Staged", ((ProductModel) item)
					.getCatalogVersion().getVersion());
		}
	}



}
