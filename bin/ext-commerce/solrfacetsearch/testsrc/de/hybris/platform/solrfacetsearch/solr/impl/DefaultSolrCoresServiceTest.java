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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.SolrCoreNameResolver;
import de.hybris.platform.solrfacetsearch.model.indexer.SolrIndexedCoresRecordModel;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSolrCoresServiceTest
{

	private static final String TENANT_ID = "testTenant";
	private DefaultSolrCoresService solrCoresService;
	@Mock
	private IndexedType indexedType;
	@Mock
	private SolrCoreNameResolver solrCoreNameResolver;

	@Mock
	private Tenant mockTenant;
	@Mock
	private ModelService modelService;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private TypeService typeService;

	private final String indexName = "indexName";



	@Before
	public void setUp() throws Exception
	{
		solrCoresService = new DefaultSolrCoresService();
		MockitoAnnotations.initMocks(this);
		solrCoresService.setSolrCoreNameResolver(solrCoreNameResolver);
		solrCoresService.setCurrentTenant(mockTenant);
		solrCoresService.setModelService(modelService);
		solrCoresService.setFlexibleSearchService(flexibleSearchService);
		solrCoresService.setTypeService(typeService);

		BDDMockito.when(solrCoreNameResolver.getOriginalSolrCoreName(indexedType)).thenReturn(indexName);
		BDDMockito.when(mockTenant.getTenantID()).thenReturn(TENANT_ID);

		solrCoresService.afterPropertiesSet();
	}

	@Test
	public void testCreateNewRecord()
	{
		BDDMockito.when(modelService.create(SolrIndexedCoresRecordModel.class)).thenReturn(new SolrIndexedCoresRecordModel());
		BDDMockito.when(flexibleSearchService.<SolrIndexedCoresRecordModel> search(BDDMockito.any(FlexibleSearchQuery.class)))
				.thenReturn(
						new SearchResultImpl<SolrIndexedCoresRecordModel>(Collections.<SolrIndexedCoresRecordModel> emptyList(), 0, 0,
								0));

		final SolrIndexedCoresRecordModel solrCoreRecord = solrCoresService.createOrUpdateRecord(indexedType);
		Assert.assertEquals(indexName, solrCoreRecord.getIndexName());
		Assert.assertEquals(TENANT_ID + "_" + indexName, solrCoreRecord.getCoreName());
	}

	@Test
	public void testUpdateExistingRecord()
	{
		BDDMockito.when(modelService.create(SolrIndexedCoresRecordModel.class)).thenReturn(new SolrIndexedCoresRecordModel());
		final SolrIndexedCoresRecordModel existingCoreRecord = new SolrIndexedCoresRecordModel();
		BDDMockito
				.when(flexibleSearchService.<SolrIndexedCoresRecordModel> search(BDDMockito.any(FlexibleSearchQuery.class)))
				.thenReturn(new SearchResultImpl<SolrIndexedCoresRecordModel>(Collections.singletonList(existingCoreRecord), 1, 1, 0));

		Assert.assertEquals(existingCoreRecord, solrCoresService.createOrUpdateRecord(indexedType));

	}
}
