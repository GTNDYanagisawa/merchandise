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
package de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.enums.FacetVisibilityRuleCondition;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.FacetVisibilityRulesService;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.config.AbstractFacetVisibilityRule;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.config.ValueCoverageFacetVisibilityRule;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.evaluators.impl.ValueCoverageFacetVisibilityRuleEvaluator;

import de.hybris.platform.commercesearch.search.solrfacetsearch.populators.FacetVisibilityRulePopulator;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class ValueCoverageFacetVisibilityRuleTest
{
	@Test
	public void shouldNotFilterIfCoverageAboveSpecifiedPercentage()
	{
		//given
		final FacetVisibilityRulePopulator<Object, ValueCoverageFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;

		final AbstractFacetVisibilityRule rule = new ValueCoverageFacetVisibilityRule(
				new ValueCoverageFacetVisibilityRuleEvaluator(), condition, Integer.valueOf(85));

		resultMap.put(indexedProperty, Lists.newArrayList(rule));
		Mockito.when(
				mockFacetVisibilityRulesService.getAllFacetVisibilityRulesForIndexedType(indexedType, facetSearchConfig))
				.thenReturn(resultMap);
		rulePopulator.setFacetVisibilityRulesService(mockFacetVisibilityRulesService);


		final SolrSearchResponse source = new SolrSearchResponse<FacetSearchConfig, IndexedType, IndexedProperty, SearchQuery, IndexedTypeSort, SolrSearchResult>();
		final SolrSearchRequest request = new SolrSearchRequest();
		request.setIndexedType(indexedType);
		request.setFacetSearchConfig(facetSearchConfig);
		source.setRequest(request);
		final ProductCategorySearchPageData<SolrSearchQueryData, Object, CategoryModel> target = new ProductCategorySearchPageData<SolrSearchQueryData, Object, CategoryModel>();
		final FacetData<SolrSearchQueryData> facetData1 = new FacetData<SolrSearchQueryData>();

		final FacetValueData<SolrSearchQueryData> value1 = new FacetValueData<SolrSearchQueryData>();
		value1.setCount(40L);
		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList(value1);
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);
		target.setFacets(facets);
		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(30l);
		target.setPagination(pagination);
		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();
		Assertions.assertThat(target.getFacets()).hasSize(1);
		Assertions.assertThat(target.getFacets()).containsOnly(facetData1);
	}

	@Test
	public void shouldNotFilterIfCoverageBelowSpecifiedPercentage()
	{
		//given
		final FacetVisibilityRulePopulator<Object, ValueCoverageFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;

		final AbstractFacetVisibilityRule rule = new ValueCoverageFacetVisibilityRule(
				new ValueCoverageFacetVisibilityRuleEvaluator(), condition, Integer.valueOf(85));

		resultMap.put(indexedProperty, Lists.newArrayList(rule));
		Mockito.when(
				mockFacetVisibilityRulesService.getAllFacetVisibilityRulesForIndexedType(indexedType, facetSearchConfig))
				.thenReturn(resultMap);
		rulePopulator.setFacetVisibilityRulesService(mockFacetVisibilityRulesService);


		final SolrSearchResponse source = new SolrSearchResponse<FacetSearchConfig, IndexedType, IndexedProperty, SearchQuery, IndexedTypeSort, SolrSearchResult>();
		final SolrSearchRequest request = new SolrSearchRequest();
		request.setIndexedType(indexedType);
		request.setFacetSearchConfig(facetSearchConfig);
		source.setRequest(request);
		final ProductCategorySearchPageData<SolrSearchQueryData, Object, CategoryModel> target = new ProductCategorySearchPageData<SolrSearchQueryData, Object, CategoryModel>();
		final FacetData<SolrSearchQueryData> facetData1 = new FacetData<SolrSearchQueryData>();

		final FacetValueData<SolrSearchQueryData> value1 = new FacetValueData<SolrSearchQueryData>();
		value1.setCount(40L);
		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList(value1);
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);
		target.setFacets(facets);
		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(50l);
		target.setPagination(pagination);
		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isEmpty();
	}
}
