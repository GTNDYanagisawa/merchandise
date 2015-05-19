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

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.order.OrderModel} as source and
 * {@link de.hybris.platform.commercefacades.order.data.OrderHistoryData} as target type.
 */
public class OrderHistoryPopulator implements Populator<OrderModel, OrderHistoryData>
{
	private EnumerationService enumerationService;
	private PriceDataFactory priceDataFactory;

	@Override
	public void populate(final OrderModel source, final OrderHistoryData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setGuid(source.getGuid());
		target.setPlaced(source.getDate());
		target.setStatus(source.getStatus());
		target.setStatusDisplay(source.getStatusDisplay());
		if (source.getTotalPrice() != null)
		{
			BigDecimal totalPrice = BigDecimal.valueOf(source.getTotalPrice().doubleValue());
			if (Boolean.TRUE.equals(source.getNet()))
			{
				totalPrice = totalPrice.add(BigDecimal.valueOf(source.getTotalTax().doubleValue()));
			}
			target.setTotal(getPriceDataFactory().create(PriceDataType.BUY, totalPrice, source.getCurrency()));
		}
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
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
