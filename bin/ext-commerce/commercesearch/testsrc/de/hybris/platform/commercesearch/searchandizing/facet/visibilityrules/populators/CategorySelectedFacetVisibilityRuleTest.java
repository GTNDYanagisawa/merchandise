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
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.config.CategorySelectedFacetVisibilityRule;
import de.hybris.platform.commercesearch.searchandizing.facet.visibilityrules.evaluators.impl.SolrCategorySelectedRuleEvaluator;
import de.hybris.platform.commercesearch.search.solrfacetsearch.populators.FacetVisibilityRulePopulator;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class CategorySelectedFacetVisibilityRuleTest
{
	@Test
	public void shouldFilterIfCategoryNotInResult()
	{
		//given
		final FacetVisibilityRulePopulator<Object, CategorySelectedFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
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

		final CategoryModel category1 = Mockito.mock(CategoryModel.class);
		final Collection<CategoryModel> categories = Lists.newArrayList(category1);
		final AbstractFacetVisibilityRule rule = new CategorySelectedFacetVisibilityRule(
				new SolrCategorySelectedRuleEvaluator(), condition, categories);

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

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);


		target.setBreadcrumbs(Collections.EMPTY_LIST);

		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isEmpty();
	}

	@Test
	public void shouldNotFilterIfCategoryInResult()
	{
		//given
		final CategoryModel category1 = Mockito.mock(CategoryModel.class);

		final CommerceCategoryService mockCommerceCategoryService = Mockito.mock(CommerceCategoryService.class);
		Mockito.when(mockCommerceCategoryService.getCategoryForCode("Hardware")).thenReturn(category1);

		final FacetVisibilityRulePopulator<Object, CategorySelectedFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setCategoryField(true);
		indexedProperty.setFacet(true);
		indexedProperty.setName("Category");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;

		final SolrCategorySelectedRuleEvaluator evaluator = new SolrCategorySelectedRuleEvaluator();
		evaluator.setCommerceCategoryService(mockCommerceCategoryService);

		final Collection<CategoryModel> categories = Lists.newArrayList(category1);
		final AbstractFacetVisibilityRule rule = new CategorySelectedFacetVisibilityRule(evaluator, condition, categories);


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

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);


		final BreadcrumbData<SolrSearchQueryData> breadcrumb = new BreadcrumbData<SolrSearchQueryData>();
		breadcrumb.setFacetCode("Category");
		breadcrumb.setFacetValueCode("Hardware");
		final List<BreadcrumbData<SolrSearchQueryData>> breadcrumbs = Lists.newArrayList(breadcrumb);
		target.setBreadcrumbs(breadcrumbs);

		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();

	}

	@Test
	public void shouldNotFilterIfCategoryOnPath()
	{
		//given
		final CategoryModel category1 = Mockito.mock(CategoryModel.class);
		final CategoryModel category2 = Mockito.mock(CategoryModel.class);

		final CommerceCategoryService mockCommerceCategoryService = Mockito.mock(CommerceCategoryService.class);
		Mockito.when(mockCommerceCategoryService.getCategoryForCode("Hardware")).thenReturn(category1);
		Mockito.when(mockCommerceCategoryService.getCategoryForCode("CM-01")).thenReturn(category2);
		Mockito.when(mockCommerceCategoryService.getPathsForCategory(category2)).thenReturn(
				Collections.singleton(((List<CategoryModel>) Lists.newArrayList(category1))));

		final FacetVisibilityRulePopulator<Object, CategorySelectedFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setCategoryField(true);
		indexedProperty.setFacet(true);
		indexedProperty.setName("Category");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;


		final Collection<CategoryModel> categories = Lists.newArrayList(category1);
		final AbstractFacetVisibilityRule rule = new CategorySelectedFacetVisibilityRule(
				new SolrCategorySelectedRuleEvaluator(), condition, categories);
		final SolrCategorySelectedRuleEvaluator evaluator = new SolrCategorySelectedRuleEvaluator();
		evaluator.setCommerceCategoryService(mockCommerceCategoryService);
		rule.setEvaluator(evaluator);

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

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);


		final BreadcrumbData<SolrSearchQueryData> breadcrumb = new BreadcrumbData<SolrSearchQueryData>();
		breadcrumb.setFacetCode("Category");
		breadcrumb.setFacetValueCode("CM-01");
		final List<BreadcrumbData<SolrSearchQueryData>> breadcrumbs = Lists.newArrayList(breadcrumb);
		target.setBreadcrumbs(breadcrumbs);

		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();

	}

	@Test
	public void shouldNotFilterIfNoCategoriesForRuleAreSet()
	{
		//given


		final FacetVisibilityRulePopulator<Object, CategorySelectedFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setCategoryField(true);
		indexedProperty.setFacet(true);
		indexedProperty.setName("Category");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;


		final AbstractFacetVisibilityRule rule = new CategorySelectedFacetVisibilityRule(
				new SolrCategorySelectedRuleEvaluator(), condition, Collections.EMPTY_LIST);
		final SolrCategorySelectedRuleEvaluator evaluator = new SolrCategorySelectedRuleEvaluator();
		rule.setEvaluator(evaluator);

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

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);

		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isNotEmpty();

	}

	@Test
	public void shouldFilterIfCategoryOnPath()
	{
		//given
		final CategoryModel category1 = Mockito.mock(CategoryModel.class);
		final CategoryModel category2 = Mockito.mock(CategoryModel.class);

		final CommerceCategoryService mockCommerceCategoryService = Mockito.mock(CommerceCategoryService.class);
		Mockito.when(mockCommerceCategoryService.getCategoryForCode("Hardware")).thenReturn(category1);
		Mockito.when(mockCommerceCategoryService.getCategoryForCode("CM-01")).thenReturn(category2);
		Mockito.when(mockCommerceCategoryService.getPathsForCategory(category2)).thenReturn(
				Collections.singleton(((List<CategoryModel>) Lists.newArrayList(category2))));

		final FacetVisibilityRulePopulator<Object, CategorySelectedFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setCategoryField(true);
		indexedProperty.setFacet(true);
		indexedProperty.setName("Category");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.AND;
		final SolrCategorySelectedRuleEvaluator evaluator = new SolrCategorySelectedRuleEvaluator();
		evaluator.setCommerceCategoryService(mockCommerceCategoryService);

		final Collection<CategoryModel> categories = Lists.newArrayList(category1);
		final AbstractFacetVisibilityRule rule = new CategorySelectedFacetVisibilityRule(evaluator, condition, categories);


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

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);


		final BreadcrumbData<SolrSearchQueryData> breadcrumb = new BreadcrumbData<SolrSearchQueryData>();
		breadcrumb.setFacetCode("Category");
		breadcrumb.setFacetValueCode("CM-01");
		final List<BreadcrumbData<SolrSearchQueryData>> breadcrumbs = Lists.newArrayList(breadcrumb);
		target.setBreadcrumbs(breadcrumbs);

		target.setCategoryCode("CM-01");
		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).isEmpty();

	}

	@Test
	public void shouldFilterIfCategoryOnPathAndNotCondition()
	{
		//given
		final CategoryModel category1 = Mockito.mock(CategoryModel.class);
		final CategoryModel category2 = Mockito.mock(CategoryModel.class);

		final CommerceCategoryService mockCommerceCategoryService = Mockito.mock(CommerceCategoryService.class);
		Mockito.when(mockCommerceCategoryService.getCategoryForCode("Hardware")).thenReturn(category1);
		Mockito.when(mockCommerceCategoryService.getCategoryForCode("CM-01")).thenReturn(category2);
		Mockito.when(mockCommerceCategoryService.getPathsForCategory(category2)).thenReturn(
				Collections.singleton(((List<CategoryModel>) Lists.newArrayList(category1))));

		final FacetVisibilityRulePopulator<Object, CategorySelectedFacetVisibilityRule> rulePopulator = new FacetVisibilityRulePopulator();
		final FacetVisibilityRulesService mockFacetVisibilityRulesService = Mockito
				.mock(FacetVisibilityRulesService.class);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setCategoryField(true);
		indexedProperty.setFacet(true);
		indexedProperty.setName("Category");


		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = Maps.newHashMap();
		indexedProperties.put("Category", indexedProperty);

		indexedType.setIndexedProperties(indexedProperties);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final Map<IndexedProperty, List<AbstractFacetVisibilityRule>> resultMap = new HashMap<IndexedProperty, List<AbstractFacetVisibilityRule>>();

		final FacetVisibilityRuleCondition condition = FacetVisibilityRuleCondition.NOT;

		final SolrCategorySelectedRuleEvaluator evaluator = new SolrCategorySelectedRuleEvaluator();
		evaluator.setCommerceCategoryService(mockCommerceCategoryService);

		final Collection<CategoryModel> categories = Lists.newArrayList(category1);
		final AbstractFacetVisibilityRule rule = new CategorySelectedFacetVisibilityRule(evaluator, condition, categories);


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

		final List<FacetValueData<SolrSearchQueryData>> values = Lists.newArrayList();
		facetData1.setCode("Category");
		facetData1.setValues(values);
		final FacetValueData<SolrSearchQueryData> fv1 = new FacetValueData<SolrSearchQueryData>();
		fv1.setSelected(true);
		final FacetValueData<SolrSearchQueryData> fv2 = new FacetValueData<SolrSearchQueryData>();
		fv2.setSelected(true);


		final List<FacetData<SolrSearchQueryData>> facets = Lists.newArrayList(facetData1);


		final BreadcrumbData<SolrSearchQueryData> breadcrumb = new BreadcrumbData<SolrSearchQueryData>();
		breadcrumb.setFacetCode("Category");
		breadcrumb.setFacetValueCode("CM-01");
		final List<BreadcrumbData<SolrSearchQueryData>> breadcrumbs = Lists.newArrayList(breadcrumb);
		target.setBreadcrumbs(breadcrumbs);

		target.setFacets(facets);

		//when
		rulePopulator.populate(source, target);

		//then
		Assertions.assertThat(target.getFacets()).containsOnly(facetData1);

	}
}
