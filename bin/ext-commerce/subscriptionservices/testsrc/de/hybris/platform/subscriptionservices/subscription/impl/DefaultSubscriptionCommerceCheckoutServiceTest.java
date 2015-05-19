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
package de.hybris.platform.subscriptionservices.subscription.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultSubscriptionCommerceCheckoutService}
 */
@UnitTest
public class DefaultSubscriptionCommerceCheckoutServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultSubscriptionCommerceCheckoutService subscriptionCommerceCheckoutService;
	private CartModel masterCart;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		subscriptionCommerceCheckoutService = new DefaultSubscriptionCommerceCheckoutService();
		masterCart = mock(CartModel.class);
	}

	@Test
	public void testPlaceOrderWhenMasterCartIsNull() throws InvalidCartException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCartModel can not be null");

		subscriptionCommerceCheckoutService.placeOrder((CartModel) null);
	}

	@Test
	public void testPlaceOrderWhenMasterCartIsCalculatedIsNull() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(null);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Customer model cannot be null");

		subscriptionCommerceCheckoutService.placeOrder(masterCart);
	}

	@Test
	public void testPlaceOrderWhenMasterCartIsNotCalculated() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(Boolean.FALSE);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("must be calculated");

		subscriptionCommerceCheckoutService.placeOrder(masterCart);
	}

	@Test
	public void testPlaceOrderWhenCustomerIsNull() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(Boolean.TRUE);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Customer model cannot be null");

		subscriptionCommerceCheckoutService.placeOrder(masterCart);
	}

}