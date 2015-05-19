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
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.location.Location;

import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
@UnitTest
public class LocationDtoWrapperTest
{

	private LocationDtoWrapper dtoWrapper;


	@Test
	public void testLocalizedBasedEmptyLocation()
	{
		final LocationDTO dtoInput = new LocationDTO();

		dtoWrapper = new LocationDtoWrapper(dtoInput);

		assertAddressData(new AddressData(), dtoWrapper);
		Assert.assertNull(dtoWrapper.getCountry());
		Assert.assertNull(dtoWrapper.getDescription());
		Assert.assertNull(dtoWrapper.getMapIconUrl());
		Assert.assertNull(dtoWrapper.getName());
		assertTextualAddress(", null null, null null", dtoWrapper);
		Assert.assertNull(dtoWrapper.getType());
	}

	@Test
	public void testLocalizedBasedNotEmptyLocation()
	{
		final LocationDTO dtoInput = new LocationDTO();
		dtoInput.setBuildingNo("building");
		dtoInput.setCity("city");
		dtoInput.setCountryIsoCode("country");
		dtoInput.setDescription("desc");
		dtoInput.setLatitude("10.10");
		dtoInput.setLongitude("11.11");
		dtoInput.setMapIconUrl("url");
		dtoInput.setName("name");
		dtoInput.setPostalCode("44101");
		dtoInput.setStreet("street");
		dtoInput.setType("type");

		dtoWrapper = new LocationDtoWrapper(dtoInput);

		assertAddressData(new AddressData("name", "street", "building", "44101", "city", "country"), dtoWrapper);
		Assert.assertEquals("country", dtoWrapper.getCountry());
		Assert.assertEquals("desc", dtoWrapper.getDescription());
		Assert.assertEquals("url", dtoWrapper.getMapIconUrl());
		Assert.assertEquals("name", dtoWrapper.getName());
		assertTextualAddress("name, street building, 44101 city", dtoWrapper);
		Assert.assertEquals("type", dtoWrapper.getType());

	}

	@Test
	public void testAdressDataBasedEmptyData()
	{

		dtoWrapper = new LocationDtoWrapper(new AddressData(), null);

		assertAddressData(new AddressData(), dtoWrapper);
		Assert.assertEquals(null, dtoWrapper.getCountry());
		Assert.assertEquals(null, dtoWrapper.getDescription());
		Assert.assertEquals(null, dtoWrapper.getMapIconUrl());
		Assert.assertEquals(null, dtoWrapper.getName());
		assertTextualAddress(", null null, null null", dtoWrapper);
		Assert.assertEquals(null, dtoWrapper.getType());

	}

	@Test
	public void testAdressDataBasedEmptyDataNotEmptyGPS()
	{
		final AddressData adressData = new AddressData();
		final GPS gpsData = new DefaultGPS(10.1, 11.1);

		dtoWrapper = new LocationDtoWrapper(adressData, gpsData);

		assertAddressData(new AddressData(), dtoWrapper);
		Assert.assertEquals(null, dtoWrapper.getCountry());
		Assert.assertEquals(null, dtoWrapper.getDescription());
		Assert.assertEquals(null, dtoWrapper.getMapIconUrl());
		Assert.assertEquals(null, dtoWrapper.getName());
		assertTextualAddress(", null null, null null", dtoWrapper);
		Assert.assertEquals(null, dtoWrapper.getType());

	}

	@Test
	public void testAdressDataBasedNotEmptyData()
	{
		final AddressData adressData = new AddressData("name", "street", "building", "44101", "city", "country");
		final GPS gpsData = new DefaultGPS(10.1, 11.1);

		dtoWrapper = new LocationDtoWrapper(adressData, gpsData);

		assertAddressData(new AddressData("name", "street", "building", "44101", "city", "country"), dtoWrapper);
		Assert.assertEquals(null, dtoWrapper.getCountry());
		Assert.assertEquals(null, dtoWrapper.getDescription());
		Assert.assertEquals(null, dtoWrapper.getMapIconUrl());
		Assert.assertEquals(null, dtoWrapper.getName());
		assertTextualAddress("name, street building, 44101 city", dtoWrapper);
		Assert.assertEquals(null, dtoWrapper.getType());

	}

	private void assertAddressData(final AddressData expected, final Location given)
	{
		final AddressData givenData = given.getAddressData();
		Assert.assertEquals(expected, givenData);

	}

	private void assertTextualAddress(final String expected, final Location given)
	{
		Assert.assertEquals(expected, given.getTextualAddress());

	}
}
