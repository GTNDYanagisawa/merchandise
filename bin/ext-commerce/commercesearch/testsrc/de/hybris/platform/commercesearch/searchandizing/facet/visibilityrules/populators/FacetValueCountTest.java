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
import de.hybris.platform.commercesearch.enums.FacetSelectedState;
import de.hybris.platform.commercesearch.enums.FacetValueCountOperator;
import de.hybris.platform.commercesearch.enums.FacetVisibilityRuleCondition;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.FacetVisibilityRulesService;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.config.AbstractFacetVisibilityRule;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.config.FacetValueCountFacetVisibilityRule;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.evaluators.impl.FacetValueCountFacetVisibilityRuleEvaluator;

import de.hybris.platform.commercesearch.search.solrfacetsearch.populators.FacetVisibilityRulePopulator;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public class FacetValueCountTest
{
	@Test
	public void shouldBeShownIfNoFacetValuesLessThanLimit()
	{
		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;
		final FacetValueCountOperator operator = FacetValueCountOperator.LESS_THAN;
		final Integer count = Integer.valueOf(2);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);
		final FacetValueCountFacetVisibilityRuleEvaluator evaluator = new FacetValueCountFacetVisibilityRuleEvaluator();
		final FacetValueCountFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(evaluator, condition, operator,
				count, selectedStates);

		final ProductSearchPageData searchPageData = new ProductSearchPageData();
		final FacetData facetToTest = new FacetData();


		final FacetValueData<String> fv1 = new FacetValueData<String>();
		fv1.setSelected(true);
		final FacetValueData<String> fv2 = new FacetValueData<String>();
		fv2.setSelected(true);
		final FacetValueData<String> fv3 = new FacetValueData<String>();
		fv3.setSelected(true);

		facetToTest.setValues(Lists.newArrayList(fv1, fv2, fv3));

		final Object cos = null;
		Assertions.assertThat(evaluator.evaluateFilter(cos, searchPageData, rule, facetToTest)).isTrue();
	}

	@Test
	public void shouldBeNotShownIfNoFacetValuesLessThanLimit()
	{
		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;
		final FacetValueCountOperator operator = FacetValueCountOperator.LESS_THAN;
		final Integer count = Integer.valueOf(2);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);
		final FacetValueCountFacetVisibilityRuleEvaluator evaluator = new FacetValueCountFacetVisibilityRuleEvaluator();

		final FacetValueCountFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(evaluator, condition, operator,
				count, selectedStates);


		final ProductSearchPageData searchPageData = new ProductSearchPageData();
		final FacetData facetToTest = new FacetData();


		final FacetValueData<String> fv1 = new FacetValueData<String>();
		fv1.setSelected(true);

		facetToTest.setValues(Lists.newArrayList(fv1));

		final Object cos = null;
		Assertions.assertThat(evaluator.evaluateFilter(cos, searchPageData, rule, facetToTest)).isFalse();
	}

	@Test
	public void shouldReturnFacetIfGreaterThanAndHasCorrectFacetValues()
	{
		//given
		final FacetVisibilityRulePopulator<Object, FacetValueCountFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");
		final IndexedProperty indexedProperty2 = new IndexedProperty();
		indexedProperty2.setName("Color");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);
		indexedProperties.put("Color", indexedProperty2);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;
		final FacetValueCountOperator operator = FacetValueCountOperator.GREATER_THAN;
		final Integer count = Integer.valueOf(1);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);

		final AbstractFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, operator, count, selectedStates);

		resultMap.put(indexedProperty, Lists.newArrayList(rule));
		resultMap.put(indexedProperty2, Lists.newArrayList(rule));
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
		final FacetData<SolrSearchQueryData> facetData2 = new FacetData<SolrSearchQueryData>();

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);

		facetData2.setCode("Color");
		facetData2.setValues(Lists.newArrayList(fv1, fv2));
		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1, facetData2);
		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();
		Assertions.assertThat(target.getFacets()).hasSize(1);
		Assertions.assertThat(target.getFacets()).containsOnly(facetData2);
	}

	@Test
	public void shouldReturnFacetIfLessThanAndHasOnlyNoFacetValues()
	{
		//given
		final FacetVisibilityRulePopulator<Object, FacetValueCountFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");
		final IndexedProperty indexedProperty2 = new IndexedProperty();
		indexedProperty2.setName("Color");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);
		indexedProperties.put("Color", indexedProperty2);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;
		final FacetValueCountOperator operator = FacetValueCountOperator.LESS_THAN;
		final Integer count = Integer.valueOf(1);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);

		final AbstractFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, operator, count, selectedStates);

		resultMap.put(indexedProperty, Lists.newArrayList(rule));
		resultMap.put(indexedProperty2, Lists.newArrayList(rule));
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
		final FacetData<SolrSearchQueryData> facetData2 = new FacetData<SolrSearchQueryData>();

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);

		facetData2.setCode("Color");
		facetData2.setValues(Lists.newArrayList(fv1, fv2));
		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1, facetData2);
		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();
		Assertions.assertThat(target.getFacets()).hasSize(1);
		Assertions.assertThat(target.getFacets()).containsOnly(facetData1);
	}

	@Test
	public void shouldNotReturnFacetIfGreaterThanAndHasOnly1FacetValue()
	{
		//given
		final FacetVisibilityRulePopulator<Object, FacetValueCountFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");
		final IndexedProperty indexedProperty2 = new IndexedProperty();
		indexedProperty2.setName("Color");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);
		indexedProperties.put("Color", indexedProperty2);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;
		final FacetValueCountOperator operator = FacetValueCountOperator.GREATER_THAN;
		final Integer count = Integer.valueOf(1);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);

		final AbstractFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, operator, count, selectedStates);

		resultMap.put(indexedProperty, Lists.newArrayList(rule));
		resultMap.put(indexedProperty2, Lists.newArrayList(rule));
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
		final FacetData<SolrSearchQueryData> facetData2 = new FacetData<SolrSearchQueryData>();

		facetData1.setCode("Category");

		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);

		facetData1.setValues(Lists.newArrayList(fv1));
		facetData2.setCode("Color");
		facetData2.setValues(Lists.newArrayList(fv1, fv2));
		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1, facetData2);
		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();
		Assertions.assertThat(target.getFacets()).hasSize(1);
		Assertions.assertThat(target.getFacets()).containsOnly(facetData2);
	}

	@Test
	public void shouldNotReturnFacetIfGreaterThanAndNotCondition()
	{
		//given
		final FacetVisibilityRulePopulator<Object, FacetValueCountFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");
		final IndexedProperty indexedProperty2 = new IndexedProperty();
		indexedProperty2.setName("Color");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);
		indexedProperties.put("Color", indexedProperty2);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.NOT;
		final FacetValueCountOperator operator = FacetValueCountOperator.GREATER_THAN;
		final Integer count = Integer.valueOf(1);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);

		final AbstractFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, operator, count, selectedStates);

		resultMap.put(indexedProperty, Lists.newArrayList(rule));
		resultMap.put(indexedProperty2, Lists.newArrayList(rule));
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
		final FacetData<SolrSearchQueryData> facetData2 = new FacetData<SolrSearchQueryData>();

		facetData1.setCode("Category");

		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);

		facetData1.setValues(Lists.newArrayList(fv1));
		facetData2.setCode("Color");
		facetData2.setValues(Lists.newArrayList(fv1, fv2));
		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1, facetData2);
		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();
		Assertions.assertThat(target.getFacets()).hasSize(1);
		Assertions.assertThat(target.getFacets()).containsOnly(facetData2);
	}

	@Test
	public void shouldNotReturnFacetIfFulfillsOrRules()
	{
		//given
		final FacetVisibilityRulePopulator<Object, FacetValueCountFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");
		final IndexedProperty indexedProperty2 = new IndexedProperty();
		indexedProperty2.setName("Color");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);
		indexedProperties.put("Color", indexedProperty2);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.OR;
		final FacetValueCountOperator operator = FacetValueCountOperator.LESS_THAN;
		final Integer count = Integer.valueOf(1);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);

		final AbstractFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, FacetValueCountOperator.GREATER_THAN,
				Integer.valueOf(3), selectedStates);

		final FacetValueCountFacetVisibilityRule facetValueCountFacetVisibilityRule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, operator, count, selectedStates);
		facetValueCountFacetVisibilityRule.setEvaluator(new FacetValueCountFacetVisibilityRuleEvaluator());

		resultMap.put(indexedProperty, Lists.newArrayList(rule, facetValueCountFacetVisibilityRule));
		resultMap.put(indexedProperty2, Lists.newArrayList(rule));
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
		final FacetData<SolrSearchQueryData> facetData2 = new FacetData<SolrSearchQueryData>();

		facetData1.setCode("Category");

		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);

		facetData1.setValues(Collections.EMPTY_LIST);
		facetData2.setCode("Color");
		facetData2.setValues(Lists.newArrayList(fv1, fv2));
		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1, facetData2);
		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();
		Assertions.assertThat(target.getFacets()).hasSize(1);
		Assertions.assertThat(target.getFacets()).containsOnly(facetData1);
	}

	@Test
	public void shouldFilterFacetsIfFulfillsOrRules()
	{
		//given
		final FacetVisibilityRulePopulator<Object, FacetValueCountFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName("Category");
		final IndexedProperty indexedProperty2 = new IndexedProperty();
		indexedProperty2.setName("Color");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);
		indexedProperties.put("Color", indexedProperty2);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.OR;
		final FacetValueCountOperator operator = FacetValueCountOperator.GREATER_THAN;
		final Integer count = Integer.valueOf(4);
		final Set<FacetSelectedState> selectedStates = Sets.newHashSet(FacetSelectedState.SELECTED);

		final AbstractFacetVisibilityRule rule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, FacetValueCountOperator.GREATER_THAN,
				Integer.valueOf(3), selectedStates);

		final FacetValueCountFacetVisibilityRule facetValueCountFacetVisibilityRule = new FacetValueCountFacetVisibilityRule(
				new FacetValueCountFacetVisibilityRuleEvaluator(), condition, operator, count, selectedStates);
		facetValueCountFacetVisibilityRule.setEvaluator(new FacetValueCountFacetVisibilityRuleEvaluator());

		resultMap.put(indexedProperty, Lists.newArrayList(rule, facetValueCountFacetVisibilityRule));
		resultMap.put(indexedProperty2, Lists.newArrayList(rule));
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
		final FacetData<SolrSearchQueryData> facetData2 = new FacetData<SolrSearchQueryData>();

		facetData1.setCode("Category");

		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);

		facetData1.setValues(Collections.EMPTY_LIST);
		facetData2.setCode("Color");
		facetData2.setValues(Lists.newArrayList(fv1, fv2));
		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1, facetData2);
		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isEmpty();
	}
}
