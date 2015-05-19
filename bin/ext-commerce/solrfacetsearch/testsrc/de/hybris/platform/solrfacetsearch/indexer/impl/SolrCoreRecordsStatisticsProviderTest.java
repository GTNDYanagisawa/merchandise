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

import static org.mockito.BDDMockito.given;

import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationStatus;
import de.hybris.platform.solrfacetsearch.model.SolrIndexOperationRecordModel;
import de.hybris.platform.solrfacetsearch.model.indexer.SolrIndexedCoresRecordModel;
import de.hybris.platform.solrfacetsearch.solr.SolrCoresService;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexerOperationsService;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */
public class SolrCoreRecordsStatisticsProviderTest
{

	private SolrCoreRecordsStatisticsProvider provider;

	@Mock
	private IndexedType alreadyIndexedType;
	@Mock
	private SolrCoresService solrCoresService;
	@Mock
	private IndexedType notYetIndexedType;
	@Mock
	private SolrIndexerOperationsService solrIndexerOperationsService;

	private SolrIndexedCoresRecordModel indexedSolrCoreRecord;

	private SolrIndexedCoresRecordModel notYetIndexedCoreRecord;

	private Date date1;
	private Date date2;


	@Before
	public void setUp() throws InterruptedException
	{
		date1 = new Date();
		Thread.sleep(100);
		date2 = new Date();

		indexedSolrCoreRecord = new SolrIndexedCoresRecordModel();
		final SolrIndexOperationRecordModel operation1 = new SolrIndexOperationRecordModel();
		operation1.setStatus(IndexerOperationStatus.SUCCESS);
		operation1.setStartTime(date1);
		final SolrIndexOperationRecordModel operation2 = new SolrIndexOperationRecordModel();
		operation2.setStatus(IndexerOperationStatus.SUCCESS);
		operation2.setStartTime(date2);
		indexedSolrCoreRecord.setIndexOperations(Arrays.asList(operation1, operation2));

		notYetIndexedCoreRecord = new SolrIndexedCoresRecordModel();

		provider = new SolrCoreRecordsStatisticsProvider();

		MockitoAnnotations.initMocks(this);

		provider.setSolrCoresService(solrCoresService);
		provider.setSolrIndexerOperationsService(solrIndexerOperationsService);
		given(solrIndexerOperationsService.getLastIndexOperationByStatus(indexedSolrCoreRecord, IndexerOperationStatus.SUCCESS))
				.willReturn(operation2);
		given(solrCoresService.getCoreRecordByIndexedType(alreadyIndexedType)).willReturn(indexedSolrCoreRecord);
		given(solrCoresService.getCoreRecordByIndexedType(notYetIndexedType)).willReturn(notYetIndexedCoreRecord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLastIndexTimeNullCase()
	{
		provider.getLastIndexTime(null, null);
	}

	@Test
	public void testGetLastIndexTimeForAlreadyIndexed()
	{
		Assert.assertEquals(date2, provider.getLastIndexTime(null, alreadyIndexedType));
	}

	@Test
	public void testGetLastIndexTimeForNotYetIndexed()
	{
		Assert.assertNull(provider.getLastIndexTime(null, notYetIndexedType));
	}
}
