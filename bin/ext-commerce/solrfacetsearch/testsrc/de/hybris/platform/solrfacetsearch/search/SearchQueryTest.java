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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests {@link SearchQuery} class
 */
public class SearchQueryTest
{

	@Mock
	private SearchConfig searchConfig;
	@Mock
	private FacetSearchConfig facetSearchConfig;

	private static final String TEST_PARAM1 = "testParam1";
	private static final String TEST_PARAM2 = "testParam2";
	private SearchQuery searchQuery;

	/**
	 * 
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		when(facetSearchConfig.getSearchConfig()).thenReturn(searchConfig);
		when(searchConfig.getDefaultSortOrder()).thenReturn(Collections.EMPTY_LIST);


		searchQuery = new SearchQuery(facetSearchConfig, null);
	}

	/**
	 * Tests {@link SearchQuery#addSolrParams(String, String...)} for the SNA-273 fix
	 */
	@Test
	public void testAddSolrParams()
	{
		searchQuery.addSolrParams(TEST_PARAM1, "1", "2", "3");

		assertThat(searchQuery.getSolrParams().get(TEST_PARAM1)).isNotNull().containsOnly("1", "2", "3");

		searchQuery.addSolrParams(TEST_PARAM1, "4");

		assertThat(searchQuery.getSolrParams().get(TEST_PARAM1)).isNotNull().containsOnly("1", "2", "3", "4");

		searchQuery.addSolrParams(TEST_PARAM2, "A");

		assertThat(searchQuery.getSolrParams().get(TEST_PARAM1)).isNotNull().containsOnly("1", "2", "3", "4");

		assertThat(searchQuery.getSolrParams().get(TEST_PARAM2)).isNotNull().containsOnly("A");

		searchQuery.addSolrParams(TEST_PARAM2, "B", "C");

		assertThat(searchQuery.getSolrParams().get(TEST_PARAM1)).isNotNull().containsOnly("1", "2", "3", "4");

		assertThat(searchQuery.getSolrParams().get(TEST_PARAM2)).isNotNull().containsOnly("A", "B", "C");

		searchQuery.clearAllFields();

		assertThat(searchQuery.getSolrParams()).isEmpty();
	}

}
