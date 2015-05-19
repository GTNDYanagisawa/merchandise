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
package de.hybris.platform.storelocator.map.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.location.DistanceAwareLocation;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.map.Map;
import de.hybris.platform.storelocator.map.utils.MapBounds;
import de.hybris.platform.storelocator.route.RouteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
public class DefaultMapServiceTest
{

	private final DefaultMapService mapService = new DefaultMapService();

	@Mock
	private RouteService routeService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		mapService.setRouteService(routeService);
	}

	@Test
	public void testGetMapBoundsForMap() throws GeoLocatorException
	{
		final GPS farPosition = mock(GPS.class);
		final GPS nearPosition = mock(GPS.class);
		final GPS centerPosition = mock(GPS.class);
		final Location locationFar = mock(DistanceAwareLocation.class);
		final Location locationNear = mock(DistanceAwareLocation.class);
		final List<Location> locations = new ArrayList<Location>();
		locations.add(locationFar);
		locations.add(locationNear);
		final Map map = mock(Map.class);
		given(map.getPointsOfInterest()).willReturn(locations);
		given(map.getGps()).willReturn(centerPosition);
		given(locationFar.getGPS()).willReturn(farPosition);
		given(locationNear.getGPS()).willReturn(nearPosition);
		given(Double.valueOf(farPosition.getDecimalLatitude())).willReturn(Double.valueOf(50.00));
		given(Double.valueOf(nearPosition.getDecimalLatitude())).willReturn(Double.valueOf(10.00));
		given(Double.valueOf(farPosition.getDecimalLongitude())).willReturn(Double.valueOf(30.00));
		given(Double.valueOf(nearPosition.getDecimalLongitude())).willReturn(Double.valueOf(14.00));

		given(farPosition.create(Mockito.anyDouble(), Mockito.anyDouble())).willReturn(farPosition);
		given(nearPosition.create(Mockito.anyDouble(), Mockito.anyDouble())).willReturn(nearPosition);
		final MapBounds mapBoundsData = mapService.getMapBoundsForMap(map);
		Assert.assertNotNull(mapBoundsData.getNorthEast());
		Assert.assertEquals(farPosition, mapBoundsData.getNorthEast());
		Assert.assertEquals(farPosition, mapBoundsData.getSouthWest());
	}

	@Test
	public void testGetMapBoundsNoPOS() throws GeoLocatorException
	{
		final Map map = mock(Map.class);
		given(map.getPointsOfInterest()).willReturn(Collections.EMPTY_LIST);

		final MapBounds mapBoundsData = mapService.getMapBoundsForMap(map);
		Assert.assertNull(mapBoundsData);
	}

	@Test
	public void testRecalculateBoundsAgainstMapCenter() throws GeoLocatorException
	{
		final GPS southWest = mock(GPS.class);
		final GPS northEast = mock(GPS.class);
		final GPS centerPosition = mock(GPS.class);
		given(Double.valueOf(southWest.getDecimalLatitude())).willReturn(Double.valueOf(25.00));
		given(Double.valueOf(southWest.getDecimalLongitude())).willReturn(Double.valueOf(30.00));
		given(Double.valueOf(northEast.getDecimalLatitude())).willReturn(Double.valueOf(10.00));
		given(Double.valueOf(northEast.getDecimalLongitude())).willReturn(Double.valueOf(50.00));
		given(Double.valueOf(centerPosition.getDecimalLatitude())).willReturn(Double.valueOf(5.00));
		given(Double.valueOf(centerPosition.getDecimalLongitude())).willReturn(Double.valueOf(5.00));

		mapService.recalculateBoundsAgainstMapCenter(southWest, northEast, centerPosition);
	}
}
