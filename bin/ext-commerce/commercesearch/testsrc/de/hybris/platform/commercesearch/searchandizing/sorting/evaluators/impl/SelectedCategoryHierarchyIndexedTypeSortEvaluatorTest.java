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
package de.hybris.platform.commercesearch.searchandizing.sorting.evaluators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.model.AbstractSolrSortConditionModel;
import de.hybris.platform.commercesearch.model.SelectedCategoryHierarchySolrSortConditionModel;
import de.hybris.platform.commercesearch.searchandizing.navigationalstate.NavigationalStateService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class SelectedCategoryHierarchyIndexedTypeSortEvaluatorTest
{
	@InjectMocks
	SelectedCategoryHierarchyIndexedTypeSortEvaluator evaluator = new SelectedCategoryHierarchyIndexedTypeSortEvaluator();

	@Mock
	NavigationalStateService navigationalStateService;

	@Mock
	SelectedCategoryHierarchySolrSortConditionModel condition;

	@Mock
	private CategoryModel mockCategory;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldEvaluateToFalseIfCategoryNotSelected()
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setDefaultSortOrder(Collections.EMPTY_LIST);
		facetSearchConfig.setSearchConfig(searchConfig);
		final SearchQuery query = new SearchQuery(facetSearchConfig, null);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchRequest searchRequest = new SolrSearchRequest();
		searchRequest.setSearchQuery(query);
		searchRequest.setSearchQueryData(searchQueryData);

		given(condition.getRootCategory()).willReturn(mockCategory);
		given(Boolean.valueOf(navigationalStateService.isCategoryOrParentCategorySelected(query, searchQueryData, mockCategory)))
				.willReturn(Boolean.FALSE);

		//when
		final boolean evaluationResult = evaluator.evaluateFilter(searchRequest, null, condition);

		//then
		assertThat(evaluationResult).isTrue();
	}

	@Test
	public void shouldEvaluateToFalseIfCategorySelected()
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setDefaultSortOrder(Collections.EMPTY_LIST);
		facetSearchConfig.setSearchConfig(searchConfig);
		final SearchQuery query = new SearchQuery(facetSearchConfig, null);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchRequest searchRequest = new SolrSearchRequest();
		searchRequest.setSearchQuery(query);
		searchRequest.setSearchQueryData(searchQueryData);

		given(condition.getRootCategory()).willReturn(mockCategory);
		given(Boolean.valueOf(navigationalStateService.isCategoryOrParentCategorySelected(query, searchQueryData, mockCategory)))
				.willReturn(Boolean.TRUE);

		//when
		final boolean evaluationResult = evaluator.evaluateFilter(searchRequest, null, condition);

		//then
		assertThat(evaluationResult).isFalse();
	}

	@Test
	public void shouldEvaluateToFalseIfConditionNotRightType()
	{
		final AbstractSolrSortConditionModel notASelectedCategoryHierarchySolrSortConditionModel = null;
		//when
		final boolean evaluationResult = evaluator.evaluateFilter(null, null, notASelectedCategoryHierarchySolrSortConditionModel);

		//then
		assertThat(evaluationResult).isFalse();
	}
}
