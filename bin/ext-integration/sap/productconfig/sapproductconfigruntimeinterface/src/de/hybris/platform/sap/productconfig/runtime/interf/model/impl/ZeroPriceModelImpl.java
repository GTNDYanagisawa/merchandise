/**
 * 
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import java.math.BigDecimal;


/**
 * Immutable Object
 * 
 */
public class ZeroPriceModelImpl extends PriceModelImpl
{

	@Override
	public void setCurrency(final String currency)
	{
		throw new IllegalArgumentException("ZeroPriceModelImpl is immutable");
	}

	@Override
	public void setPriceValue(final BigDecimal priceValue)
	{
		throw new IllegalArgumentException("ZeroPriceModelImpl is immutable");
	}

	@Override
	public String getCurrency()
	{
		return "";
	}

	@Override
	public BigDecimal getPriceValue()
	{
		return BigDecimal.ZERO;
	}

}
