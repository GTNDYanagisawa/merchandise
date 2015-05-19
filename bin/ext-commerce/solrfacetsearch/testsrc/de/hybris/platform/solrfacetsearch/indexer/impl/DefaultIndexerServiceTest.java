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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.ClusterConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigs;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfigs;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.config.IndexedTypes;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.exceptions.ParameterProviderException;
import de.hybris.platform.solrfacetsearch.indexer.callback.IndexOperationCallback;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.ExporterException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.spi.Exporter;
import de.hybris.platform.solrfacetsearch.indexer.spi.SolrDocumentFactory;
import de.hybris.platform.solrfacetsearch.indexer.strategies.FullIndexStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.RemoveIndexStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.UpdateIndexStrategy;
import de.hybris.platform.solrfacetsearch.provider.IdentityProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;


public class DefaultIndexerServiceTest extends ServicelayerTest
{
	public static final String INDEXED_TYPE_CODE = "Product";

	public final static String FULL_INDEX_QUERY = "SELECT {PK} FROM {Product}";
	public final static String DELETE_INDEX_QUERY = "SELECT {PK} FROM {Product}";
	public final static String UPDATE_INDEX_QUERY = "SELECT {PK} FROM {Product} WHERE {modifiedtime} > ?lastIndexTime";

	private IdentityProvider identityProviderMock; //NOPMD
	private IndexConfig indexConfig;
	private DefaultIndexer indexer;

	private DefaultIndexerService indexerService;

	@Mock
	private BeanFactory mockBeanFactory;

	@Resource
	private ModelService modelService;

	@Resource
	private SolrDocumentFactory solrDocumentFactory;

	@Resource(name = "defaultEmbeddedSolrService")
	private SolrService solrService;

	@Resource
	private FullIndexStrategy fullIndexStrategy;

	@Resource
	private UpdateIndexStrategy updateIndexStrategy;

	@Resource
	private RemoveIndexStrategy removeIndexStrategy;

	private FacetSearchConfig facetSearchConfig;

	private SolrConfig solrConfig;

	private Map<IndexOperation, IndexedTypeFlexibleSearchQuery> flexibleQueriesDataMap;

	private IndexedTypeFlexibleSearchQuery fullFSQData;
	private IndexedTypeFlexibleSearchQuery updateFSQData;
	private IndexedTypeFlexibleSearchQuery deleteFSQData;

	private IndexedType indexType;

	@Before
	public void initTest() throws Exception //NOPMD
	{
		MockitoAnnotations.initMocks(this);
		BDDMockito.when(mockBeanFactory.getBean("fullIndexStrategy")).thenReturn(fullIndexStrategy);
		BDDMockito.when(mockBeanFactory.getBean("updateIndexStrategy")).thenReturn(updateIndexStrategy);
		BDDMockito.when(mockBeanFactory.getBean("removeIndexStrategy")).thenReturn(removeIndexStrategy);
		createCoreData();
		createDefaultCatalog();
		flexibleQueriesDataMap = new HashMap<IndexOperation, IndexedTypeFlexibleSearchQuery>();

		fullFSQData = createIndexedTypeFSQData(FULL_INDEX_QUERY, IndexOperation.FULL);
		updateFSQData = createIndexedTypeFSQData(UPDATE_INDEX_QUERY, IndexOperation.UPDATE);
		deleteFSQData = createIndexedTypeFSQData(DELETE_INDEX_QUERY, IndexOperation.DELETE);

		flexibleQueriesDataMap.put(IndexOperation.FULL, fullFSQData);
		flexibleQueriesDataMap.put(IndexOperation.UPDATE, updateFSQData);
		flexibleQueriesDataMap.put(IndexOperation.DELETE, deleteFSQData);

		identityProviderMock = new IdentityProviderMock();

		// catalogVersions, languages, currencies, identityProvider
		final Collection<CatalogVersionModel> catalogVersions = Collections.emptyList();
		final Collection<LanguageModel> languages = Collections.emptyList();
		final Collection<CurrencyModel> currencies = Collections.emptyList();
		indexType = createIndexType();
		indexConfig = IndexConfigs.createIndexConfig(Collections.singletonList(indexType), catalogVersions, languages, currencies,
				null, 100, 1, false);
		final ClusterConfig clusterConfig = new ClusterConfig();
		clusterConfig.setAliveCheckInterval(Integer.valueOf(100));
		clusterConfig.setConnectionTimeout(Integer.valueOf(100));
		clusterConfig.setReadTimeout(Integer.valueOf(100));

		solrConfig = new SolrConfig();
		solrConfig.setClusterConfig(clusterConfig);
		solrConfig.setMode(SolrServerMode.XML_EXPORT);
		facetSearchConfig = FacetSearchConfigs.createFacetSearchConfig("name", "descriptio", indexConfig, null, solrConfig);
		final XMLExporter xmlExporter = new XMLExporter()
		{
			@Override
			protected void writeToXMLFile(final String exportFullPath, final Object jaxbDocument, final IndexConfig indexConfig,
					final String jaxbContext, final String typeName, final ExportMode exportMode) throws ExporterException
			{
				Assert.assertNotNull(modelService);
				Assert.assertNotNull(jaxbDocument);
				Assert.assertNotNull(indexConfig);
				Assert.assertNotNull(jaxbContext);
				Assert.assertNotNull(typeName);
				Assert.assertNotNull(exportMode);
			}
		};
		indexer = new DefaultIndexer()
		{
			@Override
			protected synchronized Exporter getExporter(final SolrServerMode serverMode) throws IndexerException
			{
				return xmlExporter;
			}
		};
		indexer.setSolrDocumentFactory(solrDocumentFactory);
		indexer.setSolrService(solrService);
		indexerService = new DefaultIndexerService();
		prepareIndexerServiceDependencies(indexerService, indexer);



	}

	/**
	 * @param query
	 * @param type
	 * 
	 */
	private IndexedTypeFlexibleSearchQuery createIndexedTypeFSQData(final String query, final IndexOperation type)
	{
		final IndexedTypeFlexibleSearchQuery fsqData = new IndexedTypeFlexibleSearchQuery();
		fsqData.setQuery(query);
		fsqData.setType(type);
		fsqData.setUserId("admin");
		fsqData.setInjectLastIndexTime(type.equals(IndexOperation.UPDATE));
		fsqData.setParameters(new HashMap<String, Object>());
		return fsqData;
	}

	/**
	 * @param indexer
	 */
	private void prepareIndexerServiceDependencies(final DefaultIndexerService indexerService, final DefaultIndexer indexer)
	{
		fullIndexStrategy.setBeforeOperationOnIndexCallbacks(Collections.<IndexOperationCallback> emptyList());
		fullIndexStrategy.setAfterOperationOnIndexCallbacks(Collections.<IndexOperationCallback> emptyList());

		indexerService.setIndexer(indexer);
		indexerService.setModelService(modelService);
		final Map<String, String> indexStrategiesMapping = new HashMap<String, String>();
		indexStrategiesMapping.put(de.hybris.platform.solrfacetsearch.enums.IndexMode.DIRECT.toString(), "fullIndexStrategy");
		indexStrategiesMapping.put(UpdateIndexStrategy.UPDATE_OPERATION, "updateIndexStrategy");
		indexStrategiesMapping.put(RemoveIndexStrategy.REMOVE_OPERATION, "removeIndexStrategy");
		indexerService.setIndexStrategyIdsMapping(indexStrategiesMapping);
		indexerService.setBeanFactory(mockBeanFactory);

	}

	@Test
	public void testExportAllItemTypes() throws ParameterProviderException, IndexerException //NOPMD
	{
		indexerService.performFullIndex(facetSearchConfig);
	}

	@Test
	public void testExportAllItemTypesIncremental() throws IndexerException //NOPMD
	{
		indexerService.updateIndex(facetSearchConfig);
	}

	@Test
	public void testUpdateAllItemTypesEmbeded() throws IndexerException, SolrServiceException //NOPMD
	{
		final Calendar curCallendar = Calendar.getInstance();
		curCallendar.setTime(new Date());
		curCallendar.add(Calendar.MONTH, -5);

		final ClusterConfig clusterConfig = new ClusterConfig();
		clusterConfig.setAliveCheckInterval(Integer.valueOf(100));
		clusterConfig.setConnectionTimeout(Integer.valueOf(100));
		clusterConfig.setReadTimeout(Integer.valueOf(100));

		solrConfig = new SolrConfig();
		solrConfig.setClusterConfig(clusterConfig);
		solrConfig.setMode(SolrServerMode.XML_EXPORT);
		facetSearchConfig = FacetSearchConfigs.createFacetSearchConfig("name", "descriptio", indexConfig, null, solrConfig);

		final DefaultIndexerService indexerService = new DefaultIndexerService();
		prepareIndexerServiceDependencies(indexerService, indexer);
		indexerService.updateIndex(facetSearchConfig);
	}

	@Test
	public void testUpdatedAllItemTypesEmbeded() throws IndexerException, SolrServiceException //NOPMD
	{
		final Calendar curCallendar = Calendar.getInstance();
		curCallendar.setTime(new Date());

		solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.XML_EXPORT);

		facetSearchConfig = FacetSearchConfigs.createFacetSearchConfig("name", "descriptio", indexConfig, null, solrConfig);


		final DefaultIndexerService indexerService = new DefaultIndexerService();
		prepareIndexerServiceDependencies(indexerService, indexer);
		indexerService.updateIndex(facetSearchConfig);
	}

	@Test
	public void testRemoveAllIndexedDocuments() throws IndexerException //NOPMD
	{
		indexerService.deleteFromIndex(facetSearchConfig);
	}

	private IndexedType createIndexType()
	{
		// indexedProperties, fullIndexQuery, updateIndexQuery, deleteFromIndexQuery
		final Collection<IndexedProperty> indexedPorperties = Collections.emptyList();
		final IndexedType indexedType = IndexedTypes.createIndexedType(null, false, indexedPorperties, flexibleQueriesDataMap,
				"productIdentityProvider", "defaultModelLoader", null, null, INDEXED_TYPE_CODE, null);
		indexedType.setCode(INDEXED_TYPE_CODE);

		return indexedType;
	}

	static class IdentityProviderMock implements IdentityProvider, Serializable
	{
		private static int uniqueCounter;

		@Override
		public String getIdentifier(final IndexConfig config, final Object model)
		{
			final StringBuffer uniqueIdBuf = new StringBuffer("uniqueKey_");
			uniqueIdBuf.append(uniqueCounter++);
			return uniqueIdBuf.toString();
		}
	}
}
