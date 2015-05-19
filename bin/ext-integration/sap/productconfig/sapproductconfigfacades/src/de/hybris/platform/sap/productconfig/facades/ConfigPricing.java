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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.impl.NoConfigPrice;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;


/**
 * Factory for pricing data for product configuration.
 */
public interface ConfigPricing
{
	/**
	 * Value-Object to model the case, when no price information is available
	 */
	public static final PriceData NO_PRICE = new NoConfigPrice();

	/**
	 * Factory method to extract pricing data from the given product configuration model
	 * 
	 * @param model
	 *           product configuration model
	 * @return pricing data
	 */
	public PricingData getPricingData(ConfigModel model);

	/**
	 * @param priceModel
	 * @return price DTO
	 */
	public PriceData getPriceData(PriceModel priceModel);
}
