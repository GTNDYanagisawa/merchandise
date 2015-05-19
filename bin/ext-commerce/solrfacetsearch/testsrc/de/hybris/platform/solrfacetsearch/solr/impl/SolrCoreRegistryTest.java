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

import static org.mockito.Mockito.mock;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.solrfacetsearch.model.indexer.SolrIndexedCoresRecordModel;

import java.io.File;

import org.junit.After;
import org.junit.Test;
import org.mockito.BDDMockito;


/**
 *
 */
public class SolrCoreRegistryTest extends ServicelayerTest
{

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrationFromNullCoreInfo()
	{
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(null);
	}

	@Test
	public void testRegistrationFromCoreInfo()
	{
		final SolrIndexedCoresRecordModel record = mock(SolrIndexedCoresRecordModel.class);
		BDDMockito.when(record.getIndexName()).thenReturn(getTestIndexName());
		BDDMockito.when(record.getCurrentIndexDataSubDirectory()).thenReturn(getTestIndexDir());
		BDDMockito.when(record.getTenantId()).thenReturn(Registry.getCurrentTenant().getTenantID());
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(record);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrationFromCoreInfoNullTenantId()
	{
		final SolrIndexedCoresRecordModel record = mock(SolrIndexedCoresRecordModel.class);
		BDDMockito.when(record.getIndexName()).thenReturn(getTestIndexName());
		BDDMockito.when(record.getCurrentIndexDataSubDirectory()).thenReturn(getTestIndexDir());
		BDDMockito.when(record.getTenantId()).thenReturn(null);
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(record);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegistrationFromCoreInfoNullCoreName()
	{
		final SolrIndexedCoresRecordModel record = mock(SolrIndexedCoresRecordModel.class);
		BDDMockito.when(record.getCurrentIndexDataSubDirectory()).thenReturn(getTestIndexDir());
		BDDMockito.when(record.getTenantId()).thenReturn(Registry.getCurrentTenant().getTenantID());
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(record);
	}

	@Test
	public void testRegistrationFromCoreInfoNullIndexDir()
	{
		final SolrIndexedCoresRecordModel record = mock(SolrIndexedCoresRecordModel.class);
		BDDMockito.when(record.getIndexName()).thenReturn(getTestIndexName());
		BDDMockito.when(record.getTenantId()).thenReturn(Registry.getCurrentTenant().getTenantID());
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(record);
	}

	@Test
	public void testRegistrationFromCoreInfoInvalidIndexDir()
	{
		final SolrIndexedCoresRecordModel record = mock(SolrIndexedCoresRecordModel.class);
		BDDMockito.when(record.getIndexName()).thenReturn(getTestIndexName());
		BDDMockito.when(record.getTenantId()).thenReturn(Registry.getCurrentTenant().getTenantID());
		BDDMockito.when(record.getCurrentIndexDataSubDirectory()).thenReturn("bleeeee");
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(record);
	}

	@After
	public void cleanUp()
	{
		SolrCoreRegistry.clearEmbeddedServersForTenant(Registry.getCurrentTenant().getTenantID());
	}

	private String getTestIndexDir()
	{
		return System.getProperty("solr.data.dir") + File.separator + "JUNIT" + File.separator + getTestIndexName();
	}

	private String getTestIndexName()
	{
		return "Core4RegistrationTest";
	}


}
