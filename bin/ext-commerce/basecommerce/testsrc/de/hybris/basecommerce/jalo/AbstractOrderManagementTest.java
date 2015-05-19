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
package de.hybris.basecommerce.jalo;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;


/**
 * 
 */
@Ignore
abstract public class AbstractOrderManagementTest extends ServicelayerTest
{
	protected OrderModel order;

	@Resource
	protected ProductService productService;
	@Resource
	protected CartService cartService;
	@Resource
	protected UserService userService;
	@Resource
	protected OrderService orderService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();
		order = placeTestOrder();
	}

	protected OrderModel placeTestOrder() throws InvalidCartException
	{
		try
		{
			final CartModel cart = cartService.getSessionCart();
			final UserModel user = userService.getCurrentUser();
			cartService.addToCart(cart, productService.getProduct("testProduct1"), 1, null);
			cartService.addToCart(cart, productService.getProduct("testProduct2"), 2, null);
			cartService.addToCart(cart, productService.getProduct("testProduct3"), 3, null);

			final AddressModel deliveryAddress = new AddressModel();
			deliveryAddress.setOwner(user);
			deliveryAddress.setFirstname("Der");
			deliveryAddress.setLastname("Buck");
			deliveryAddress.setTown("Muenchen");

			final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
			paymentInfo.setOwner(cart);
			paymentInfo.setBank("MeineBank");
			paymentInfo.setUser(user);
			paymentInfo.setAccountNumber("34434");
			paymentInfo.setBankIDNumber("1111112");
			paymentInfo.setBaOwner("Ich");

			return orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		}
		catch (final InvalidCartException e)
		{
			getLogger().error("Error placing test order: " + e.getMessage(), e);
			throw e;
		}
	}

	abstract public Logger getLogger();

}
