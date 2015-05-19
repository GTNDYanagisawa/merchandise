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
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultBundleCommerceCheckoutService}
 */
@UnitTest
public class DefaultBundleCommerceCheckoutServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultBundleCommerceCheckoutService bundleCommerceCheckoutService;
	private CartModel masterCart;

	@Mock
	BundleCommerceCartService bundleCommerceCartService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bundleCommerceCheckoutService = new DefaultBundleCommerceCheckoutService();
		bundleCommerceCheckoutService.setBundleCommerceCartService(bundleCommerceCartService);
		masterCart = mock(CartModel.class);
	}

	@Test
	public void testPlaceOrderWhenMasterCartIsNull() throws InvalidCartException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCartModel can not be null");

		bundleCommerceCheckoutService.placeOrder((CartModel) null);
	}

	@Test
	public void testPlaceOrderWhenMasterCartIsCalculatedIsNull() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(null);
		given(bundleCommerceCartService.getFirstInvalidComponentInCart(masterCart)).willReturn(null);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Customer model cannot be null");

		bundleCommerceCheckoutService.placeOrder(masterCart);
	}

	@Test
	public void testPlaceOrderWhenMasterCartIsNotCalculated() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(Boolean.FALSE);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("must be calculated");

		bundleCommerceCheckoutService.placeOrder(masterCart);
	}

	@Test
	public void testPlaceOrderWhenCustomerIsNull() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(Boolean.TRUE);
		given(bundleCommerceCartService.getFirstInvalidComponentInCart(masterCart)).willReturn(null);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Customer model cannot be null");

		bundleCommerceCheckoutService.placeOrder(masterCart);
	}

}