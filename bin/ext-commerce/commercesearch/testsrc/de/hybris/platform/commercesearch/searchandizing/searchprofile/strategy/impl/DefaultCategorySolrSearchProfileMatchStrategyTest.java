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
package de.hybris.platform.commercesearch.searchandizing.searchprofile.strategy.impl;


import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.model.CategorySolrSearchProfileModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class DefaultCategorySolrSearchProfileMatchStrategyTest
{
	private final DefaultCategorySolrSearchProfileMatchStrategy categoryGuidedNavConfigMatchStrategy = new DefaultCategorySolrSearchProfileMatchStrategy();

	private static final String OPEN_CATALOG_CATEGORY_CODE = "openCatalog";
	private static final String CAMERAS_CATEGORY_CODE = "cameras";
	private static final String DIGITAL_CAMERAS_CATEGORY_CODE = "digital-cameras";

	private CategoryModel openCatalogCategory;
	private CategoryModel camerasCategory;
	private CategoryModel digitalCamerasCategory;

	private CategorySolrSearchProfileModel camerasNavConfig;
	private CategorySolrSearchProfileModel digitalCamerasNavConfig;

	@Before
	public void setUp()
	{
		openCatalogCategory = new CategoryModel();
		openCatalogCategory.setCode("openCatalog");
		camerasCategory = new CategoryModel();
		camerasCategory.setCode("cameras");
		digitalCamerasCategory = new CategoryModel();
		digitalCamerasCategory.setCode("digital-cameras");

		camerasNavConfig = new CategorySolrSearchProfileModel();
		camerasNavConfig.setCategoryCode(CAMERAS_CATEGORY_CODE);

		digitalCamerasNavConfig = new CategorySolrSearchProfileModel();
		digitalCamerasNavConfig.setCategoryCode(DIGITAL_CAMERAS_CATEGORY_CODE);
	}

	@Test
	public void shouldReturnParentReconfiguration()
	{
		//given
		final List<CategoryModel> categories = Arrays.asList(openCatalogCategory, camerasCategory, digitalCamerasCategory);
		final Collection<CategorySolrSearchProfileModel> configs = Arrays.asList(camerasNavConfig);

		//when
		final CategorySolrSearchProfileModel nearestMatch = categoryGuidedNavConfigMatchStrategy.findNearestMatch(configs,
				Arrays.asList(categories), digitalCamerasCategory);

		//then
		assertThat(nearestMatch).isEqualTo(camerasNavConfig);
	}

	@Test
	public void shouldIgnoreExactReconfigurationWhenAskedForNearest()
	{
		//given
		final List<CategoryModel> categories = Arrays.asList(openCatalogCategory, camerasCategory, digitalCamerasCategory);
		final Collection<CategorySolrSearchProfileModel> configs = Arrays.asList(camerasNavConfig);

		//when
		final CategorySolrSearchProfileModel nearestMatch = categoryGuidedNavConfigMatchStrategy.findNearestMatch(configs,
				Arrays.asList(categories), digitalCamerasCategory);

		//then
		assertThat(nearestMatch).isEqualTo(camerasNavConfig);
	}


	@Test
	public void shouldReturnNullWhenThereIsOnlyExactReconfiguration()
	{
		//given
		final List<CategoryModel> categories = Arrays.asList(digitalCamerasCategory);
		final Collection<CategorySolrSearchProfileModel> configs = Arrays.asList(digitalCamerasNavConfig);

		//when
		final CategorySolrSearchProfileModel nearestMatch = categoryGuidedNavConfigMatchStrategy.findNearestMatch(configs,
				Arrays.asList(categories), digitalCamerasCategory);

		//then
		assertThat(nearestMatch).isNull();
	}


	@Test
	public void shouldReturnNullWhenNearestReconfigurationCannotBeFound()
	{
		//given
		final List<CategoryModel> categories = Arrays.asList(openCatalogCategory, camerasCategory, digitalCamerasCategory);
		final Collection<CategorySolrSearchProfileModel> configs = Collections.EMPTY_LIST;

		//when
		final CategorySolrSearchProfileModel nearestMatch = categoryGuidedNavConfigMatchStrategy.findNearestMatch(configs,
				Arrays.asList(categories), digitalCamerasCategory);

		//then
		assertThat(nearestMatch).isNull();
	}



	@Test
	public void shouldReturnExactMatchWhenReconfigurationExistsForSpecificCategory()
	{
		//given
		final Collection<CategorySolrSearchProfileModel> reconfigs = Arrays.asList(digitalCamerasNavConfig, camerasNavConfig);

		//when
		final CategorySolrSearchProfileModel exactMatch = categoryGuidedNavConfigMatchStrategy.findExactMatch(reconfigs,
				camerasCategory);

		//then
		assertThat(exactMatch).isEqualTo(camerasNavConfig);

	}

	@Test
	public void shouldReturnNullWhenReconfigurationCollectionIsEmpty()
	{
		//given
		final Collection<CategorySolrSearchProfileModel> reconfigs = Collections.EMPTY_LIST;
		final CategoryModel camerasCategory = new CategoryModel();

		//when
		final CategorySolrSearchProfileModel exactMatch = categoryGuidedNavConfigMatchStrategy.findExactMatch(reconfigs,
				camerasCategory);

		//then
		assertThat(exactMatch).isNull();
	}
}
