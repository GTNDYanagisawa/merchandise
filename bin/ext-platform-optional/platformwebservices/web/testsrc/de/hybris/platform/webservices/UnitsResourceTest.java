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

import de.hybris.platform.core.dto.product.UnitDTO;
import de.hybris.platform.core.dto.product.UnitsDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * UnitsResourceTest.
 */
public class UnitsResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "units/";

	/**
	 * @throws Exception
	 */
	public UnitsResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpUnits() throws ConsistencyCheckException
	{
		createTestCatalogs();
		createTestProductsUnits();
	}

	@Test
	public void testGetUnits()
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final UnitsDTO units = result.getEntity(UnitsDTO.class);
		assertNotNull("No units found at response: " + result, units);
		assertEquals("Wrong number of units found at response: " + result, 2, units.getUnits().size());
		assertIsNotNull(units.getUnits(), UnitDTO.class, "code", "uri");
		assertIsNull(units.getUnits(), UnitDTO.class, "unitType", "name", "conversion");
	}



	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, POST, DELETE);
	}
}
