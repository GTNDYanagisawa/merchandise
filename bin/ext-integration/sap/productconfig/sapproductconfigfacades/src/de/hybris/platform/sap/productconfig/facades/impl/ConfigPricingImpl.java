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

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import org.springframework.beans.factory.annotation.Required;


public class ConfigPricingImpl implements ConfigPricing
{
	private PriceDataFactory priceDataFactory;

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	@Override
	public PricingData getPricingData(final ConfigModel model)
	{
		final PricingData pricingData = new PricingData();

		final PriceData basePrice;
		final PriceData selectedOptions;
		final PriceData currentTotal;

		final PriceModel basePriceModel = model.getBasePrice();
		basePrice = getPriceData(basePriceModel);

		final PriceModel selectedOptionsPriceModel = model.getSelectedOptionsPrice();
		selectedOptions = getPriceData(selectedOptionsPriceModel);

		final PriceModel currentTotalPriceModel = model.getCurrentTotalPrice();
		currentTotal = getPriceData(currentTotalPriceModel);

		pricingData.setBasePrice(basePrice);
		pricingData.setSelectedOptions(selectedOptions);
		pricingData.setCurrentTotal(currentTotal);

		return pricingData;
	}

	@Override
	public PriceData getPriceData(final PriceModel priceModel)
	{
		final PriceData priceData;
		if (priceModel == null || priceModel == PriceModel.NO_PRICE || priceModel.getPriceValue() == null
				|| priceModel.getCurrency() == null || priceModel.getCurrency().isEmpty())
		{
			priceData = ConfigPricing.NO_PRICE;
		}
		else
		{
			priceData = priceDataFactory.create(PriceDataType.BUY, priceModel.getPriceValue(), priceModel.getCurrency());
		}
		return priceData;
	}

}
