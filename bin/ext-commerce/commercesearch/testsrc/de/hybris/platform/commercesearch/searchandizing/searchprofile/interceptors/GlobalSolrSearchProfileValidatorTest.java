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
import de.hybris.platform.commercesearch.model.GlobalSolrSearchProfileModel;
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
public class GlobalSolrSearchProfileValidatorTest
{
	private GlobalSolrSearchProfileValidator globalSolrSearchProfileValidator;

	@Mock
	private SolrIndexedTypeModel solrIndexedType01;
	@Mock
	private DefaultSearchProfileDao solrSearchProfileDao;
	@Mock
	private GlobalSolrSearchProfileModel globalProfile;
	@Mock
	private GlobalSolrSearchProfileModel existingGlobalProfile;
	@Mock
	private InterceptorContext ctx;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		globalSolrSearchProfileValidator = new GlobalSolrSearchProfileValidator();
		globalSolrSearchProfileValidator.setSearchProfileDao(solrSearchProfileDao);
	}

	@Test
	public void testCreateGlobalSearchProfile()
	{
		when(globalProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(solrSearchProfileDao.findGlobalSolrSearchProfiles(solrIndexedType01)).thenReturn(Collections.EMPTY_LIST);
		try
		{
			globalSolrSearchProfileValidator.onValidate(globalProfile, ctx);
		}
		catch (final InterceptorException e)
		{
			fail("should have created the global profile instead!");
		}
	}

	@Test
	public void testCreateDuplicatedGlobalSearchProfile()
	{
		when(globalProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(solrIndexedType01.getIdentifier()).thenReturn("identifier");
		when(solrSearchProfileDao.findGlobalSolrSearchProfiles(solrIndexedType01)).thenReturn(
				Collections.singletonList(existingGlobalProfile));
		when(Boolean.valueOf(ctx.isNew(globalProfile))).thenReturn(Boolean.TRUE);

		try
		{
			globalSolrSearchProfileValidator.onValidate(globalProfile, ctx);
			fail("should NEVER create the global profile!");
		}
		catch (final InterceptorException e)
		{
			//expected
		}
	}

	@Test
	public void testUpdateGlobalSearchProfile()
	{
		when(globalProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(solrSearchProfileDao.findGlobalSolrSearchProfiles(solrIndexedType01)).thenReturn(
				Collections.singletonList(globalProfile));
		when(Boolean.valueOf(ctx.isNew(globalProfile))).thenReturn(Boolean.FALSE);

		try
		{
			globalSolrSearchProfileValidator.onValidate(globalProfile, ctx);
		}
		catch (final InterceptorException e)
		{
			fail("should update the global profile without problem!");
		}
	}

	@Test
	public void testUpdateToDuplicateGlobalSearchProfile()
	{
		when(globalProfile.getIndexedType()).thenReturn(solrIndexedType01);
		when(solrIndexedType01.getIdentifier()).thenReturn("identifier");
		when(solrSearchProfileDao.findGlobalSolrSearchProfiles(solrIndexedType01)).thenReturn(
				Collections.singletonList(existingGlobalProfile));
		when(Boolean.valueOf(ctx.isNew(globalProfile))).thenReturn(Boolean.FALSE);

		try
		{
			globalSolrSearchProfileValidator.onValidate(globalProfile, ctx);
			fail("should NEVER update the global profile!");
		}
		catch (final InterceptorException e)
		{
			//expected
		}
	}

}
