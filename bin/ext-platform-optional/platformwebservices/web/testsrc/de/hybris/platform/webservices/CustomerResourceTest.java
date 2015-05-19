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

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

import de.hybris.platform.core.dto.c2l.LanguageDTO;
import de.hybris.platform.core.dto.user.CustomerDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.UserManager;


public class CustomerResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "customers/";
	private Customer testCustomer;

	public CustomerResourceTest() throws Exception // NOPMD
	{
		super();
	}

	@Before
	public void setUpCustomers() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCustomers();
		this.testCustomer = (Customer) UserManager.getInstance().getUserByLogin("testCustomer1");
	}

	@Test
	public void testGetCustomers() throws IOException
	{
		final ClientResponse result = webResource.path(URI + testCustomer.getUID()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CustomerDTO user = result.getEntity(CustomerDTO.class);
		assertNotNull("No customer within body at response: " + result, user);
		assertEquals("Wrong user UID at response (different user retrieved): " + result, this.testCustomer.getUID(), user.getUid());
		assertEquals("Wrong country name at response: " + result, this.testCustomer.getName(), user.getName());
		assertEquals("Wrong number of addresses at response: ", 2, user.getAddresses().size());
	}

	@Test
	public void testPutCustomerCreate() throws Exception
	{
		final CustomerDTO user = new CustomerDTO();
		user.setUid("newTestCustomer");
		user.setName("testName");

		final ClientResponse result = webResource.path(URI + "newTestCustomer").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(user).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		final Customer newTestCustomer = (Customer) UserManager.getInstance().getUserByLogin("newTestCustomer");
		assertEquals("Name was not set correctly", "testName", newTestCustomer.getName());

		//remove the newly created customer
		newTestCustomer.remove();
	}

	@Test
	public void testPutCustomerUpdate()
	{
		final CustomerDTO user = new CustomerDTO();
		user.setUid("testCustomer1");
		user.setName("testChangedName");

		final ClientResponse result = webResource.path(URI + this.testCustomer.getUID()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(user).put(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		//final Customer newTestCustomer = UserManager.getInstance().getCustomerByLogin("testCustomer1");
		/*
		 * result = webResource.path(URI + testCustomer.getUID()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
		 * HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		 * result.bufferEntity(); final UserDTO returnedUser = result.getEntity(UserDTO.class);
		 */
		assertEquals("Name was not changed correctly", "testChangedName", this.testCustomer.getName());

	}


	//PLA-8262 (dup of: PLA-7687) ; create and assign will not work; what works is assign existing entities
	/*
	 * The purpose of this method is to test if update of a UserDTO property that is itself a DTO (i.e. sessionLanguage)
	 * works. This should be merged into one method together with testPutUserUpdate, but for now (fixing a bug) it's
	 * better to leave it separated.
	 */
	@Test
	public void testPutCustomerUpdateLanguageProperty()
	{
		//Get the "Czech" language using webresource
		final ClientResponse languageResult = webResource.path("languages/" + "cs").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		languageResult.bufferEntity();
		assertOk(languageResult, false);
		final LanguageDTO czechLanguage = languageResult.getEntity(LanguageDTO.class);
		assertEquals("Language \"cs\" not found!", "cs", czechLanguage.getIsocode());

		final String userUID = this.testCustomer.getUID();
		final String newUserName = "testChangedName1234";

		final CustomerDTO user = new CustomerDTO();
		user.setUid(userUID);
		user.setName(newUserName);
		user.setSessionLanguage(czechLanguage);

		//Our UserDTO has the same uid as this.testCustomer object which was originally created with Polish language,
		//so after update we should observe language's change: Polish -> Czech.
		final ClientResponse result = webResource.path(URI + userUID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(user).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		assertEquals("Name was not changed correctly", newUserName, this.testCustomer.getName());
		assertEquals("SessionLanguage was not changed correctly", "cs", this.testCustomer.getSessionLanguage().getIsoCode());

	}

	@Test(expected = JaloItemNotFoundException.class)
	public void testDeleteCustomer()
	{
		final ClientResponse result = webResource.path(URI + testCustomer.getUID()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		UserManager.getInstance().getUserByLogin("testCustomer1");
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testCustomer.getUID(), POST);
	}

}
