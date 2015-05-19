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

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.Company;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.dto.user.AddressDTO;
import de.hybris.platform.core.dto.user.AddressesDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.user.Address;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.UserManager;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class AddressResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "addresses/";
	private Address testAddress;
	private Customer testCustomer;

	public AddressResourceTest() throws Exception // NOPMD
	{
		super();
	}

	@Before
	public void setUpAddresses() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCustomers();
		this.testCustomer = UserManager.getInstance().getCustomerByLogin("testCustomer1");
		this.testAddress = (Address) testCustomer.getAllAddresses().iterator().next();
	}

	@Test
	public void testGetAddress() throws JaloSecurityException
	{
		final String path1 = URI + this.testAddress.getPK().getLongValue();
		final ClientResponse result = webResource.path(path1).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final AddressDTO address1 = result.getEntity(AddressDTO.class);
		assertNotNull("No address within body at response: " + result, address1);
		assertEquals("Wrong customer at response: " + result, testCustomer.getUID(), ((UserModel) modelService.get(PK
				.fromLong(address1.getOwner().getPk().longValue()))).getUid());
		assertEquals("Wrong street name at response: " + result, testAddress.getAttribute(Address.STREETNAME), address1
				.getStreetname());
	}

	@Test
	public void testPutAddressUpdate() throws JaloSecurityException
	{
		final AddressDTO addressDto = new AddressDTO();
		addressDto.setStreetname("testChangedStreetName");

		final String path1 = URI + this.testAddress.getPK().getLongValue();
		ClientResponse result = webResource.path(path1).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).entity(addressDto).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(path1).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final AddressDTO address1 = result.getEntity(AddressDTO.class);
		assertNotNull("No address within body at response: " + result, address1);
		assertEquals("Wrong customer at response: " + result, testCustomer.getUID(), ((UserModel) modelService.get(PK
				.fromLong(address1.getOwner().getPk().longValue()))).getUid());
		assertEquals("Wrong street name at response: " + result, "testChangedStreetName", address1.getStreetname());
		assertEquals("Wrong street name at response: " + result, "testChangedStreetName", testAddress
				.getAttribute(Address.STREETNAME));
	}

	@Test
	public void testPutAddressNew()
	{

		//First check if there are exactly two addresses:
		{
			//forcing scope to avoid unintentional variable overlapping!
			final ClientResponse beginAddressesResult = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
					HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			beginAddressesResult.bufferEntity();
			assertOk(beginAddressesResult, false);

			final AddressesDTO addressesDto = beginAddressesResult.getEntity(AddressesDTO.class);
			assertNotNull("No addresses within body at response: " + beginAddressesResult, addressesDto);
			assertEquals("Wrong number of addresses at response: " + beginAddressesResult, 4, addressesDto.getAddresses().size());
		}

		final AddressDTO addressDto = new AddressDTO();
		final UserDTO userDto = new UserDTO();
		userDto.setUid(this.testCustomer.getUID());
		addressDto.setOwner(userDto);
		addressDto.setStreetname("putAddressNewStreetName");
		addressDto.setPk(Long.valueOf("adsfadfwqefqwefqw".hashCode()));
		final ClientResponse result = webResource.path(URI + addressDto.getPk()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(addressDto).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);

		//Now check if any address was added:
		{
			//forcing scope to avoid unintentional variable overlapping!
			final ClientResponse updateResult = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
					HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			updateResult.bufferEntity();
			assertOk(updateResult, false);

			final AddressesDTO addressesDto = updateResult.getEntity(AddressesDTO.class);
			assertNotNull("No addresses within body at response: " + updateResult, addressesDto);

			//there should be three addresses now:
			assertEquals("Wrong number of addresses at response: " + updateResult, 5, addressesDto.getAddresses().size());
		}
	}

	@Test
	public void testDeleteAddress()
	{
		final String path1 = URI + this.testAddress.getPK().getLongValue();
		ClientResponse result = webResource.path(path1).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(path1).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();

		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + this.testAddress.getPK().getLongValue(), POST);
	}

	/*
	 * DEL-253 There's a problem with GET request to AddressResource when it's underlying AddressModel is combined with
	 * anything but UserModel. This test case uses a Company (CompanyModel is-not a UserModel) to demonstrate bug.
	 */
	@Ignore("DEL-253, PLA-11441")
	@Test
	public void testAddressWithCompanyAsOwner() throws JaloBusinessException
	{
		final Company testCompany = CatalogManager.getInstance().createCompany("testCompanyABC");
		testCompany.setName("testCompanyABC's name");
		testCompany.setDescription("testCompanyABC's description");
		final Address testAddress = UserManager.getInstance().createAddress(testCompany);
		testAddress.setAttribute(Address.STREETNAME, "testStreet");

		final String path1 = URI + testAddress.getPK().getLongValue();
		final ClientResponse result = webResource.path(path1).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		testCompany.remove();
	}
}
