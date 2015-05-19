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
package de.hybris.platform.solrfacetsearch.config.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SolrIndexedPropertyLoadInterceptorTest
{

	private SolrIndexedPropertyLoadInterceptor loadInterceptor;

	private final static String DEFAULT_PROVIDER = "myDefaultProvider";

	@Mock
	private InterceptorContext cntx;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		loadInterceptor = new SolrIndexedPropertyLoadInterceptor();
		loadInterceptor.setDefaultFacetSortProvider(DEFAULT_PROVIDER);
	}

	@Test
	public void testProviderSet() throws InterceptorException
	{
		final SolrIndexedPropertyModel indexedProprty = new SolrIndexedPropertyModel();
		indexedProprty.setCustomFacetSortProvider("specialProvider");

		loadInterceptor.onLoad(indexedProprty, cntx);
		Assertions.assertThat(indexedProprty.getCustomFacetSortProvider()).isEqualTo("specialProvider");
	}

	@Test
	public void testProviderNoSet() throws InterceptorException
	{
		final SolrIndexedPropertyModel indexedProprty = new SolrIndexedPropertyModel();

		loadInterceptor.onLoad(indexedProprty, cntx);
		Assertions.assertThat(indexedProprty.getCustomFacetSortProvider()).isEqualTo(DEFAULT_PROVIDER);
	}
}
