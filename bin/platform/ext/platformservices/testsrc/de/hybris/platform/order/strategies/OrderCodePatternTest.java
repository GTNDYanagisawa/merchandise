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
package de.hybris.platform.order.strategies;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class OrderCodePatternTest extends ServicelayerBaseTest
{

	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private ModelService modelService;

	/**
	 * Retrieves the session cart with service layer, calls the placeOrder for the cart, and get the first order code.
	 * Retrieves another cart within jalo session, converts it the model, also calls the placeOrder for it, and get the
	 * second order code. Compare the pattern of these two codes, and check they have the same length and pattern. For
	 * the pattern, the test is to check if they begin with the same code except the last two characters.
	 */
	@Test
	public void testOrderCode() throws Exception
	{
		//order code with normal service layer creation
		final CartModel cart1 = cartService.getSessionCart();
		final OrderModel order1 = orderService.placeOrder(cart1, null, null, null);
		final String patternCode = order1.getCode();

		//cart from jalo manager
		final Cart cart = jaloSession.getCart();
		final CartModel cartModel = modelService.get(cart);
		System.out.println(cart.getCode());
		final OrderModel order2 = orderService.placeOrder(cartModel, null, null, null);
		final String jaloOrderCode = order2.getCode();

		assertEquals("both codes must have the same length", patternCode.length(), jaloOrderCode.length());
		int codeLength = patternCode.length();
		if (codeLength > 15)
		{
			codeLength = codeLength - 5;
		}
		else
		{
			codeLength = codeLength - 2;
		}
		assertEquals("both codes must have the same pattern", patternCode.substring(0, codeLength),
				jaloOrderCode.substring(0, codeLength));
	}

}
