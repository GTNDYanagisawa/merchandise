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
package com.hybris.cis.client.rest.avs.mock;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.avs.model.AvsResult;
import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.client.rest.avs.AvsClient;
import com.hybris.commons.client.RestResponse;
import com.hybris.commons.mock.GenericMockFactory;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@IntegrationTest
public class AvsMockClientTest extends ServicelayerTest
{
	private static final String CLIENT_REF = "abc";

	private AvsClient avsClient;

	@Before
	public void setUp()
	{
		final GenericMockFactory avsClientFactory = (GenericMockFactory) Registry.getApplicationContext().getBean(
				"&avsClientFactory");
		avsClientFactory.setMockMode(true);

		avsClient = (AvsClient) avsClientFactory.getObject();
	}

	@Test
	public void shouldAcceptAddress()
	{
		System.out.println("enteringshouldAcceptAddress()");

		Assert.assertNotNull(avsClient);
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		final RestResponse<AvsResult> response = avsClient.verifyAddress(CLIENT_REF, address);
		Assert.assertEquals(CisDecision.ACCEPT, response.getResult().getDecision());
	}



}
