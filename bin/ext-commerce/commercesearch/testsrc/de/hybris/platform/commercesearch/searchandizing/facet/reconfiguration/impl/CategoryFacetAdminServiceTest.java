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
package de.hybris.platform.commercesearch.searchandizing.facet.reconfiguration.impl;

import static org.fest.assertions.Assertions.*;

import de.hybris.platform.commercesearch.facet.config.FacetSearchStateData;

import org.junit.Before;
import org.junit.Test;


public class CategoryFacetAdminServiceTest
{

	private static final String CATEGORY_CODE = "categoryCode";
	private CategoryFacetAdminService categoryFacetAdminService = new CategoryFacetAdminService();
	private FacetSearchStateData facetSearchStateData;

	@Before
	public void setUp()
	{
		facetSearchStateData = new FacetSearchStateData();
	}

	@Test
	public void shouldReturnTrueWhenCategoryWasSelected()
	{
		//given
		facetSearchStateData.setSelectedCategoryCode(CATEGORY_CODE);

		//when
		final boolean applicable = categoryFacetAdminService.isApplicable(facetSearchStateData);

		//then
		assertThat(applicable).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenCategoryWasNotSelected()
	{
		//given
		//when
		final boolean applicable = categoryFacetAdminService.isApplicable(facetSearchStateData);

		//then
		assertThat(applicable).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenCategoryCodeIsEmpty()
	{
		//given
		facetSearchStateData.setSelectedCategoryCode("");

		//when
		final boolean applicable = categoryFacetAdminService.isApplicable(facetSearchStateData);

		//then
		assertThat(applicable).isFalse();
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFacetSearchStateDataIsNull()
	{
		//given
		//when
		try
		{
			categoryFacetAdminService.isApplicable(null);
		}
		catch (IllegalArgumentException e)
		{
			//then
			assertThat(e).hasMessage("Facet search state data can not be null");
		}
	}
}
