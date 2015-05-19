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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.indexer.SolrIndexStatisticsProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSolrIndexStatisticsProviderTest
{

	private DefaultSolrIndexStatisticsProvider compositeProvider;
	private Date date1;
	private Date date3;
	private SolrIndexStatisticsProvider innerProvider1;
	private SolrIndexStatisticsProvider innerProvider2;
	private SolrIndexStatisticsProvider innerProvider3;
	@Mock
	private SolrConfig solrConfig;
	@Mock
	private IndexedType indexType;

	@Before
	public void setUp() throws InterruptedException
	{
		MockitoAnnotations.initMocks(this);
		compositeProvider = new DefaultSolrIndexStatisticsProvider();
		date1 = new Date();
		Thread.sleep(10);
		date3 = new Date();
		innerProvider1 = new SolrIndexStatisticsProvider()
		{
			@Override
			public Date getLastIndexTime(final SolrConfig solrConfig, final IndexedType indexType)
			{
				return date1;
			}
		};

		innerProvider2 = new SolrIndexStatisticsProvider()
		{
			@Override
			public Date getLastIndexTime(final SolrConfig solrConfig, final IndexedType indexType)
			{
				return null;
			}
		};

		innerProvider3 = new SolrIndexStatisticsProvider()
		{
			@Override
			public Date getLastIndexTime(final SolrConfig solrConfig, final IndexedType indexType)
			{
				return date3;
			}
		};
	}



	@Test(expected = ConfigurationException.class)
	public void testEmptyProvidersList()
	{
		compositeProvider.setProviders(Collections.<SolrIndexStatisticsProvider> emptyList());
		compositeProvider.getLastIndexTime(solrConfig, indexType);
	}

	@Test
	public void testOneProviderReturningNull()
	{
		compositeProvider.setProviders(Collections.singletonList(innerProvider2));
		Assert.assertEquals(new Date(0l), compositeProvider.getLastIndexTime(solrConfig, indexType));
	}

	@Test
	public void testTwoProvidersReturningNull()
	{
		compositeProvider.setProviders(Arrays.asList(innerProvider2, innerProvider2));
		Assert.assertEquals(new Date(0l), compositeProvider.getLastIndexTime(solrConfig, indexType));
	}

	@Test
	public void testTwoProvidersNullLast()
	{
		compositeProvider.setProviders(Arrays.asList(innerProvider1, innerProvider2));
		Assert.assertEquals(date1, compositeProvider.getLastIndexTime(solrConfig, indexType));
	}

	@Test
	public void testTwoProvidersNullFirst()
	{
		compositeProvider.setProviders(Arrays.asList(innerProvider2, innerProvider1));
		Assert.assertEquals(date1, compositeProvider.getLastIndexTime(solrConfig, indexType));
	}

	@Test
	public void testTwoProviders1()
	{
		compositeProvider.setProviders(Arrays.asList(innerProvider1, innerProvider3));
		Assert.assertEquals(date1, compositeProvider.getLastIndexTime(solrConfig, indexType));
	}

	@Test
	public void testTwoProviders2()
	{
		compositeProvider.setProviders(Arrays.asList(innerProvider3, innerProvider1));
		Assert.assertEquals(date3, compositeProvider.getLastIndexTime(solrConfig, indexType));
	}
}
