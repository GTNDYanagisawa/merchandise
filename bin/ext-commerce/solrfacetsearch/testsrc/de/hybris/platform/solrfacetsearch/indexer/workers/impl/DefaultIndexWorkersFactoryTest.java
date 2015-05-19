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
package de.hybris.platform.solrfacetsearch.indexer.workers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.indexer.workers.SolrIndexWorker;
import de.hybris.platform.solrfacetsearch.indexer.workers.exceptions.SolrIndexWorkerFactoryException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;



@UnitTest
public class DefaultIndexWorkersFactoryTest
{

	private DefaultIndexWorkersFactory factory;
	private IndexerOperationValues existing;
	private IndexerOperationValues nonExisting;
	private IndexerOperationValues wrongBean;

	private SolrIndexWorker testWorker;

	private Map<String, String> mapping;
	@Mock
	private BeanFactory beanFactory;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		factory = new DefaultIndexWorkersFactory();

		existing = IndexerOperationValues.FULL;
		nonExisting = IndexerOperationValues.DELETE;
		wrongBean = IndexerOperationValues.UPDATE;

		testWorker = new TestWorker();
		mapping = new HashMap<String, String>();
		mapping.put("full", "testWorker");
		mapping.put("update", "wrongBeanName");
		factory.setBeanFactory(beanFactory);
		BDDMockito.when(beanFactory.getBean("testWorker")).thenReturn(testWorker);
		BDDMockito.when(beanFactory.getBean("wrongBeanName")).thenThrow(new NoSuchBeanDefinitionException("wrongBeanName"));
	}

	@Test(expected = SolrIndexWorkerFactoryException.class)
	public void testNoMapping()
	{
		factory.createIndexingWorker(existing);
	}

	@Test(expected = SolrIndexWorkerFactoryException.class)
	public void testCreateNonInitializedNonExisting()
	{
		factory.setWorkerBeanMapping(mapping);
		factory.createIndexingWorker(nonExisting);
	}

	@Test(expected = SolrIndexWorkerFactoryException.class)
	public void testCreateNonInitializedWrongBeanName()
	{
		factory.setWorkerBeanMapping(mapping);
		factory.createIndexingWorker(wrongBean);
	}

	@Test
	public void testCreateNonInitializedExisting()
	{
		factory.setWorkerBeanMapping(mapping);
		final SolrIndexWorker worker = factory.createIndexingWorker(existing);
		Assert.assertNotNull(worker);
		Assert.assertFalse(worker.isInitialized());
	}

	@Test
	public void testCreateInitializedExisting()
	{
		factory.setWorkerBeanMapping(mapping);
		final SolrIndexWorker worker = factory.createIndexingWorker(existing, Collections.EMPTY_MAP);
		Assert.assertNotNull(worker);
		Assert.assertTrue(worker.isInitialized());
	}

	private class TestWorker implements SolrIndexWorker
	{
		private boolean initialized;

		@Override
		public void run()
		{
			// YTODO Auto-generated method stub
		}

		@Override
		public void initialize(final Map<String, Object> indexWorkerContext)
		{
			initialized = true;

		}

		@Override
		public boolean isInitialized()
		{
			return initialized;
		}
	}
}
