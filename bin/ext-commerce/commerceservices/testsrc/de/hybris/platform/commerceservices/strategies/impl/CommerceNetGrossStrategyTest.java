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
package de.hybris.platform.commerceservices.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.NetGrossStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * 
 */
@UnitTest
public class CommerceNetGrossStrategyTest
{
	private CommerceNetGrossStrategy strategy;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CartService cartService;
	@Mock
	private CartModel cart;
	@Mock
	private BaseStoreModel store;
	@Mock
	private NetGrossStrategy defaultStrategy;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy = new CommerceNetGrossStrategy()
		{
			@Override
			protected CartService getCartService()
			{
				return cartService;
			}
		};
		strategy.setBaseStoreService(baseStoreService);
		strategy.setDefaultNetGrossStrategy(defaultStrategy);
		given(cartService.getSessionCart()).willReturn(cart);
	}

	@Test
	public void testBaseStore()
	{
		given(baseStoreService.getCurrentBaseStore()).willReturn(store);
		given(Boolean.valueOf(store.isNet())).willReturn(Boolean.FALSE);
		given(cart.getNet()).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(defaultStrategy.isNet())).willReturn(Boolean.TRUE);
		Assert.assertEquals(false, strategy.isNet());
	}

	@Test
	public void testCart()
	{
		given(baseStoreService.getCurrentBaseStore()).willReturn(null);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cart.getNet()).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(defaultStrategy.isNet())).willReturn(Boolean.TRUE);
		Assert.assertEquals(false, strategy.isNet());
	}

	@Test
	public void testDefault()
	{
		given(baseStoreService.getCurrentBaseStore()).willReturn(null);
		given(cart.getNet()).willReturn(null);
		given(Boolean.valueOf(defaultStrategy.isNet())).willReturn(Boolean.FALSE);
		Assert.assertEquals(false, strategy.isNet());
	}
}
