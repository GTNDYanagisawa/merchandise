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
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;

import org.cxml.ItemIn;
import org.cxml.PunchOutOrderMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutOrderMessagePopulatorTest
{

	private static final int ORDER_ENTRY_COUNT = 1;
	private static final String CURRENCY_ISO = "USD";
	private static final Double TOTAL_PRICE = new Double(1.2D);
	private static final String TOTAL_PRICE_STRING = "1.2";

	@InjectMocks
	private final DefaultPunchOutOrderMessagePopulator cartPopulator = new DefaultPunchOutOrderMessagePopulator();

	@Mock
	private Converter<AbstractOrderEntryModel, ItemIn> itemInConverter;

	@Mock
	private AbstractOrderEntryModel orderEntryModel;

	private CartModel cartModel;
	private PunchOutOrderMessage punchOutOrder;

	@Mock
	private CurrencyModel currency;

	@Before
	public void setUp()
	{
		ItemIn itemIn = null;

		punchOutOrder = new PunchOutOrderMessage();
		cartModel = mock(CartModel.class);
		itemIn = new ItemIn();

		given(cartModel.getCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn(CURRENCY_ISO);
		given(cartModel.getTotalPrice()).willReturn(TOTAL_PRICE);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(orderEntryModel));

		given(itemInConverter.convert(orderEntryModel)).willReturn(itemIn);
	}

	@Test
	public void normalPopulation()
	{

		cartPopulator.populate(cartModel, punchOutOrder);

		final String currency2Check = punchOutOrder.getPunchOutOrderMessageHeader().getTotal().getMoney().getCurrency();
		assertNotNull("Currency must be set", currency2Check);
		assertEquals(cartModel.getCurrency().getIsocode(), currency2Check);
		assertEquals(TOTAL_PRICE_STRING, punchOutOrder.getPunchOutOrderMessageHeader().getTotal().getMoney().getvalue());

		assertEquals(ORDER_ENTRY_COUNT, punchOutOrder.getItemIn().size());

	}




}
