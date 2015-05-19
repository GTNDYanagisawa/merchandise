/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.price;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;

import java.util.Date;
import java.util.List;


/**
 * This interface describes a price including various price conditions.
 */
public interface Price
{
	/**
	 * Checks if this price is net based.
	 * 
	 * @return true if net based
	 */
	boolean isNet();

	/**
	 * Checks if this price is gross based.
	 * 
	 * @return true if gross based
	 */
	boolean isGross();

	/**
	 * The date when this price becomes valid.
	 * 
	 * @return staring date
	 */
	Date getValidFromDate();

	/**
	 * The date when this price is no longer valid.
	 * 
	 * @return end date
	 */
	Date getValidToDate();

	/**
	 * The price itself.
	 * 
	 * @return price value
	 */
	double getPriceValue();

	/**
	 * Checks if a valid price value is available. In general this returns true when the price is greater than zero.
	 * 
	 * @return true if available
	 */
	boolean isAvailable();

	/**
	 * The amount/quantity which is bound to this price.
	 * 
	 * @return amount value
	 */
	int getAmount();

	/**
	 * The unit which is bound to this price.
	 * 
	 * @return unit
	 */
	UnitModel getUnit();

	CurrencyModel getCurrency();

	/**
	 * An old pricing; for marketing issues or similar.
	 * 
	 * @return old price {@link Price}
	 */
	Price getOldPricing();

	/**
	 * List of all taxes which have an impact on this price.
	 * 
	 * @return list of {@link PriceLeverage}
	 */
	List<PriceLeverage> getTaxes();

	/**
	 * List of all discounts (voucher etc.) which have an impact on this price.
	 * 
	 * @return list of {@link PriceLeverage}
	 */
	List<PriceLeverage> getDiscounts();

	boolean isEmptyPrice();
}
