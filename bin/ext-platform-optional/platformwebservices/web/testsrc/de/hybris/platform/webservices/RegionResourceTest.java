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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.dto.c2l.CountryDTO;
import de.hybris.platform.core.dto.c2l.RegionDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.c2l.Region;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class RegionResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "regions/";
	private static final String TEST_REGION_COUNTRY = "testCountry1";
	private static final String TEST_REGION_CODE = "testRegion1";
	private static final String TEST_REGION_NAME = TEST_REGION_CODE + " name";
	private static final String NEW_REGION_ISOCODE = "testRegion3";
	private static final String NEW_REGION_NAME = NEW_REGION_ISOCODE + " name";


	private Region testRegion;

	public RegionResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpRegions() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCountries();
		testRegion = C2LManager.getInstance().getCountryByIsoCode("testCountry1").getRegionByCode(TEST_REGION_CODE);
	}

	@Test
	public void testGetRegion()
	{
		final ClientResponse result = webResource.path(URI + testRegion.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final RegionDTO region = result.getEntity(RegionDTO.class);
		assertNotNull("No region within body at response: " + result, region);
		assertEquals("Invalid region in response: " + result, testRegion.getIsoCode(), region.getIsocode());
		assertEquals("Invalid region name in response: " + result, TEST_REGION_NAME, region.getName());
		assertEquals("Invalid country in response: ", TEST_REGION_COUNTRY, region.getCountry().getIsocode());
	}

	@Test
	public void testGetRegionJsonFormat()
	{
		final ClientResponse result = webResource.path(URI + testRegion.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		result.bufferEntity();
		//assertOk() not used because of different media type: APPLICATION_JSON_TYPE
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		assertTrue("Wrong content-type at response: " + result, MediaType.APPLICATION_JSON_TYPE.isCompatible(result.getType()));

		final RegionDTO region = result.getEntity(RegionDTO.class);
		assertNotNull("No region within body at response: " + result, region);
		assertEquals("Invalid region in response: " + result, testRegion.getIsoCode(), region.getIsocode());
		assertEquals("Invalid region name in response: " + result, TEST_REGION_NAME, region.getName());
		assertEquals("Invalid country in response: ", TEST_REGION_COUNTRY, region.getCountry().getIsocode());
	}

	@Test
	public void testPutNewRegion() throws ConsistencyCheckException
	{

		final CountryDTO countryDto = new CountryDTO();
		countryDto.setIsocode(testRegion.getCountry().getIsoCode());
		final RegionDTO newRegionDto = new RegionDTO();
		newRegionDto.setIsocode(NEW_REGION_ISOCODE);
		newRegionDto.setName(NEW_REGION_NAME);
		newRegionDto.setCountry(countryDto);

		//send message for creating of the region
		final ClientResponse result = webResource.path(URI + NEW_REGION_ISOCODE).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(newRegionDto).put(ClientResponse.class);
		result.bufferEntity();

		//check the response 
		assertCreated(result, true);

		//check if the region has been created
		assertEquals("The number of country regions is incorrect", 3, testRegion.getCountry().getRegions().size());
		final Region createdRegion = C2LManager.getInstance().getRegionByCode(testRegion.getCountry(), NEW_REGION_ISOCODE);
		assertNotNull("The region wasn't created", createdRegion);
		assertEquals("The region's name property wasn't saved correctly", createdRegion.getName(), NEW_REGION_NAME);
		assertEquals("The region's country wasn't saved correctly", testRegion.getCountry().getIsoCode(), createdRegion
				.getCountry().getIsoCode());
		//no need to remove region here, it'll be removed automatically together with test countries.
	}


	@Test(expected = JaloItemNotFoundException.class)
	public void testDeleteRegion() throws IOException
	{
		final String testRegionCode = testRegion.getCode();
		final Country testRegionCountry = testRegion.getCountry();

		//assert there are only two regions at the beginning
		assertEquals("The number of country regions is incorrect", 2, testRegionCountry.getRegions().size());

		//send message for deleting of the region
		final ClientResponse deleteRegionResult = webResource.path(URI + testRegionCode).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		deleteRegionResult.bufferEntity();
		//check the response
		assertOk(deleteRegionResult, true);

		//check if the region has been deleted 
		assertEquals("The number of country regions is incorrect", 1, testRegionCountry.getRegions().size());
		final Region deletedRegion = C2LManager.getInstance().getRegionByCode(testRegionCountry, testRegionCode);
		assertNull("The region wasn't deleted", deletedRegion);
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testRegion.getIsoCode(), POST);
	}
}
