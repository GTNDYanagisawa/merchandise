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
package de.hybris.platform.commercesearch.searchandizing.searchprofile.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.model.AbstractSolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.CategorySolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.GlobalSolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.SolrBoostRuleModel;
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


import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CategorySearchProfileServiceTest
{
	@Mock
	private CommerceCategoryService commerceCategoryService;
	@Mock
	private SearchProfileDao guidedNavConfigDao;
	@Mock
	private CategorySolrSearchProfileMatchStrategy categoryGuidedNavConfigMatchStrategy;
	@Mock
	private FacetSearchConfigDao facetSearchConfigDao;

	private DefaultCategorySearchProfileService categorySearchProfileService;
	private List<CategorySolrSearchProfileModel> categorySearchProfileModels;

	private CategoryModel camerasCategory;
	private SolrFacetSearchConfigModel facetSearchConfigModel;
	private CategorySolrSearchProfileModel categorySearchProfileModel;
	private Collection<List<CategoryModel>> orphanCategoryPath;

	private CategoryModel leafCategory;
	private CategoryModel middleCategory;
	private CategoryModel rootCategory;
	private CategorySolrSearchProfileModel categorySearchProfile;
	private SolrBoostRuleModel globalBoostRule;
	private Collection<SolrBoostRuleModel> globalBoostRules;
	private GlobalSolrSearchProfileModel globalSearchProfile;
	private SolrBoostRuleModel boostRule;
	private Collection<SolrBoostRuleModel> boostRules;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		categorySearchProfileService = new DefaultCategorySearchProfileService();
		categorySearchProfileService.setCommerceCategoryService(commerceCategoryService);
		categorySearchProfileService.setCategorySearchProfileMatchStrategy(categoryGuidedNavConfigMatchStrategy);
		categorySearchProfileService.setSearchProfileDao(guidedNavConfigDao);
		categorySearchProfileService.setFacetSearchConfigDao(facetSearchConfigDao);

		camerasCategory = new CategoryModel();
		orphanCategoryPath = Arrays.asList(Arrays.asList(camerasCategory));
		categorySearchProfileModel = mock(CategorySolrSearchProfileModel.class);
		categorySearchProfileModels = Arrays.asList(categorySearchProfileModel);

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
		globalSearchProfile = new GlobalSolrSearchProfileModel();
		globalBoostRules = Collections.singletonList(globalBoostRule);
		globalSearchProfile.setBoostRules(globalBoostRules);
	}

	@Test
	public void shouldReturnExactReconfigurationForSpecificCategory()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(orphanCategoryPath);
		given(guidedNavConfigDao.findCategorySolrSearchProfiles(any(SolrIndexedTypeModel.class), anyString()))
				.willReturn(categorySearchProfileModels);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categorySearchProfileModels, camerasCategory)).willReturn(
				categorySearchProfileModel);

		//when
		categorySearchProfileService.getFacetSearchProfileForCategoryAndIndexedType(new IndexedType(), camerasCategory,
				new FacetSearchConfig());

		//then
		verify(categoryGuidedNavConfigMatchStrategy, never()).findNearestMatch(anyCollection(), anyCollection(),
				any(CategoryModel.class));
	}

	@Test
	public void shouldReturnInheritedReconfigurationForSpecificCategory()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(orphanCategoryPath);
		given(guidedNavConfigDao.getSolrFacetReconfigSearchProfiles(anyCollection(), any(SolrIndexedTypeModel.class)))
				.willReturn(categorySearchProfileModels);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categorySearchProfileModels, camerasCategory)).willReturn(null);

		//when
		categorySearchProfileService.getFacetSearchProfileForCategoryAndIndexedType(new IndexedType(), camerasCategory,
				new FacetSearchConfig());

		//then
		verify(categoryGuidedNavConfigMatchStrategy).findNearestMatch(anyCollection(), anyCollection(), any(CategoryModel.class));

	}

	@Test
	public void shouldReturnNullWhenReconfigurationForSpecificCategoryDoesNotExist()
	{
		//given
		given(facetSearchConfigDao.findSolrFacetSearchConfigByName(anyString())).willReturn(facetSearchConfigModel);
		given(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).willReturn(orphanCategoryPath);
		given(guidedNavConfigDao.findCategorySolrSearchProfiles(any(SolrIndexedTypeModel.class), anyString()))
				.willReturn(Collections.EMPTY_LIST);
		given(categoryGuidedNavConfigMatchStrategy.findExactMatch(categorySearchProfileModels, camerasCategory)).willReturn(null);

		//when
		final AbstractSolrSearchProfileModel result = categorySearchProfileService.getFacetSearchProfileForCategoryAndIndexedType(
				new IndexedType(), camerasCategory, new FacetSearchConfig());

		//then
		assertThat(result).isNull();
	}

}
