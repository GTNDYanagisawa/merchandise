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

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class CartModificationPopulator implements Populator<CommerceCartModification, CartModificationData>
{
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	@Override
	public void populate(final CommerceCartModification source, final CartModificationData target)
	{
		if (source.getEntry().getOrder() != null)
		{
			target.setCartCode(source.getEntry().getOrder().getCode());
		}
		target.setStatusCode(source.getStatusCode());
		target.setQuantity(source.getQuantity());
		target.setQuantityAdded(source.getQuantityAdded());
		if (source.getEntry() != null)
		{
			target.setEntry(getOrderEntryConverter().convert(source.getEntry()));
		}
		target.setDeliveryModeChanged(source.getDeliveryModeChanged());
	}
}
