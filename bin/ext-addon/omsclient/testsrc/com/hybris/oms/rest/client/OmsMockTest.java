/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.hybris.oms.rest.client;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.hybris.oms.api.ats.AtsFacade;
import com.hybris.oms.api.inventory.InventoryFacade;
import com.hybris.oms.domain.ats.AtsFormula;
import com.hybris.oms.domain.inventory.Bin;

import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.bootstrap.annotations.IntegrationTest;


/**
 * Unit test for making sure the mocks are available.
 */
@IntegrationTest
public class OmsMockTest extends ServicelayerBaseTest
{
	@Resource
	private AtsFacade omsAtsRestClient;

	@Resource
	private InventoryFacade omsInventoryRestClient;

	@Test
	@Ignore
	public void testAtsMock()
	{
		omsAtsRestClient.createFormula(new AtsFormula("test", "I[ON_HAND]", "test", null));
		Assert.assertTrue("empty result", omsAtsRestClient.findAllFormulas().isEmpty());
		Assert.assertTrue("empty ATS result", omsAtsRestClient.findLocalAts(null, null, null).isEmpty());
	}

	@Test
	@Ignore
	public void testInventoryMock()
	{
		final Bin bin = new Bin();
		bin.setBinCode("bin1");
		bin.setLocationId("loc1");
		Assert.assertNotNull(omsInventoryRestClient.createBin(bin));
	}
}
