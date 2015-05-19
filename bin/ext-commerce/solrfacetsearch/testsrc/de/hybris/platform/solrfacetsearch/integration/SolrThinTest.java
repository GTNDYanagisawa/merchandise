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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 * Thin solr test framework - can be used to test simple cases - not integration tests. Setup produces only small amount
 * of data.
 */
public class SolrThinTest extends AbstractSolrIntegrationTest
{

	@Resource
	protected ProductService productService;

	private CatalogVersionModel myCatalogVersion;
	private ProductModel testProduct0;
	private ProductModel testProduct1;


	@Override
	protected void setUpBasic() throws Exception
	{
		createCoreData();
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		createThinCatalog();
		myCatalogVersion = catalogVersionService.getCatalogVersion("my Catalog", "Online");
		testProduct0 = productService.getProductForCode("testProduct0");
		testProduct1 = productService.getProductForCode("testProduct1");
		Assert.assertNotNull(myCatalogVersion);
		Assert.assertNotNull(testProduct0);
		Assert.assertNotNull(testProduct1);
	}

	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Collections.singletonList(myCatalogVersion);
	}

	@Override
	protected String getSolrConfigName()
	{
		return "ThinJunitConfig";
	}

	/**
	 * Test case for issue SNA-264
	 */
	@Test
	public void testSpaceInCatalogId() throws Exception
	{
		//myCatalogVersion -> my catalog:Online
		query.setCatalogVersion(myCatalogVersion);
		final SearchResult result = facetSearchService.search(query);
		final List<ProductModel> products = (List<ProductModel>) result.getResults();
		Assert.assertTrue(products.contains(testProduct0));
		Assert.assertTrue(products.contains(testProduct1));
	}



	protected void createThinCatalog() throws ImpExException
	{
		importCsv("/test/thinSolrConfig.csv", "utf-8");
	}

}
