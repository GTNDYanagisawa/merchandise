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

import de.hybris.platform.core.dto.user.CustomerDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.testframework.TestUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class ProcessDtoIdTest extends AbstractWebServicesTest
{
	private static final String URI = "customers/";
	private static final String TEST_CUSTOMER_UID1 = "testCustomer1";
	private static final String TEST_CUSTOMER_UID2 = "testCustomer2";
	private static final String NEW_CUSTOMER_UID = "testCustomerNew";

	public ProcessDtoIdTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCustomers() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCustomers();
	}

	@Test
	public void testCorrectChangingNotPkUniqueKey()
	{
		final CustomerDTO newCustomerDTO = new CustomerDTO();
		newCustomerDTO.setUid(NEW_CUSTOMER_UID);

		ClientResponse result = webResource.path(URI + TEST_CUSTOMER_UID1).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(newCustomerDTO).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + TEST_CUSTOMER_UID1).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());

		result = webResource.path(URI + NEW_CUSTOMER_UID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
	}

	@Test
	public void testIncorrectChangingNotPkUniqueKey()
	{
		ClientResponse result = webResource.path(URI + TEST_CUSTOMER_UID2).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		final CustomerDTO customer2DTO = result.getEntity(CustomerDTO.class);

		TestUtils.disableFileAnalyzer();
		result = webResource.path(URI + TEST_CUSTOMER_UID1).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(customer2DTO).put(ClientResponse.class);
		result.bufferEntity();
		//ambiguous unique keys
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.BAD_REQUEST, result.getResponseStatus());

		result = webResource.path(URI + "NotExistingCustomer1").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(customer2DTO).put(ClientResponse.class);
		result.bufferEntity();
		//Resource identifier 'NotExistingCustomer1' doesn't exist and doesn't match DTO identifier 'testCustomer2'
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.BAD_REQUEST, result.getResponseStatus());
		TestUtils.enableFileAnalyzer();
	}

}
