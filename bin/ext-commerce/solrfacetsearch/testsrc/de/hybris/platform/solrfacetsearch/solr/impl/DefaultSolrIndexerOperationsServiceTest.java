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
package de.hybris.platform.solrfacetsearch.solr.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationStatus;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.model.SolrIndexOperationRecordModel;
import de.hybris.platform.solrfacetsearch.model.indexer.SolrIndexedCoresRecordModel;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexerOperationsService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class DefaultSolrIndexerOperationsServiceTest extends ServicelayerTest
{


	private static final String TEST_CORE_NAME = "testCoreName";
	@Resource(name = "solrIndexerOperationsService")
	private SolrIndexerOperationsService indexOperationsService;
	private SolrIndexedCoresRecordModel indexCoreRecord;

	private SolrIndexOperationRecordModel operation1;
	private SolrIndexOperationRecordModel operation2;
	private SolrIndexOperationRecordModel operation3;
	private SolrIndexOperationRecordModel operation4;
	private SolrIndexOperationRecordModel operation5;
	private SolrIndexOperationRecordModel operation6;
	private SolrIndexOperationRecordModel operation7;
	private SolrIndexOperationRecordModel operation8;
	@Resource
	private ModelService modelService;


	@Before
	public void setUp() throws InterruptedException
	{

		indexCoreRecord = modelService.create(SolrIndexedCoresRecordModel.class);
		indexCoreRecord.setCoreName(TEST_CORE_NAME);
		indexCoreRecord.setIndexName(TEST_CORE_NAME);

		operation1 = createTestIndexerOperation(IndexerOperationStatus.ABORTED, indexCoreRecord);
		operation2 = createTestIndexerOperation(IndexerOperationStatus.FAILED, indexCoreRecord);
		operation3 = createTestIndexerOperation(IndexerOperationStatus.RUNNING, indexCoreRecord);
		operation4 = createTestIndexerOperation(IndexerOperationStatus.SUCCESS, indexCoreRecord);
		operation5 = createTestIndexerOperation(IndexerOperationStatus.FAILED, indexCoreRecord);
		operation6 = createTestIndexerOperation(IndexerOperationStatus.SUCCESS, indexCoreRecord);
		operation7 = createTestIndexerOperation(IndexerOperationStatus.ABORTED, indexCoreRecord);
		operation8 = createTestIndexerOperation(IndexerOperationStatus.FAILED, indexCoreRecord);

		modelService.saveAll();

	}

	private SolrIndexOperationRecordModel createTestIndexerOperation(final IndexerOperationStatus status,
			final SolrIndexedCoresRecordModel record) throws InterruptedException
	{
		Thread.sleep(50);
		final SolrIndexOperationRecordModel operation1 = modelService.create(SolrIndexOperationRecordModel.class);
		operation1.setStatus(status);
		operation1.setThreadId(String.valueOf(Thread.currentThread().getId()));
		operation1.setClusterId(0);
		operation1.setMode(IndexerOperationValues.FULL);
		operation1.setStartTime(new Date());
		operation1.setSolrIndexCoreRecord(record);
		return operation1;
	}

	@Test
	public void testPurgeOldRecords()
	{
		indexOperationsService.purgePreviousOperations(operation1);
		assertThat(indexCoreRecord.getIndexOperations()).contains(operation1, operation2, operation3, operation4, operation5,
				operation6, operation7, operation8);
		indexOperationsService.purgePreviousOperations(operation3);
		assertThat(indexCoreRecord.getIndexOperations()).contains(operation3, operation4, operation5, operation6, operation7,
				operation8);
		indexOperationsService.purgePreviousOperations(operation5);
		assertThat(indexCoreRecord.getIndexOperations()).contains(operation5, operation6, operation7, operation8);
		indexOperationsService.purgePreviousOperations(operation8);
		assertThat(indexCoreRecord.getIndexOperations()).contains(operation8);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPurgeOldRecordsNull()
	{
		indexOperationsService.purgePreviousOperations(null);
	}

}
