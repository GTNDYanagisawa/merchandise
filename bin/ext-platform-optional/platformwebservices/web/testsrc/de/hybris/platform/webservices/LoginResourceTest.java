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

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class LoginResourceTest extends AbstractWebServicesTest
{

	private static final String URI = "login/";

	public LoginResourceTest() throws Exception // NOPMD
	{
		super();
	}

	@Test
	public void testGetLogin()
	{
		//admin user
		ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();

		if (!INTERNAL_SERVER)
		{
			assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		}

		//no user
		result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, null).accept(MediaType.APPLICATION_XML).get(
				ClientResponse.class);
		result.bufferEntity();

		assertEquals("Wrong HTTP status at response: " + result, Response.Status.UNAUTHORIZED, result.getResponseStatus());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, POST, PUT, DELETE);
	}
}
