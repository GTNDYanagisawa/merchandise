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
package de.hybris.platform.solrfacetsearch.indexer.workers.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.spi.Indexer;
import de.hybris.platform.solrfacetsearch.indexer.workers.exceptions.SolrIndexWorkerException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class SolrUpdatingWorkerTest extends ServicelayerTest
{


	private SolrUpdatingWorker solrUpdatingWorker;


	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private SessionService sessionService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	private Map<String, Object> indexWorkerContext;

	private CatalogVersionModel catalogVersion;
	private ProductModel product1;
	private ProductModel product2;
	private UserModel user;

	@Mock
	private IndexedType indexedType;
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private SolrConfig solrConfig;
	@Mock
	private CountDownLatch done;

	boolean testPassed = false;



	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		solrUpdatingWorker = new SolrUpdatingWorker();
		solrUpdatingWorker.setCatalogVersionService(catalogVersionService);
		solrUpdatingWorker.setFlexibleSearchService(flexibleSearchService);
		solrUpdatingWorker.setIndexer(new Indexer()
		{

			@Override
			public Collection<String> removeItems(final Collection<ItemModel> items, final IndexConfig indexConfig,
					final IndexedType indexedType, final SolrConfig solrConfig) throws IndexerException
			{
				// YTODO Auto-generated method stub
				return null;
			}

			@Override
			public void removeAllItems(final IndexConfig indexConfig, final IndexedType indexedType, final SolrConfig solrConfig)
					throws IndexerException
			{
				// YTODO Auto-generated method stub

			}

			@Override
			public void optimize(final SolrConfig solrConfig, final IndexedType indexedType) throws IndexerException
			{
				// YTODO Auto-generated method stub

			}

			@Override
			public Collection<SolrInputDocument> indexItems(final Collection<ItemModel> items, final IndexConfig indexConfig,
					final IndexedType indexedType, final SolrConfig solrConfig) throws IndexerException, FieldValueProviderException
			{
				testPassed = items.containsAll(Arrays.asList(product1, product2))
						&& indexConfig.equals(SolrUpdatingWorkerTest.this.indexConfig)
						&& indexedType.equals(SolrUpdatingWorkerTest.this.indexedType)
						&& solrConfig.equals(SolrUpdatingWorkerTest.this.solrConfig);
				return null;
			}
		});
		solrUpdatingWorker.setSessionService(sessionService);
		solrUpdatingWorker.setUserService(userService);

		indexWorkerContext = new HashMap<String, Object>();

		createCoreData();
		createDefaultCatalog();

		catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		product1 = productService.getProductForCode(catalogVersion, "testProduct1");
		product2 = productService.getProductForCode(catalogVersion, "testProduct2");

		user = modelService.create(UserModel.class);
		user.setUid("testUser");
		modelService.save(user);

		BDDMockito.when(indexedType.getCode()).thenReturn("Product");
		BDDMockito.when(indexedType.getCode()).thenReturn("Product");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullInitialization()
	{
		solrUpdatingWorker.initialize(null);
	}

	@Test(expected = SolrIndexWorkerException.class)
	public void testMalformedInitialization()
	{
		indexWorkerContext.put(SolrUpdatingWorker.INDEX_USER, "1");
		solrUpdatingWorker.initialize(indexWorkerContext);
	}

	@Test
	public void testInitialization()
	{
		setUpWorkerContext(indexWorkerContext);
		solrUpdatingWorker.initialize(indexWorkerContext);
	}


	@Test(expected = SolrIndexWorkerException.class)
	public void testRunningNonInitialized()
	{
		solrUpdatingWorker.run();
	}



	/**
	 * @param indexWorkerContext
	 * 
	 */
	private void setUpWorkerContext(final Map<String, Object> indexWorkerContext)
	{
		indexWorkerContext.put(SolrUpdatingWorker.ITEM_PKS, Arrays.asList(product1.getPk(), product2.getPk()));
		indexWorkerContext.put(SolrUpdatingWorker.INDEXED_TYPE, indexedType);
		indexWorkerContext.put(SolrUpdatingWorker.INDEXED_CONFIG, indexConfig);
		indexWorkerContext.put(SolrUpdatingWorker.SOLR_CONFIG, solrConfig);
		indexWorkerContext.put(SolrUpdatingWorker.INDEX_USER, user);
		indexWorkerContext.put(SolrUpdatingWorker.INDEXED_CATALOGVERSIONS, Collections.singletonList(catalogVersion));
		indexWorkerContext.put(SolrUpdatingWorker.WORKER_NUMBER, Integer.valueOf(1));
		indexWorkerContext.put(SolrUpdatingWorker.COUNTER_LATCH, done);
		indexWorkerContext.put(SolrUpdatingWorker.PARENT_SESSION_CTX, JaloSession.getCurrentSession().getSessionContext());
	}
}
