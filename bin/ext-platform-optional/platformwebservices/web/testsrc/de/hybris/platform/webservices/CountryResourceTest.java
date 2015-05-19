/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.dto.c2l.CountryDTO;
import de.hybris.platform.core.dto.c2l.RegionDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Country;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CountryResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "countries/";
	private Country testCountry;

	public CountryResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCountries() throws ConsistencyCheckException
	{
		createTestCountries();
		testCountry = C2LManager.getInstance().getCountryByIsoCode("testCountry1");
	}

	@Test
	public void testGetCountry() throws IOException
	{
		final ClientResponse result = webResource.path(URI + testCountry.getIsoCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CountryDTO country = result.getEntity(CountryDTO.class);
		//assertEqual not used because of differences in isoCode property name
		assertNotNull("No country within body at response: " + result, country);
		assertEquals("Wrong country isocode at response: " + result, testCountry.getIsoCode(), country.getIsocode());
		assertEquals("Wrong country name at response: " + result, testCountry.getName(), country.getName());
		assertEquals("Wrong country active status at response: " + result, Boolean.FALSE, country.getActive());

		assertEquals("Wrong number of regions at response: " + result, 2, country.getRegions().size());
		assertIsNotNull(country.getRegions(), RegionDTO.class, "isocode");
	}

	@Test
	public void testPutCountryNew() throws Exception
	{
		final CountryDTO country = new CountryDTO();
		country.setIsocode("newCountry");
		country.setName("name");
		country.setActive(Boolean.FALSE);

		final ClientResponse result = webResource.path(URI + "newCountry").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(country).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		final Country newCountry = C2LManager.getInstance().getCountryByIsoCode("newCountry");
		assertEquals("Name was not changed correct", "name", newCountry.getName());
		assertTrue("Regions were not empty", newCountry.getRegions().isEmpty());

		//remove created Country
		newCountry.remove();
	}

	@Test
	public void testPutCountryUpdate() throws Exception
	{
		final CountryDTO country = new CountryDTO();
		country.setIsocode(testCountry.getIsoCode());
		country.setName("changedName");
		country.setActive(Boolean.FALSE);

		final ClientResponse result = webResource.path(URI + testCountry.getIsoCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(country).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		assertEquals("Name was not changed correct", "changedName", testCountry.getName());
		assertEquals("Regions were changed", 2, testCountry.getRegions().size());
	}

	@Test
	public void testPutCountryUpdateWithRegions() throws IOException
	{
		final CountryDTO country = new CountryDTO();
		country.setIsocode("testCountry1");
		country.setName("changedName");
		country.setActive(Boolean.FALSE);

		final RegionDTO region1 = new RegionDTO();
		region1.setIsocode("testRegion1");
		region1.setActive(Boolean.FALSE);

		final RegionDTO region2 = new RegionDTO();
		region2.setIsocode("testRegion2");
		region2.setActive(Boolean.FALSE);

		country.setRegions(Arrays.asList(region1, region2));

		final ClientResponse result = webResource.path(URI + testCountry.getIsoCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(country).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		assertEquals("Name was not changed correct", "changedName", C2LManager.getInstance().getCountryByIsoCode("testCountry1")
				.getName());
		assertEquals("Regions were changed", 2, testCountry.getRegions().size());
	}

	@Test
	public void testGetRegionsFromCountryCollectionApi()
	{
		//start: Collection - Relation 1:N paging test [parameters: size, page, sort]
		ClientResponse result = webResource.path(URI + testCountry.getIsoCode()).queryParam("regions_size", "1").queryParam(
				"regions_page", "0").queryParam("regions_sort", "isocode").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		CountryDTO countryDto1 = result.getEntity(CountryDTO.class);
		final List<RegionDTO> regions1 = (List<RegionDTO>) countryDto1.getRegions();


		result = webResource.path(URI + testCountry.getIsoCode()).queryParam("regions_size", "1").queryParam("regions_page", "1")
				.queryParam("regions_sort", "isocode").cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CountryDTO countryDto2 = result.getEntity(CountryDTO.class);
		final List<RegionDTO> regions2 = (List<RegionDTO>) countryDto2.getRegions();


		assertFalse("Regions cannot have the same isocode. First one comes from page 1, seconf from page 2.", regions1.get(0)
				.getIsocode().equals(regions2.get(0).getIsocode()));
		//end: Collection - Relation 1:N paging test

		//start: Collection - Relation 1:N paging test [parameters: query={isocode} LIKE '%Region%' and not {isocode} LIKE '%Region2']
		result = webResource.path(URI + testCountry.getIsoCode()).queryParam("regions_query",
				"%7Bisocode%7D%20like%20'%25Region%25'%20and%20not%20%7Bisocode%7D%20like%20'%25Region2'").cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		countryDto1 = result.getEntity(CountryDTO.class);

		assertEquals("Wrong number of regions at response: " + result, 1, countryDto1.getRegions().size());
		assertEquals("Wrong isocode for queried region: ", "testRegion1", countryDto1.getRegions().iterator().next().getIsocode());
		//end: Collection - Relation 1:N paging test [parameters: query]
	}

	@Test(expected = JaloItemNotFoundException.class)
	public void testDeleteCountry() throws IOException
	{
		final ClientResponse result = webResource.path(URI + testCountry.getIsoCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		C2LManager.getInstance().getCountryByIsoCode("testCountry1");
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testCountry.getIsoCode(), POST);
	}
}
