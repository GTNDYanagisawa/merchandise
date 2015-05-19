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
package de.hybris.platform.commercesearch.searchandizing.boost.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.model.AbstractSolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.CategorySolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.GlobalSolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.SolrBoostRuleModel;
import de.hybris.platform.commercesearch.searchandizing.searchprofile.CategorySearchProfileService;
import de.hybris.platform.commercesearch.searchandizing.searchprofile.GlobalSearchProfileService;
import de.hybris.platform.commercesearch.searchandizing.searchprofile.dao.SearchProfileDao;
import de.hybris.platform.commercesearch.searchandizing.searchprofile.strategy.CategorySolrSearchProfileMatchStrategy;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.impl.FacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;

@UnitTest
public class DefaultBoostServiceTest
{
	@Mock
	private CommerceCategoryService commerceCategoryService;
	@Mock
	private GlobalSearchProfileService globalSearchProfileService;
	@Mock
	private SearchProfileDao guidedNavConfigDao;
	@Mock
	private CategorySolrSearchProfileMatchStrategy categoryGuidedNavConfigMatchStrategy;
	@Mock
	private FacetSearchConfigDao facetSearchConfigDao;
	@Mock
	private CategorySearchProfileService categorySearchProfileService;

	private DefaultBoostService defaultBoostService;

	private SolrFacetSearchConfigModel facetSearchConfigModel;

	private CategoryModel leafCategory;
	private CategoryModel middleCategory;
	private CategoryModel rootCategory;
	private Collection<List<CategoryModel>> categoryPath;
	private CategorySolrSearchProfileModel categorySearchProfile;
	private List<CategorySolrSearchProfileModel> categoryProfiles;
	private SolrBoostRuleModel globalBoostRule;
	private Collection<SolrBoostRuleModel> globalBoostRules;
	private GlobalSolrSearchProfileModel globalSearchProfile;
	private SolrBoostRuleModel boostRule;
	private Collection<SolrBoostRuleModel> boostRules;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultBoostService = new DefaultBoostService();
		defaultBoostService.setCommerceCategoryService(commerceCategoryService);
		defaultBoostService.setGlobalSearchProfileService(globalSearchProfileService);
		defaultBoostService.setCategorySearchProfileMatchStrategy(categoryGuidedNavConfigMatchStrategy);
		defaultBoostService.setSearchProfileDao(guidedNavConfigDao);
		defaultBoostService.setFacetSearchConfigDao(facetSearchConfigDao);
		defaultBoostService.setCategorySearchProfileService(categorySearchProfileService);

		facetSearchConfigModel = mock(SolrFacetSearchConfigModel.class);

		leafCategory = new CategoryModel();
		leafCategory.setCode("leaf");
		middleCategory = new CategoryModel();
		middleCategory.setCode("middle");
		rootCategory = new CategoryModel();
		rootCategory.setCode("root");
		boostRule = new SolrBoostRuleModel();
		globalBoostRule = new SolrBoostRuleModel();
		boostRules = Collections.singleton(boostRule);
		categorySearchProfile = new CategorySolrSearchProfileModel();
		categorySearchProfile.setBoostRules(boostRules);
		categoryPath = Arrays.asList(Arrays.asList(rootCategory, middleCategory, leafCategory));
		categoryProfiles = Collections.singletonList(categorySearchProfile);
		globalSearchProfile = new GlobalSolrSearchProfileModel();
		globalBoostRules = Collections.singletonList(globalBoostRule);
		globalSearchProfile.setBoostRules(globalBoostRules);
	}

	@Test
	public void shouldReturnLeafCategoryProfileBoostRules()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(categoryPath);
		given(guidedNavConfigDao.getSolrBoostSearchProfiles(anyCollection(), any(SolrIndexedTypeModel.class))).willReturn(
				categoryProfiles);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles,
				leafCategory)).willReturn(categorySearchProfile);

		//when
		final Collection<SolrBoostRuleModel> result = defaultBoostService.getBoostRulesForCategoryAndIndexedType(
				new IndexedType(), leafCategory, new FacetSearchConfig());
		assertEquals(result, boostRules);
	}

	@Test
	public void shouldReturnMiddleCategoryProfileBoostRules()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(categoryPath);
		given(guidedNavConfigDao.getSolrBoostSearchProfiles(anyCollection(), any(SolrIndexedTypeModel.class))).willReturn(
				categoryProfiles);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, leafCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, middleCategory)).willReturn(
				categorySearchProfile);
		given(categoryGuidedNavConfigMatchStrategy.findNearestMatch(anyCollection(), eq(categoryPath), eq(leafCategory)))
				.willReturn(categorySearchProfile);

		//when
		final Collection<SolrBoostRuleModel> result = defaultBoostService.getBoostRulesForCategoryAndIndexedType(
				new IndexedType(), leafCategory, new FacetSearchConfig());
		assertEquals(result, boostRules);
	}

	@Test
	public void shouldReturnRootCategoryProfileBoostRules()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(categoryPath);
		given(guidedNavConfigDao.getSolrBoostSearchProfiles(anyCollection(), any(SolrIndexedTypeModel.class))).willReturn(
				categoryProfiles);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, leafCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, middleCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles,
				rootCategory)).willReturn(categorySearchProfile);
		given(categoryGuidedNavConfigMatchStrategy.findNearestMatch(anyCollection(), eq(categoryPath), eq(leafCategory)))
				.willReturn(categorySearchProfile);

		//when
		final Collection<SolrBoostRuleModel> result = defaultBoostService.getBoostRulesForCategoryAndIndexedType(
				new IndexedType(), leafCategory, new FacetSearchConfig());
		assertEquals(result, boostRules);
	}

	@Test
	public void shouldReturnGlobalProfileBoostRules()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(categoryPath);
		given(guidedNavConfigDao.getSolrBoostSearchProfiles(anyCollection(), any(SolrIndexedTypeModel.class))).willReturn(
				categoryProfiles);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, leafCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, middleCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, rootCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findNearestMatch(anyCollection(), eq(categoryPath), eq(leafCategory)))
				.willReturn(null);
		given(globalSearchProfileService.getGlobalSolrSearchProfileForIndexedType(any(IndexedType.class),
				any(FacetSearchConfig.class))).willReturn(globalSearchProfile);

		//when
		final Collection<SolrBoostRuleModel> result = defaultBoostService.getBoostRulesForCategoryAndIndexedType(
				new IndexedType(), leafCategory, new FacetSearchConfig());
		assertEquals(result, globalBoostRules);
	}

	@Test
	public void shouldReturnEmptyListProfileBoostRules()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(categoryPath);
		given(guidedNavConfigDao.getSolrBoostSearchProfiles(anyCollection(), any(SolrIndexedTypeModel.class))).willReturn(
				categoryProfiles);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, leafCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, middleCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categoryProfiles, rootCategory)).willReturn(null);
		given(categoryGuidedNavConfigMatchStrategy.findNearestMatch(anyCollection(), eq(categoryPath), eq(leafCategory)))
				.willReturn(null);
		given(guidedNavConfigDao.findGlobalSolrSearchProfiles(any(SolrIndexedTypeModel.class))).willReturn(
				Collections.EMPTY_LIST);

		//when
		final Collection<SolrBoostRuleModel> result = defaultBoostService.getBoostRulesForCategoryAndIndexedType(
				new IndexedType(), leafCategory, new FacetSearchConfig());
		assertEquals(result, Collections.EMPTY_LIST);
	}

	@Test
	public void shouldReturnNullIfProfileDoesNotExist()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(guidedNavConfigDao.findCategorySolrSearchProfiles(any(SolrIndexedTypeModel.class),
				eq(leafCategory.getCode()))).willReturn(Collections.EMPTY_LIST);
		given(categorySearchProfileService.getCategorySolrSearchProfile(eq(leafCategory.getCode()), any(SolrIndexedTypeModel.class))).willReturn(null);

		//when
		final AbstractSolrSearchProfileModel result = defaultBoostService.getProfileForCategoryAndIndexedType(
				new IndexedType(), leafCategory, new FacetSearchConfig());
		assertEquals(null, result);
	}

	@Test
	public void shouldReturnCategoryProfileIfExists()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(guidedNavConfigDao.findCategorySolrSearchProfiles(any(SolrIndexedTypeModel.class),
				eq(leafCategory.getCode()))).willReturn(categoryProfiles);
		given(categorySearchProfileService.getCategorySolrSearchProfile(eq(leafCategory.getCode()), any(SolrIndexedTypeModel.class))).willReturn(categorySearchProfile);

		//when
		final AbstractSolrSearchProfileModel result = defaultBoostService.getProfileForCategoryAndIndexedType(
				new IndexedType(), leafCategory, new FacetSearchConfig());
		assertEquals(categorySearchProfile, result);
	}

	@Test
	public void shouldReturnGlobalProfileIfCategoryNotProvided()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(globalSearchProfileService.getGlobalSolrSearchProfileForIndexedType(any(IndexedType.class),
				any(FacetSearchConfig.class))).willReturn(globalSearchProfile);

		//when
		final AbstractSolrSearchProfileModel result = defaultBoostService.getProfileForCategoryAndIndexedType(
				new IndexedType(), null, new FacetSearchConfig());
		assertEquals(globalSearchProfile, result);
	}

}
