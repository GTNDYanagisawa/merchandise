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
package de.hybris.platform.solrfacetsearch.embedded;

import de.hybris.platform.core.Registry;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrCoreRegistry;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.io.IOException;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Before;


/**
 *
 */
public class FetchLukeParameterTest extends HybrisJUnit4Test
{

	private SolrServer solrServer;


	@Before
	public void setUp()
	{
		solrServer = new SolrServer(SolrCoreRegistry.getInstance().getEmbeddedSolrServer(Registry.getCurrentTenant().getTenantID(),
				"testSolrServer"), true);
	}

	//@Test
	// disabled: testGetLastIndexTime() doesn't work like this anymore. SolrIndexStatisticsProvider.getLastIndexTime 
	public void testGetLastIndexTime() throws SolrServerException, IOException, InterruptedException
	{
		final Object date = solrServer.getLukeSatatisticsValue(SolrServer.LAST_INDEX_TIME);
		Assert.assertTrue(date instanceof Date);

		final Date dateBefore = (Date) date;
		// wait at least one seconds to make sure changing the index has got a chance to
		// happen 'later' since java time 'before' may be the same 'after' otherwise
		Thread.sleep(1000);
		changeIndex(solrServer);

		final Date dateAfter = (Date) solrServer.getLukeSatatisticsValue(SolrServer.LAST_INDEX_TIME);

		Assert.assertTrue(dateAfter.after(dateBefore));
	}

	private void changeIndex(final SolrServer solrServer) throws SolrServerException, IOException
	{
		final SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "testId");
		solrServer.add(doc);
		solrServer.commit();
	}

}
