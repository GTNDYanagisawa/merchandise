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
package de.hybris.platform.solrfacetsearch.search.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.solrfacetsearch.enums.ConverterType;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.SolrResultPostProcessor;
import de.hybris.platform.solrfacetsearch.search.product.SolrProductData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * @author wojciech.gruszczyk
 */
public class SolrSearchResultTest extends AbstractSolrIntegrationTest
{



	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final List<SolrIndexedPropertyModel> superIndexedProperties = new ArrayList<SolrIndexedPropertyModel>(super
				.setUpIndexProperties());

		final SolrIndexedPropertyModel categoryProperty = modelService.create(SolrIndexedPropertyModel.class);
		categoryProperty.setFacet(true);
		categoryProperty.setName("category");
		categoryProperty.setType(SolrPropertiesTypes.STRING);
		categoryProperty.setFieldValueProvider("categoryCodeValueProvider");
		categoryProperty.setMultiValue(true);

		superIndexedProperties.add(categoryProperty);
		return superIndexedProperties;
	}

	@Override
	protected SolrIndexedTypeModel setUpIndexedType()
	{
		final SolrIndexedTypeModel indexedType = super.setUpIndexedType();
		indexedType.setSolrResultConverter("defaultSolrProductConverter");
		return indexedType;
	}


	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_STAGED);
	}

	/**
	 * Test method for {@link de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult#getResultData()}.
	 * 
	 * @throws FacetSearchException
	 */
	@Test
	public void testGetResultData() throws FacetSearchException
	{

		final SearchResult res = facetSearchService.search(query);
		final List<SolrProductData> resultData = res.<SolrProductData> getResultData(ConverterType.DEFAULT);
		assertFalse("Empty set not expected", resultData.isEmpty());
		final List<Long> resPk = new ArrayList<Long>();
		for (final SolrProductData data : resultData)
		{
			assertNotNull("Code required", data.getCode());
			assertNotNull("PK required", data.getPk());
			assertFalse("All PK's should be unique in the search", resPk.contains(data.getPk()));
			resPk.add(data.getPk());
			assertFalse(data.getCategories().isEmpty());
			//not Indexed
			assertNull(data.getCatalog());
		}
	}

	@Test
	public void testGetResultDataWithPostProcessors() throws FacetSearchException
	{
		final DefaultFacetSearchService dfss = (DefaultFacetSearchService) facetSearchService;
		final List<SolrResultPostProcessor> initialPostProcessors = dfss.getResultPostProcessors();
		try
		{
			final SolrResultPostProcessor processor1 = new SolrResultPostProcessor()
			{
				@Override
				public SearchResult process(final SearchResult searchResult)
				{
					//sample post processor which returns empty result data list
					final SolrSearchResult input = (SolrSearchResult) searchResult;

					return new SolrSearchResult(//
							input.getNumberOfResults(),//
							input.getFacetMap(), //
							input.getIndexedType(), //
							input.getIdentifiers(), //
							input.getQueryResponse(), //
							input.getPageSize(), //
							input.getOffset(), //
							input.getBreadcrumbs(), //
							input.getQuery(), //
							Collections.EMPTY_LIST);

				}
			};


			SearchResult res = dfss.search(query);
			List<SolrProductData> resultData = res.<SolrProductData> getResultData(ConverterType.DEFAULT);
			assertFalse("Empty set not expected", resultData.isEmpty());

			dfss.setResultPostProcessors(Collections.singletonList(processor1));
			res = dfss.search(query);
			resultData = res.<SolrProductData> getResultData(ConverterType.DEFAULT);
			assertTrue("Empty set expected - forced by post processor", resultData.isEmpty());

		}
		finally
		{
			dfss.setResultPostProcessors(initialPostProcessors);
		}
	}
}
