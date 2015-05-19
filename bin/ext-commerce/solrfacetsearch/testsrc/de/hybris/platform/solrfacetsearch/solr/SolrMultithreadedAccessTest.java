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
package de.hybris.platform.solrfacetsearch.solr;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.solrfacetsearch.config.ClusterConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypes;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchQueryConverter;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;
import de.hybris.platform.test.RunnerCreator;
import de.hybris.platform.test.TestThreadsHolder;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
@PerformanceTest
public class SolrMultithreadedAccessTest extends ServicelayerTest
{

	private static final int NO_OF_THREADS = 20;
	//due to solr config limitation (max warming searchers)
	private static final int NO_OF_THREADS_PER_INDEX = 2;

	private static final Logger LOG = Logger.getLogger(SolrMultithreadedAccessTest.class);

	@Resource
	private SolrService solrService;

	@Test
	public void testMultithreadedAccess()
	{

		//Now take 20 threads, (2 per index) and do querying and indexing.
		final CountDownLatch solrOperationLatch = new CountDownLatch(NO_OF_THREADS);
		final TestThreadsHolder<SolrOperationRunner> threadsHolder = new TestThreadsHolder<SolrOperationRunner>(NO_OF_THREADS,//
				new RunnerCreator<SolrOperationRunner>()
				{
					@Override
					public SolrOperationRunner newRunner(final int threadNumber)
					{
						// 2 threads per index 
						final String indexIdentifier = "index_" + Integer.toString(threadNumber / NO_OF_THREADS_PER_INDEX);

						//for each index identifier even threads will do indexing, odd threads will query
						final boolean isIndexing = (threadNumber % 2) == 0;

						return new SolrOperationRunner(indexIdentifier, isIndexing, solrOperationLatch);
					}
				},//
				true);

		threadsHolder.startAll();
		try
		{
			solrOperationLatch.await();
		}
		catch (final InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		Assert.assertTrue("not all test threads shut down orderly", threadsHolder.stopAndDestroy(15));
		Assert.assertEquals("found worker errors", Collections.EMPTY_MAP, threadsHolder.getErrors());
		for (final SolrOperationRunner solrRunner : threadsHolder.getRunners())
		{
			Assert.assertEquals("runner " + solrRunner + " had error turns", Collections.EMPTY_LIST, solrRunner.errorTurns);
		}
	}

	private abstract class AbstractSolrRunner implements Runnable
	{

		protected final String indexIdentifier;
		volatile public List<Exception> errorTurns;
		protected final CountDownLatch countDownLatch;

		public AbstractSolrRunner(final String indexIdentifier, final CountDownLatch countDownLatch)
		{
			super();
			this.indexIdentifier = indexIdentifier;
			this.countDownLatch = countDownLatch;
		}

		abstract public void run();


		protected void indexServer(final SolrServer server) throws SolrServerException, IOException
		{
			logForThread("index start");
			final SolrInputDocument doc = new SolrInputDocument();
			doc.setField("id", generateString());
			server.add(doc);
			server.commit();
			logForThread("index end");
		}

		protected void queryServer(final SolrServer server) throws SolrServerException, IOException
		{
			logForThread("query start");
			server.query(new SolrQuery(SolrSearchQueryConverter.ALL_QUERY));
			logForThread("query end");
		}

		protected String generateString()
		{
			return new BigInteger(130, new Random()).toString(32);
		}

		protected void logForThread(final String msg)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(indexIdentifier + " : " + msg);
			}
		}
	}


	private class SolrOperationRunner extends AbstractSolrRunner
	{
		private final boolean isIndex;

		public SolrOperationRunner(final String indexIdentifier, final boolean isIndex, final CountDownLatch countDownLatch)
		{
			super(indexIdentifier, countDownLatch);
			this.isIndex = isIndex;
		}

		@Override
		public void run()
		{
			final List<Exception> recordedErrorTurns = new LinkedList<Exception>();
			try
			{
				final SolrServer server = solrService.getSolrServer(mockSolrConfig(), mockIndexTypeForIndexName(indexIdentifier));
				if (isIndex)
				{
					indexServer(server);
				}
				else
				{
					queryServer(server);
				}
			}
			catch (final Exception e)
			{
				recordedErrorTurns.add(e);
			}
			finally
			{
				countDownLatch.countDown();
			}

			this.errorTurns = recordedErrorTurns; // volatile write
		}
	}

	private IndexedType mockIndexTypeForIndexName(final String indexName)
	{
		return IndexedTypes.createIndexedType(
		//
				null, false, Collections.EMPTY_LIST, null, null, null, Collections.EMPTY_SET, null, indexName, null);
	}

	private SolrConfig mockSolrConfig()
	{

		final ClusterConfig clusterConfig = new ClusterConfig();
		clusterConfig.setAliveCheckInterval(Integer.valueOf(100));
		clusterConfig.setConnectionTimeout(Integer.valueOf(100));
		clusterConfig.setReadTimeout(Integer.valueOf(100));
		clusterConfig.setEmbeddedMaster(true);

		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setClusterConfig(clusterConfig);
		solrConfig.setMode(SolrServerMode.EMBEDDED);

		return solrConfig;
	}


}
