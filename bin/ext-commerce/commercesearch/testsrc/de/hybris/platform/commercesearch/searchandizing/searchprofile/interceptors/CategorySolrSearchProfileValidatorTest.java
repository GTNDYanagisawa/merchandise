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
package de.hybris.platform.commercesearch.searchandizing.searchprofile.interceptors;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercesearch.model.CategorySolrSearchProfileModel;
import de.hybris.platform.commercesearch.searchandizing.searchprofile.dao.impl.DefaultSearchProfileDao;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CategorySolrSearchProfileValidatorTest
{
	private static final String DEFAULT_CATEGORY_CODE = "111";

	private CategorySolrSearchProfileValidator categorySolrSearchProfileValidator;
	@Mock
	private SolrIndexedTypeModel solrIndexedType01;
	@Mock
	private DefaultSearchProfileDao searchProfileDao;
	@Mock
	private CategorySolrSearchProfileModel categoryProfile;
	@Mock
	private CategorySolrSearchProfileModel existingCategoryProfile;
	@Mock
	private InterceptorContext ctx;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		categorySolrSearchProfileValidator = new CategorySolrSearchProfileValidator();
		categorySolrSearchProfileValidator.setSearchProfileDao(searchProfileDao);
	}

	@Test
	public void testCreateCategorySearchProfile()
	{
		when(categoryProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(searchProfileDao.findCategorySolrSearchProfiles(solrIndexedType01, DEFAULT_CATEGORY_CODE)).thenReturn(
				Collections.EMPTY_LIST);

		try
		{
			categorySolrSearchProfileValidator.onValidate(categoryProfile, ctx);
		}
		catch (final InterceptorException e)
		{
			fail("should have created the category profile instead!");
		}
	}

	@Test
	public void testCreateDuplicatedCategorySearchProfile()
	{
		when(categoryProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(categoryProfile.getCategoryCode()).thenReturn(DEFAULT_CATEGORY_CODE);
		when(solrIndexedType01.getIdentifier()).thenReturn("identifier");
		when(searchProfileDao.findCategorySolrSearchProfiles(solrIndexedType01, DEFAULT_CATEGORY_CODE)).thenReturn(
				Collections.singletonList(existingCategoryProfile));
		when(Boolean.valueOf(ctx.isNew(categoryProfile))).thenReturn(Boolean.TRUE);

		try
		{
			categorySolrSearchProfileValidator.onValidate(categoryProfile, ctx);
			fail("should NEVER create the category profile!");
		}
		catch (final InterceptorException e)
		{
			//expected
		}
	}

	@Test
	public void testUpdateCategorySearchProfile()
	{
		when(categoryProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(categoryProfile.getCategoryCode()).thenReturn(DEFAULT_CATEGORY_CODE);
		when(searchProfileDao.findCategorySolrSearchProfiles(solrIndexedType01, DEFAULT_CATEGORY_CODE)).thenReturn(
				Collections.singletonList(categoryProfile));
		when(Boolean.valueOf(ctx.isNew(categoryProfile))).thenReturn(Boolean.FALSE);

		try
		{
			categorySolrSearchProfileValidator.onValidate(categoryProfile, ctx);
		}
		catch (final InterceptorException e)
		{
			fail("should update the category profile without problem!");

		}
	}

	@Test
	public void testUpdateToDuplicateCategorySearchProfile()
	{
		when(categoryProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(categoryProfile.getCategoryCode()).thenReturn(DEFAULT_CATEGORY_CODE);
		when(solrIndexedType01.getIdentifier()).thenReturn("identifier");
		when(searchProfileDao.findCategorySolrSearchProfiles(solrIndexedType01, DEFAULT_CATEGORY_CODE)).thenReturn(
				Collections.singletonList(existingCategoryProfile));
		when(Boolean.valueOf(ctx.isNew(categoryProfile))).thenReturn(Boolean.FALSE);

		try
		{
			categorySolrSearchProfileValidator.onValidate(categoryProfile, ctx);
			fail("should NEVER update the category profile!");
		}
		catch (final InterceptorException e)
		{
			//expected
		}
	}
}
