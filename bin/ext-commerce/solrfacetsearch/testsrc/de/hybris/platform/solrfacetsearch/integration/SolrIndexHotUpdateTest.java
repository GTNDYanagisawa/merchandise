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
package de.hybris.platform.solrfacetsearch.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.indexer.SolrIndexStatisticsProvider;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


/**
 *
 */
public class SolrIndexHotUpdateTest extends AbstractSolrTest
{



	@Resource
	private SolrIndexStatisticsProvider indexStatisticsProvider;

	private ProductModel indexedProduct1;
	private ProductModel indexedProduct2;


	@Override
	protected void setUpProductData() throws Exception
	{
		super.setUpProductData();
		indexedProduct1 = modelService.create(ProductModel.class);
		indexedProduct1.setCode("product1");
		indexedProduct1.setCatalogVersion(cv);

		indexedProduct2 = modelService.create(ProductModel.class);
		indexedProduct2.setCode("product2");
		indexedProduct2.setCatalogVersion(cv);

		modelService.saveAll(indexedProduct1, indexedProduct2);
	}


	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		reindex();
	}

	/**
	 * @throws IndexerException
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws SolrServiceException
	 * 
	 */
	private void reindex() throws IndexerException, SolrServiceException, SolrServerException, IOException
	{
		dropIndex();
		indexerService.performFullIndex(facetSearchConfig);
	}

	@Override
	protected String getIndexedTypeIdentifier()
	{
		return "HotUpdateTest";
	}


	@Test
	public void testHotUpdateNotChangeLastIndexTime() throws IndexerException, InterruptedException
	{
		final Date lastIndexTime1 = indexStatisticsProvider.getLastIndexTime(solrConfig, indexedType);
		assertNotNull(lastIndexTime1);

		// wait for one second since MySQL doesn't store milliseconds !
		Thread.sleep(1000);

		//full index
		indexerService.performFullIndex(facetSearchConfig);
		final Date lastIndexTime2 = indexStatisticsProvider.getLastIndexTime(solrConfig, indexedType);
		assertNotNull(lastIndexTime2);
		//date should change
		assertTrue(lastIndexTime2.after(lastIndexTime1));

		//Hot update of single product!
		indexerService.updateIndex(facetSearchConfig, indexedType, Collections.<ItemModel> singletonList(indexedProduct1));
		final Date lastIndexTime3 = indexStatisticsProvider.getLastIndexTime(solrConfig, indexedType);
		assertNotNull(lastIndexTime3);

		//Date should be the same
		assertEquals(lastIndexTime3, lastIndexTime2);
	}

}
