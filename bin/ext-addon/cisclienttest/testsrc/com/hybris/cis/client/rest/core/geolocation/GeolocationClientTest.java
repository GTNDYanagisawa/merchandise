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
package com.hybris.cis.client.rest.core.geolocation;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.hybris.cis.api.geolocation.model.CisLocationRequest;
import com.hybris.cis.api.geolocation.model.GeoLocationResult;
import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.client.rest.geolocation.GeolocationClient;
import com.hybris.commons.client.RestResponse;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@ManualTest
public class GeolocationClientTest extends ServicelayerTest
{
	@Resource
	private GeolocationClient geolocationClient;

	@Test
	public void shouldReturnAddress()
	{
		final CisAddress address = new CisAddress();
		address.setCity("Lindenau");
		address.setZipCode("1945");
		address.setCountry("DE");

		final CisLocationRequest location = new CisLocationRequest();
		final List<CisAddress> locations = new ArrayList<CisAddress>();
		locations.add(address);
		location.setAddresses(locations);


		final RestResponse<GeoLocationResult> response = this.geolocationClient.getGeolocation("test", location);

		final CisAddress responseLocation = response.getResult().getGeoLocations().get(0);


		Assert.assertEquals("51.4", responseLocation.getLatitude());
		Assert.assertEquals("13.7333", responseLocation.getLongitude());
		Assert.assertEquals("1945", responseLocation.getZipCode());
	}

}
