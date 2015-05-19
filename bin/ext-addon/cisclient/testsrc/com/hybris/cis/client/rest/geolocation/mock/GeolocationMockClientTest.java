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
package com.hybris.cis.client.rest.geolocation.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.geolocation.model.CisLocationRequest;
import com.hybris.cis.api.geolocation.model.GeoLocationResult;
import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.client.rest.geolocation.GeolocationClient;
import com.hybris.commons.client.RestResponse;
import com.hybris.commons.mock.GenericMockFactory;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@IntegrationTest
public class GeolocationMockClientTest extends ServicelayerTest
{
	private GeolocationClient geolocationClient;

	@Before
	public void setUp()
	{
		final GenericMockFactory geolocationClientFactory = (GenericMockFactory) Registry.getApplicationContext().getBean(
				"&geolocationClientFactory");
		geolocationClientFactory.setMockMode(true);

		geolocationClient = (GeolocationClient) geolocationClientFactory.getObject();
	}


	@Test
	public void shouldReturnAddress()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");

		final CisLocationRequest location = new CisLocationRequest();
		final List<CisAddress> locations = new ArrayList<CisAddress>();
		locations.add(address);
		location.setAddresses(locations);


		final RestResponse<GeoLocationResult> response = geolocationClient.getGeolocation("test", location);

		final CisAddress responseLocation = response.getResult().getGeoLocations().get(0);

		Assert.assertEquals("10019", responseLocation.getZipCode());
	}

}
