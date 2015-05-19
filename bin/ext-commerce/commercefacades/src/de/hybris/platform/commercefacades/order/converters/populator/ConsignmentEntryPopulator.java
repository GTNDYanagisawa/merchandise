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

import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator implementation for {@link de.hybris.platform.ordersplitting.model.ConsignmentEntryModel} as source and
 * {@link de.hybris.platform.commercefacades.order.data.ConsignmentEntryData} as target type.
 */
public class ConsignmentEntryPopulator implements Populator<ConsignmentEntryModel, ConsignmentEntryData>
{
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;


	public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	@Override
	public void populate(final ConsignmentEntryModel source, final ConsignmentEntryData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setQuantity(source.getQuantity());
		target.setShippedQuantity(source.getShippedQuantity());
		target.setOrderEntry(getOrderEntryConverter().convert(source.getOrderEntry()));

	}
}
