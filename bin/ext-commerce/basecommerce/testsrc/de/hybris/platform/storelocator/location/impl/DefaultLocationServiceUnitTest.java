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
package de.hybris.platform.storelocator.location.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.LocationInstantiationException;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.exception.PointOfServiceDaoException;
import de.hybris.platform.storelocator.location.DistanceAwareLocation;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 *
 */
@UnitTest
public class DefaultLocationServiceUnitTest
{
	@Spy
	private final DefaultLocationService service = new DefaultLocationService();


	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		service.setPointOfServiceDao(pointOfServiceDao);
	}

	@Test
	public void testGetSortedLocationsNearbyWithNotNullDistance() throws LocationServiceException, PointOfServiceDaoException,
			GeoLocatorException
	{
		final PointOfServiceModel posOne = Mockito.mock(PointOfServiceModel.class);
		BDDMockito.given(posOne.getName()).willReturn("one");

		final PointOfServiceModel posTwo = Mockito.mock(PointOfServiceModel.class);
		BDDMockito.given(posTwo.getName()).willReturn("two");

		final PointOfServiceModel posThree = Mockito.mock(PointOfServiceModel.class);
		BDDMockito.given(posThree.getName()).willReturn("three");

		final GPS gps = Mockito.mock(GPS.class);
		final BaseStoreModel store = Mockito.mock(BaseStoreModel.class);
		final double distance = 1012.91;

		BDDMockito.given(pointOfServiceDao.getAllGeocodedPOS(gps, distance, store)).willReturn(
				Arrays.asList(posOne, posTwo, posThree));

		Mockito.doReturn(Double.valueOf(10.0)).when(service).calculateDistance(gps, posOne);
		Mockito.doReturn(Double.valueOf(100.0)).when(service).calculateDistance(gps, posTwo);
		Mockito.doReturn(Double.valueOf(1.0)).when(service).calculateDistance(gps, posThree);

		final List<DistanceAwareLocation> result = service.getSortedLocationsNearby(gps, distance, store);

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.get(0).getDistance());
		Assert.assertEquals("three", result.get(0).getName());

		Assert.assertNotNull(result.get(1).getDistance());
		Assert.assertEquals("one", result.get(1).getName());

		Assert.assertNotNull(result.get(2).getDistance());
		Assert.assertEquals("two", result.get(2).getName());

		//check order
	}

	@Test
	public void testGetSortedLocationNearbyWithNullLatitudeDistance() throws LocationServiceException, PointOfServiceDaoException,
			LocationInstantiationException
	{
		final PointOfServiceModel posOne = Mockito.mock(PointOfServiceModel.class);
		BDDMockito.given(posOne.getName()).willReturn("one");
		BDDMockito.given(posOne.getLatitude()).willReturn(null);
		BDDMockito.given(posOne.getLongitude()).willReturn(Double.valueOf(45));


		final GPS gps = Mockito.mock(GPS.class);
		final BaseStoreModel store = Mockito.mock(BaseStoreModel.class);
		final double distance = 1012.91;

		BDDMockito.given(pointOfServiceDao.getAllGeocodedPOS(gps, distance, store)).willReturn(Arrays.asList(posOne));
		try
		{
			service.getSortedLocationsNearby(gps, distance, store);
			Assert.fail("PoS has a null latitude");
		}
		catch (final LocationServiceException lse)
		{
			//expected
		}
	}

	@Test
	public void testGetSortedLocationNearbyWithNullLongitudeDistance() throws LocationServiceException,
			PointOfServiceDaoException, LocationInstantiationException
	{
		final PointOfServiceModel posOne = Mockito.mock(PointOfServiceModel.class);
		BDDMockito.given(posOne.getName()).willReturn("one");
		BDDMockito.given(posOne.getLatitude()).willReturn(Double.valueOf(45));
		BDDMockito.given(posOne.getLongitude()).willReturn(null);


		final GPS gps = Mockito.mock(GPS.class);
		final BaseStoreModel store = Mockito.mock(BaseStoreModel.class);
		final double distance = 1012.91;

		BDDMockito.given(pointOfServiceDao.getAllGeocodedPOS(gps, distance, store)).willReturn(Arrays.asList(posOne));
		try
		{
			service.getSortedLocationsNearby(gps, distance, store);
			Assert.fail("PoS has a null longitude");
		}
		catch (final LocationServiceException lse)
		{
			//expected
		}
	}
}
