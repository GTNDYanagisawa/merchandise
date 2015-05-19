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
package de.hybris.platform.storelocator.impl;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.route.DistanceAndRoute;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@ManualTest
public class GeoWebServiceWrapperTest extends ServicelayerTest
{

	@Resource
	private GeoWebServiceWrapper geoServiceWrapper;

	@Mock
	private Location start;
	@Mock
	private Location dest;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetLeedsCoordinates()
	{

		final AddressData address = new AddressData();
		address.setCity("Leeds");
		final GPS gps = geoServiceWrapper.geocodeAddress(address);

		Assert.assertEquals(-1.549416d, gps.getDecimalLongitude(), 0.01);
		Assert.assertEquals(53.805517d, gps.getDecimalLatitude(), 0.01);

	}

	@Test
	public void testGetBerlinCoordinates()
	{

		final AddressData address = new AddressData();
		address.setCity("Berlin");
		final GPS gps = geoServiceWrapper.geocodeAddress(address);

		Assert.assertEquals(13.4060912d, gps.getDecimalLongitude(), 0.01);
		Assert.assertEquals(52.5191710d, gps.getDecimalLatitude(), 0.01);
	}

	@Test
	public void testDistanceFromLondonToLeeds()
	{
		final AddressData londonAddress = new AddressData();
		londonAddress.setCity("London");
		final AddressData leedsAddress = new AddressData();
		leedsAddress.setCity("Leeds");

		final GPS londonGps = geoServiceWrapper.geocodeAddress(londonAddress);
		final GPS leedsGps = geoServiceWrapper.geocodeAddress(leedsAddress);

		BDDMockito.given(start.getAddressData()).willReturn(londonAddress);
		BDDMockito.given(dest.getAddressData()).willReturn(leedsAddress);
		BDDMockito.given(start.getGPS()).willReturn(londonGps);
		BDDMockito.given(dest.getGPS()).willReturn(leedsGps);
		final DistanceAndRoute route = geoServiceWrapper.getDistanceAndRoute(start, dest);
		Assert.assertNotNull(route);
		Assert.assertEquals(272.0, route.getEagleFliesDistance(), 1);
		Assert.assertEquals(313306.0, route.getRoadDistance(), 1);
		Assert.assertNotNull(route.getRoute());
	}
}
