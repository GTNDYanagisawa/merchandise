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
import static org.junit.Assert.fail;

import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.order.Cart;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class CartResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "carts/";
	private static final String TEST_CART_CODE = "cartForPut";
	private Cart testCart;

	public CartResourceTest() throws Exception //NOPMD
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

		//get a reference to a test cart Model
		getWsUtilService();
		final Collection<CartModel> allCarts = wsUtilService.getAllCarts();
		for (final CartModel cart : allCarts)
		{
			if (TEST_CART_CODE.equals(cart.getCode()))
			{
				testCart = modelService.getSource(cart);
				break;
			}
		}
	}

	@Test
	public void testGetCart() throws IOException
	{
		final ClientResponse cartResultGET = webResource.path(URI + testCart.getCode()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResultGET.bufferEntity();
		assertOk(cartResultGET, false);

		final CartDTO cart = cartResultGET.getEntity(CartDTO.class);
		assertNotNull("Expected cart not found", cart);
		assertEquals("Expected cart not found in response: " + cartResultGET, TEST_CART_CODE, cart.getCode());
		assertEquals("Number of cart entries is invalid in response: " + cartResultGET, 1, cart.getEntries().size());
		assertEquals("Currency is invalid in response: " + cartResultGET, "EUR", cart.getCurrency().getIsocode());
	}

	@Test
	public void testGetCartJson() throws IOException
	{
		final ClientResponse cartResultGET = webResource.path(URI + testCart.getCode()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		cartResultGET.bufferEntity();

		//assertOk() not used because of different media type: APPLICATION_JSON_TYPE
		assertEquals("Wrong HTTP status at response: " + cartResultGET, Response.Status.OK, cartResultGET.getResponseStatus());
		assertTrue("Wrong content-type at response: " + cartResultGET, MediaType.APPLICATION_JSON_TYPE.isCompatible(cartResultGET
				.getType()));

		final CartDTO cart = cartResultGET.getEntity(CartDTO.class);
		assertNotNull("Expected cart not found", cart);
		assertEquals("Expected cart not found in response: " + cartResultGET, TEST_CART_CODE, cart.getCode());
		assertEquals("Number of cart entries is invalid in response: " + cartResultGET, 1, cart.getEntries().size());
		assertEquals("Currency is invalid in response: " + cartResultGET, "EUR", cart.getCurrency().getIsocode());
	}

	/**
	 * This tests "update" request only. We do not test "create" request with PUT method, because this would require
	 * prior knowledge of a target cart's PK.
	 */
	@Test
	public void testPutUpdateCart()
	{
		final CartDTO cart2update = new CartDTO();
		cart2update.setCode(testCart.getCode());
		cart2update.setPk(Long.valueOf(testCart.getPK().getLongValue()));
		final Calendar cal = Calendar.getInstance();

		//this "switching" variable is introduced to make this test actually TESTING things also at: january.10, 11:12
		final int MINUTE = ((cal.get(Calendar.MINUTE) > 30) ? 12 : 36);

		cal.set(Calendar.DAY_OF_YEAR, 10);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, MINUTE);

		cart2update.setStatusInfo("JustATestInfo");
		cart2update.setDate(cal.getTime());

		final ClientResponse cartResultPUT = webResource.path(URI + testCart.getCode()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(cart2update).put(
				ClientResponse.class);
		cartResultPUT.bufferEntity();
		assertOk(cartResultPUT, false);

		final Calendar resultCalendar = Calendar.getInstance();
		resultCalendar.setTime(testCart.getDate());

		/*
		 * WARNINIG: Unfortunately it might break in some cases! It's EXTREMALY HARD to predict Calendar/Date behavior
		 * with different timezone/locale settings. It's kept as simple as possible to catch "real" bugs instead of
		 * Calendar/Date oddities.
		 */
		assertEquals("Cart 'date' property is not modified correctly: " + cartResultPUT, 10, resultCalendar
				.get(Calendar.DAY_OF_YEAR));
		assertEquals("Cart 'date' property is not modified correctly: " + cartResultPUT, 11, resultCalendar
				.get(Calendar.HOUR_OF_DAY));
		assertEquals("Cart 'date' property is not modified correctly: " + cartResultPUT, MINUTE, resultCalendar
				.get(Calendar.MINUTE));

		assertEquals("Cart 'statusInfo' property is not modified correctly: " + cartResultPUT, "JustATestInfo", testCart
				.getStatusInfo());

		//just to check if those remain untouched...
		assertEquals("Number of cart entries is invalid: " + cartResultPUT, 1, testCart.getAllEntries().size());
		assertEquals("Cart 'isocode' property is invalid: " + cartResultPUT, "EUR", testCart.getCurrency().getIsoCode());
	}

	@Test
	public void testPutDeleteCart()
	{
		assertEquals("Initial number of Carts is invalid", 3, wsUtilService.getAllCarts().size());

		final ClientResponse cartResultGET = webResource.path(URI + testCart.getCode()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		cartResultGET.bufferEntity();
		assertOk(cartResultGET, false);

		final Collection<CartModel> allCarts = wsUtilService.getAllCarts();
		assertEquals("Final number of Carts is invalid", 2, allCarts.size());

		for (final CartModel cart : allCarts)
		{
			if (TEST_CART_CODE.equals(cart.getCode()))
			{
				fail("The cart was not deleted");
			}
		}
	}
}
