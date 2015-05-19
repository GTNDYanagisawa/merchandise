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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderEntryGroupData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Updates {@link AbstractOrderData} with order group data. Order entry data must be already populated in
 * {@link AbstractOrderData}.
 * 
 */
public class OrderEntryGroupPopulator implements Populator<AbstractOrderModel, AbstractOrderData>
{
	private PriceDataFactory priceDataFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
	}



	protected void updateGroupTotalPriceWithTax(final AbstractOrderEntryModel entryModel, final OrderEntryGroupData groupData)
	{
		final CurrencyModel currency = entryModel.getOrder().getCurrency();

		final PriceData totalPriceWithTax = groupData.getTotalPriceWithTax();
		final PriceData entryModelPriceWithTax = getPriceDataFactory()
				.create(
						PriceDataType.BUY,
						BigDecimal.valueOf(entryModel.getTotalPrice().doubleValue()
								+ TaxValue.sumAppliedTaxValues(entryModel.getTaxValues())), currency);

		if (totalPriceWithTax == null)
		{
			groupData.setTotalPriceWithTax(entryModelPriceWithTax);
		}
		else
		{
			final PriceData newTotalPriceWithTax = getPriceDataFactory().create(PriceDataType.BUY,
					totalPriceWithTax.getValue().add(entryModelPriceWithTax.getValue()), currency);

			groupData.setTotalPriceWithTax(newTotalPriceWithTax);
		}
	}


	protected Double calcTotalWithTax(final AbstractOrderModel source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}
		double totalPrice = source.getTotalPrice() == null ? 0.0d : source.getTotalPrice().doubleValue();

		// Add the taxes to the total price if the cart is net; if the total was null taxes should be null as well
		if (Boolean.TRUE.equals(source.getNet()) && totalPrice != 0.0d)
		{
			totalPrice += source.getTotalTax() == null ? 0.0d : source.getTotalTax().doubleValue();
		}

		return Double.valueOf(totalPrice);
	}


	protected OrderEntryData getOrderEntryData(final AbstractOrderData target, final Integer entryNumber)
	{
		for (final OrderEntryData entryData : target.getEntries())
		{
			if (entryNumber.equals(entryData.getEntryNumber()))
			{
				return entryData;
			}
		}
		return null;
	}

	public long sumOrderGroupQuantity(final OrderEntryGroupData group)
	{
		long sum = 0;
		for (final OrderEntryData entry : group.getEntries())
		{
			sum += entry.getQuantity().longValue();
		}
		return sum;
	}

	public long sumPickupItemsQuantity(final AbstractOrderModel source)
	{
		long sum = 0;
		for (final AbstractOrderEntryModel entryModel : source.getEntries())
		{
			if (entryModel.getDeliveryPointOfService() != null)
			{
				sum += entryModel.getQuantity().longValue();
			}
		}
		return sum;
	}

	public long sumDeliveryItemsQuantity(final AbstractOrderModel source)
	{
		long sum = 0;
		for (final AbstractOrderEntryModel entryModel : source.getEntries())
		{
			if (entryModel.getDeliveryPointOfService() == null)
			{
				sum += entryModel.getQuantity().longValue();
			}
		}
		return sum;
	}



	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

}
