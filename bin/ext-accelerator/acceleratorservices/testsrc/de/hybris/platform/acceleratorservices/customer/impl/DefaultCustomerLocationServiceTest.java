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
package de.hybris.platform.acceleratorservices.customer.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCustomerLocationServiceTest
{
	private static final double LATITUDE = 123.0;
	private static final double LONGITUDE = 45.0;
	private static final String LOCATION = "Tokyo";
	private static final double DELTA = 0.01;


	private DefaultCustomerLocationService service;

	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private GeoWebServiceWrapper geoWebServiceWrapper;
	@Mock
	private SessionService sessionService;
	@Mock
	private PointOfServiceModel pointOfServiceModel;
	@Mock
	private BaseStoreModel baseStoreModel;

	private UserLocationData userLocationData;
	private GeoPoint geoPoint;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		service = new DefaultCustomerLocationService();
		service.setBaseStoreService(baseStoreService);
		service.setGeoWebServiceWrapper(geoWebServiceWrapper);
		service.setSessionService(sessionService);

		geoPoint = new GeoPoint();
		geoPoint.setLatitude(LATITUDE);
		geoPoint.setLongitude(LONGITUDE);

		userLocationData = new UserLocationData();
		userLocationData.setSearchTerm(LOCATION);
		userLocationData.setPoint(geoPoint);

		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getPointsOfService()).willReturn(Collections.singletonList(pointOfServiceModel));

	}



	@Test
	public void testSetUserLocation()
	{
		userLocationData.setPoint(null);

		final GPS gps = mock(GPS.class);
		given(Double.valueOf(gps.getDecimalLatitude())).willReturn(Double.valueOf(LATITUDE));
		given(Double.valueOf(gps.getDecimalLongitude())).willReturn(Double.valueOf(LONGITUDE));

		given(geoWebServiceWrapper.geocodeAddress(any(AddressData.class))).willReturn(gps);

		service.setUserLocation(userLocationData);

		Assert.assertEquals(LATITUDE, userLocationData.getPoint().getLatitude(), DELTA);
		verify(sessionService).setAttribute(DefaultCustomerLocationService.SESSION_USER_LOCATION_KEY, userLocationData);
	}


	@Test
	public void testSetUserLocationFromCache()
	{
		userLocationData.setPoint(null);

		final Map<String, GeoPoint> locationsCache = new HashMap<String, GeoPoint>();
		locationsCache.put(LOCATION, geoPoint);

		given(sessionService.getAttribute(DefaultCustomerLocationService.SESSION_LOCATIONS_CACHE_KEY)).willReturn(locationsCache);

		service.setUserLocation(userLocationData);

		verify(geoWebServiceWrapper, times(0)).geocodeAddress(any(AddressData.class));
		verify(sessionService, times(0)).setAttribute(DefaultCustomerLocationService.SESSION_LOCATIONS_CACHE_KEY, userLocationData);
		Assert.assertEquals(LATITUDE, userLocationData.getPoint().getLatitude(), DELTA);
	}

}
