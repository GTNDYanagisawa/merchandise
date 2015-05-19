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

import de.hybris.platform.core.dto.c2l.CountriesDTO;
import de.hybris.platform.core.dto.c2l.CountryDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CountriesResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "countries";

	public CountriesResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCountries() throws ConsistencyCheckException
	{
		createTestCountries();
	}

	@Test
	public void testGetCountries() throws IOException
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CountriesDTO countries = result.getEntity(CountriesDTO.class);
		assertNotNull("No countries within body at response: " + result, countries);
		assertEquals("Wrong number of countries at response: " + result, 2, countries.getCountries().size());
		assertIsNotNull(countries.getCountries(), CountryDTO.class, "isocode", "uri");
		assertIsNull(countries.getCountries(), CountryDTO.class, "active");
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, DELETE, POST);
	}
}
