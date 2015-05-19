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
package de.hybris.platform.acceleratorservices.storelocator.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.storelocator.populators.PointOfServiceDistancePopulator;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.helpers.DistanceHelper;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



@UnitTest
public class PointOfServiceDistancePopulatorTest
{
	private static final Double DISTANCE = Double.valueOf(20.0);
	private static final String FORMATTED_DISTANCE = "20.0";

	private final PointOfServiceDistancePopulator populator = new PointOfServiceDistancePopulator();

	@Mock
	private CustomerLocationService customerLocationService;
	@Mock
	private DistanceHelper distanceHelper;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		populator.setCustomerLocationService(customerLocationService);
		populator.setDistanceHelper(distanceHelper);
	}

	@Test
	public void testPopulate()
	{
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		final BaseStoreModel baseStore = mock(BaseStoreModel.class);

		final GeoPoint point = mock(GeoPoint.class);
		final UserLocationData userLocationData = mock(UserLocationData.class);
		given(userLocationData.getPoint()).willReturn(point);
		given(customerLocationService.getUserLocation()).willReturn(userLocationData);


		given(Double.valueOf(customerLocationService.calculateDistance(point, pointOfServiceModel))).willReturn(DISTANCE);
		given(pointOfServiceModel.getBaseStore()).willReturn(baseStore);
		given(distanceHelper.getDistanceStringForStore(baseStore, DISTANCE.doubleValue())).willReturn(FORMATTED_DISTANCE);
		given(distanceHelper.getDistanceStringForStore(baseStore, Double.parseDouble("0.0"))).willReturn(FORMATTED_DISTANCE);

		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		populator.populate(pointOfServiceModel, pointOfServiceData);

		Assert.assertEquals(DISTANCE, pointOfServiceData.getDistanceKm());
		Assert.assertEquals(FORMATTED_DISTANCE, pointOfServiceData.getFormattedDistance());
	}

	@Test
	public void testPopulateWhenCustomerLocationIsNotSet()
	{
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		given(customerLocationService.getUserLocation()).willReturn(null);

		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		populator.populate(pointOfServiceModel, pointOfServiceData);

		Assert.assertNull(pointOfServiceData.getDistanceKm());
		Assert.assertNull(pointOfServiceData.getFormattedDistance());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullSource()
	{
		final PointOfServiceData pointOfServiceData = new PointOfServiceData();
		populator.populate(null, pointOfServiceData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullTarget()
	{
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);
		populator.populate(pointOfServiceModel, null);
	}
}
