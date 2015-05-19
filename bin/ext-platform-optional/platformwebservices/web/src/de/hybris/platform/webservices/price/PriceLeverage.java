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


/**
 * This interface describes the leveraged price by various options. (Vouchers, Taxes etc.)
 */
public interface PriceLeverage
{
	int TYPE_UNKNOWN = -1;
	int TYPE_TAX = 0;
	int TYPE_DISCOUNT = 0;

	boolean ABSOLUTE = true;
	boolean RELATIVE = false;

	/**
	 * The type of the price leverage.
	 * 
	 * @return type
	 */
	int getType();

	/**
	 * The id of the price leverage.
	 * 
	 * @return id
	 */
	String getId();


	/**
	 * The name for the price leverage.
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * Checks if the value is used absolutely.
	 * 
	 * @return true if it is absolute
	 */
	boolean isAbsolute();

	/**
	 * Checks if the value is used relatively (as percentage).
	 * 
	 * @return true if it is relative
	 */
	boolean isRelative();

	/**
	 * The value which is used as leverage. Either absolute (as it is) or relatively (as percentage).
	 * 
	 * @return value
	 */
	double getValue();

	/**
	 * The applied price value of the leverage.
	 * 
	 * @return applied price
	 */
	double getAppliedValue();

	/**
	 * The applied currency of the leverage.
	 * 
	 * @return applied currency
	 */
	CurrencyModel getAppliedCurrency();

}