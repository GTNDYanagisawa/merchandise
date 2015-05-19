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
package de.hybris.platform.commercefacades.storesession.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CurrencyPopulatorTest
{
	private static final String ISOCODE = "EN";

	private final AbstractPopulatingConverter<CurrencyModel, CurrencyData> currencyConverter = new ConverterFactory<CurrencyModel, CurrencyData, CurrencyPopulator>()
			.create(CurrencyData.class, new CurrencyPopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvert()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);

		given(currencyModel.getName()).willReturn(ISOCODE);
		given(currencyModel.getIsocode()).willReturn(ISOCODE);
		given(currencyModel.getActive()).willReturn(Boolean.TRUE);
		given(currencyModel.getSymbol()).willReturn(ISOCODE);

		final CurrencyData currencyData = currencyConverter.convert(currencyModel);

		Assert.assertEquals(currencyModel.getIsocode(), currencyData.getIsocode());
		Assert.assertEquals(currencyModel.getActive(), Boolean.valueOf(currencyData.isActive()));
		Assert.assertEquals(currencyModel.getSymbol(), currencyData.getSymbol());
		Assert.assertEquals(currencyModel.getName(), currencyData.getName());
	}
}
