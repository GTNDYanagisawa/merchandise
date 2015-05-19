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

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.solrfacetsearch.config.ClusterConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.enums.IndexMode;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.test.RunnerCreator;
import de.hybris.platform.test.TestThreadsHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * This Test checks the scenario of multi-threaded indexation.
 */
@IntegrationTest
public class MultithreadedIndexerServiceAccessTest extends AbstractSolrTest
{
	private static final int NO_OF_THREADS = 10;
	private static final int NO_OF_PRODUCTS_IN_CATALOG = 10;
	private static final String TEST_CONFIG_NAME = "TestConfigFORTHREAD";
	private static final String CATALOG_ID = "CatalogForMultiThreadTest";
	private static final String PRODUCTCODE_PREFIX = "TestProduct:";
	private static final String TEST_USER = "testUser123";

	@Resource
	private FacetSearchService facetSearchService;
	@Resource
	private UserService userService;

	private Map<Integer, FacetSearchConfig> facetSearchConfigs;
	private Map<Integer, IndexedType> indexedTypes;
	private List<CatalogVersionModel> testCatalogVersions;
	private CatalogModel testCatalog;
	volatile public List<Exception> errorTurns;


	@Override
	@Before
	public void setUp()
	{
		createTestUser();
		createCatalog();
		createCatalogVersions(NO_OF_THREADS);
		createSolrFacetSearchConfigs(NO_OF_THREADS);

		modelService.saveAll();
		errorTurns = new ArrayList<Exception>();
		facetSearchConfigs = new HashMap<Integer, FacetSearchConfig>(NO_OF_THREADS);
		indexedTypes = new HashMap<Integer, IndexedType>(NO_OF_THREADS);
	}




	@Test
	public void testMultithreadedAccess() throws FacetSearchException
	{

		final CountDownLatch solrOperationLatch = new CountDownLatch(NO_OF_THREADS);
		final RunnerCreator<IndexerServiceRunner> runnerCreator = new IndexerServiceRunnerCreator(solrOperationLatch);
		final TestThreadsHolder<IndexerServiceRunner> threadsHolder = new TestThreadsHolder<MultithreadedIndexerServiceAccessTest.IndexerServiceRunner>(
				NO_OF_THREADS, runnerCreator);

		threadsHolder.startAll();
		try
		{
			solrOperationLatch.await();
		}
		catch (final InterruptedException e)
		{
			Assert.fail(e.getMessage());
		}

		assertThat(errorTurns).isEmpty();

		//Each thread is indexing products from different catalog version.
		for (int i = 0; i < NO_OF_THREADS; i++)
		{
			final SearchQuery searchQuery = new SearchQuery(facetSearchConfigs.get(Integer.valueOf(i)), indexedTypes.get(Integer
					.valueOf(i)));
			final SearchResult searchResult = facetSearchService.search(searchQuery);
			final List<ProductModel> products = (List<ProductModel>) searchResult.getResults();
			for (final ProductModel product : products)
			{
				Assert.assertEquals(testCatalogVersions.get(i), product.getCatalogVersion());
			}
		}
	}

	private class IndexerServiceRunner implements Runnable
	{
		private final FacetSearchConfig facetSearchConfigFromRunner;
		private final CountDownLatch countDownLatch;
		private final Tenant tenant;

		@Override
		public void run()
		{
			final List<Exception> recordedErrorTurns = new LinkedList<Exception>();
			try
			{
				Registry.setCurrentTenant(tenant);
				JaloSession.getCurrentSession().activate();
				indexerService.performFullIndex(facetSearchConfigFromRunner);
			}
			catch (final Exception e)
			{
				recordedErrorTurns.add(e);
			}
			finally
			{
				countDownLatch.countDown();
				JaloSession.deactivate();
				JaloSession.getCurrentSession().close();
				Registry.unsetCurrentTenant();
			}
			errorTurns.addAll(recordedErrorTurns);
		}

		public IndexerServiceRunner(final FacetSearchConfig facetSearchConfigFromRunner, final CountDownLatch countDownLatch,
				final Tenant tenant)
		{
			super();
			this.countDownLatch = countDownLatch;
			this.facetSearchConfigFromRunner = facetSearchConfigFromRunner;
			this.tenant = tenant;
		}
	}

	private class IndexerServiceRunnerCreator implements RunnerCreator<IndexerServiceRunner>
	{
		private final Tenant currentTenant;
		private final CountDownLatch countDownLatch;

		public IndexerServiceRunnerCreator(final CountDownLatch solrOperationLatch)
		{
			this.countDownLatch = solrOperationLatch;
			this.currentTenant = Registry.getCurrentTenant();
		}

		@Override
		public IndexerServiceRunner newRunner(final int threadNumber)
		{
			return new IndexerServiceRunner(createFacetSarchConfigForThread(threadNumber), countDownLatch, currentTenant);
		}

		private FacetSearchConfig createFacetSarchConfigForThread(final int threadNumber)
		{
			final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
			facetSearchConfig.setSolrConfig(prepareSolrConfig());
			facetSearchConfig.setName(TEST_CONFIG_NAME + threadNumber);
			facetSearchConfig.setIndexConfig(createIndexConfig(threadNumber));
			facetSearchConfig.setSearchConfig(createSearchConfig());
			facetSearchConfigs.put(Integer.valueOf(threadNumber), facetSearchConfig);
			return facetSearchConfig;
		}

		private SearchConfig createSearchConfig()
		{
			final SearchConfig searchConfig = new SearchConfig();
			searchConfig.setDefaultSortOrder(Collections.singletonList("score"));
			searchConfig.setPageSize(100);
			return searchConfig;
		}

		private IndexConfig createIndexConfig(final int threadNumber)
		{
			final IndexConfig indexConfig = new IndexConfig();
			indexConfig.setBatchSize(100);
			indexConfig.setIndexMode(IndexMode.DIRECT);
			indexConfig.setNumberOfThreads(1);
			final IndexedType indexedType = createIndexType(threadNumber);
			indexConfig.setIndexedTypes(Collections.singletonMap(indexedType.getUniqueIndexedTypeCode(), indexedType));
			indexConfig.setCatalogVersions(getCatalogVersionForThread(threadNumber));
			return indexConfig;
		}

		private Collection<CatalogVersionModel> getCatalogVersionForThread(final int threadNumber)
		{
			return Collections.singletonList(testCatalogVersions.get(threadNumber));
		}

		private IndexedType createIndexType(final int threadNumber)
		{
			final IndexedType indexType = new IndexedType();
			indexType.setIndexName(String.valueOf(threadNumber));
			indexType.setCode("Product");
			indexType.setUniqueIndexedTypeCode("Product_" + threadNumber);
			indexType.setFlexibleSearchQueries(createFlexibleSearchQueries(testCatalogVersions.get(threadNumber)));
			indexType.setIndexedProperties(createIndexProperties());
			indexType.setIdentityProvider("productIdentityProvider");
			indexType.setTypeFacets(Collections.<String> emptySet());
			indexType.setModelLoader("defaultModelLoader");
			indexedTypes.put(Integer.valueOf(threadNumber), indexType);
			return indexType;
		}


		private Map<String, IndexedProperty> createIndexProperties()
		{
			final Map<String, IndexedProperty> properties = new HashMap<String, IndexedProperty>();
			properties.put("code", createProductCodeProperty());
			return properties;
		}

		private IndexedProperty createProductCodeProperty()
		{
			final IndexedProperty property = new IndexedProperty();
			property.setType("String");
			property.setName("code");
			property.setFieldValueProvider("modelPropertyFieldValueProvider");
			property.setExportId("code");
			return property;
		}

		private Map<IndexOperation, IndexedTypeFlexibleSearchQuery> createFlexibleSearchQueries(
				final CatalogVersionModel threadCatalogVersion)
		{
			return Collections.singletonMap(IndexOperation.FULL, createQuery(IndexOperation.FULL, threadCatalogVersion));
		}

		private IndexedTypeFlexibleSearchQuery createQuery(final IndexOperation type, final CatalogVersionModel threadCatalogVersion)
		{
			final IndexedTypeFlexibleSearchQuery query = new IndexedTypeFlexibleSearchQuery();
			query.setType(type);
			query.setQuery("SELECT {pk} FROM {Product} where {catalogVersion} = ?catalogVersion");
			query.setParameters(Collections.<String, Object> singletonMap("catalogVersion", threadCatalogVersion));
			query.setUserId(TEST_USER);
			return query;
		}

		private SolrConfig prepareSolrConfig()
		{
			final SolrConfig solrConfig = new SolrConfig();
			solrConfig.setMode(SolrServerMode.EMBEDDED);
			solrConfig.setClusterConfig(createClusterConfig());
			return solrConfig;
		}


		private ClusterConfig createClusterConfig()
		{
			final ClusterConfig clusterConfig = new ClusterConfig();
			clusterConfig.setEmbeddedMaster(true);
			return clusterConfig;
		}
	}

	private void createCatalogVersions(final int noOfThreads)
	{
		testCatalogVersions = new ArrayList<CatalogVersionModel>(noOfThreads);
		for (int i = 0; i < noOfThreads; i++)
		{
			final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
			catalogVersion.setVersion(String.valueOf(i));
			catalogVersion.setCatalog(testCatalog);
			testCatalogVersions.add(catalogVersion);
			createProducts(catalogVersion);
		}
	}



	private void createProducts(final CatalogVersionModel catalogVersion)
	{
		for (int j = 0; j < NO_OF_PRODUCTS_IN_CATALOG; j++)
		{
			final ProductModel product = modelService.create(ProductModel.class);
			product.setCatalogVersion(catalogVersion);
			product.setCode(PRODUCTCODE_PREFIX + CATALOG_ID + ":" + catalogVersion.getVersion() + ":" + j);
		}
	}


	private void createCatalog()
	{
		testCatalog = modelService.create(CatalogModel.class);
		testCatalog.setId(CATALOG_ID);
	}

	private void createTestUser()
	{
		final UserModel testUser = modelService.create(UserModel.class);
		testUser.setUid(TEST_USER);
		modelService.save(testUser);
		userService.setCurrentUser(testUser);
	}

	private void createSolrFacetSearchConfigs(final int noOfThreads)
	{
		for (int i = 0; i < noOfThreads; i++)
		{
			final SolrFacetSearchConfigModel config = modelService.create(SolrFacetSearchConfigModel.class);
			config.setName(TEST_CONFIG_NAME + i);
			config.setCatalogVersions(Collections.singletonList(testCatalogVersions.get(i)));
			config.setSolrSearchConfig(setUpSearchConfig());
			config.setSolrIndexedTypes(Collections.singletonList(createSolrIndexTypeForThread(i)));
			config.setSolrServerConfig(createSolrServerConfigForThread(i));
			config.setSolrIndexConfig(createIndexConfigForThread(i));
		}
	}


	private SolrServerConfigModel createSolrServerConfigForThread(final int threadNo)
	{
		final SolrServerConfigModel solrServerConfig = setUpSolrServerConfig();
		solrServerConfig.setName(solrServerConfig.getName().concat(String.valueOf(threadNo)));
		return solrServerConfig;
	}





	private SolrIndexConfigModel createIndexConfigForThread(final int threadNo)
	{
		final SolrIndexConfigModel config = setUpIndexConfig();
		config.setName(config.getName().concat(String.valueOf(threadNo)));
		return config;
	}

	private SolrIndexedTypeModel createSolrIndexTypeForThread(final int i)
	{
		final SolrIndexedTypeModel indexedType = modelService.create(SolrIndexedTypeModel.class);
		indexedType.setType(typeService.getComposedTypeForCode("Product"));
		indexedType.setIdentifier(String.valueOf(i));
		indexedType.setIndexName(String.valueOf(i));
		indexedType.setSolrIndexedProperties(setUpIndexProperties());
		return indexedType;
	}

}
