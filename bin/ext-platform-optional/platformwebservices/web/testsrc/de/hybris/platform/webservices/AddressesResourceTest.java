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

import de.hybris.platform.core.PK;
import de.hybris.platform.core.dto.user.AddressDTO;
import de.hybris.platform.core.dto.user.AddressesDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.UserManager;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class AddressesResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "addresses";
	private Customer testCustomer;

	public AddressesResourceTest() throws Exception // NOPMD
	{
		super();
	}

	@Before
	public void setUpAddresses() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCustomers();
		this.testCustomer = UserManager.getInstance().getCustomerByLogin("testCustomer1");
	}

	@Test
	public void testGetAddresses()
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final AddressesDTO addressesDto = result.getEntity(AddressesDTO.class);
		assertNotNull("No addresses within body at response: " + result, addressesDto);

		//two created addresses
		assertEquals("Wrong number of addresses at response: " + result, 4, addressesDto.getAddresses().size());
		assertIsNotNull(addressesDto.getAddresses(), AddressDTO.class, "uri");
	}

	@Test
	public void testPostAddresses()
	{
		final AddressDTO address = new AddressDTO();
		final UserDTO user = new UserDTO();
		//create one address for the test user, 
		//since this user and all his addresses will be deleted after the whole test, we do not delete this address here
		user.setUid(this.testCustomer.getUID());
		address.setOwner(user);
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(address).post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);

		final AddressDTO resultAddress = result.getEntity(AddressDTO.class);

		assertEquals("Wrong customer id at response: " + result, this.testCustomer.getUID(), ((UserModel) modelService.get(PK
				.fromLong(resultAddress.getOwner().getPk().longValue()))).getUid());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, DELETE);
	}

}
