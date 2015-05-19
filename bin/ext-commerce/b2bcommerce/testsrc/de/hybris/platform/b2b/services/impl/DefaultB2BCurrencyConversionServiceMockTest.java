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
package de.hybris.platform.b2b.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class DefaultB2BCurrencyConversionServiceMockTest extends HybrisMokitoTest
{
	DefaultB2BCurrencyConversionService defaultB2BConversionService;

	@Before
	public void setUp() throws Exception
	{
		defaultB2BConversionService = new DefaultB2BCurrencyConversionService();
	}

	@Test
	public void testConvertAmount() throws Exception
	{
		final Double amount = new Double(9);
		final CurrencyModel mockSourceCurrencyModel = mock(CurrencyModel.class);
		final CurrencyModel mockTargetCurrencyModel = mock(CurrencyModel.class);
		when(mockSourceCurrencyModel.getConversion()).thenReturn(amount);
		when(mockTargetCurrencyModel.getConversion()).thenReturn(amount);
		assertThat(defaultB2BConversionService.convertAmount(amount, mockSourceCurrencyModel, mockTargetCurrencyModel),
				equalTo(amount));
	}

	@Test
	public void testFormatCurrencyAmount() throws Exception
	{
		final CurrencyModel currency = mock(CurrencyModel.class);
		when(currency.getIsocode()).thenReturn("USD");
		final double amount = 93.21;
		final String formattedAmount = defaultB2BConversionService.formatCurrencyAmount(Locale.ENGLISH, currency, amount);
		assertThat(formattedAmount, equalTo("USD93.21"));

	}
}
