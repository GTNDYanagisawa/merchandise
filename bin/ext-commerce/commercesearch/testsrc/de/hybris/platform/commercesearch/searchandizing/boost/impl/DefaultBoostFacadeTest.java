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

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.data.SearchProfileData;
import de.hybris.platform.commercesearch.model.CategorySolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.GlobalSolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.SolrBoostRuleModel;
import de.hybris.platform.commercesearch.searchandizing.boost.BoostService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;

/**
 * @author Igor Rohal
 */
public class DefaultBoostFacadeTest
{

	private final String CATEGORY_PROFILE_ID = "selected_category_profile";
	private final String CATEGORY_ID = "selected_category";

	private final String BOOST_PROFILE_ID = "boost_profile";
	private final String BOOST_PROFILE_CATEGORY_ID = "boost_profile_category";
	private final String BOOST_PROFILE_CATEGORY_NAME = "Boost Profile Category";

	private final String GLOBAL_PROFILE_ID = "global_profile";

	@Mock
	private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;
	@Mock
	private FacetSearchConfigService facetSearchConfigService;
	@Mock
	private BoostService boostService;
	@Mock
	private CategoryService categoryService;

	private DefaultBoostFacade defaultBoostFacade;

	@Mock
	FacetSearchConfig facetSearchConfig;
	@Mock
	IndexConfig indexConfig;
	@Mock
	SolrFacetSearchConfigModel solrFacetSearchConfigModel;
	@Mock
	CategorySolrSearchProfileModel categorySearchProfileModel;
	@Mock
	CategorySolrSearchProfileModel boostSearchProfileModel;
	@Mock
	GlobalSolrSearchProfileModel globalSearchProfileModel;
	@Mock
	SolrBoostRuleModel solrBoostRuleModel;
	@Mock
	CategoryModel boostProfileCategory;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultBoostFacade = new DefaultBoostFacade();
		defaultBoostFacade.setCategoryService(categoryService);
		defaultBoostFacade.setBoostService(boostService);
		defaultBoostFacade.setFacetSearchConfigService(facetSearchConfigService);
		defaultBoostFacade.setSolrFacetSearchConfigSelectionStrategy(solrFacetSearchConfigSelectionStrategy);

		given(facetSearchConfigService.getConfiguration(anyString())).willReturn(facetSearchConfig);
		given(facetSearchConfig.getIndexConfig()).willReturn(indexConfig);
		given(indexConfig.getIndexedTypes()).willReturn(new HashMap<String, IndexedType>());
		given(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig()).willReturn(solrFacetSearchConfigModel);
		given(categorySearchProfileModel.getCategoryCode()).willReturn(CATEGORY_ID);
		given(categorySearchProfileModel.getCode()).willReturn(CATEGORY_PROFILE_ID);
		given(categorySearchProfileModel.getBoostRules()).willReturn(Collections.singleton(solrBoostRuleModel));
		given(boostSearchProfileModel.getCategoryCode()).willReturn(BOOST_PROFILE_CATEGORY_ID);
		given(boostSearchProfileModel.getCode()).willReturn(BOOST_PROFILE_ID);
		given(boostProfileCategory.getName()).willReturn(BOOST_PROFILE_CATEGORY_NAME);
		given(globalSearchProfileModel.getCode()).willReturn(GLOBAL_PROFILE_ID);
	}

	@Test
	public void testGetBoostProfileCategoryIdReturnsCorrectCategoryId() throws Exception
	{
		// given
		given(boostService.getBoostProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(categorySearchProfileModel);
		// when
		String boostProfileCategoryId = defaultBoostFacade.getBoostProfileCategoryId(CATEGORY_ID);
		// then
		assertEquals(CATEGORY_ID, boostProfileCategoryId);
	}

	@Test
	public void testGetBoostProfileCategoryIdReturnsNull() throws Exception
	{
		// given
		given(boostService.getBoostProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(globalSearchProfileModel);
		// when
		String boostProfileCategoryId = defaultBoostFacade.getBoostProfileCategoryId(CATEGORY_ID);
		// then
		assertNull(boostProfileCategoryId);
	}

	@Test
	public void testGetSearchProfileDataWhenSearchProfileExistsAndBoostProfileIsDifferent() throws Exception
	{
		// given
		given(boostService.getProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(categorySearchProfileModel);
		given(boostService.getBoostProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(boostSearchProfileModel);
		given(categoryService.getCategoryForCode(eq(BOOST_PROFILE_CATEGORY_ID))).willReturn(boostProfileCategory);

		// when
		SearchProfileData searchProfileData = defaultBoostFacade.getSearchProfileData(CATEGORY_ID);
		// then
		assertNotNull(searchProfileData);
		assertTrue(searchProfileData.getHasOwnSearchProfile());
		assertEquals(CATEGORY_PROFILE_ID, searchProfileData.getSearchProfileId());
		assertEquals(BOOST_PROFILE_ID, searchProfileData.getBoostProfileId());
		assertEquals(BOOST_PROFILE_CATEGORY_NAME, searchProfileData.getBoostProfileCategoryName());
	}

	@Test
	public void testGetSearchProfileDataWhenSearchProfileDoesntExist() throws Exception
	{
		// given
		given(boostService.getProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(null);
		given(boostService.getBoostProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(boostSearchProfileModel);
		given(categoryService.getCategoryForCode(eq(BOOST_PROFILE_CATEGORY_ID))).willReturn(boostProfileCategory);

		// when
		SearchProfileData searchProfileData = defaultBoostFacade.getSearchProfileData(CATEGORY_ID);
		// then
		assertNotNull(searchProfileData);
		assertFalse(searchProfileData.getHasOwnSearchProfile());
		assertNull(searchProfileData.getSearchProfileId());
		assertEquals(BOOST_PROFILE_ID, searchProfileData.getBoostProfileId());
		assertEquals(BOOST_PROFILE_CATEGORY_NAME, searchProfileData.getBoostProfileCategoryName());
	}

	@Test
	public void testGetSearchProfileDataWhenGlobalBoostProfileUsed() throws Exception
	{
		// given
		given(boostService.getProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(null);
		given(boostService.getBoostProfileForCategoryAndIndexedType(any(IndexedType.class), any(CategoryModel.class),
				any(FacetSearchConfig.class))).willReturn(globalSearchProfileModel);
		// when
		SearchProfileData searchProfileData = defaultBoostFacade.getSearchProfileData(CATEGORY_ID);
		// then
		assertEquals(GLOBAL_PROFILE_ID, searchProfileData.getBoostProfileId());
		assertNull(searchProfileData.getBoostProfileCategoryName());
	}

}
