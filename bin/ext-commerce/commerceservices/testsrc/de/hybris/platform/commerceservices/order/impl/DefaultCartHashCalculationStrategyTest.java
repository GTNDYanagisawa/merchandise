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
package de.hybris.platform.commerceservices.order.impl;


import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceOrderParameter;
import de.hybris.platform.core.model.order.CartModel;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class DefaultCartHashCalculationStrategyTest
{
	private final DefaultCommerceCartHashCalculationStrategy defaultCartHashCalculationStrategy = new DefaultCommerceCartHashCalculationStrategy();
	private CartModel cartModel;

	@Before
	public void initCart()
	{
		cartModel = Mockito.mock(CartModel.class);
	}

	@Test
	public void shouldCalculateSameHash()
	{
		final CommerceOrderParameter parameter = new CommerceOrderParameter();
		parameter.setOrder(cartModel);

		final String hash1 = defaultCartHashCalculationStrategy.buildHashForAbstractOrder(parameter);
		final String hash2 = defaultCartHashCalculationStrategy.buildHashForAbstractOrder(parameter);
		Assert.assertThat(hash1, CoreMatchers.equalTo(hash2));
	}

	@Test
	public void shouldCalculateDifferentHashesWithCartUpdate()
	{
		final CommerceOrderParameter parameter = new CommerceOrderParameter();
		parameter.setOrder(cartModel);
		final String hash1 = defaultCartHashCalculationStrategy.buildHashForAbstractOrder(parameter);
		given(cartModel.getModifiedtime()).willReturn(new Date());

		final String hash2 = defaultCartHashCalculationStrategy.buildHashForAbstractOrder(parameter);
		Assert.assertThat(hash1, CoreMatchers.not(CoreMatchers.equalTo(hash2)));
	}

}
