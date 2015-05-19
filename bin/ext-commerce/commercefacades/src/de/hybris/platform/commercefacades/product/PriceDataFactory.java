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
package de.hybris.platform.commercefacades.product;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.math.BigDecimal;


/**
 * Interface for generating PriceData.
 */
public interface PriceDataFactory
{
	/**
	 * Creates a PriceData object with a formatted currency string based on the price type and currency ISO code.
	 * 
	 * @param priceType
	 *           The price type
	 * @param value
	 *           The price amount
	 * @param currencyIso
	 *           The currency ISO code
	 * 
	 * @return the price data
	 */
	PriceData create(PriceDataType priceType, BigDecimal value, String currencyIso);

	/**
	 * Creates a PriceData object with a formatted currency string based on the price type and currency.
	 *
	 * @param priceType
	 *           The price type
	 * @param value
	 *           The price amount
	 * @param currency
	 *           The currency
	 *
	 * @return the price data
	 */
	PriceData create(PriceDataType priceType, BigDecimal value, CurrencyModel currency);
}
