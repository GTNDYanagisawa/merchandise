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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.MediaTypes;


public class RestJerseyTest extends AbstractWebServicesTest
{
	public RestJerseyTest() throws Exception //NOPMD
	{
		super();
	}

	@Test
	public void testWADL() throws IOException
	{
		final ClientResponse result = webResource.path("application.wadl").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaTypes.WADL).get(ClientResponse.class);
		result.bufferEntity();

		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		assertTrue("No content found at response: " + result, result.getEntity(String.class).length() > 0);
	}

}
