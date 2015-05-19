/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.orderscheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class OrderUtilityTest extends ServicelayerTransactionalTest
{
	@Resource
	private OrderUtility orderUtility;
	@Resource
	private ModelService modelService;
	@Resource
	private CartService cartService;
	@Resource
	private UserService userService;
	@Resource
	private ProductService productService;
	@Resource
	private OrderService orderService;


	private AddressModel deliveryAddress;
	private DebitPaymentInfoModel paymentInfo;
	private CartModel cart;

	@Before
	public void initTest() throws Exception //NOPMD
	{
		createCoreData();
		createDefaultCatalog();

		cart = cartService.getSessionCart();

		deliveryAddress = modelService.create(AddressModel.class);
		deliveryAddress.setFirstname("Krzysztof");
		deliveryAddress.setLastname("Kwiatosz");
		deliveryAddress.setTown("Katowice");
		deliveryAddress.setOwner(userService.getCurrentUser());

		modelService.save(deliveryAddress);

		paymentInfo = modelService.create(DebitPaymentInfoModel.class);
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("Bank");
		paymentInfo.setUser(userService.getCurrentUser());
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("I");

		cartService.addToCart(cart, productService.getProduct("testProduct1"), 1, null);
		cartService.addToCart(cart, productService.getProduct("testProduct2"), 1, null);

	}

	@Test
	public void testDoNotCreateOrderFromEmptyCart() throws InvalidCartException
	{
		cartService.removeSessionCart();
		cart = cartService.getSessionCart();

		assertNull("No order should be placed", orderUtility.createOrderFromCart(cart, deliveryAddress, null, paymentInfo));
	}

	@Test
	public void testCreateOrderFromCart() throws InvalidCartException
	{
		final OrderModel order = orderUtility.createOrderFromCart(cart, deliveryAddress, deliveryAddress, paymentInfo);
		assertNotNull("No order should be placed", order);
		assertEquals("There should be 2 order entries", order.getEntries().size(), 2);

	}

	@Test
	public void testCreateOrderFromOrderTemplate() throws InvalidCartException
	{
		final OrderModel orderTemplate = orderService.placeOrder(cart, deliveryAddress, deliveryAddress, paymentInfo);

		final OrderModel order = orderUtility.createOrderFromOrderTemplate(orderTemplate);

		assertNotNull("No order should be placed", order);
		assertEquals("There should be 2 order entries", order.getEntries().size(), 2);
	}



}
