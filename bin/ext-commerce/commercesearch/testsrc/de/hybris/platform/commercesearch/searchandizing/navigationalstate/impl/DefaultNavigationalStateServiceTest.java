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
package de.hybris.platform.commercesearch.searchandizing.navigationalstate.impl;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class DefaultNavigationalStateServiceTest
{
	private static final String CATEGORY_CODE = "CAT-CODE01";

	@InjectMocks
	DefaultNavigationalStateService out = new DefaultNavigationalStateService();

	@Mock
	private CommerceCategoryService commerceCategoryService;

	@Mock
	private CategoryModel mockCategory;

	@Mock
	private CategoryModel mockCategory2;

	@Mock
	private CategoryModel mockCategory3;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldExtractCategoriesThatAreInQuery()
	{
		//given
		given(commerceCategoryService.getCategoryForCode(CATEGORY_CODE)).willReturn(mockCategory);
		given(commerceCategoryService.getCategoryForCode("10kg")).willReturn(mockCategory2);

		final SearchQuery query = createSearchQuery(true);

		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setCategoryCode(CATEGORY_CODE);

		//when
		final List<CategoryModel> categories = out.extractSelectedCategories(query, searchQueryData);

		//then
		assertThat(categories).containsOnly(mockCategory, mockCategory2);
	}

	@Test
	public void shouldReturnTrueIfCategoryIsOnPath()
	{
		//given
		given(commerceCategoryService.getCategoryForCode(CATEGORY_CODE)).willReturn(mockCategory);
		given(commerceCategoryService.getCategoryForCode("10kg")).willReturn(mockCategory2);
		given(commerceCategoryService.getPathsForCategory(mockCategory2)).willReturn(
				singletonList((List<CategoryModel>) newArrayList(mockCategory, mockCategory2)));

		final SearchQuery query = createSearchQuery(true);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setCategoryCode(CATEGORY_CODE);

		//when
		final boolean isSelected = out.isCategoryOrParentCategorySelected(query, searchQueryData, mockCategory);

		//then
		assertThat(isSelected).isTrue();
	}

	@Test
	public void shouldReturnFalseIfCategoryIsNotOnPath()
	{
		//given
		given(commerceCategoryService.getCategoryForCode(CATEGORY_CODE)).willReturn(mockCategory);
		given(commerceCategoryService.getCategoryForCode("10kg")).willReturn(mockCategory2);
		given(commerceCategoryService.getPathsForCategory(mockCategory3)).willReturn(
				singletonList((List<CategoryModel>) newArrayList(mockCategory, mockCategory2)));

		final SearchQuery query = createSearchQuery(true);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setCategoryCode(CATEGORY_CODE);

		//when
		final boolean isSelected = out.isCategoryOrParentCategorySelected(query, searchQueryData, mockCategory3);

		//then
		assertThat(isSelected).isFalse();
	}

	@Test
	public void shouldReturnFalseIfNoCategoriesWereExtractedFromQuery()
	{
		//given

		final SearchQuery query = createSearchQuery(false);
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();

		//when
		final boolean isSelected = out.isCategoryOrParentCategorySelected(query, searchQueryData, mockCategory);

		//then
		assertThat(isSelected).isFalse();
	}


	protected SearchQuery createSearchQuery(final boolean categoryField)
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setDefaultSortOrder(Collections.EMPTY_LIST);
		facetSearchConfig.setSearchConfig(searchConfig);
		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = new HashMap<String, IndexedProperty>();
		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setCategoryField(categoryField);
		indexedProperties.put("weight", indexedProperty);
		indexedType.setIndexedProperties(indexedProperties);
		final SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);
		query.addFacetValue("weight", "10kg");
		return query;
	}
}
