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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;

import java.math.BigDecimal;


public class NoConfigPrice extends PriceData
{

	@Override
	public void setCurrencyIso(final String currencyIso)
	{
		throw new IllegalArgumentException("NoConfigPrice is immutable");
	}

	@Override
	public String getCurrencyIso()
	{
		return "";
	}

	@Override
	public void setPriceType(final PriceDataType priceType)
	{
		throw new IllegalArgumentException("NoConfigPrice is immutable");
	}

	@Override
	public PriceDataType getPriceType()
	{
		return PriceDataType.BUY;
	}

	@Override
	public void setValue(final BigDecimal value)
	{
		throw new IllegalArgumentException("NoConfigPrice is immutable");
	}

	@Override
	public BigDecimal getValue()
	{
		return BigDecimal.ZERO;
	}

	@Override
	public void setMaxQuantity(final Long maxQuantity)
	{
		throw new IllegalArgumentException("NoConfigPrice is immutable");
	}

	@Override
	public Long getMaxQuantity()
	{
		return Long.valueOf(0);
	}

	@Override
	public void setMinQuantity(final Long minQuantity)
	{
		throw new IllegalArgumentException("NoConfigPrice is immutable");
	}

	@Override
	public Long getMinQuantity()
	{
		return Long.valueOf(0);
	}

	@Override
	public void setFormattedValue(final String formattedValue)
	{
		throw new IllegalArgumentException("NoConfigPrice is immutable");
	}

	@Override
	public String getFormattedValue()
	{
		return "-";
	}


}
