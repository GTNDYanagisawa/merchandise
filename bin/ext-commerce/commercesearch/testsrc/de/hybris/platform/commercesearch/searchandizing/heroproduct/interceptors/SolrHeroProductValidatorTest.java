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
package de.hybris.platform.commercesearch.searchandizing.heroproduct.interceptors;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.model.SolrHeroProductDefinitionModel;
import de.hybris.platform.commercesearch.searchandizing.heroproduct.dao.impl.DefaultHeroProductDefinitionDao;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SolrHeroProductValidatorTest
{

	private SolrHeroProductValidator solrHeroProductValidator;

	@Mock
	private CategoryModel category01;
	@Mock
	private SolrIndexedTypeModel solrIndexedType01;
	@Mock
	private SolrHeroProductDefinitionModel heroProduct01;
	@Mock
	private DefaultHeroProductDefinitionDao defaultHeroProductDefinitionDao;
	@Mock
	private InterceptorContext ctx;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		solrHeroProductValidator = new SolrHeroProductValidator();
		solrHeroProductValidator.setHeroProductDefinitionDao(defaultHeroProductDefinitionDao);
	}

	@Test
	public void testCreateHeroProductForEmptyCategory()
	{
		when(heroProduct01.getCategory()).thenReturn(category01);
		when(heroProduct01.getIndexedType()).thenReturn(solrIndexedType01);
		when(defaultHeroProductDefinitionDao.findSolrHeroProductDefinitionsByCategory(category01, solrIndexedType01))
				.thenReturn(Collections.EMPTY_LIST);
		when(Boolean.valueOf(ctx.isNew(heroProduct01))).thenReturn(Boolean.TRUE);

		try
		{
			solrHeroProductValidator.onValidate(heroProduct01, ctx);
		}
		catch (final InterceptorException ie)
		{
			fail("should have created the hero product instead!");
		}
	}

	@Test
	public void testCreateDuplicatedHeroProduct()
	{
		when(heroProduct01.getCategory()).thenReturn(category01);
		when(heroProduct01.getIndexedType()).thenReturn(solrIndexedType01);
		when(defaultHeroProductDefinitionDao.findSolrHeroProductDefinitionsByCategory(category01, solrIndexedType01))
				.thenReturn(Collections.singletonList(heroProduct01));
		when(Boolean.valueOf(ctx.isNew(heroProduct01))).thenReturn(Boolean.TRUE);

		try
		{
			solrHeroProductValidator.onValidate(heroProduct01, ctx);
			fail("should have thrown InterceptorException since hero product existed!");
		}
		catch (final InterceptorException ie)
		{
			//expected
		}
	}

	@Test
	public void testUpdateDuplicatedHeroProduct()
	{
		when(heroProduct01.getCategory()).thenReturn(category01);
		when(heroProduct01.getIndexedType()).thenReturn(solrIndexedType01);
		when(defaultHeroProductDefinitionDao.findSolrHeroProductDefinitionsByCategory(category01, solrIndexedType01))
				.thenReturn(Collections.singletonList(heroProduct01));
		when(Boolean.valueOf(ctx.isNew(heroProduct01))).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(ctx.isModified(heroProduct01, SolrHeroProductDefinitionModel.CATEGORY))).thenReturn(Boolean.TRUE);

		try
		{
			solrHeroProductValidator.onValidate(heroProduct01, ctx);
			fail("should have thrown InterceptorException since hero product existed!");
		}
		catch (final InterceptorException ie)
		{
			//expected
		}
	}

}
