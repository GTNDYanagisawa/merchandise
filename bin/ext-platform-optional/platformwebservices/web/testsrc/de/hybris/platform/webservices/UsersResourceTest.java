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

import de.hybris.platform.core.dto.user.CustomerDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.dto.user.UsersDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.testframework.TestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class UsersResourceTest extends AbstractWebServicesTest
{
	protected static final String URI = "users";

	public UsersResourceTest() throws Exception // NOPMD
	{
		super();
	}

	@Before
	public void setUpUsers() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCustomers();
	}

	@Test
	public void testGetUsers() throws IOException
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final UsersDTO usersDto = result.getEntity(UsersDTO.class);
		assertNotNull("No customers within body at response: " + result, usersDto);
		final List<UserDTO> users = usersDto.getUsers();

		// UserResource/UserDTO is designed to return users and not only customers
		// however, for now 'admin' isn't present because restjersey-service actually only returns 'Customers'
		this.removeUsersFromList(users, "anonymous", "admin");

		//two created users
		assertEquals("Wrong number of customers at response: " + result, 4, users.size());
		assertIsNotNull(users, UserDTO.class, "uid", "uri");
	}


	private void removeUsersFromList(final List<UserDTO> userList, final String... users)
	{
		final HashSet<String> userSet = new HashSet<String>(Arrays.asList(users));
		for (final Iterator<UserDTO> iter = userList.iterator(); iter.hasNext();)
		{
			final UserDTO user = iter.next();
			if (userSet.contains(user.getUid()))
			{
				iter.remove();
			}
		}
	}

	@Test
	public void testGetUsersCollectionApi()
	{
		//start: RootCollection paging test
		ClientResponse result = webResource.path(URI).queryParam("users_size", "1").queryParam("users_page", "0").queryParam(
				"users_sort", "").cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		UsersDTO usersDto = result.getEntity(UsersDTO.class);
		assertNotNull("No users within body at response: " + result, usersDto);
		List<UserDTO> users = usersDto.getUsers();

		//one user per page
		assertEquals("Wrong number of users at response: " + result, 1, users.size());
		assertIsNotNull(users, UserDTO.class, "uid", "uri");
		//end: RootCollection paging test

		//start: CollectionType paging test
		TestUtils.disableFileAnalyzer();
		// should return appropriate message error, because this is CollectionType collection
		result = webResource.path(URI + "/testCustomer1").queryParam("addresses_query", "fakequery").queryParam(
				"addresses_subtypes", "false").cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		TestUtils.enableFileAnalyzer();
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.BAD_REQUEST, result.getResponseStatus());
		assertEquals(
				result.getEntity(String.class),
				"FlexibleSearch querying is not supported for: addresses collection property.\nThe subtyping is not supported for: addresses collection property.");
		//end: CollectionType paging test

		//start: Collection - Relation N:M paging test
		result = webResource.path(URI + "/testCustomer1").queryParam("groups_query", "%7Buid%7D%20LIKE%20'%251'").cookie(
				tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CustomerDTO userDto = result.getEntity(CustomerDTO.class);
		assertEquals("Wrong number of usergroups. Only one group is queried: testCustomerGroup1", 1, userDto.getGroups().size());
		//end: Collection - Relation N:M paging test

		//start: RootCollection paging test Subtypes set to false
		result = webResource.path(URI).queryParam("users_subtypes", "false").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		usersDto = result.getEntity(UsersDTO.class);
		users = usersDto.getUsers();
		//two created users
		assertEquals("Wrong number of customers at response: " + result, null, users);
		//start: RootCollection paging test
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, DELETE, POST);
	}

}
