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
package de.hybris.platform.solrfacetsearch.indexer.strategies;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FlexibleSearchQuerySpec;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.factories.FlexibleSearchQuerySpecFactory;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.indexer.SolrIndexedTypeCodeResolver;
import de.hybris.platform.solrfacetsearch.indexer.callback.IndexOperationCallback;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.spi.Indexer;
import de.hybris.platform.solrfacetsearch.indexer.workers.IndexWorkersFactory;
import de.hybris.platform.solrfacetsearch.indexer.workers.SolrIndexWorker;
import de.hybris.platform.solrfacetsearch.model.SolrIndexOperationRecordModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.indexer.SolrIndexedCoresRecordModel;
import de.hybris.platform.solrfacetsearch.solr.SolrCoresService;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexerOperationsService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class FullIndexStrategyTest
{

	private static final String TEST_CODE = "testCode";

	@Mock
	private SolrFacetSearchConfigModel solrFacetSearchConfigModel;

	@Mock
	private IndexConfig indexConfig;

	@Mock
	private FacetSearchConfig facetSearchConfig;

	@Mock
	private IndexOperationCallback before;

	@Mock
	private IndexOperationCallback after;

	@Mock
	private IndexedType indexedType;

	@Mock
	private SolrIndexedTypeModel solrIndexedTypeModel;

	@Mock
	private ComposedTypeModel composedTypeModel;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SolrConfig solrConfig;

	@Mock
	private FlexibleSearchQuerySpec querySpec;

	@Mock
	private SolrIndexedTypeCodeResolver solrIndexedTypeCodeResolver;

	private FullIndexStrategy strategy;

	@Mock
	private IndexWorkersFactory indexWorkersFactory;

	@Mock
	private SolrIndexWorker solrIndexerWorker;

	@Mock
	private UserService userService;

	@Mock
	private UserModel user;

	@Mock
	private SolrCoresService solrCoresService;

	@Mock
	private ModelService modelService;

	@Mock
	private SolrIndexedCoresRecordModel solrCoreRecord;

	@Mock
	private SolrIndexerOperationsService solrIndexerOperationsService;

	@Mock
	private FlexibleSearchQuerySpecFactory flexibleSearchQuerySpecFactory;

	private Map<IndexOperation, IndexedTypeFlexibleSearchQuery> flexibleQueries;

	private IndexedTypeFlexibleSearchQuery fullFSQData;

	@Before
	public void setUp() throws IndexerException
	{
		MockitoAnnotations.initMocks(this);
		strategy = new FullIndexStrategy()
		{
			@Override
			protected boolean performOperationOnIndex(final FacetSearchConfig facetSearchConfig,
					final FlexibleSearchQuerySpec querySpec, final IndexConfig indexConfig, final IndexedType indexedType,
					final SolrConfig solrConfig) throws IndexerException
			{
				return false;
			}

			@Override
			protected IndexOperation getIndexerOperation()
			{
				return IndexOperation.FULL;
			}
		};
		fullFSQData = new IndexedTypeFlexibleSearchQuery();
		flexibleQueries = new HashMap<IndexOperation, IndexedTypeFlexibleSearchQuery>();
		flexibleQueries.put(IndexOperation.FULL, fullFSQData);
		facetSearchConfig = mock(FacetSearchConfig.class);
		when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
		when(indexedType.getCode()).thenReturn(TEST_CODE);
		when(indexedType.getUniqueIndexedTypeCode()).thenReturn(TEST_CODE);
		when(indexedType.getFlexibleSearchQueries()).thenReturn(flexibleQueries);
		when(composedTypeModel.getCode()).thenReturn(TEST_CODE);
		when(solrIndexedTypeModel.getType()).thenReturn(composedTypeModel);
		when(querySpec.getUser()).thenReturn("anonymous");
		when(solrFacetSearchConfigModel.getSolrIndexedTypes()).thenReturn(Collections.singletonList(solrIndexedTypeModel));
		when(indexConfig.getIndexedTypes()).thenReturn(Collections.singletonMap(TEST_CODE, indexedType));
		when(facetSearchConfig.getSolrConfig()).thenReturn(solrConfig);
		when(solrConfig.getMode()).thenReturn(SolrServerMode.EMBEDDED);
		when(solrIndexedTypeCodeResolver.resolveIndexedTypeCode(solrIndexedTypeModel)).thenReturn(TEST_CODE);
		when(userService.getUserForUID(any(String.class))).thenReturn(user);
		when(modelService.create(SolrIndexOperationRecordModel.class)).thenReturn(new SolrIndexOperationRecordModel());
		when(solrCoresService.createOrUpdateRecord(indexedType)).thenReturn(solrCoreRecord);
		when(solrIndexerOperationsService.getCurrentlyRunningIndexOperation(solrCoreRecord, IndexerOperationValues.FULL))
				.thenReturn(new SolrIndexOperationRecordModel());
		when(flexibleSearchQuerySpecFactory.createIndexQuery(fullFSQData, indexedType, solrConfig, indexConfig)).thenReturn(
				querySpec);

		strategy.setBeforeOperationOnIndexCallbacks(Collections.singletonList(before));
		strategy.setAfterOperationOnIndexCallbacks(Collections.singletonList(after));
		strategy.setFlexibleSearchService(flexibleSearchService);
		strategy.setIndexer(mock(Indexer.class));
		strategy.setSolrIndexedTypeCodeResolver(solrIndexedTypeCodeResolver);
		strategy.setIndexWorkersFactory(indexWorkersFactory);
		strategy.setUserService(userService);
		strategy.setSolrCoresService(solrCoresService);
		strategy.setModelService(modelService);
		strategy.setSolrIndexerOperationsService(solrIndexerOperationsService);
		strategy.setFlexibleSearchQuerySpecFactory(flexibleSearchQuerySpecFactory);
		when(indexWorkersFactory.createIndexingWorker(any(IndexerOperationValues.class), any(Map.class))).thenReturn(
				solrIndexerWorker);
		stub(flexibleSearchService.getModelByExample(any())).toReturn(solrFacetSearchConfigModel);
	}

	@Test
	public void testExecuteWithOperationCallbacks() throws IndexerException //NOPMD
	{
		strategy.execute(facetSearchConfig);
		final InOrder inOrder = inOrder(before, after);
		inOrder.verify(before).invoke(solrFacetSearchConfigModel, solrIndexedTypeModel);
		inOrder.verify(after).invoke(solrFacetSearchConfigModel, solrIndexedTypeModel);
	}

}
