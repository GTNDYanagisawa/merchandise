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
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.dto.c2l.RegionDTO;
import de.hybris.platform.core.dto.c2l.RegionsDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class RegionsResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "regions";

	public RegionsResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpRegions() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCountries();
	}

	@Test
	public void testGetRegions() throws IOException
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();

		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		assertTrue("Wrong content-type at response: " + result, MediaType.APPLICATION_XML_TYPE.isCompatible(result.getType()));

		final RegionsDTO regions = result.getEntity(RegionsDTO.class);
		assertNotNull("No regions found at response: " + result, regions);
		assertEquals("Wrong number of regions found at response: " + result, 2, regions.getRegions().size());
		for (final RegionDTO region : regions.getRegions())
		{
			assertNotNull("Region isocode has to be set at response: " + result, region.getIsocode());
		}
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, POST, DELETE);
	}

}
