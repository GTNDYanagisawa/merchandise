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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.tax.strategies.CisShippingAddressStrategy;
import de.hybris.platform.integration.commons.strategies.OndemandDeliveryAddressStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisAddressType;


/**
 * Default shipping address strategy to send the appropriate addresses to the tax service. Which address is selected for
 * ship from depends on the implementation of onDemandDeliveryStrategy.
 * 
 */
public class DefaultCisShippingAddressStrategy implements CisShippingAddressStrategy
{
	private Converter<AddressModel, CisAddress> cisAddressConverter;
	private OndemandDeliveryAddressStrategy ondemandDeliveryAddressStrategy;

	@Override
	public List<CisAddress> getAddresses(final AbstractOrderModel abstractOrder)
	{
		final AddressModel deliveryAddressForOrder = getOndemandDeliveryAddressStrategy().getDeliveryAddressForOrder(abstractOrder);

		final List<CisAddress> addresses = new ArrayList<CisAddress>();

		if (deliveryAddressForOrder != null)
		{
			final CisAddress shipTo = getCisAddressConverter().convert(deliveryAddressForOrder);
			shipTo.setType(CisAddressType.SHIP_TO);
			addresses.add(shipTo);
		}

		final AddressModel deliveryFromAddressForOrder = abstractOrder.getDeliveryFromAddress() != null ? abstractOrder
				.getDeliveryFromAddress() : deliveryAddressForOrder;
		if (deliveryFromAddressForOrder != null)
		{
			final CisAddress shipFrom = getCisAddressConverter()
					.convert(
							(abstractOrder.getDeliveryFromAddress() != null ? abstractOrder.getDeliveryFromAddress()
									: deliveryAddressForOrder));
			shipFrom.setType(CisAddressType.SHIP_FROM);
			addresses.add(shipFrom);
		}

		return addresses;
	}

	protected Converter<AddressModel, CisAddress> getCisAddressConverter()
	{
		return cisAddressConverter;
	}

	@Required
	public void setCisAddressConverter(final Converter<AddressModel, CisAddress> cisAddressConverter)
	{
		this.cisAddressConverter = cisAddressConverter;
	}

	protected OndemandDeliveryAddressStrategy getOndemandDeliveryAddressStrategy()
	{
		return ondemandDeliveryAddressStrategy;
	}

	@Required
	public void setOndemandDeliveryAddressStrategy(final OndemandDeliveryAddressStrategy ondemandDeliveryAddressStrategy)
	{
		this.ondemandDeliveryAddressStrategy = ondemandDeliveryAddressStrategy;
	}
}
