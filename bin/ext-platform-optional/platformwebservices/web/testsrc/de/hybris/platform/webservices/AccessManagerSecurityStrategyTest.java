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

import de.hybris.platform.core.dto.security.PrincipalGroupDTO;
import de.hybris.platform.core.dto.user.AddressDTO;
import de.hybris.platform.core.dto.user.CustomerDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.dto.user.UserGroupDTO;
import de.hybris.platform.core.dto.user.UsersDTO;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.Base64;
import de.hybris.platform.webservices.functional.FunctionalTestConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * Tests for our default Security Strategy based on AccessManager(managed by hmc).
 */
public class AccessManagerSecurityStrategyTest extends AbstractWebServicesTest
{
	private static final String URI_USER = "users";
	private static final String URI_CUSTOMER = "customers";
	private static final String TESTCUSTOMER2 = "testCustomer2";
	private String testCustomer1Auth = null;

	public AccessManagerSecurityStrategyTest() throws Exception // NOPMD
	{
		super();
	}

	@Before
	public void setUpData() throws ConsistencyCheckException, JaloBusinessException
	{
		AbstractWebServicesTest.importCSVFromResources("accessManagerSecurityStrategyTest");
		configureTestUsers();
	}

	@Test
	public void testAccessManagerSecurityStrategy() //NOPMD
	{
		//create authorization header value for customer and employee
		testCustomer1Auth = createAuthorizationHeaderValue("testCustomer1", "My answer1");
		final String testCustomerWithoutUserRights1Auth = createAuthorizationHeaderValue(TESTCUSTOMER2, "My answer2"); //NOPMD
		final String testEmployee1Auth = createAuthorizationHeaderValue("testEmployee1", "My answer3");
		final String testEmployee2Auth = createAuthorizationHeaderValue("testEmployee2", "My answer4");


		/* START: type-based security | User rights: READ, CHANGE, CREATE, DELETE */

		//testCustomer1Auth gets 3users (2customers+anonymous)
		getListOfUsers(testCustomer1Auth, false);
		//testEmployee1Auth gets 6 users (2customers+2employess+anonymous+admin)
		getListOfUsers(testEmployee1Auth, false);
		//read access forbidden for testCustomerWithoutUserRights1Auth
		getListOfUsers(testCustomerWithoutUserRights1Auth, true);
		//create user | If isForbidden is set to false means that testCustomer1Auth can create new newTestCustomer1.
		createUser(testCustomer1Auth, false, "newTestCustomer1");
		createUser(testEmployee1Auth, true, "newTestCustomer2");
		createUser(testCustomerWithoutUserRights1Auth, true, "newTestCustomer3");
		//update user | If isForbidden is set to false means that testEmployee1Auth can update newTestCustomer1.
		updateUser(testCustomer1Auth, true, "newTestCustomer1");
		updateUser(testEmployee1Auth, false, "newTestCustomer1");
		updateUser(testCustomerWithoutUserRights1Auth, true, "newTestCustomer1");
		//delete user | If isForbidden is set to false means that testEmployee1Auth can delete newTestCustomer1.
		deleteUser(testCustomer1Auth, true, "newTestCustomer1");
		deleteUser(testEmployee1Auth, false, "newTestCustomer1");
		deleteUser(testCustomerWithoutUserRights1Auth, true, "newTestCustomer1");


		/* START: attribute-based security | User rights: READ, CHANGE */
		//all below methods work with "testCustomer2" user.

		//the testCustomer1Auth cannot see testCustomer2 attribute <addresses>.
		readUserAddresses(testCustomer1Auth);
		//the testEmployee1Auth sees two addresses within testCustomer2 attribute <addresses>.
		readUserAddresses(testEmployee1Auth);
		//the testEmployee2Auth can change testCustomer2 attribute <addresses>.
		changeUserAddresses(testEmployee2Auth, false);
		//the testEmployee1Auth cannot change testCustomer2 attribute <addresses>.
		changeUserAddresses(testEmployee1Auth, true);


		/* START: type-based security on attribute level | User rights: READ, CHANGE, CREATE, DELETE */

		//the testCustomer1Auth cannot see any testCustomer2 user groups.
		readUserUserGroups(testCustomer1Auth);
		//the testEmployee1Auth can see testCustomer2 user groups.
		readUserUserGroups(testEmployee1Auth);
		//the testEmployee2Auth cannot change testCustomer2 user groups.
		changeOrCreateUserUserGroups(testEmployee2Auth, true, "customergroup2");
		//the testEmployee1Auth can change testCustomer2 user groups.
		changeOrCreateUserUserGroups(testEmployee1Auth, false, "customergroup2");
		//the testEmployee2Auth cannot create testCustomer2 user groups.
		changeOrCreateUserUserGroups(testEmployee2Auth, true, "customergroup3");
		//the testEmployee2Auth can create testCustomer2 user groups.
		changeOrCreateUserUserGroups(testEmployee1Auth, false, "customergroup3");

		//deleting is not supported within the attribute-list by ws.
	}

	private String createAuthorizationHeaderValue(final String user, final String passAnswer)
	{
		final UserDTO customer = new UserDTO();
		customer.setPasswordAnswer(passAnswer);
		customer.setUid(user);
		final ClientResponse result = webResource.path(FunctionalTestConstants.URI_RETRIEVE_PASS + "/" + user).header(
				"Content-Type", MediaType.APPLICATION_XML).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).entity(customer)
				.put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		final String response = result.getEntity(String.class);
		final Pattern pattern = Pattern.compile("\\w{8}");
		final Matcher matcher = pattern.matcher(response);
		matcher.find();//skip first result, it will be word 'password'
		matcher.find();
		final String password = matcher.group();
		assertNotNull("Password should not be null at this point", password);

		return "Basic " + Base64.encodeBytes((user + ":" + password).getBytes());
	}

	private void getListOfUsers(final String auth, final boolean isForbidden)
	{
		fileAnalyzer(isForbidden, true);
		final ClientResponse result = webResource.path(URI_USER).cookie(tenantCookie).header(HEADER_AUTH_KEY, auth).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		fileAnalyzer(isForbidden, false);

		result.bufferEntity();
		if (isForbidden)
		{
			assertForbidden(result, false);
		}
		else
		{
			assertOk(result, false);

			final UsersDTO usersDto = result.getEntity(UsersDTO.class);
			assertNotNull("No customers within body at response: " + result, usersDto);
			final List<UserDTO> users = usersDto.getUsers();

			//employee and customer gets 6 users (2customers+2employess+anonymous+admin)
			assertEquals("Wrong number of users at response: " + result, 6, users.size());
			assertIsNotNull(users, UserDTO.class, "uid", "uri", "pk");
		}
	}

	private void createUser(final String auth, final boolean isForbidden, final String customerName)
	{
		final CustomerDTO customer = new CustomerDTO();
		customer.setUid(customerName);

		fileAnalyzer(isForbidden, true);
		final ClientResponse result = webResource.path(URI_CUSTOMER + "/" + customer.getUid()).header("Content-Type",
				MediaType.APPLICATION_XML).header(HEADER_AUTH_KEY, auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML)
				.entity(customer).put(ClientResponse.class);
		fileAnalyzer(isForbidden, false);

		result.bufferEntity();
		if (isForbidden)
		{
			assertForbidden(result, false);
		}
		else
		{
			//check the response 
			assertCreated(result, true);
		}
	}

	private void updateUser(final String auth, final boolean isForbidden, final String customerName)
	{
		final CustomerDTO customer = new CustomerDTO();
		customer.setUid(customerName);
		customer.setName(customerName);

		fileAnalyzer(isForbidden, true);
		final ClientResponse result = webResource.path(URI_CUSTOMER + "/" + customer.getUid()).header("Content-Type",
				MediaType.APPLICATION_XML).header(HEADER_AUTH_KEY, auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML)
				.entity(customer).put(ClientResponse.class);
		fileAnalyzer(isForbidden, false);

		result.bufferEntity();
		if (isForbidden)
		{
			assertForbidden(result, false);
		}
		else
		{
			//check the response 
			assertOk(result, true);
		}
	}

	private void deleteUser(final String auth, final boolean isForbidden, final String customerName)
	{
		fileAnalyzer(isForbidden, true);
		final ClientResponse result = webResource.path(URI_CUSTOMER + "/" + customerName).header("Content-Type",
				MediaType.APPLICATION_XML).header(HEADER_AUTH_KEY, auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);
		fileAnalyzer(isForbidden, false);

		result.bufferEntity();
		if (isForbidden)
		{
			assertForbidden(result, false);
		}
		else
		{
			//check the response 
			assertOk(result, true);
		}
	}

	private void readUserAddresses(final String auth)
	{
		final ClientResponse result = webResource.path(URI_CUSTOMER + "/" + TESTCUSTOMER2).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, auth).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		//check the response 
		assertOk(result, false);
		final CustomerDTO customer = result.getEntity(CustomerDTO.class);
		assertNotNull("No customer within body at response: " + result, customer);
		if (testCustomer1Auth.equals(auth))
		{
			assertEquals("Addresses shouldn't exist: " + result, null, customer.getAddresses());
		}
		else
		{
			assertEquals("Wrong number of user addresses at response: " + result, 2, customer.getAddresses().size());
		}
	}

	private void changeUserAddresses(final String auth, final boolean isForbidden)
	{
		final AddressDTO newAddress = new AddressDTO();
		final UserDTO user = new UserDTO();
		user.setUid(TESTCUSTOMER2);
		final CustomerDTO customer = new CustomerDTO();
		customer.setUid(TESTCUSTOMER2);
		newAddress.setOwner(user);
		customer.setAddresses(Arrays.asList(newAddress));

		fileAnalyzer(isForbidden, true);
		final ClientResponse result = webResource.path(URI_CUSTOMER + "/" + customer.getUid()).header("Content-Type",
				MediaType.APPLICATION_XML).header(HEADER_AUTH_KEY, auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML)
				.entity(customer).put(ClientResponse.class);
		fileAnalyzer(isForbidden, false);

		result.bufferEntity();
		if (isForbidden)
		{
			assertForbidden(result, false);
		}
		else
		{
			//check the response 
			assertOk(result, true);
		}
	}

	private void readUserUserGroups(final String auth)
	{
		final ClientResponse result = webResource.path(URI_CUSTOMER + "/" + TESTCUSTOMER2).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, auth).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		//check the response 
		assertOk(result, false);
		final CustomerDTO customer = result.getEntity(CustomerDTO.class);
		assertNotNull("No customer within body at response: " + result, customer);
		if (testCustomer1Auth.equals(auth))
		{
			assertEquals("Wrong number of user groups at response: " + result, 0, customer.getGroups().size());
		}
		else
		{
			assertEquals("Wrong number of user groups at response: " + result, 1, customer.getGroups().size());
		}
	}

	private void changeOrCreateUserUserGroups(final String auth, final boolean isForbidden, final String userGroupName)
	{
		final UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setUid(userGroupName);
		final CustomerDTO customer = new CustomerDTO();
		customer.setUid(TESTCUSTOMER2);
		customer.setGroups(Collections.singleton((PrincipalGroupDTO) userGroupDTO));

		fileAnalyzer(isForbidden, true);
		final ClientResponse result = webResource.path(URI_CUSTOMER + "/" + customer.getUid()).header("Content-Type",
				MediaType.APPLICATION_XML).header(HEADER_AUTH_KEY, auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML)
				.entity(customer).put(ClientResponse.class);
		fileAnalyzer(isForbidden, false);

		result.bufferEntity();
		if (isForbidden)
		{
			assertForbidden(result, false);
		}
		else
		{
			//check the response 
			assertOk(result, true);
		}
	}

	@After
	public void cleanUpData()
	{
		final UserGroupModel newGroup1 = userService.getUserGroupForUID("customergroup3");
		modelService.remove(newGroup1);
	}

	private void fileAnalyzer(final boolean isForbidden, final boolean disable)
	{
		if (isForbidden && disable)
		{
			TestUtils.disableFileAnalyzer();
		}
		else if (isForbidden && !disable)
		{
			TestUtils.enableFileAnalyzer();
		}
	}
}
