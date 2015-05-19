package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;

public abstract class AbstractCarOrderHistoryBaseExtractor
{
	
	
	/**
	 * extract subtotal price
	 * @param props
	 * @return
	 */
	abstract PriceData extractTotalNetAmount(final Map<String, Object> props);
	
	/**
	 * extract tax amount
	 * @param props
	 * @return
	 */
	abstract PriceData extractTaxAmount(final Map<String, Object> props);
	

	protected PriceData createPrice()
	{
		final PriceData priceData = new PriceData();
		
		priceData.setPriceType(PriceDataType.BUY);

		return priceData;

	}
	
	
	/**
	 * utility method to create a price
	 * @param amount
	 * @param currency
	 * @return
	 */
	protected PriceData createPrice(BigDecimal price, String currency)
	{
		PriceData priceData = createPrice();
		
		priceData.setValue(price);

		// set currency
		priceData.setCurrencyIso(currency);

		return priceData;

	}
	
	protected PriceData createPriceWithTax(BigDecimal price,BigDecimal taxAmount, String currency)
	{
		PriceData priceData = createPrice();
		
		priceData.setValue(price.add(taxAmount));
		
		// set currency
		priceData.setCurrencyIso(currency);

		return priceData;

	}
	
	
	protected PriceData createBasePrice(PriceData entryTotalPrice, BigDecimal quantity)
	{
		return createPrice(entryTotalPrice.getValue().divide(quantity, 2, RoundingMode.HALF_UP), entryTotalPrice.getCurrencyIso());
		
	}
	
	protected PriceData createBasePriceWithTax(PriceData entryTotalPrice,PriceData entryTotalTax, BigDecimal quantity)
	{
		return createPrice(entryTotalPrice.getValue().add(entryTotalTax.getValue()).divide(quantity, 2, RoundingMode.HALF_UP), entryTotalPrice.getCurrencyIso());
		
	}

	
}
