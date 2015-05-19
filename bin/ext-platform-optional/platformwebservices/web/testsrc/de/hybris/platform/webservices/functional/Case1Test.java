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
package de.hybris.platform.webservices.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.platform.category.dto.CategoryDTO;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.dto.order.CartsDTO;
import de.hybris.platform.core.dto.order.OrderDTO;
import de.hybris.platform.core.dto.order.delivery.DeliveryModeDTO;
import de.hybris.platform.core.dto.order.payment.CreditCardPaymentInfoDTO;
import de.hybris.platform.core.dto.order.payment.PaymentModeDTO;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.dto.user.AddressDTO;
import de.hybris.platform.core.dto.user.CustomerDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.util.Base64;
import de.hybris.platform.webservices.AbstractWebServicesTest;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */

public class Case1Test extends AbstractWebServicesTest
{
	private String cartCode;
	private Long address1PK;
	private String auth;


	public Case1Test() throws Exception // NOPMD
	{
		super();
	}

	@Before
	public void setUpData() throws ConsistencyCheckException, JaloBusinessException
	{
		importCSVFromResources("testcase1");
	}

	@Test
	public void testWalkthrough() //NOPMD
	{
		registerNewCustomer();
		retrieveAndChangePassword();
		browseProductsAndCategories();
		addProductsToCart();
		setUserAddress();
		getProductAsUser();
		addProductsAsUser();
		setPaymentAndDeliveryAddress();
		setPaymentAndDeliveryMode();
		createPaymentInfo();
		previewOfCart();
		orderCart();
	}

	private void registerNewCustomer()
	{
		// generated usergroupDTO issue
		// add new usergroup with id customergroup
		//		final ClientResponse putResult = webResource.path(
		//				FunctionalTestConstants.URI_USERGROUPS + "/" + FunctionalTestConstants.UID_CUSTOMERGROUP1).cookie(tenantCookie)
		//				.accept(MediaType.APPLICATION_XML).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).header("Content-Type",
		//						MediaType.APPLICATION_XML).entity(FunctionalTestConstants.TEST_USERGROUP1).put(ClientResponse.class);
		//		putResult.bufferEntity();
		//		assertCreated(putResult, true);


		// adding new user is done as an admin
		// admin however cannot set a password for a user
		final ClientResponse result = webResource.path(
				FunctionalTestConstants.URI_CUSTOMERS + "/" + FunctionalTestConstants.UID_USER1).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).header("Content-Type", MediaType.APPLICATION_XML).cookie(tenantCookie).accept(
				MediaType.APPLICATION_XML).entity(FunctionalTestConstants.TEST_CUSTOMER1).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);
	}

	@SuppressWarnings("null")
	private void retrieveAndChangePassword()
	{
		// adding new user is done as an admin
		// admin however cannot set a password for a user
		// so I'm retrieving this user's password
		final UserDTO customer = new UserDTO();
		customer.setPasswordAnswer(FunctionalTestConstants.TEST_CUSTOMER1.getPasswordAnswer());
		customer.setUid(FunctionalTestConstants.TEST_CUSTOMER1.getUid());
		final ClientResponse result = webResource.path(
				FunctionalTestConstants.URI_RETRIEVE_PASS + "/" + FunctionalTestConstants.UID_USER1).header("Content-Type",
				MediaType.APPLICATION_XML).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).entity(customer).put(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		final String response = result.getEntity(String.class);
		final Pattern pattern = Pattern.compile("\\w{8}");
		final Matcher matcher = pattern.matcher(response);
		matcher.find();//skip first result, it will be word 'password'
		matcher.find();
		final String password = matcher.group();
		assertNotNull("Password should not be null at this point", password);
		auth = Base64.encodeBytes((FunctionalTestConstants.UID_USER1 + ":" + password).getBytes());
	}

	private void browseProductsAndCategories()
	{
		final ClientResponse categoryResult = webResource.path(FunctionalTestConstants.URI_SHORT_CATEGORY1).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertOk(categoryResult, false);
		categoryResult.bufferEntity();
		final CategoryDTO category = categoryResult.getEntity(CategoryDTO.class);
		assertNotNull("No category within body at response: " + categoryResult, category);
		assertNotNull("Category should have products list", category.getProducts());
		assertFalse("There should be at least one product in the cateogry", category.getProducts().isEmpty());

		final ClientResponse productResult = webResource.path(FunctionalTestConstants.URI_SHORT_PRODUCT1).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertOk(productResult, false);
		productResult.bufferEntity();
		final ProductDTO product = productResult.getEntity(ProductDTO.class);
		assertNotNull("No product within body at response: " + productResult, product);
	}

	@SuppressWarnings("null")
	private void addProductsToCart()
	{
		final ClientResponse cartsResult = webResource.path(FunctionalTestConstants.URI_CARTS).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartsResult.bufferEntity();
		final CartsDTO carts = cartsResult.getEntity(CartsDTO.class);
		if (carts.getCarts().isEmpty())
		{
			fail("No carts defined");
		}
		CartDTO cart = null;
		for (CartDTO tempCart : carts.getCarts())
		{
			final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + tempCart.getCode()).header(
					HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			cartResult.bufferEntity();
			tempCart = cartResult.getEntity(CartDTO.class);
			if ("anonymousCart".equals(tempCart.getCode()))
			{
				cart = tempCart;
				break;
			}
		}
		assertNotNull("Expected cart not found", cart);
		assertNotNull("Expected cart not found", cart.getCode()); //NOPMD
		assertEquals("No entries in cart expected", 0, cart.getEntries().size());
		cartCode = cart.getCode();
		final ClientResponse postResult = webResource.path(
				FunctionalTestConstants.URI_CARTS + "/" + cartCode + FunctionalTestConstants.URI_ENTRIES).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).header("Content-Type",
				MediaType.APPLICATION_XML).entity(FunctionalTestConstants.TEST_ENTRY1).post(ClientResponse.class);
		assertCreated(postResult, false);

		final ClientResponse postResult2 = webResource.path(
				FunctionalTestConstants.URI_CARTS + "/" + cartCode + FunctionalTestConstants.URI_ENTRIES).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).header("Content-Type",
				MediaType.APPLICATION_XML).entity(FunctionalTestConstants.TEST_ENTRY2).post(ClientResponse.class);
		assertCreated(postResult2, false);

		final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		cart = cartResult.getEntity(CartDTO.class);
		assertEquals("Two entries should be present in a cart", 2, cart.getEntries().size());
	}

	private void setUserAddress()
	{
		final ClientResponse postResult = webResource.path(FunctionalTestConstants.URI_ADDRESSES).cookie(tenantCookie).accept(
				MediaType.APPLICATION_XML).header(HEADER_AUTH_KEY, "Basic " + auth).header("Content-Type", MediaType.APPLICATION_XML)
				.entity(FunctionalTestConstants.TEST_ADDRESS1).post(ClientResponse.class);
		assertCreated(postResult, false);

		final ClientResponse customerResult = webResource.path(
				FunctionalTestConstants.URI_CUSTOMERS + "/" + FunctionalTestConstants.UID_USER1).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).header("Content-Type",
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertOk(customerResult, false);
		customerResult.bufferEntity();
		final CustomerDTO customer = customerResult.getEntity(CustomerDTO.class);
		final Collection<AddressDTO> addresses = customer.getAddresses();
		assertFalse("List of user's addresses should not be empty", addresses.isEmpty());
		// store this PK for future use
		address1PK = addresses.iterator().next().getPk();
	}

	private void getProductAsUser()
	{
		final ClientResponse productResult = webResource.path(FunctionalTestConstants.URI_SHORT_PRODUCT1).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertOk(productResult, false);
		productResult.bufferEntity();
		final ProductDTO product = productResult.getEntity(ProductDTO.class);
		assertNotNull("No product within body at response: " + productResult, product);
	}

	private void addProductsAsUser()
	{
		// try to add product 3 to cart
		final ClientResponse postResult = webResource.path(
				FunctionalTestConstants.URI_CARTS + "/" + cartCode + FunctionalTestConstants.URI_ENTRIES).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).header("Content-Type",
				MediaType.APPLICATION_XML).entity(FunctionalTestConstants.TEST_ENTRY3).post(ClientResponse.class);
		assertCreated(postResult, false);

		final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		final CartDTO cart = cartResult.getEntity(CartDTO.class);
		assertEquals("Three entries should be present in a cart", 3, cart.getEntries().size());
		for (final CartEntryDTO cartEntry : cart.getEntries())
		{
			// cartEntry.getUri().replace(webResource.getURI().toString(), "")
			// above call takes full URI from cart entry and strips it from the
			// starting part - it is not desired while retrieving the entry
			final ClientResponse entryResult = webResource.path(cartEntry.getUri().replace(webResource.getURI().toString(), ""))
					.header(HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(
							ClientResponse.class);
			entryResult.bufferEntity();
			final CartEntryDTO entry = entryResult.getEntity(CartEntryDTO.class);
			assertEquals("Quantity should be 1 in " + entry.getProduct().getCode(), Long.valueOf(1), entry.getQuantity());
		}

		// try to add yet another product 3 to cart
		final ClientResponse postResult2 = webResource.path(
				FunctionalTestConstants.URI_CARTS + "/" + cartCode + FunctionalTestConstants.URI_ENTRIES).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).header("Content-Type",
				MediaType.APPLICATION_XML).entity(FunctionalTestConstants.TEST_ENTRY3Q2).post(ClientResponse.class);
		assertOk(postResult2, false);

		final ClientResponse cartResult2 = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult2.bufferEntity();
		final CartDTO cart2 = cartResult2.getEntity(CartDTO.class);
		assertEquals("Three entries should be present in a cart", 3, cart2.getEntries().size());
		for (final CartEntryDTO cartEntry : cart2.getEntries())
		{
			// cartEntry.getUri().replace(webResource.getURI().toString(), "")
			// above call takes full URI from cart entry and strips it from the
			// starting part - it is not desired while retrieving the entry
			final ClientResponse entryResult = webResource.path(cartEntry.getUri().replace(webResource.getURI().toString(), ""))
					.header(HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(
							ClientResponse.class);
			entryResult.bufferEntity();
			final CartEntryDTO entry = entryResult.getEntity(CartEntryDTO.class);
			if (entry.getProduct().getCode().equals(FunctionalTestConstants.CODE_PRODUCT3))
			{
				assertEquals("Quantity should be 2 in " + entry.getProduct().getCode(), Long.valueOf(2), entry.getQuantity());
			}
			else
			{
				assertEquals("Quantity should be 1 in " + entry.getProduct().getCode(), Long.valueOf(1), entry.getQuantity());
			}
		}

	}

	private void setPaymentAndDeliveryAddress()
	{
		// set payment and delivery address to already existing user's address
		final AddressDTO address = new AddressDTO();
		address.setPk(address1PK);

		final CartDTO cart = new CartDTO();
		cart.setPaymentAddress(address);
		cart.setDeliveryAddress(address);

		final ClientResponse putResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).header("Content-Type",
				MediaType.APPLICATION_XML).entity(cart).put(ClientResponse.class);
		assertOk(putResult, false);

		final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		final CartDTO cart2 = cartResult.getEntity(CartDTO.class);
		assertEquals("Carts should have the same pk", cartCode, cart2.getCode());
		assertNotNull("Delivery address should not be empty", cart2.getDeliveryAddress());
		assertNotNull("Payment address should not be empty", cart2.getPaymentAddress());
	}

	private void setPaymentAndDeliveryMode()
	{
		// get delivery mode to set
		final ClientResponse deliveryModeResult = webResource.path(
				FunctionalTestConstants.URI_DELIVERYMODE + "/" + FunctionalTestConstants.UID_DELIVERYMODE1).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		deliveryModeResult.bufferEntity();
		assertOk(deliveryModeResult, false);

		final DeliveryModeDTO deliveryModeDTO = deliveryModeResult.getEntity(DeliveryModeDTO.class);
		assertNotNull("No deliveryMode within body at response: " + deliveryModeResult, deliveryModeDTO);
		assertNotNull("No deliveryMode code at response: " + deliveryModeResult, deliveryModeDTO.getCode());

		// get payment mode to set
		final ClientResponse paymentModeResult = webResource.path(
				FunctionalTestConstants.URI_PAYMENTMODE + "/" + FunctionalTestConstants.UID_PAYMENTMODE1).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		paymentModeResult.bufferEntity();
		assertOk(paymentModeResult, false);

		final PaymentModeDTO paymentModeDTO = paymentModeResult.getEntity(PaymentModeDTO.class);
		assertNotNull("No paymentMode within body at response: " + paymentModeResult, paymentModeDTO);
		assertNotNull("No paymentMode code at response: " + paymentModeResult, paymentModeDTO.getCode());

		final CartDTO cart = new CartDTO();
		cart.setCode(cartCode);
		cart.setPaymentMode(paymentModeDTO);
		cart.setDeliveryMode(deliveryModeDTO);

		final ClientResponse putResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).header("Content-Type",
				MediaType.APPLICATION_XML).entity(cart).put(ClientResponse.class);
		assertOk(putResult, false);
		final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		final CartDTO cart2 = cartResult.getEntity(CartDTO.class);
		assertEquals("Carts should have the same code", cart.getCode(), cart2.getCode());
		assertNotNull("Delivery mode should not be empty", cart2.getDeliveryMode());
		assertNotNull("Payment mode should not be empty", cart2.getPaymentMode());
	}


	private void createPaymentInfo()
	{
		//get user
		ClientResponse result = webResource.path(FunctionalTestConstants.URI_CUSTOMERS + "/" + FunctionalTestConstants.UID_USER1)
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, "Basic " + auth).accept(MediaType.APPLICATION_XML).get(
						ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		CustomerDTO customer = result.getEntity(CustomerDTO.class);

		final CreditCardPaymentInfoDTO creditCardPaymentInfoDTO = new CreditCardPaymentInfoDTO();
		creditCardPaymentInfoDTO.setCode("newCreditCard");
		creditCardPaymentInfoDTO.setDuplicate(Boolean.FALSE);
		creditCardPaymentInfoDTO.setType("visa");
		creditCardPaymentInfoDTO.setValidFromMonth("5");
		creditCardPaymentInfoDTO.setValidToMonth("6");
		creditCardPaymentInfoDTO.setValidFromYear("1999");
		creditCardPaymentInfoDTO.setValidToYear("2002");
		creditCardPaymentInfoDTO.setCcOwner("Jurgen");
		creditCardPaymentInfoDTO.setNumber("4111 1111 1111 1111");
		creditCardPaymentInfoDTO.setUser(customer);

		//create credit card paymentinfo from root resource
		result = webResource.path(FunctionalTestConstants.URI_PAYMENTINFO_CREDITCARD).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				"Basic " + auth).accept(MediaType.APPLICATION_XML).entity(creditCardPaymentInfoDTO).post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);
		//achieve generated PK
		CreditCardPaymentInfoDTO creditCardPaymentInfo2 = result.getEntity(CreditCardPaymentInfoDTO.class);
		final String creditCardPk = creditCardPaymentInfo2.getPk().toString();
		//get user
		result = webResource.path(FunctionalTestConstants.URI_CUSTOMERS + "/" + FunctionalTestConstants.UID_USER1).cookie(
				tenantCookie).header(HEADER_AUTH_KEY, "Basic " + auth).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		customer = result.getEntity(CustomerDTO.class);
		//information about credit card payment info code from user
		final String creditCardPkFromUser = customer.getPaymentInfos().iterator().next().getPk().toString();

		result = webResource.path(FunctionalTestConstants.URI_PAYMENTINFO_CREDITCARD + "/" + creditCardPaymentInfo2.getPk())
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, "Basic " + auth).accept(MediaType.APPLICATION_XML).get(
						ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		//information about credit card payment info code from root creadit card resource
		creditCardPaymentInfo2 = result.getEntity(CreditCardPaymentInfoDTO.class);

		//comparison between payment info gained from two different contexts
		assertEquals("Incorrect value in credit card paymentinfo(originates from user )", creditCardPk, creditCardPkFromUser);
		assertEquals("Incorrect value in credit card paymentinfo(originates from root resource)", creditCardPk,
				creditCardPaymentInfo2.getPk().toString());
	}

	@SuppressWarnings("null")
	private void previewOfCart()
	{
		//get all carts
		final ClientResponse cartsResult = webResource.path(FunctionalTestConstants.URI_CARTS).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartsResult.bufferEntity();
		final CartsDTO carts = cartsResult.getEntity(CartsDTO.class);

		// if no carts:
		if (carts.getCarts().isEmpty())
		{
			fail("No carts defined");
		}
		CartDTO cart = null;

		for (CartDTO tempCart : carts.getCarts())
		{
			final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + tempCart.getCode()).header(
					HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			cartResult.bufferEntity();
			tempCart = cartResult.getEntity(CartDTO.class);
			if ("anonymousCart".equals(tempCart.getCode()))
			{
				cart = tempCart;
				break;
			}
		}
		assertNotNull("Expected cart not found", cart);
		assertNotNull("Expected cart not found", cart.getCode()); //NOPMD

		cartCode = cart.getCode();

		// get customer cart
		final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		cart = cartResult.getEntity(CartDTO.class);

		assertEquals("Three entries should be present in a cart", 3, cart.getEntries().size());

		for (final CartEntryDTO cartEntry : cart.getEntries())
		{
			//get cart entry
			final ClientResponse entryResult = webResource.path(cartEntry.getUri().replace(webResource.getURI().toString(), ""))
					.header(HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(
							ClientResponse.class);
			entryResult.bufferEntity();
			final CartEntryDTO entry = entryResult.getEntity(CartEntryDTO.class);

			//for testProduct3 quantity should = 2 
			if (entry.getProduct().getCode().equals(FunctionalTestConstants.CODE_PRODUCT3))
			{
				assertEquals("Quantity should be 2 in " + entry.getProduct().getCode(), Long.valueOf(2), entry.getQuantity());
			}
			else
			{
				assertEquals("Quantity should be 1 in " + entry.getProduct().getCode(), Long.valueOf(1), entry.getQuantity());
			}
		}
	}

	@SuppressWarnings("null")
	private void orderCart()
	{
		//get all carts
		final ClientResponse cartsResult = webResource.path(FunctionalTestConstants.URI_CARTS).header(HEADER_AUTH_KEY,
				"Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartsResult.bufferEntity();
		final CartsDTO carts = cartsResult.getEntity(CartsDTO.class);

		// if no carts:
		if (carts.getCarts().isEmpty())
		{
			fail("No carts defined");
		}
		CartDTO cart = null;

		for (CartDTO tempCart : carts.getCarts())
		{
			final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + tempCart.getCode()).header(
					HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			cartResult.bufferEntity();
			tempCart = cartResult.getEntity(CartDTO.class);
			if ("anonymousCart".equals(tempCart.getCode()))
			{
				cart = tempCart;
				break;
			}
		}
		assertNotNull("Expected cart not found", cart);
		assertNotNull("Expected cart not found", cart.getCode()); //NOPMD

		cartCode = cart.getCode();

		// get anonymous cart
		final ClientResponse cartResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResult.bufferEntity();
		cart = cartResult.getEntity(CartDTO.class);

		// try to post (order) cart
		final ClientResponse putResult = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).queryParam("cmd",
				"PlaceOrderCommand").header(HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML)
				.header("Content-Type", MediaType.APPLICATION_XML).entity(cart).post(ClientResponse.class);

		// assert order was CREATED
		assertCreated(putResult, false);

		//check if cart was really ordered
		final ClientResponse cartResultAfter = webResource.path(FunctionalTestConstants.URI_CARTS + "/" + cartCode).header(
				HEADER_AUTH_KEY, "Basic " + auth).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		cartResultAfter.bufferEntity();

		//deleting order in order to prepare the removal of the user in cleanUpData method
		final OrderDTO order = putResult.getEntity(OrderDTO.class);
		final OrderModel orderModel = modelService.get(PK.parse(order.getPk().toString()));
		modelService.remove(orderModel);

		try
		{
			cartResultAfter.getEntity(CartDTO.class);
			fail("After cart order, cart should not be present any more!");
		}
		catch (final ClientHandlerException che)
		{
			//the cartDTO was not found! Expected behavior.
		}

	}

	/**
	 * Cleans up after class. Remember to manually remove whatever was created using webservices - those items will not
	 * be removed unless they are parts of other items that will (like cart entries)
	 * 
	 * @throws ConsistencyCheckException
	 * @throws JaloBusinessException
	 */
	@After
	public void cleanUpData() throws ConsistencyCheckException, JaloBusinessException
	{
		// remove user created via webservice
		try
		{
			final UserModel user1 = userService.getUserForUID(FunctionalTestConstants.UID_USER1);
			final Collection<OrderModel> orders = user1.getOrders();
			if (orders != null)
			{
				for (final OrderModel order : orders)
				{
					for (final AbstractOrderEntryModel entry : order.getEntries())
					{
						modelService.remove(entry);
					}
					modelService.remove(order);
				}
			}
		}
		catch (final JaloItemNotFoundException e) //NOPMD
		{
			// ignore, we don't need to remove anything
		}
		// no need to remove cart entries, they will be removed with cart (cart was created via impex during test setup)
		// no need to remove products, they will be removed automatically (created via impex during test setup)
	}
}
