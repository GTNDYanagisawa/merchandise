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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.dto.order.CartsDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class CartEntryResourceTest extends AbstractWebServicesTest
{
	public CartEntryResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCarts() throws ConsistencyCheckException
	{
		createTestCatalogs();
		createTestProductsUnits();
		createTestCarts();
		createTestProductPrices();
	}

	/**
	 * This test retrieves the cart entry, on a step by step basis, first it gets a cart with "adminCart" code, then it
	 * retrieves its first entry.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetCartEntry() throws IOException
	{

		final ClientResponse cartsResult = webResource.path("/carts").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartsResult.bufferEntity();
		final CartsDTO carts = cartsResult.getEntity(CartsDTO.class);
		if (carts.getCarts().isEmpty())
		{
			fail("No carts defined");
		}
		CartDTO cart = null;
		for (CartDTO tempCart : carts.getCarts())
		{
			final ClientResponse cartResult = webResource.path("/carts/" + tempCart.getCode()).cookie(tenantCookie).header(
					HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			cartResult.bufferEntity();
			tempCart = cartResult.getEntity(CartDTO.class);
			if ("adminCart".equals(tempCart.getCode()))
			{
				cart = tempCart;
				break;
			}
		}
		assertNotNull("Expected cart not found", cart);
		assertNotNull("Expected cart not found", cart.getCode()); //NOPMD
		final List<CartEntryDTO> entries = cart.getEntries();
		if (entries.isEmpty())
		{
			fail("No entries in cart defined");
		}
		CartEntryDTO entry = entries.get(0);
		final ClientResponse entryResult = webResource.path("/carts/" + cart.getCode() + "/cartentries/" + entry.getPk()).cookie(
				tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(
				ClientResponse.class);
		entryResult.bufferEntity();
		assertOk(entryResult, false);
		entry = entryResult.getEntity(CartEntryDTO.class);
		//		assertNotNull("No entry within body at response: " + entryResult, entry);
		assertNotNull("Entry quantity needs to be set at response: " + entryResult, entry.getQuantity());
		assertNotNull("Entry unit needs to be set at response: " + entryResult, entry.getUnit());
	}

	/**
	 * This test checks system behavior if you do a PUT request with a 0 value as an amount - the cart entry should be
	 * removed. To do so it retrieves the cart entry on a step by step basis, first getting a cart with "cartForPut"
	 * code, then its first entry, regardless of how many there may be.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPutCartEntry() throws IOException
	{
		final ClientResponse cartsResult = webResource.path("/carts").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartsResult.bufferEntity();
		final CartsDTO carts = cartsResult.getEntity(CartsDTO.class);
		if (carts.getCarts().isEmpty())
		{
			fail("No carts defined");
		}
		CartDTO cart = null;
		for (CartDTO tempCart : carts.getCarts())
		{
			final ClientResponse cartResult = webResource.path("/carts/" + tempCart.getCode()).cookie(tenantCookie).header(
					HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			cartResult.bufferEntity();
			tempCart = cartResult.getEntity(CartDTO.class);
			if ("cartForPut".equals(tempCart.getCode()))
			{
				cart = tempCart;
				break;
			}
		}
		assertNotNull("Expected cart not found", cart);
		assertNotNull("Expected cart not found", cart.getCode()); //NOPMD
		final List<CartEntryDTO> entries = cart.getEntries();
		final int oldSize = entries.size();
		assertFalse("Unexpected number of entries in cart", 0 == oldSize);
		final CartEntryDTO entry = entries.get(0);
		//prepare entity for put update
		final CartEntryDTO updateEntry = entries.get(0);
		updateEntry.setQuantity(Long.valueOf(0));
		//perform put with 0 value
		final ClientResponse entryResult = webResource.path("/carts/" + cart.getCode() + "/cartentries/" + entry.getPk()).cookie(
				tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(
				updateEntry).put(ClientResponse.class);
		entryResult.bufferEntity();
		assertOk(entryResult, true);
		// retrieve the cart again and verify that one entry is gone
		final ClientResponse cartResult = webResource.path("/carts/" + cart.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		cart = cartResult.getEntity(CartDTO.class);
		assertEquals("Unexpected number of entries in cart", oldSize - 1, cart.getEntries().size());

	}
}
