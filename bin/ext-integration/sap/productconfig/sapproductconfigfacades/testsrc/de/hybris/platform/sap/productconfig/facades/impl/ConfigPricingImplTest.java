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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ConfigPricingImplTest
{
	private PriceDataFactory priceDataFactory;

	private ConfigPricingImpl pricing;


	@Before
	public void setup()
	{
		priceDataFactory = new DummyPriceDataFactory();

		pricing = new ConfigPricingImpl();
		pricing.setPriceDataFactory(priceDataFactory);
	}

	@Test
	public void testConfigurationPrices_empty() throws Exception
	{
		final ConfigModel model = new ConfigModelImpl();

		final PriceModel basePrice = new PriceModelImpl();
		basePrice.setPriceValue(null);
		basePrice.setCurrency(null);
		model.setBasePrice(basePrice);

		final PriceModel selectedOptionsPrice = new PriceModelImpl();
		selectedOptionsPrice.setPriceValue(new BigDecimal(100));
		selectedOptionsPrice.setCurrency(null);
		model.setSelectedOptionsPrice(selectedOptionsPrice);

		final PriceModel currentTotalPrice = new PriceModelImpl();
		currentTotalPrice.setPriceValue(new BigDecimal(1100));
		currentTotalPrice.setCurrency(null);
		model.setCurrentTotalPrice(currentTotalPrice);

		final PricingData pricingData = pricing.getPricingData(model);

		assertSame(ConfigPricing.NO_PRICE, pricingData.getBasePrice());
		assertSame(ConfigPricing.NO_PRICE, pricingData.getSelectedOptions());
		assertSame(ConfigPricing.NO_PRICE, pricingData.getCurrentTotal());
	}


	@Test
	public void testConfigurationPrices() throws Exception
	{
		final ConfigModel model = new ConfigModelImpl();

		final PriceModel basePrice = new PriceModelImpl();
		basePrice.setPriceValue(new BigDecimal(1000));
		basePrice.setCurrency("EUR");
		model.setBasePrice(basePrice);

		final PriceModel selectedOptionsPrice = new PriceModelImpl();
		selectedOptionsPrice.setPriceValue(new BigDecimal(100));
		selectedOptionsPrice.setCurrency("EUR");
		model.setSelectedOptionsPrice(selectedOptionsPrice);

		final PriceModel currentTotalPrice = new PriceModelImpl();
		currentTotalPrice.setPriceValue(new BigDecimal(1100));
		currentTotalPrice.setCurrency("EUR");
		model.setCurrentTotalPrice(currentTotalPrice);

		final PricingData pricingData = pricing.getPricingData(model);

		assertNotNull(pricingData.getBasePrice());
		assertNotNull(pricingData.getSelectedOptions());
		assertNotNull(pricingData.getCurrentTotal());

		assertEquals(model.getBasePrice().getPriceValue(), pricingData.getBasePrice().getValue());
		assertEquals(model.getSelectedOptionsPrice().getPriceValue(), pricingData.getSelectedOptions().getValue());
		assertEquals(model.getCurrentTotalPrice().getPriceValue(), pricingData.getCurrentTotal().getValue());
	}

	public static class DummyPriceDataFactory implements PriceDataFactory
	{

		@Override
		public PriceData create(final PriceDataType priceType, final BigDecimal value, final String currencyIso)
		{
			final PriceData priceData = new PriceData();

			priceData.setCurrencyIso(currencyIso);
			priceData.setValue(value);
			priceData.setPriceType(priceType);
			priceData.setFormattedValue(value.toString() + " " + currencyIso);

			return priceData;
		}

		@Override
		public PriceData create(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currency)
		{
			throw new IllegalArgumentException("Not used");
		}
	}
}
