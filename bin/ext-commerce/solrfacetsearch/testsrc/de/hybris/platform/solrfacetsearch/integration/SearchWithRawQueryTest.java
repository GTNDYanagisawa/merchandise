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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;
import de.hybris.platform.testframework.Assert;

import java.util.Collection;

import org.junit.Test;


/**
 *
 */
public class SearchWithRawQueryTest extends AbstractSolrIntegrationTest
{

	@Test
	public void testRawSolrQueryShortcut() throws Exception
	{
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query1 = new SearchQuery(facetSearchConfig, indexedType);
		query1.searchInField("code", "HW2300-2356");
		final SearchResult result = facetSearchService.search(query1);
		final Collection<ProductModel> products1 = (Collection<ProductModel>) result.getResults();

		final SearchQuery query2 = new SearchQuery(facetSearchConfig, indexedType);
		query2.addRawQuery("code_string:HW2300-2356", Operator.AND);
		final SearchResult result2 = facetSearchService.search(query2);
		final Collection<ProductModel> products2 = (Collection<ProductModel>) result2.getResults();

		Assert.assertCollection(products1, products2);

		final SearchQuery query3 = new SearchQuery(facetSearchConfig, indexedType);
		query3.setLanguage("en");
		query3.searchInField("name", "Intel Desktop Board");
		query3.searchInField("code", "HW2200-0623");
		query3.searchInField("code", "HW2200-0812", Operator.OR);

		final SearchResult result3 = facetSearchService.search(query3);
		final Collection<ProductModel> products3 = (Collection<ProductModel>) result3.getResults();

		final SearchQuery query4 = new SearchQuery(facetSearchConfig, indexedType);
		query4.addRawQuery("(name_text_en:Intel Desktop Board) AND ((code_string:HW2200-0623) OR (code_string:HW2200-0812))",
				Operator.AND);

		final SearchResult result4 = facetSearchService.search(query4);
		final Collection<ProductModel> products4 = (Collection<ProductModel>) result4.getResults();

		Assert.assertCollection(products3, products4);

		final SearchQuery query5 = new SearchQuery(facetSearchConfig, indexedType);
		query5.searchInField("name", "Intel Desktop Board");
		query5.addRawQuery("((code_string:HW2200-0623) OR (code_string:HW2200-0812))", Operator.AND);

		final SearchResult result5 = facetSearchService.search(query5);
		final Collection<ProductModel> products5 = (Collection<ProductModel>) result5.getResults();

		Assert.assertCollection(products3, products5);




	}
}
