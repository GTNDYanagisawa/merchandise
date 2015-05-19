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
package de.hybris.platform.storelocator.location;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.exception.LocationInstantiationException;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.location.impl.DefaultLocationMapService;
import de.hybris.platform.storelocator.location.impl.DefaultLocationService;
import de.hybris.platform.storelocator.location.impl.DistanceUnawareLocation;
import de.hybris.platform.storelocator.map.Map;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.impl.DefaultPointOfServiceService;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


public class LocationMapServiceTest
{
	private static final double RADIUS_MAX = 100D;
	private static final double RADIUS_STEP = 20D;

	@Mock
	private GeoWebServiceWrapper geoServiceWrapper;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private MapService mapService;

	private final DefaultPointOfServiceService pointOfServiceService = new DefaultPointOfServiceService();

	@Spy
	private final LocationService locationService = new DefaultLocationService();

	private final DefaultLocationMapService locationMapService = new DefaultLocationMapService();

	private BaseStoreModel baseStore;
	private GPS gps;

	private List<Location> locations;
	private List<Location> locationsNearby;

	@Before
	public void setUp() throws GeoLocatorException, LocationInstantiationException
	{
		MockitoAnnotations.initMocks(this);

		//locationMapService.setDistanceAwareLocationService(distanceAwareLocationService);
		locationMapService.setGeoServiceWrapper(geoServiceWrapper);
		locationMapService.setMapService(mapService);
		locationMapService.setRadiusMax(RADIUS_MAX);
		locationMapService.setRadiusStep(RADIUS_STEP);
		locationMapService.setDistanceAwareLocationService(locationService);

		doReturn(locationMapService).when((DefaultLocationService) locationService).lookupLocationMapService();
		((DefaultLocationService) locationService).setMapService(mapService);
		((DefaultLocationService) locationService).setLocationMapService(locationMapService);

		pointOfServiceService.setPointOfServiceDao(pointOfServiceDao);

		baseStore = new BaseStoreModel();
		baseStore.setUid("littleShop");

		gps = new DefaultGPS();
		gps = gps.create("20\u00B0S", "150\u00B0W");

		final PointOfServiceModel pos = new PointOfServiceModel();
		pos.setName("myPos");
		pos.setBaseStore(baseStore);

		final Location location = new DistanceUnawareLocation(pos);
		locations = new ArrayList<Location>();
		locations.add(location);

		locationsNearby = new ArrayList<Location>();
		locationsNearby.add(location);
		for (int i = 0; i < 9; i++)
		{
			locationsNearby.add(new DistanceUnawareLocation(pos));
		}
	}

	@Test
	public void testGetMapOfLocations() throws GeoServiceWrapperException, LocationServiceException, MapServiceException
	{
		final Map map = Mockito.mock(Map.class);
		final MapService localMapService = Mockito.mock(MapService.class);
		locationMapService.setMapService(localMapService);

		when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.doReturn(locations).when(locationService)
				.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(), Mockito.argThat(baseStoreMatcher()));
		when(localMapService.getMap(gps, 100D, locations, "")).thenReturn(map);

		final Map result = locationMapService.getMapOfLocations("44-100", "PL", 10, baseStore);
		Assert.assertEquals(map, result);
	}

	private ArgumentMatcher<GPS> gpsMatcher()
	{
		return new ArgumentMatcher<GPS>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				return argument instanceof GPS && argument.toString().equals(gps.toString());
			}
		};
	}

	private ArgumentMatcher<BaseStoreModel> baseStoreMatcher()
	{
		return new ArgumentMatcher<BaseStoreModel>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				return argument instanceof BaseStoreModel && argument.equals(baseStore);
			}
		};
	}

}
