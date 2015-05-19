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

package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;


public class PriceModelImpl extends BaseModelImpl implements PriceModel
{
	private BigDecimal priceValue;
	private String currency;

	@Override
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	@Override
	public String getCurrency()
	{
		return currency;
	}

	@Override
	public BigDecimal getPriceValue()
	{
		return priceValue;
	}

	@Override
	public void setPriceValue(final BigDecimal priceValue)
	{
		this.priceValue = priceValue;
	}

	@Override
	public PriceModel clone()
	{
		return (PriceModel) super.clone();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((priceValue == null) ? 0 : priceValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final PriceModelImpl other = (PriceModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}
		if (currency == null)
		{
			if (other.currency != null)
			{
				return false;
			}
		}
		else if (!currency.equals(other.currency))
		{
			return false;
		}
		if (priceValue == null)
		{
			if (other.priceValue != null)
			{
				return false;
			}
		}
		else if (!priceValue.equals(other.priceValue))
		{
			return false;
		}
		return true;
	}


}
