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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


/**
 *
 */
public class IndexationInLanguageContextTest extends AbstractSolrTest
{

	@Resource
	private FacetSearchService facetSearchService;

	private ProductModel product;

	@Override
	protected void setUpProductData() throws Exception
	{
		super.setUpProductData();
		product = modelService.create(ProductModel.class);

		product.setCode("testId");
		product.setName("EnglishName", Locale.ENGLISH);
		product.setName("DeutchesName", Locale.GERMAN);
		product.setCatalogVersion(cv);
		modelService.save(product);
	}

	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final SolrIndexedPropertyModel nameProperty = modelService.create(SolrIndexedPropertyModel.class);
		nameProperty.setName("name");
		nameProperty.setType(SolrPropertiesTypes.TEXT);
		return Collections.singletonList(nameProperty);
	}

	@Test
	public void testIndexInEnglish() throws IndexerException, SolrServiceException, SolrServerException, IOException,
			FacetSearchException
	{
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		dropIndex();
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);
		query.searchInField("name", "EnglishName");

		SearchResult search = facetSearchService.search(query);
		org.fest.assertions.Assertions.assertThat(search.getResults()).containsOnly(product);

		query.clearAllFields();
		query.searchInField("name", "DeutchesName");

		search = facetSearchService.search(query);
		org.fest.assertions.Assertions.assertThat(search.getResults()).isEmpty();

	}

	@Test
	public void testIndexInGerman() throws IndexerException, SolrServiceException, SolrServerException, IOException,
			FacetSearchException
	{
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("de"));
		dropIndex();
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);
		query.searchInField("name", "EnglishName");

		SearchResult search = facetSearchService.search(query);
		org.fest.assertions.Assertions.assertThat(search.getResults()).isEmpty();

		query.clearAllFields();
		query.searchInField("name", "DeutchesName");

		search = facetSearchService.search(query);
		org.fest.assertions.Assertions.assertThat(search.getResults()).containsOnly(product);


	}

}
