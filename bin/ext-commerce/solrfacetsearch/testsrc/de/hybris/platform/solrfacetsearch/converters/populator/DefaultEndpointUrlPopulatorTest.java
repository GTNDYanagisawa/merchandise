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
package de.hybris.platform.solrfacetsearch.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.EndpointURL;
import de.hybris.platform.solrfacetsearch.model.config.SolrEndpointUrlModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultEndpointUrlPopulatorTest
{
	private DefaultEndpointUrlPopulator populator;
	private final static String TEST_URL = "testUrl";

	@Before
	public void setUp()
	{
		populator = new DefaultEndpointUrlPopulator();
	}

	@Test
	public void testPopulate()
	{
		final SolrEndpointUrlModel source = new SolrEndpointUrlModel();
		source.setUrl(null);
		source.setMaster(false);
		final EndpointURL target = new EndpointURL();
		populator.populate(source, target);
		Assert.assertNull(target.getUrl());
		Assert.assertFalse(target.isMaster());

		source.setUrl(TEST_URL);
		source.setMaster(true);

		populator.populate(source, target);
		Assert.assertEquals(TEST_URL, target.getUrl());
		Assert.assertTrue(target.isMaster());
	}
}
