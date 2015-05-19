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
package de.hybris.platform.acceleratorservices.store.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorservices.customer.impl.DefaultCustomerLocationService;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class DefaultLocalStorePreferencesServiceTest
{
	private static final double LATITUDE = 123.0;
	private static final double LONGITUDE = 45.0;
	private static final String LOCATION = "Tokyo";

	private DefaultLocalStorePreferencesService defaultLocalStorePreferencesService;

	@Mock
	private SessionService sessionService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CustomerLocationService customerLocationService;
	@Mock
	private StoreFinderService storeFinderService;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private PointOfServiceModel pointOfServiceModel;

	private UserLocationData userLocationData;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultLocalStorePreferencesService = new DefaultLocalStorePreferencesService();
		defaultLocalStorePreferencesService.setBaseStoreService(baseStoreService);
		defaultLocalStorePreferencesService.setSessionService(sessionService);
		defaultLocalStorePreferencesService.setStoreFinderService(storeFinderService);
		defaultLocalStorePreferencesService.setCustomerLocationService(customerLocationService);

		final GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLatitude(LATITUDE);
		geoPoint.setLongitude(LONGITUDE);

		userLocationData = new UserLocationData();
		userLocationData.setSearchTerm(LOCATION);
		userLocationData.setPoint(geoPoint);

		given(customerLocationService.getUserLocation()).willReturn(userLocationData);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getPointsOfService()).willReturn(Collections.singletonList(pointOfServiceModel));
	}



	@Test
	public void testGetAllPointsOfService()
	{
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchData = mock(StoreFinderSearchPageData.class);
		final List<PointOfServiceDistanceData> posData = new ArrayList<PointOfServiceDistanceData>(1);
		posData.add(new PointOfServiceDistanceData());

		given(searchData.getResults()).willReturn(posData);
		given(storeFinderService.positionSearch(any(BaseStoreModel.class), any(GeoPoint.class), any(PageableData.class)))
				.willReturn(searchData);
		given(sessionService.getAttribute(DefaultCustomerLocationService.SESSION_USER_LOCATION_KEY)).willReturn(userLocationData);

		final List<PointOfServiceDistanceData> result = defaultLocalStorePreferencesService.getAllPointsOfService();

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		verify(sessionService).setAttribute(DefaultLocalStorePreferencesService.SESSION_NEAREST_STORES_KEY, result);
	}



	@Test
	public void testGetAllPointsOfServiceFromSession()
	{
		final List<PointOfServiceDistanceData> posData = new ArrayList<PointOfServiceDistanceData>(1);
		posData.add(new PointOfServiceDistanceData());

		given(sessionService.getAttribute(DefaultLocalStorePreferencesService.SESSION_NEAREST_STORES_KEY)).willReturn(posData);

		final List<PointOfServiceDistanceData> result = defaultLocalStorePreferencesService.getAllPointsOfService();

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		verify(storeFinderService, times(0))
				.positionSearch(any(BaseStoreModel.class), any(GeoPoint.class), any(PageableData.class));
		verify(sessionService, times(0)).setAttribute(DefaultLocalStorePreferencesService.SESSION_NEAREST_STORES_KEY, result);
	}



	@Test
	public void testGetFavoritesPOSNotNull()
	{
		final List<PointOfServiceModel> result = defaultLocalStorePreferencesService.getFavoritePointOfServices();

		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}



	@Test
	public void testGetFavoritesPOS()
	{
		final List<PointOfServiceModel> favorites = new ArrayList<PointOfServiceModel>();
		favorites.add(mock(PointOfServiceModel.class));
		favorites.add(mock(PointOfServiceModel.class));
		favorites.add(mock(PointOfServiceModel.class));

		given(sessionService.getAttribute(DefaultLocalStorePreferencesService.SESSION_FAVOURITES_STORES_KEY)).willReturn(favorites);

		final List<PointOfServiceModel> result = defaultLocalStorePreferencesService.getFavoritePointOfServices();

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.size());
	}



	@Test
	public void testSetFavoritesPOS()
	{
		final List<PointOfServiceModel> favorites = new ArrayList<PointOfServiceModel>();
		favorites.add(mock(PointOfServiceModel.class));
		favorites.add(mock(PointOfServiceModel.class));

		defaultLocalStorePreferencesService.setFavoritesPointOfServices(favorites);

		verify(sessionService).setAttribute(DefaultLocalStorePreferencesService.SESSION_FAVOURITES_STORES_KEY, favorites);
	}



	@Test
	public void testAddToFavoritesPOS()
	{
		final List<PointOfServiceModel> favorites = new ArrayList<PointOfServiceModel>();
		favorites.add(mock(PointOfServiceModel.class));
		favorites.add(mock(PointOfServiceModel.class));

		final PointOfServiceModel newPOS = mock(PointOfServiceModel.class);

		given(sessionService.getAttribute(DefaultLocalStorePreferencesService.SESSION_FAVOURITES_STORES_KEY)).willReturn(favorites);

		defaultLocalStorePreferencesService.addToFavouritePointOfServices(newPOS);

		Assert.assertEquals(3, favorites.size());
		Assert.assertTrue(favorites.contains(newPOS));
	}



	@Test
	public void testRemoveFromFavoritesPOS()
	{
		final List<PointOfServiceModel> favorites = new ArrayList<PointOfServiceModel>();
		favorites.add(mock(PointOfServiceModel.class));
		favorites.add(mock(PointOfServiceModel.class));

		final PointOfServiceModel newPOS = mock(PointOfServiceModel.class);
		favorites.add(newPOS);

		given(sessionService.getAttribute(DefaultLocalStorePreferencesService.SESSION_FAVOURITES_STORES_KEY)).willReturn(favorites);

		defaultLocalStorePreferencesService.removeFromFavoritePointOfServices(newPOS);

		Assert.assertEquals(2, favorites.size());
		Assert.assertFalse(favorites.contains(newPOS));

	}

}
