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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Test refers to Google Maps API.
 */
@ManualTest
//see https://wiki.hybris.com/display/RD/Storelocator+manual+tests
public class GeocodingLoadTest extends AbstractGeocodingTest
{

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		//20 batch size - the amount of the test entries is 35
		//1sec internal delay - this is the pause between every geocoding query
		createTestCronJob(Integer.valueOf(20), Integer.valueOf(1));
		createTestPosEntries();
	}

	/**
	 * Tests geocoding of larger amount of test data (35 entries). Checks proper behavior of the service in case of delta
	 * address modifications.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGeocodingCronJob() throws Exception
	{
		assertEquals("Initially, we expect all entries to be submitted for geocoding", 35, pointOfServiceDao.getPosToGeocode()
				.size());
		assertEquals("With batch size limitation:20, dao resulting collection has unexpected size", 20, pointOfServiceDao
				.getPosToGeocode(20).size());
		assertEquals("With batch size limitation:100, dao resulting collection has unexpected size", 35, pointOfServiceDao
				.getPosToGeocode(100).size());
		assertEquals("With batch size limitation:35, dao resulting collection has unexpected size", 35, pointOfServiceDao
				.getPosToGeocode(35).size());

		for (final PointOfServiceModel posModel : pointOfServiceDao.getPosToGeocode())
		{
			assertNull("Initially all pos entries should not be timestamped", posModel.getGeocodeTimestamp());
		}
		//run the cron job
		geocodeAddressesJob.perform(cronJobService.getCronJob("testCronJob"));

		final List<PointOfServiceModel> geocoded = new ArrayList<PointOfServiceModel>();
		final List<PointOfServiceModel> notGeocoded = new ArrayList<PointOfServiceModel>();
		for (final PointOfServiceModel pos : pointOfServiceDao.getAllPos())
		{
			if (pos.getGeocodeTimestamp() != null)
			{
				geocoded.add(pos);
			}
			else
			{
				notGeocoded.add(pos);
			}
		}

		assertEquals("Geocoded entries amount must be equal to job's batch size", 20, geocoded.size());
		assertEquals("Not geocoded entries amount must be equal to 15", 15, notGeocoded.size());
		assertEquals("not geocoded entries amount not as expected", pointOfServiceDao.getPosToGeocode().size(), notGeocoded.size());

		//If the pos address is changed they should be considered back as not geocoded:
		AddressModel address = null;
		for (int i = 0; i < 5; i++)
		{
			address = geocoded.get(i).getAddress();
			address.setAppartment("1");
			modelService.save(address);
		}
		assertEquals("Not geocoded entries amount should be increased by 5", notGeocoded.size() + 5, pointOfServiceDao
				.getPosToGeocode().size());

		//run the cron job
		geocodeAddressesJob.perform(cronJobService.getCronJob("testCronJob"));

		//there should not be any not geocoded entries in  the end.
		assertEquals("There should not be any not geocoded entries in the end", 0, pointOfServiceDao.getPosToGeocode().size());

	}
}
