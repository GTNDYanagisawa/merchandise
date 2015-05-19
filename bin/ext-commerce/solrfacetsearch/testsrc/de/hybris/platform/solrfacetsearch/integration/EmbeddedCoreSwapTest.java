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
package de.hybris.platform.solrfacetsearch.integration;

import de.hybris.platform.core.Registry;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrCoreRegistry;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import junit.framework.Assert;

import org.apache.solr.core.SolrCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class EmbeddedCoreSwapTest extends HybrisJUnit4TransactionalTest
{
	private static final String CORE_1 = "index1";
	private static final String CORE_2 = "index2";

	private String coreName1;
	private String coreName2;
	private SolrCore core1;
	private SolrCore core2;

	@Before
	public void setUp()
	{
		final String tenantId = Registry.getCurrentTenant().getTenantID();
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(tenantId, CORE_1);
		SolrCoreRegistry.getInstance().getEmbeddedSolrServer(tenantId, CORE_2);

		coreName1 = tenantId + '_' + CORE_1;
		coreName2 = tenantId + '_' + CORE_2;

	}

	@Test
	public void testEmbeddedCoresSwap()
	{
		core1 = SolrCoreRegistry.getInstance().getCore(coreName1);
		core2 = SolrCoreRegistry.getInstance().getCore(coreName2);

		SolrCoreRegistry.getInstance().swap(coreName1, coreName2);

		Assert.assertEquals(core2, SolrCoreRegistry.getInstance().getCore(coreName1));
		Assert.assertEquals(core1, SolrCoreRegistry.getInstance().getCore(coreName2));
	}

	@After
	public void tearDown()
	{
		//		core1.close();
		//		core2.close();
	}

}
