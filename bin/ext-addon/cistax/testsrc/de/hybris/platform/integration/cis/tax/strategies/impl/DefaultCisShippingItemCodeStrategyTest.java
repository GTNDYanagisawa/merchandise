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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;



@UnitTest
public class DefaultCisShippingItemCodeStrategyTest
{
	private DefaultCisShippingItemCodeStrategy defaultCisShippingItemCodeStrategy;

	@Before
	public void setUp()
	{
		defaultCisShippingItemCodeStrategy = new DefaultCisShippingItemCodeStrategy();
	}

	@Test
	public void shouldGetShippingItemCodeWithOneEntry()
	{
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);
		final AbstractOrderEntryModel abstractEntry = Mockito.mock(AbstractOrderEntryModel.class);
		given(abstractEntry.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(abstractOrder.getEntries()).willReturn(Collections.singletonList(abstractEntry));
		final Integer shippingItem = defaultCisShippingItemCodeStrategy.getShippingItemCode(abstractOrder);
		Assert.assertEquals(Integer.valueOf(1), shippingItem);
	}


	@Test
	public void shouldGetShippingItemCodeForOrderWithNoEntries()
	{
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);
		given(abstractOrder.getEntries()).willReturn(Collections.<AbstractOrderEntryModel> emptyList());
		final Integer shippingItem = defaultCisShippingItemCodeStrategy.getShippingItemCode(abstractOrder);
		Assert.assertEquals(Integer.valueOf(0), shippingItem);
	}
}
