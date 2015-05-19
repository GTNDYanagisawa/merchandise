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

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.AbstractGeocodingTest;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
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
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


public class LocationServiceTest extends AbstractGeocodingTest
{
	private static final double RADIUS_MAX = 100D;
	private static final double RADIUS_STEP = 20D;

	@Mock
	private GeoWebServiceWrapper geoServiceWrapper;

	@Mock
	private LocationService mockedLocationService;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private MapService mapService;

	@Spy
	private final DefaultLocationService locationService = new DefaultLocationService();

	private final DefaultPointOfServiceService pointOfServiceService = new DefaultPointOfServiceService();

	private final DefaultLocationMapService locationMapService = new DefaultLocationMapService();

	private BaseStoreModel baseStore;
	private GPS gps;

	private List<Location> locations;
	private List<Location> locationsNearby;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		locationMapService.setDistanceAwareLocationService(mockedLocationService);
		locationMapService.setGeoServiceWrapper(geoServiceWrapper);
		locationMapService.setMapService(mapService);
		locationMapService.setRadiusMax(RADIUS_MAX);
		locationMapService.setRadiusStep(RADIUS_STEP);

		doReturn(locationMapService).when(locationService).lookupLocationMapService();

		locationService.setMapService(mapService);
		locationService.setPointOfServiceDao(pointOfServiceDao);
		locationService.setModelService(modelService);
		locationService.setI18nService(i18nService);
		locationService.setLocationMapService(locationMapService);

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
	public void testGetLocationsNearbyByPostcode() throws GeoServiceWrapperException, LocationServiceException,
			MapServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForPostcode("44-100", "PL", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLocationsNearbyByPostcodeInputData() throws GeoServiceWrapperException, LocationServiceException
	{
		Mockito.when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForPostcode("", "", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	@Test
	public void testGetLocationsNearbyByTown() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForTown("Gliwice", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLocationsNearbyByTownInputData() throws GeoServiceWrapperException, LocationServiceException
	{
		Mockito.when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForTown("", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	//testing situation, when we can return all found shops
	@Test
	public void testGetLocationsNearby() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.when(geoServiceWrapper.geocodeAddress((Location) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForPoint(gps, 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLocationsNearbyInputData() throws LocationServiceException
	{
		locationService.getLocationsForPoint(null, 10, baseStore);
		Assert.fail("The IllegalArgumentException should be thrown.");
	}

	//testing situation, when there were more shops found than user have configured
	@Test
	public void testGetLocationsNearbySubList() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.when(geoServiceWrapper.geocodeAddress((Location) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locationsNearby);
		Mockito.when(mapService.getMap(gps, RADIUS_STEP, locations, "")).thenReturn(map);
		Mockito.when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForPoint(gps, 1, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	//testing situation radius
	@Test
	public void testGetLocationsNearbyRadius() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.when(geoServiceWrapper.geocodeAddress((Location) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.eq(500.0),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locationsNearby.subList(0, 2));
		Mockito.when(mapService.getMap(gps, RADIUS_MAX, Collections.<Location> emptyList(), "")).thenReturn(map);
		Mockito.when(map.getPointsOfInterest()).thenReturn(locationsNearby.subList(0, 2));

		final List<Location> listLocation = locationService.getLocationsForPoint(gps, 2, baseStore);
		Assert.assertEquals(2, listLocation.size());
	}

	@Test
	public void testGetLocationsForSearch() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);
		Mockito.when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.when(
				mockedLocationService.getSortedLocationsNearby(Mockito.argThat(gpsMatcher()), Mockito.anyDouble(),
						Mockito.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForSearch("44-100", "PL", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
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

}
