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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationStatus;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.model.SolrIndexOperationRecordModel;
import de.hybris.platform.solrfacetsearch.model.indexer.SolrIndexedCoresRecordModel;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexerOperationsDao;

import java.util.Date;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SolrIndexerOperationsDaoTest extends ServicelayerTest
{
	@Resource
	private SolrIndexerOperationsDao solrIndexerOperationsDao;
	@Resource
	private ModelService modelService;

	private SolrIndexedCoresRecordModel coreRecord1;
	private SolrIndexedCoresRecordModel coreRecord2;
	private SolrIndexOperationRecordModel runningFull1;
	private SolrIndexOperationRecordModel failedUpdate2;
	private SolrIndexOperationRecordModel failedFull1;
	private SolrIndexOperationRecordModel failedFull2;
	private SolrIndexOperationRecordModel runningUpdate2b;

	@Before
	public void setUp() throws InterruptedException
	{
		prepareTestData();
	}

	@Test
	public void testFindSingleResult()
	{
		Assert.assertEquals(runningFull1, solrIndexerOperationsDao.findSolrIndexerOperation(coreRecord1,
				IndexerOperationValues.FULL, IndexerOperationStatus.RUNNING));
		Assert.assertEquals(failedUpdate2, solrIndexerOperationsDao.findSolrIndexerOperation(coreRecord2,
				IndexerOperationValues.UPDATE, IndexerOperationStatus.FAILED));
		Assert.assertEquals(failedFull1, solrIndexerOperationsDao.findSolrIndexerOperation(coreRecord1,
				IndexerOperationValues.FULL, IndexerOperationStatus.FAILED));
		Assert.assertEquals(failedFull2, solrIndexerOperationsDao.findSolrIndexerOperation(coreRecord2,
				IndexerOperationValues.FULL, IndexerOperationStatus.FAILED));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testOperationNotFound()
	{
		solrIndexerOperationsDao.findSolrIndexerOperation(coreRecord2, IndexerOperationValues.DELETE,
				IndexerOperationStatus.ABORTED);
	}

	@Test
	public void testAmbiguousOperations()
	{
		Assert.assertEquals(runningUpdate2b, solrIndexerOperationsDao.findSolrIndexerOperation(coreRecord2,
				IndexerOperationValues.UPDATE, IndexerOperationStatus.RUNNING));
	}

	@Test
	public void testGetLastByStatus()
	{
		Assert.assertEquals(failedFull1,
				solrIndexerOperationsDao.findLastSolrIndexOperationByStatus(coreRecord1, IndexerOperationStatus.FAILED));
		Assert.assertEquals(runningFull1,
				solrIndexerOperationsDao.findLastSolrIndexOperationByStatus(coreRecord1, IndexerOperationStatus.RUNNING));
		Assert.assertEquals(failedFull2,
				solrIndexerOperationsDao.findLastSolrIndexOperationByStatus(coreRecord2, IndexerOperationStatus.FAILED));
		Assert.assertEquals(runningUpdate2b,
				solrIndexerOperationsDao.findLastSolrIndexOperationByStatus(coreRecord2, IndexerOperationStatus.RUNNING));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetLastByStatusNotPresent()
	{
		solrIndexerOperationsDao.findLastSolrIndexOperationByStatus(coreRecord1, IndexerOperationStatus.ABORTED);
	}

	private void prepareTestData()
	{
		coreRecord1 = createCoreRecord("core1");
		coreRecord2 = createCoreRecord("core2");
		runningFull1 = createSolrIndexOperationRecordModel(IndexerOperationStatus.RUNNING, IndexerOperationValues.FULL, coreRecord1, generateStartTime(1000L));
		failedUpdate2 = createSolrIndexOperationRecordModel(IndexerOperationStatus.FAILED, IndexerOperationValues.UPDATE, coreRecord2, generateStartTime(2000L));
		failedFull1 = createSolrIndexOperationRecordModel(IndexerOperationStatus.FAILED, IndexerOperationValues.FULL, coreRecord1, generateStartTime(3000L));
		failedFull2 = createSolrIndexOperationRecordModel(IndexerOperationStatus.FAILED, IndexerOperationValues.FULL, coreRecord2, generateStartTime(4000L));
		createSolrIndexOperationRecordModel(IndexerOperationStatus.RUNNING, IndexerOperationValues.UPDATE, coreRecord2, generateStartTime(5000L));
		runningUpdate2b = createSolrIndexOperationRecordModel(IndexerOperationStatus.RUNNING, IndexerOperationValues.UPDATE, coreRecord2, generateStartTime(6000L));
		modelService.saveAll();
	}

	private Date generateStartTime(long millis)
	{
		final Date date = new Date();
		final long time = date.getTime() + millis;
		date.setTime(time);
		return date;
	}

	private SolrIndexOperationRecordModel createSolrIndexOperationRecordModel(final IndexerOperationStatus status,
			final IndexerOperationValues mode, final SolrIndexedCoresRecordModel coreRecord, Date date)
	{
		final SolrIndexOperationRecordModel operationRecord = modelService.create(SolrIndexOperationRecordModel.class);
		operationRecord.setStatus(status);
		operationRecord.setMode(mode);
		operationRecord.setSolrIndexCoreRecord(coreRecord);
		operationRecord.setStartTime(date);
		operationRecord.setThreadId(String.valueOf(Thread.currentThread().getId()));
		operationRecord.setClusterId(0);
		return operationRecord;
	}

	private SolrIndexedCoresRecordModel createCoreRecord(final String coreName)
	{
		final SolrIndexedCoresRecordModel record = modelService.create(SolrIndexedCoresRecordModel.class);
		record.setCoreName(coreName);
		record.setIndexName(coreName);
		return record;
	}
}
