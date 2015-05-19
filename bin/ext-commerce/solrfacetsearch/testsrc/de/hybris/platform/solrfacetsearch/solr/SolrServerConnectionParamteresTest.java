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

import de.hybris.platform.solrfacetsearch.config.ClusterConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;

import junit.framework.Assert;

import org.junit.Assume;
import org.junit.Test;


/**
 *
 */
public class SolrServerConnectionParamteresTest extends AbstractSolrTest
{

	private static final Integer ALIVE_CHECK_INT = Integer.valueOf(100);
	private static final Integer CONN_TIMEOUT = Integer.valueOf(200);
	private static final Integer SOCKET_TIMEOUT = Integer.valueOf(300);
	private static final Integer MAX_CONNECTIONS = Integer.valueOf(400);
	private static final Integer MAX_CONNECTIONS_PER_HOST = Integer.valueOf(500);


	private SolrServerConfigModel testSolrServerConfig;

	@Test
	public void testConnectionParameters() throws FacetConfigServiceException
	{
		// only run this test against standalone solr servers
		Assume.assumeTrue(getSolrServerModeConfiguration() == STANDALONE_MODE);


		final ClusterConfig clusterConfigInitial = facetSearchConfigService.getConfiguration(SOLR_CONFIG_NAME).getSolrConfig()
				.getClusterConfig();
		//check default values
		Assert.assertNotNull(clusterConfigInitial);
		Assert.assertEquals(5000, clusterConfigInitial.getAliveCheckInterval().intValue());
		Assert.assertEquals(5000, clusterConfigInitial.getConnectionTimeout().intValue());
		Assert.assertEquals(8000, clusterConfigInitial.getSocketTimeout().intValue());
		Assert.assertEquals(100, clusterConfigInitial.getMaxConnections().intValue());
		Assert.assertEquals(50, clusterConfigInitial.getMaxConnectionsPerHost().intValue());
		Assert.assertTrue(clusterConfigInitial.isTcpNoDelay());

		testSolrServerConfig.setAliveCheckInterval(ALIVE_CHECK_INT);
		testSolrServerConfig.setConnectionTimeout(CONN_TIMEOUT);
		testSolrServerConfig.setSocketTimeout(SOCKET_TIMEOUT);
		testSolrServerConfig.setMaxTotalConnections(MAX_CONNECTIONS);
		testSolrServerConfig.setMaxTotalConnectionsPerHostConfig(MAX_CONNECTIONS_PER_HOST);
		testSolrServerConfig.setTcpNoDelay(false);
		modelService.save(testSolrServerConfig);

		final ClusterConfig solrClusterConfigChanged = facetSearchConfigService.getConfiguration(SOLR_CONFIG_NAME).getSolrConfig()
				.getClusterConfig();
		Assert.assertEquals(100, solrClusterConfigChanged.getAliveCheckInterval().intValue());
		Assert.assertEquals(200, solrClusterConfigChanged.getConnectionTimeout().intValue());
		Assert.assertEquals(300, solrClusterConfigChanged.getSocketTimeout().intValue());
		Assert.assertEquals(400, solrClusterConfigChanged.getMaxConnections().intValue());
		Assert.assertEquals(500, solrClusterConfigChanged.getMaxConnectionsPerHost().intValue());
		Assert.assertFalse(solrClusterConfigChanged.isTcpNoDelay());
	}
	}
