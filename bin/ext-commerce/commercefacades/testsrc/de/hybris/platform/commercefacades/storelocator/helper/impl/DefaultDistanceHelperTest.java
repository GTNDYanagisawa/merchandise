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
package de.hybris.platform.commercefacades.storelocator.helper.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.DistanceUnit;
import de.hybris.platform.commercefacades.storelocator.helpers.impl.DefaultDistanceHelper;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link DefaultDistanceHelperTest}
 */
@UnitTest
public class DefaultDistanceHelperTest
{
	private static final double DISTANCE_KM = 100;
	private static final double DISTANCE_MILES = 99.78;
	private static final String FORMATTED_DISTANCE_KM = "100 km";
	private static final String FORMATTED_DISTANCE_MILES = "62 miles";
	private static final String LOCATION_NAME = "location";


	@Mock
	private PointOfServiceService pointOfServiceService;
	@Mock
	private EnumerationService enumerationService;

	private DefaultDistanceHelper defaultDistanceHelper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		given(enumerationService.getEnumerationName(DistanceUnit.KM)).willReturn("km");
		given(enumerationService.getEnumerationName(DistanceUnit.MILES)).willReturn("miles");

		defaultDistanceHelper = new DefaultDistanceHelper();
		defaultDistanceHelper.setEnumerationService(enumerationService);
		defaultDistanceHelper.setPointOfServiceService(pointOfServiceService);
	}


	@Test
	public void testGetDistanceStringMetric()
	{
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);

		given(baseStoreModel.getStorelocatorDistanceUnit()).willReturn(DistanceUnit.KM);

		final String result = defaultDistanceHelper.getDistanceStringForStore(baseStoreModel, DISTANCE_KM);

		Assert.assertEquals(FORMATTED_DISTANCE_KM, result);
	}


	@Test
	public void testGetDistanceStringImperial()
	{
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);

		given(baseStoreModel.getStorelocatorDistanceUnit()).willReturn(DistanceUnit.MILES);

		final String result = defaultDistanceHelper.getDistanceStringForStore(baseStoreModel, DISTANCE_MILES);

		Assert.assertEquals(FORMATTED_DISTANCE_MILES, result);
	}


	@Test
	public void testGetDistanceString()
	{
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		final PointOfServiceModel pointOfServiceModel = mock(PointOfServiceModel.class);

		given(pointOfServiceModel.getBaseStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getStorelocatorDistanceUnit()).willReturn(DistanceUnit.KM);
		given(pointOfServiceService.getPointOfServiceForName(LOCATION_NAME)).willReturn(pointOfServiceModel);

		final String result = defaultDistanceHelper.getDistanceStringForLocation(LOCATION_NAME, DISTANCE_KM);

		Assert.assertEquals(FORMATTED_DISTANCE_KM, result);
	}
}
