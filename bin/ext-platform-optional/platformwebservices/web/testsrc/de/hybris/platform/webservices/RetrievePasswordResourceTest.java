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

import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.jalo.JaloBusinessException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class RetrievePasswordResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "/retrievepassword";
	private static final String EXPECTED_GET_RESPONSE = "Password question is: My question1\nTry to put correct answer. Use PUT method:\n<user uid=\"the same id as the one in the URL\">\n	<passwordAnswer>YOUR ANSWER</passwordAnswer>\n</user>";
	private static final String EXPECTED_PARTIAL_PUT_RESPONSE = "Correct answer.\nNew password is:";

	/**
	 * @throws Exception
	 */
	public RetrievePasswordResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpUsers() throws JaloBusinessException
	{
		createTestCustomers();
	}

	@Test
	public void testGetRetrievePassword()
	{
		final ClientResponse result = webResource.path(URI + "/testCustomer1").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		final String entity = result.getEntity(String.class);
		assertEquals("Wrong HTTP status at response: " + result, 200, result.getStatus());
		assertNotNull("No entity set", entity);
		assertEquals("Wrong response obtained", EXPECTED_GET_RESPONSE, entity);
	}

	@Test
	public void testPutRetrievePasswordXML()
	{
		final UserDTO user = new UserDTO();
		user.setPasswordAnswer("My answer1");
		user.setUid("testCustomer1");
		final ClientResponse result = webResource.path(URI + "/testCustomer1").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).header(HEADER_CONTENT_TYPE, MediaType.APPLICATION_XML).accept(
				MediaType.APPLICATION_XML).entity(user).put(ClientResponse.class);
		result.bufferEntity();
		final String entity = result.getEntity(String.class);
		assertEquals("Wrong HTTP status at response: " + result, 200, result.getStatus());
		assertNotNull("No entity set", entity);
		assertTrue("Unexpected server response", entity.contains(EXPECTED_PARTIAL_PUT_RESPONSE));
	}

	@Test
	public void testPutRetrievePasswordJSON()
	{
		final UserDTO user = new UserDTO();
		user.setPasswordAnswer("My answer1");
		user.setUid("testCustomer1");
		final ClientResponse result = webResource.path(URI + "/testCustomer1").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).header(HEADER_CONTENT_TYPE, MediaType.APPLICATION_JSON).accept(
				MediaType.APPLICATION_XML).entity(user).put(ClientResponse.class);
		result.bufferEntity();
		final String entity = result.getEntity(String.class);
		assertEquals("Wrong HTTP status at response: " + result, 200, result.getStatus());
		assertNotNull("No entity set", entity);
		assertTrue("Unexpected server response", entity.contains(EXPECTED_PARTIAL_PUT_RESPONSE));
	}

	@Test
	public void testPutIncorrectFormat()
	{
		final UserDTO user = new UserDTO();
		user.setPasswordAnswer("My answer1");
		user.setUid("testCustomer1");
		final ClientResponse result = webResource.path(URI + "/testCustomer1").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).header(HEADER_CONTENT_TYPE, MediaType.APPLICATION_XML).accept(
				MediaType.APPLICATION_XML).entity("BSFHSDJFHAUHAS").put(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, 400, result.getStatus());
	}
}
