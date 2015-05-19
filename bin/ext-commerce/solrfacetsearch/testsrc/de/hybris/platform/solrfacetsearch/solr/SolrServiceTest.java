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
package de.hybris.platform.solrfacetsearch.solr;

import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



public class SolrServiceTest extends AbstractSolrTest
{


	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SolrServiceTest.class.getName());


	@Mock
	private IndexedType indexedType1;
	@Mock
	private IndexedType indexedType2;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		MockitoAnnotations.initMocks(this);
		Mockito.when(indexedType1.getIndexName()).thenReturn("indexedType1");
		Mockito.when(indexedType2.getIndexName()).thenReturn("indexedType2");
	}


	@Test
	public void testSolrService() throws Exception
	{
		// Get an indexed type

		// Verify that we can retrieve a solr server instance
		final SolrServer solrServer1 = getSolrService().getSolrServerMaster(solrConfig, indexedType1);
		Assert.assertNotNull(solrServer1);

		final SolrServer solrServer2 = getSolrService().getSolrServerMaster(solrConfig, indexedType2);
		Assert.assertNotNull(solrServer2);
	}

}
