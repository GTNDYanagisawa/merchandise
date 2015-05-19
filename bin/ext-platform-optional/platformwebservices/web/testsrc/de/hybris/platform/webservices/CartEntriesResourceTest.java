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
import static org.junit.Assert.fail;

import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.dto.order.CartsDTO;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.dto.product.UnitDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class CartEntriesResourceTest extends AbstractWebServicesTest
{

	public CartEntriesResourceTest() throws Exception //NOPMD
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
	 * This test checks system behavior if you do a POST request. To do so it retrieves the cart entry on a step by step
	 * basis, first getting a cart with "emptyCart" code, then its first entry, regardless of how many there may be.
	 * 
	 * @throws IOException
	 */
	//Commented due to problems with order, which is not set correctly !!!
	@Test
	public void testPostCartEntry() throws IOException
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
			if ("emptyCart".equals(tempCart.getCode()))
			{
				cart = tempCart;
				break;
			}
		}
		assertNotNull("Expected cart not found", cart);
		assertNotNull("Expected cart not found", cart.getCode()); //NOPMD
		assertEquals("Unexpected number of entries in cart", 0, cart.getEntries().size());
		final String cartCode = cart.getCode();
		//prepare entity for post request
		final CartEntryDTO postEntry = new CartEntryDTO();
		final ProductDTO product = new ProductDTO();
		final UnitDTO unit = new UnitDTO();
		unit.setCode("testUnit1");
		//product.setUri(PRODUCT1URI);
		product.setCode("testProduct1");
		postEntry.setProduct(product);
		postEntry.setQuantity(Long.valueOf(1));
		postEntry.setUnit(unit);
		//problem with order, which is not set correctly !!!
		//perform post with non 0 value
		final ClientResponse entryResult = webResource.path("/carts/" + cartCode + "/cartentries").cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(postEntry).post(
				ClientResponse.class);
		entryResult.bufferEntity();
		assertCreated(entryResult, false);
		// retrieve the cart again and verify that one entry is added
		final ClientResponse cartResult = webResource.path("/carts/" + cartCode).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		cart = cartResult.getEntity(CartDTO.class);
		assertEquals("Unexpected number of entries in cart", 1, cart.getEntries().size());

		// perform post once again 'null' quantity, same product, same unit
		// result should be that already existing entry is updated by adding 
		// posted quantity

		// set quantity to 'null' invokes special business logic of 'increment by one' (relative increasing by one)
		// any other quantity value directly writes the passed value down (absolute increasing and decreasing)
		postEntry.setQuantity(null);
		final ClientResponse entryResult2 = webResource.path("/carts/" + cartCode + "/cartentries").cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(postEntry).post(
				ClientResponse.class);
		entryResult2.bufferEntity();
		// update means: 200 (OK) and not 201 (Created)
		assertOk(entryResult2, false);
		// retrieve the cart again and verify that one entry is added
		final ClientResponse cartResult2 = webResource.path("/carts/" + cartCode).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult2.bufferEntity();
		cart = cartResult2.getEntity(CartDTO.class);
		assertEquals("Unexpected number of entries in cart", 1, cart.getEntries().size());
		final CartEntryDTO cartEntry = cart.getEntries().get(0);
		// cartEntry.getUri().replace(webResource.getURI().toString(), "")
		// above call takes full URI from cart entry and strips it from the
		// starting part - it is not desired while retrieving the entry
		final ClientResponse entryResult3 = webResource.path(cartEntry.getUri().replace(webResource.getURI().toString(), ""))
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(
						ClientResponse.class);
		entryResult3.bufferEntity();
		assertOk(entryResult3, false);
		final CartEntryDTO entry = entryResult3.getEntity(CartEntryDTO.class);
		assertEquals("Quantity should be 2 in " + entry.getProduct().getCode(), Long.valueOf(2), entry.getQuantity());

	}
}
