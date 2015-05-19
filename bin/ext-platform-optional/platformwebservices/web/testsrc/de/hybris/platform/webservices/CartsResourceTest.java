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
import de.hybris.platform.core.dto.c2l.CurrencyDTO;
import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.dto.order.CartsDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CartsResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "carts";

	public CartsResourceTest() throws Exception //NOPMD
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

	@Test
	public void testGetCarts() throws IOException
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CartsDTO carts = result.getEntity(CartsDTO.class);
		assertNotNull("No carts found at response: " + result, carts);
		assertEquals("Wrong number of carts found at response: " + result, 3, carts.getCarts().size());
	}

	@Test
	public void testPostCarts()
	{
		final String newCartCode = String.valueOf(System.currentTimeMillis()).substring(10);
		final CartDTO newCart = new CartDTO();
		final CurrencyDTO euro = new CurrencyDTO();
		euro.setIsocode("EUR");
		final UserDTO user = new UserDTO();
		user.setUid("admin");
		newCart.setCurrency(euro);
		newCart.setDate(new java.util.Date());
		newCart.setUser(user);
		newCart.setCalculated(Boolean.FALSE);
		newCart.setNet(Boolean.FALSE);
		newCart.setCode(newCartCode);

		//newCart.setDeliveryCost(Double.valueOf(12.0));
		//newCart.setPaymentCost(Double.valueOf(2.5));
		//newCart.setTotalPrice(Double.valueOf(69.0));
		//newCart.setTotalTax(Double.valueOf(6.0));


		getWsUtilService();

		//Ensure there are 3 Carts initially
		assertEquals("initial number of Carts is invalid", 3, wsUtilService.getAllCarts().size());

		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(newCart).post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);

		//Ensure there are 4 Carts now
		assertEquals("initial number of Carts is invalid", 4, wsUtilService.getAllCarts().size());

		//check the DTO retrieved from response
		final CartDTO resultCart = result.getEntity(CartDTO.class);
		assertEquals("Wrong cart user at response: " + result, user.getName(), resultCart.getUser().getName());

		//check the actual item retrieved from the system.
		final CartModel cart2delete = modelService.get(PK.parse(String.valueOf(resultCart.getPk())));

		modelService.remove(cart2delete);

		//Ensure there are 3 Carts finally
		assertEquals("final number of Carts is invalid", 3, wsUtilService.getAllCarts().size());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, DELETE);
	}

}
