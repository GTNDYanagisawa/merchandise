/**
 *
 */
package de.hybris.platform.sap.sapcarcommercefacades.order.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderConverter;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderEntryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryBase;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * @author I827395
 * 
 */
public class DefaultCarOrderConverter implements CarOrderConverter
{


	private PriceDataFactory priceDataFactory;

	private BaseStoreService baseStoreService;

	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	@Override
	@Deprecated
	public void convertOrders(final List<CarOrderHistoryData> orderList)
	{

		for (final CarOrderHistoryData order : orderList)
		{
			convertOrder(order);

		}
	}

	@Override
	public void convertOrdersBase(final List<? extends CarOrderHistoryBase> orderList)
	{

		for (final CarOrderHistoryBase order : orderList)
		{
			convertOrderBase(order);

		}
	}


	@Override
	public void convertOrderBase(final CarOrderHistoryBase order)
	{

		order.setNet(getBaseStoreService().getCurrentBaseStore().isNet());

		// format subtotal
		if (order.getSubTotal() != null)
		{
			// taxes included
			if (!order.isNet())
			{
				order.setSubTotal(getPriceDataFactory().create(PriceDataType.BUY,
						order.getSubTotal().getValue().add(order.getTotalTax().getValue()), order.getSubTotal().getCurrencyIso()));
			}

			order.setSubTotal(formatPrice(order.getSubTotal()));
		}

		// format tax
		if (order.getTotalTax() != null)
		{
			order.setTotalTax(formatPrice(order.getTotalTax()));
		}
		// format total with tax
		if (order.getTotalPriceWithTax() != null)
		{
			order.setTotalPriceWithTax(formatPrice(order.getTotalPriceWithTax()));
		}
	}

	@Override
	public void convertOrderEntries(final List<CarOrderEntryData> orderEntries)
	{
		for (final CarOrderEntryData orderEntry : orderEntries)
		{
			convertOrderEntry(orderEntry);

		}

	}


	protected void convertOrderEntry(final CarOrderEntryData orderEntry)
	{

		// format base price
		if (orderEntry.getBasePrice() != null)
		{
			orderEntry.setBasePrice(formatPrice(orderEntry.getBasePrice()));
		}

		// format total price
		if (orderEntry.getTotalPrice() != null)
		{
			orderEntry.setTotalPrice(formatPrice(orderEntry.getTotalPrice()));
		}

		// format base price with tax
		if (orderEntry.getBasePriceWithTax() != null)
		{
			orderEntry.setBasePriceWithTax(formatPrice(orderEntry.getBasePriceWithTax()));
		}

		// format total price with tax
		if (orderEntry.getTotalPriceWithTax() != null)
		{
			orderEntry.setTotalPriceWithTax(formatPrice(orderEntry.getTotalPriceWithTax()));
		}

	}

	protected PriceData formatPrice(final PriceData priceData)
	{

		if (priceData.getValue() != null)
		{
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode(priceData.getCurrencyIso());
			currency.setSymbol(priceData.getCurrencyIso());
			final BigDecimal price = BigDecimal.valueOf(priceData.getValue().doubleValue());

			return getPriceDataFactory().create(PriceDataType.BUY, price, currency);

		}

		return priceData;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderConverter#convertOrder(de.hybris.platform.sap.
	 * sapcarintegration.data.CarOrderHistoryData)
	 */
	@Override
	@Deprecated
	public void convertOrder(final CarOrderHistoryData order)
	{
		this.convertOrderBase(order);

	}

	@Override
	public void convertOrder(final CarMultichannelOrderHistoryData order)
	{
		this.convertOrderBase(order);

	}

}
