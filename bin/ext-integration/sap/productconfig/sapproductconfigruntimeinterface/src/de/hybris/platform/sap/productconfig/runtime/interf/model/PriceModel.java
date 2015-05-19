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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ZeroPriceModelImpl;

import java.math.BigDecimal;


/**
 * Represents the price model including currency.
 */
public interface PriceModel extends BaseModel
{

	/**
	 * Value-Object to model the case, when no price information is available
	 */
	public static final PriceModel NO_PRICE = new ZeroPriceModelImpl();

	/**
	 * @param currency
	 *           price currency
	 */
	public void setCurrency(String currency);

	/**
	 * @return price currency
	 */
	public String getCurrency();

	/**
	 * @return price value
	 */
	public BigDecimal getPriceValue();

	/**
	 * @param priceValue
	 *           price value
	 */
	public void setPriceValue(BigDecimal priceValue);

	/**
	 * @return cloned <code>PriceModel</code>
	 */
	@Override
	public PriceModel clone();

}
