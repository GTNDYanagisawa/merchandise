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

import de.hybris.platform.core.dto.c2l.LanguageDTO;
import de.hybris.platform.core.dto.user.CustomerDTO;
import de.hybris.platform.core.dto.user.EmployeeDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.dto.user.UsersDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 *
 */
public class UsersResourceSelectorTest extends AbstractWebServicesTest
{
	private static final String URI = "users";

	public UsersResourceSelectorTest() throws Exception // NOPMD
	{
		super();
	}

	@Before
	public void setUpUsers() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCustomers();
	}

	/**
	 * This test test Attribute Selector API runtime config for the following scenario: We are testing selecting the
	 * attributes of a "sessionLanguage" subresource of a "user" resource using "users" root resource. No suffix
	 * ("detail"/"reference") is used. Configuration by property name "sessionLanguage" is used. Additionally, it is
	 * tested if "uri" attribute is not null on all returned resources.
	 */
	@Test
	public void testGetUsersRuntimeConfigNoSuffixScopedPropertyName()
	{
		performTestBody("sessionLanguage_attributes");
	}

	/**
	 * This test test Attribute Selector API runtime config for the following scenario: We are testing selecting the
	 * attributes of a "sessionLanguage" subresource of a "user" resource using "users" root resource. No suffix
	 * ("detail"/"reference") is used. Configuration by resource type name "language" is used. Additionally, it is tested
	 * if "uri" attribute is not null on all returned resources.
	 */
	@Test
	public void testGetUsersRuntimeConfigNoSuffixTypeName()
	{
		performTestBody("language_attributes");
	}

	/**
	 * @param selectorQueryParamName
	 *           Attribute Selector API type selector for the sessionLanguage attribute of the "user" resource type.
	 */
	private void performTestBody(final String selectorQueryParamName)
	{
		final ClientResponse result = webResource.path(URI) //
				.queryParam("user_attributes", " uid, namE , sessionlanGuage ") //
				.queryParam(selectorQueryParamName, " aCTIVE , CreatioNTimE , isocoDe") //
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(
						ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final UsersDTO usersDto = result.getEntity(UsersDTO.class);
		assertNotNull("No customers within body at response: " + result, usersDto);
		final List<UserDTO> users = usersDto.getUsers();

		// UserResource/UserDTO is designed to return users and not only customers
		// however, for now 'admin' isn't present because restjersey-service actually only returns 'Customers'
		this.removeUsersFromList(users, "anonymous", "admin");

		//four created users
		assertEquals("Wrong number of customers at response: " + result, 4, users.size());
		assertIsNotNull(users, UserDTO.class, "uid", "uri", "name", "sessionLanguage");

		for (final UserDTO user : users)
		{
			final LanguageDTO language = user.getSessionLanguage();
			assertNotNull("\"uri\" should not be null", language.getUri());
			assertNotNull("\"active\" should not be null", language.getActive());
			assertNotNull("\"creationtime\" should not be null", language.getCreationtime());
			assertNotNull("\"isocode\" should not be null", language.getIsocode());
			assertNull("\"modifiedtime\" should be null", language.getModifiedtime());

			if ("employee_name2".equals(user.getName()))
			{
				assertEquals("incorrect sessionLanguage: ", "cs", user.getSessionLanguage().getIsocode());
			}
		}
	}

	/**
	 * This test test Attribute Selector API runtime config for the following scenario: We are testing selecting the
	 * attributes of a "sessionLanguage" subresource of a "user" resource using "users" root resource. No suffix
	 * ("detail"/"reference") is used. Two possible configurations are used: by resource type name "language" and by
	 * property name "sessionLanguage". Configuration by property name should take precedence in that case. Additionally,
	 * it is tested if "uri" attribute is not null on all returned resources.
	 */
	@Test
	public void testGetUsersRuntimeConfigPropertyNameOverType()
	{
		final ClientResponse result = webResource.path(URI) //
				.queryParam("user_attributes", "uid, namE , sessionlanGuage ") //
				.queryParam("language_attributes", " modifiedtime") //
				.queryParam("sessionLanguage_attributes", " aCTIVE , CreatioNTimE , isocoDe") //
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(
						ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final UsersDTO usersDto = result.getEntity(UsersDTO.class);
		assertNotNull("No customers within body at response: " + result, usersDto);
		final List<UserDTO> users = usersDto.getUsers();

		// UserResource/UserDTO is designed to return users and not only customers
		// however, for now 'admin' isn't present because restjersey-service actually only returns 'Customers'
		this.removeUsersFromList(users, "anonymous", "admin");

		//four created users
		assertEquals("Wrong number of customers at response: " + result, 4, users.size());
		assertIsNotNull(users, UserDTO.class, "uid", "uri", "name", "sessionLanguage");

		for (final UserDTO user : users)
		{
			final LanguageDTO language = user.getSessionLanguage();
			assertNotNull("\"uri\" should not be null", language.getUri());
			assertNotNull("\"active\" should not be null", language.getActive());
			assertNotNull("\"creationtime\" should not be null", language.getCreationtime());
			assertNotNull("\"isocode\" should not be null", language.getIsocode());
			assertNull("\"modifiedtime\" should be null", language.getModifiedtime());

			if ("employee_name2".equals(user.getName()))
			{
				assertEquals("incorrect sessionLanguage: ", "cs", user.getSessionLanguage().getIsocode());
			}
		}
	}

	/**
	 * This test test Attribute Selector API runtime config for the following scenario: We are testing selecting the
	 * attributes of a "user" resource using "users" root resource. No suffix ("detail"/"reference") is used. Each
	 * element of returned "users" collection can be of a type "employee" or "customer" - which are subtypes of "user".
	 * Three configuration sources are provided: for "user" type, for "employee" type and for "customer" type. This test
	 * verifies, that for each returned collection element, best matching configuration will be picked up i.e. for
	 * elements of type "customer" a configuration for "customer" will be used and for elements of type "employee" a
	 * configuration for "employee" will be used. Configuration for "user" should not be used, because all elements are
	 * of either "customer" or "employee" type.
	 */
	@Test
	public void testGetUsersRuntimeConfigSubtypeOverSuperType()
	{
		final ClientResponse result = webResource.path(URI) //
				.queryParam("user_attributes", "uid, namE , sessionlanGuage ") //
				.queryParam("employee_attributes", "uid, namE, passwordquestion") //
				.queryParam("customer_attributes", " customerid, nAme, uid") //
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(
						ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final UsersDTO usersDto = result.getEntity(UsersDTO.class);
		assertNotNull("No customers within body at response: " + result, usersDto);
		final List<UserDTO> users = usersDto.getUsers();

		// UserResource/UserDTO is designed to return users and not only customers
		// however, for now 'admin' isn't present because restjersey-service actually only returns 'Customers'
		this.removeUsersFromList(users, "anonymous", "admin");

		//four created users
		assertEquals("Wrong number of customers at response: " + result, 4, users.size());
		assertIsNull(users, UserDTO.class, "sessionLanguage");
		assertIsNotNull(users, UserDTO.class, "uid", "uri", "name");

		for (final UserDTO user : users)
		{
			if (user instanceof EmployeeDTO)
			{
				assertNotNull("passwordQuestion should not be null", user.getPasswordQuestion());
				assertNull("sessionLanguage should be null", user.getSessionLanguage());
			}
			else if (user instanceof CustomerDTO)
			{
				assertNull("passwordQuestion should be null", user.getPasswordQuestion());
				assertNull("sessionLanguage should be null", user.getSessionLanguage());
				assertNotNull("customerId should not be null", ((CustomerDTO) user).getCustomerID());
			}
		}
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
}
