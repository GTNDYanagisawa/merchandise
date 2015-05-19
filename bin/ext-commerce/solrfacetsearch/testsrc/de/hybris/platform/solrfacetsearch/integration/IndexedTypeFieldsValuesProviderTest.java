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
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.provider.impl.MockupIndexTypeValuesProvider;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collection;

import org.junit.Test;


/**
 * 
 */
public class IndexedTypeFieldsValuesProviderTest extends AbstractSolrIntegrationTest
{

	//config name suitable for this test
	private final static String CONFIG_NAME = "ConfigWithModelValuesProvider";


	@Override
	protected String getSolrConfigName()
	{
		return CONFIG_NAME;
	}


	@Override
	protected SolrIndexedTypeModel setUpIndexedType()
	{
		final SolrIndexedTypeModel indexedType = super.setUpIndexedType();
		indexedType.setValuesProvider("mockupIndexTypeValuesProvider");
		return indexedType;
	}


	@Test
	public void testFieldsFromItemValueProvider() throws Exception
	{
		query.setCatalogVersion(hwOnline);
		query.clearOrderFields();
		//here: the mockup provider returns field 'arbitraryField1_string' = 'top' if product is a topseller 
		query.searchInField(MockupIndexTypeValuesProvider.name + "_string", "TOP");

		final SearchResult result = facetSearchService.search(query);
		final Collection<ProductModel> products = (Collection<ProductModel>) result.getResults();
		checkProductsCategoryByCode(products, "topseller");
	}
}
