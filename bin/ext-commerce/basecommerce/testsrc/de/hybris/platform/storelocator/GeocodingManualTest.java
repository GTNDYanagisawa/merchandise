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
package de.hybris.platform.storelocator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.impl.GoogleMapTools;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.route.DistanceAndRoute;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;



//see https://wiki.hybris.com/display/RD/Storelocator+manual+tests
@ManualTest
public class GeocodingManualTest extends AbstractGeocodingTest
{

	@Resource
	private GoogleMapTools googleMapTools;

	final String title = "test title";
	private Location start;
	private Location destination;
	private Location center;
	private Location poi1;
	private Location poi2;

	@Before
	public void setupCoreData() throws Exception
	{
		createCoreData();
		createTestCronJob(Integer.valueOf(10), Integer.valueOf(1));

		//create test locations
		center = createAndStoreTestLocation("center", "Nymphenburger strasse", "86", "80636", "Munchen", "DE");
		poi1 = createAndStoreTestLocation("poi1", "Nymphenburger strasse", "1", "80636", "Munchen", "DE");
		poi2 = createAndStoreTestLocation("poi2", "Nymphenburger strasse", "10", "80636", "Munchen", "DE");
		start = createAndStoreTestLocation("hybris Muenchen", "Nymphenburger Strasse", "86", "80636", "Munchen", "DE");
		destination = createAndStoreTestLocation("locationX", "Nymphenburger Strasse", "100", "80636", "Munchen", "DE");

		//geocode by means of cronjob
		geocodeAddressesJob.perform(cronJobService.getCronJob("testCronJob"));

		//refresh locations
		start = locationService.getLocationByName(start.getName());
		destination = locationService.getLocationByName(destination.getName());
		center = locationService.getLocationByName(center.getName());
		poi1 = locationService.getLocationByName(poi1.getName());
		poi2 = locationService.getLocationByName(poi2.getName());
	}

	/**
	 * Tests IGeoWebServiceWrapper, which returns a route for a well defined inputs: GPS and Address
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetDistanceAndRoute() throws Exception
	{
		final DistanceAndRoute result = geoServiceWrapper.getDistanceAndRoute(start, destination);
		assertNotNull("Resulting IDistanceAndRoute object was null", result);
		assertNotNull("Resulting IRoute object was null", result.getRoute());
		assertTrue("Road distance between two known test points should be determined", result.getRoadDistance() != 0);
		assertTrue("'Eagle flies' distance between two known test points should be determined", result.getEagleFliesDistance() != 0);
	}

	/**
	 * Tests IGeoWebServiceWrapper, which throws meaningful exception when addresses cannot be properly resolved
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetDistanceAndRouteForWrongInput() throws Exception
	{
		boolean passed = false;
		try
		{
			final Location start = locationService.getLocation("xyz", "2", "41506", "abc", "PL", true);

			final Location destination = locationService.getLocation("qwerty", "3", "41506", "test", "PL", true);
			geoServiceWrapper.getDistanceAndRoute(start, destination);
		}
		catch (final LocationServiceException e)
		{
			passed = true;
		}
		assertTrue("test case should have failed because of LocationServiceException", passed);
	}


}
