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
package de.hybris.platform.commercefacades.storelocator.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.helpers.DistanceHelper;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.exception.LocationInstantiationException;
import de.hybris.platform.storelocator.exception.LocationMapServiceException;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.storelocator.location.DistanceAwareLocation;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.LocationMapService;
import de.hybris.platform.storelocator.location.LocationService;
import de.hybris.platform.storelocator.location.impl.DefaultLocation;
import de.hybris.platform.storelocator.map.Map;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.map.impl.DefaultMapService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Test suite for {@link DefaultStoreLocatorFacade}
 */
@UnitTest
public class DefaultStoreLocatorFacadeTest
{
	private static final int LOCATIONS_LIMIT = 3;
	private static final String SEARCH_TERM = "tokio";
	private static final String COUNTRY_CODE = "ja";
	private static final int STORE_COUNT = 5;
	private static final String POS_NAME = "somePos";
	private static final String MAP_NAME = "storeMap";
	private static final Double DISTANCE = Double.valueOf(12.3D);
	private static final String FORMATTED_DISTANCE = "12.3km";

	private DefaultStoreLocatorFacade defaultStoreLocatorFacade;

	@Mock
	private CommonI18NService commonI18NService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private BaseSiteService siteService;
	@Mock
	private PointOfServiceService pointOfServiceService;
	@Spy
	private final MapService mapService = new DefaultMapService();
	@Mock
	private AbstractPopulatingConverter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	@Mock
	private LocationService<Location> locationService;
	@Mock
	private LocationMapService locationMapService;
	@Mock
	private DistanceHelper distanceHelper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultStoreLocatorFacade = new DefaultStoreLocatorFacade();
		defaultStoreLocatorFacade.setBaseSiteService(siteService);
		defaultStoreLocatorFacade.setCommonI18NService(commonI18NService);
		defaultStoreLocatorFacade.setMapService(mapService);
		defaultStoreLocatorFacade.setPointOfServiceConverter(pointOfServiceConverter);
		defaultStoreLocatorFacade.setPointOfServiceService(pointOfServiceService);
		defaultStoreLocatorFacade.setLocationMapService(locationMapService);
		defaultStoreLocatorFacade.setDistanceHelper(distanceHelper);
		defaultStoreLocatorFacade.setLocationService(locationService);
	}

	@Test
	public void testGetNullLocationsForNullTerm() throws MapServiceException
	{
		final Map result = defaultStoreLocatorFacade.getLocationsForQuery(null, STORE_COUNT);
		Assert.assertNotNull(result);
		Assert.assertEquals(Collections.EMPTY_LIST, result.getPointsOfInterest());
		Assert.assertNull(result.getDistanceAndRoute());
		Assert.assertNull(result.getTitle());
		Assert.assertNull(result.getGps());
		Assert.assertNull(result.getKml());
		Assert.assertNull(result.getMapBounds());
		Assert.assertEquals(Double.valueOf(0.0d), Double.valueOf(result.getRadius()));
	}

	@Test
	public void testGetLocationsForQuery() throws MapServiceException
	{
		final LanguageModel currentLanguage = mock(LanguageModel.class);
		final BaseSiteModel currentSite = mock(BaseSiteModel.class);
		final BaseStoreModel storeModel = mock(BaseStoreModel.class);
		final Map map = mock(Map.class);
		final GPS centerPosition = mock(GPS.class);
		final Location locationFar = mock(DistanceAwareLocation.class);
		final Location locationNear = mock(DistanceAwareLocation.class);
		final Location locationFail = mock(Location.class);
		final Map resultMap = mock(Map.class);

		final List<Location> locations = new ArrayList<Location>();
		locations.add(locationFar);
		locations.add(locationNear);
		locations.add(locationFail);

		given(map.getGps()).willReturn(centerPosition);
		given(map.getPointsOfInterest()).willReturn(locations);
		given(siteService.getCurrentBaseSite()).willReturn(currentSite);
		given(currentSite.getStores()).willReturn(Collections.singletonList(storeModel));
		given(commonI18NService.getCurrentLanguage()).willReturn(currentLanguage);
		given(currentLanguage.getIsocode()).willReturn(COUNTRY_CODE);
		given(locationMapService.getMapOfLocations(SEARCH_TERM, COUNTRY_CODE, STORE_COUNT, storeModel)).willReturn(map);

		doReturn(resultMap).when(mapService).getMap(centerPosition, STORE_COUNT, locations, MAP_NAME);

		final Map result = defaultStoreLocatorFacade.getLocationsForQuery(SEARCH_TERM, STORE_COUNT);
		Assert.assertNotNull(result);
		Assert.assertEquals(resultMap, result);
	}


	@Test
	public void testThrowGetLocationsForQuery() throws MapServiceException
	{
		final LanguageModel currentLanguage = mock(LanguageModel.class);
		final BaseSiteModel currentSite = mock(BaseSiteModel.class);
		final BaseStoreModel storeModel = mock(BaseStoreModel.class);

		given(commonI18NService.getCurrentLanguage()).willReturn(currentLanguage);
		given(siteService.getCurrentBaseSite()).willReturn(currentSite);
		given(currentSite.getStores()).willReturn(Collections.singletonList(storeModel));
		given(currentLanguage.getIsocode()).willReturn(COUNTRY_CODE);
		given(locationMapService.getMapOfLocations(SEARCH_TERM, COUNTRY_CODE, STORE_COUNT, storeModel)).willThrow(
				new LocationMapServiceException());

		defaultStoreLocatorFacade.getLocationsForQuery(SEARCH_TERM, STORE_COUNT);
		verify(mapService, Mockito.never()).getMap(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.anyList(),
				Mockito.anyString());
	}


	@Test
	public void testGetPosForNameForNonExistingName() throws LocationServiceException
	{

		given(locationService.getLocationByName(POS_NAME)).willThrow(new LocationServiceException("No such location"));
		try
		{
			defaultStoreLocatorFacade.getPOSForName(POS_NAME);
			Assert.fail("Should throw a LocationServiceException");
		}
		catch (final LocationServiceException lse)
		{
			//ok here
		}
	}

	@Test
	public void testGetPosForName() throws LocationServiceException
	{
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		final PointOfServiceData pointOfServiceData = mock(PointOfServiceData.class);

		final Location locationFar = mock(DistanceAwareLocation.class);
		given(locationFar.getName()).willReturn(POS_NAME);

		given(locationService.getLocationByName(POS_NAME)).willReturn(locationFar);
		given(pointOfServiceService.getPointOfServiceForName(POS_NAME)).willReturn(pointOfServiceModel);
		given(pointOfServiceConverter.convert(pointOfServiceModel)).willReturn(pointOfServiceData);

		final PointOfServiceData result = defaultStoreLocatorFacade.getPOSForName(POS_NAME);

		Assert.assertEquals(pointOfServiceData, result);
	}

	@Test
	public void testGetPosForLocation()
	{
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		final PointOfServiceData pointOfServiceData = mock(PointOfServiceData.class);
		final DistanceAwareLocation location = mock(DistanceAwareLocation.class);
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);

		given(pointOfServiceService.getPointOfServiceForName(POS_NAME)).willReturn(pointOfServiceModel);
		given(pointOfServiceConverter.convert(pointOfServiceModel)).willReturn(pointOfServiceData);
		given(location.getName()).willReturn(POS_NAME);
		given((location).getDistance()).willReturn(DISTANCE);
		given(pointOfServiceModel.getBaseStore()).willReturn(baseStoreModel);
		given(distanceHelper.getDistanceStringForStore(baseStoreModel, DISTANCE.doubleValue())).willReturn(FORMATTED_DISTANCE);

		final PointOfServiceData result = defaultStoreLocatorFacade.getPOSForLocation(location);
		Assert.assertEquals(pointOfServiceData, result);
		verify(pointOfServiceData).setFormattedDistance(FORMATTED_DISTANCE);
	}


	/**
	 * This test checks 3 things(!!)
	 * <p>
	 * <ul>
	 * <li>is the locations list concatenated - even when for one store there is an exception thrown</li>
	 * <li>is the limit of locations respected</li>
	 * <li>is the order of that locations respected</li> *
	 * </ul>
	 * 
	 * @throws LocationInstantiationException
	 * 
	 */
	@Test
	public void testThreeBaseStoresMiddleOneHasNoLocation() throws MapServiceException, LocationInstantiationException
	{
		final PointOfServiceModel poiModel = mock(PointOfServiceModel.class);
		given(poiModel.getLatitude()).willReturn(Double.valueOf(52.210533));
		given(poiModel.getLongitude()).willReturn(Double.valueOf(0.156842));

		final Location locationTokyoOne = new DefaultLocation(poiModel, Double.valueOf(123.3));
		final Location locationTokyoTwo = new DefaultLocation(poiModel, Double.valueOf(23.3));
		final List<Location> tokyoLocations = Arrays.asList(locationTokyoOne, locationTokyoTwo);

		final Location locationKobeOne = new DefaultLocation(poiModel, Double.valueOf(12.3));
		final Location locationKobeTwo = new DefaultLocation(poiModel, Double.valueOf(3.3));
		final List<Location> kobeLocations = Arrays.asList(locationKobeOne, locationKobeTwo);

		final LanguageModel currentLanguage = mock(LanguageModel.class);

		final GPS tokyoGps = mock(GPS.class);
		final GPS kobeGps = mock(GPS.class);

		final BaseStoreModel tokyoStore = mock(BaseStoreModel.class);
		final BaseStoreModel kobeStore = mock(BaseStoreModel.class);
		final BaseStoreModel sapporoStore = mock(BaseStoreModel.class);



		final Map tokyoLocationMap = mock(Map.class);
		given(tokyoLocationMap.getPointsOfInterest()).willReturn(tokyoLocations);
		given(tokyoLocationMap.getGps()).willReturn(tokyoGps);
		final Map kobeLocationMap = mock(Map.class);
		given(kobeLocationMap.getPointsOfInterest()).willReturn(kobeLocations);
		given(kobeLocationMap.getGps()).willReturn(kobeGps);

		final List<BaseStoreModel> baseStores = Arrays.asList(tokyoStore, kobeStore, sapporoStore);


		given(currentLanguage.getIsocode()).willReturn("jpa");
		given(commonI18NService.getCurrentLanguage()).willReturn(currentLanguage);
		given(siteService.getCurrentBaseSite().getStores()).willReturn(baseStores);

		given(
				locationMapService.getMapOfLocations(Mockito.eq(SEARCH_TERM), Mockito.eq("jpa"), Mockito.eq(LOCATIONS_LIMIT),
						Mockito.eq(tokyoStore))).willReturn(tokyoLocationMap);
		given(
				locationMapService.getMapOfLocations(Mockito.eq(SEARCH_TERM), Mockito.eq("jpa"), Mockito.eq(LOCATIONS_LIMIT),
						Mockito.eq(kobeStore))).willReturn(kobeLocationMap);
		given(
				locationMapService.getMapOfLocations(Mockito.eq(SEARCH_TERM), Mockito.eq("jpa"), Mockito.eq(LOCATIONS_LIMIT),
						Mockito.eq(sapporoStore))).willThrow(new LocationMapServiceException());

		final Map result = defaultStoreLocatorFacade.getLocationsForQuery(SEARCH_TERM, LOCATIONS_LIMIT);

		Assert.assertNotNull(result);
		Assert.assertEquals("Center should be the 'first' location", tokyoGps, result.getGps());
		Assert.assertEquals("Result should contain all available locations ", LOCATIONS_LIMIT, result.getPointsOfInterest().size());//limit

		Assert.assertEquals("Order should be dependent of distance ", locationKobeTwo, result.getPointsOfInterest().get(0));
		Assert.assertEquals("Order should be dependent of distance ", locationKobeOne, result.getPointsOfInterest().get(1));
		Assert.assertEquals("Order should be dependent of distance ", locationTokyoTwo, result.getPointsOfInterest().get(2));

	}


	@Test
	public void testTwoBaseStores() throws MapServiceException, LocationInstantiationException
	{
		final PointOfServiceModel poiModel = mock(PointOfServiceModel.class);
		given(poiModel.getLatitude()).willReturn(Double.valueOf(52.210533));
		given(poiModel.getLongitude()).willReturn(Double.valueOf(0.156842));

		final Location locationTokyoOne = new DefaultLocation(poiModel, Double.valueOf(123.3));
		final Location locationTokyoTwo = new DefaultLocation(poiModel, Double.valueOf(23.3));
		final List<Location> tokyoLocations = Arrays.asList(locationTokyoOne, locationTokyoTwo);

		final Location locationKobeOne = new DefaultLocation(poiModel, Double.valueOf(12.3));
		final Location locationKobeTwo = new DefaultLocation(poiModel, Double.valueOf(3.3));
		final List<Location> kobeLocations = Arrays.asList(locationKobeOne, locationKobeTwo);

		final LanguageModel currentLanguage = mock(LanguageModel.class);

		final GPS tokyoGps = mock(GPS.class);
		final GPS kobeGps = mock(GPS.class);

		final BaseStoreModel tokyoStore = mock(BaseStoreModel.class);
		final BaseStoreModel kobeStore = mock(BaseStoreModel.class);


		final Map tokyoLocationMap = mock(Map.class);
		given(tokyoLocationMap.getPointsOfInterest()).willReturn(tokyoLocations);
		given(tokyoLocationMap.getGps()).willReturn(tokyoGps);
		final Map kobeLocationMap = mock(Map.class);
		given(kobeLocationMap.getPointsOfInterest()).willReturn(kobeLocations);
		given(kobeLocationMap.getGps()).willReturn(kobeGps);

		final List<BaseStoreModel> baseStores = Arrays.asList(tokyoStore, kobeStore);


		given(currentLanguage.getIsocode()).willReturn("jpa");
		given(commonI18NService.getCurrentLanguage()).willReturn(currentLanguage);
		given(siteService.getCurrentBaseSite().getStores()).willReturn(baseStores);

		given(
				locationMapService.getMapOfLocations(Mockito.eq(SEARCH_TERM), Mockito.eq("jpa"), Mockito.eq(LOCATIONS_LIMIT),
						Mockito.eq(tokyoStore))).willReturn(tokyoLocationMap);
		given(
				locationMapService.getMapOfLocations(Mockito.eq(SEARCH_TERM), Mockito.eq("jpa"), Mockito.eq(LOCATIONS_LIMIT),
						Mockito.eq(kobeStore))).willReturn(kobeLocationMap);

		final Map result = defaultStoreLocatorFacade.getLocationsForQuery(SEARCH_TERM, LOCATIONS_LIMIT);

		Assert.assertNotNull(result);
		Assert.assertEquals("Center should be the 'first' location", tokyoGps, result.getGps());
		Assert.assertEquals("Result should contain all available locations ", LOCATIONS_LIMIT, result.getPointsOfInterest().size());//limit

		Assert.assertEquals("Order should be dependent of distance ", locationKobeTwo, result.getPointsOfInterest().get(0));
		Assert.assertEquals("Order should be dependent of distance ", locationKobeOne, result.getPointsOfInterest().get(1));
		Assert.assertEquals("Order should be dependent of distance ", locationTokyoTwo, result.getPointsOfInterest().get(2));

	}


}
