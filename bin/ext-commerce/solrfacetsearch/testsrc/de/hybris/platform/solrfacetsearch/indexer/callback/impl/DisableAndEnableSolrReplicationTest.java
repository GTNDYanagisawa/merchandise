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
package de.hybris.platform.solrfacetsearch.indexer.callback.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.solrfacetsearch.enums.SolrServerModes;
import de.hybris.platform.solrfacetsearch.indexer.callback.replication.IndexOperationCallbackParams;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test Full Index Operation Callbacks: {@link EnableReplicationForFullIndex} and {@link DisableReplicationForFullIndex}
 */
public class DisableAndEnableSolrReplicationTest
{
	/**
	 * counterpart of replication flag on Solr Server
	 */
	protected static boolean isEnabledReplication = false;
	protected EnableReplicationForFullIndex enableReplicationForFullIndex;
	protected DisableReplicationForFullIndex disableReplicationForFullIndex;

	@Before
	public void before()
	{
		enableReplicationForFullIndex = mock(MockedEnableReplicationForFullIndex.class, Mockito.CALLS_REAL_METHODS);
		disableReplicationForFullIndex = mock(MockedDisableReplicationForFullIndex.class, Mockito.CALLS_REAL_METHODS);
		final Map<IndexOperationCallbackParams, Integer> disabledReplicationConfigMap = new HashMap<IndexOperationCallbackParams, Integer>();
		enableReplicationForFullIndex.setDisabledReplicationConfigMap(disabledReplicationConfigMap);
		disableReplicationForFullIndex.setDisabledReplicationConfigMap(disabledReplicationConfigMap);
	}

	@Test
	public void testThatDisablingAndEnablingReplicationShouldPertainOnlyToStandaloneSolr()
	{
		isEnabledReplication = false;
		//given
		final SolrFacetSearchConfigModel facetSearchConfig = createSolrConfigModel(SolrServerModes.EMBEDDED);
		//when
		disableReplicationForFullIndex.invoke(facetSearchConfig, null);
		enableReplicationForFullIndex.invoke(facetSearchConfig, null);
		//then
		Assert.assertFalse(isEnabledReplication);
		verify(enableReplicationForFullIndex, never()).enableReplicationOnMaster(any(SolrServer.class));
		verify(disableReplicationForFullIndex, never()).disableReplicationOnMaster(any(SolrServer.class));
	}

	@Test
	public void testThatStandaloneServerWithDisabledReplicationDoesNotEnableReplication()
	{
		//given
		isEnabledReplication = false;
		final SolrFacetSearchConfigModel facetSearchConfig = createSolrConfigModel(SolrServerModes.STANDALONE);
		//when-then

		// disable - enable
		disableReplicationForFullIndex.invoke(facetSearchConfig, null);
		enableReplicationForFullIndex.invoke(facetSearchConfig, null);
		Assert.assertFalse(isEnabledReplication);

		// disable disable enable enable
		disableReplicationForFullIndex.invoke(facetSearchConfig, null);
		disableReplicationForFullIndex.invoke(facetSearchConfig, null);
		Assert.assertFalse(isEnabledReplication);

		enableReplicationForFullIndex.invoke(facetSearchConfig, null);
		enableReplicationForFullIndex.invoke(facetSearchConfig, null);
		Assert.assertFalse(isEnabledReplication);

		verify(enableReplicationForFullIndex, never()).enableReplicationOnMaster(any(SolrServer.class));
		verify(disableReplicationForFullIndex, never()).disableReplicationOnMaster(any(SolrServer.class));
	}

	@Test
	public void testDisableReplicationForStandaloneServer()
	{
		//given
		isEnabledReplication = true;
		final SolrFacetSearchConfigModel facetSearchConfig = createSolrConfigModel(SolrServerModes.STANDALONE);
		//when-then
		disableReplicationForFullIndex.invoke(facetSearchConfig, null);
		Assert.assertFalse(isEnabledReplication);
		enableReplicationForFullIndex.invoke(facetSearchConfig, null);
		Assert.assertTrue(isEnabledReplication);
		verify(disableReplicationForFullIndex, times(1)).disableReplicationOnMaster(any(SolrServer.class));
		verify(enableReplicationForFullIndex, times(1)).enableReplicationOnMaster(any(SolrServer.class));

		disableReplicationForFullIndex.invoke(facetSearchConfig, null);
		disableReplicationForFullIndex.invoke(facetSearchConfig, null);
		Assert.assertFalse(isEnabledReplication);
		enableReplicationForFullIndex.invoke(facetSearchConfig, null);
		enableReplicationForFullIndex.invoke(facetSearchConfig, null);
		Assert.assertTrue(isEnabledReplication);

		verify(disableReplicationForFullIndex, times(2)).disableReplicationOnMaster(any(SolrServer.class));
		verify(enableReplicationForFullIndex, times(2)).enableReplicationOnMaster(any(SolrServer.class));
	}

	protected SolrFacetSearchConfigModel createSolrConfigModel(final SolrServerModes mode)
	{
		final SolrFacetSearchConfigModel facetSearchConfig = new SolrFacetSearchConfigModel();
		final SolrServerConfigModel solrServerConfigModel = new SolrServerConfigModel();
		solrServerConfigModel.setMode(mode);
		facetSearchConfig.setSolrServerConfig(solrServerConfigModel);
		return facetSearchConfig;
	}

	/**
	 * "Mocked" version of class DisableReplicationForFullIndex, disabling/enabling/reading replication flag work on
	 * {@link DisableAndEnableSolrReplicationTest#isEnabledReplication}
	 */
	protected static class MockedDisableReplicationForFullIndex extends DisableReplicationForFullIndex
	{
		@Override
		protected SolrServer getSolrServerMaster(final SolrFacetSearchConfigModel facetSearchConfig,
				final SolrIndexedTypeModel indexedType)
		{
			return null;
		}

		@Override
		protected boolean isReplicationEnabledOnMaster(final SolrServer masterServer)
		{
			return isEnabledReplication;
		}

		@Override
		protected boolean disableReplicationOnMaster(final SolrServer masterServer)
		{
			isEnabledReplication = false;
			return true;
		}

		@Override
		protected boolean enableReplicationOnMaster(final SolrServer masterServer)
		{
			isEnabledReplication = true;
			return true;
		}
	}

	/**
	 * "Mocked" version of class EnableReplicationForFullIndex, disabling/enabling/reading replication flag work on
	 * {@link DisableAndEnableSolrReplicationTest#isEnabledReplication}
	 */
	protected static class MockedEnableReplicationForFullIndex extends EnableReplicationForFullIndex
	{
		@Override
		protected SolrServer getSolrServerMaster(final SolrFacetSearchConfigModel facetSearchConfig,
				final SolrIndexedTypeModel indexedType)
		{
			return null;
		}

		@Override
		protected boolean isReplicationEnabledOnMaster(final SolrServer masterServer)
		{
			return isEnabledReplication;
		}

		@Override
		protected boolean disableReplicationOnMaster(final SolrServer masterServer)
		{
			isEnabledReplication = false;
			return true;
		}

		@Override
		protected boolean enableReplicationOnMaster(final SolrServer masterServer)
		{
			isEnabledReplication = true;
			return true;
		}
	}
}
