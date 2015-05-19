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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CustomerShipToData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


public class CustomerShipToDataPopulator extends AbstractResultPopulator<CartModel, CustomerShipToData>
{
	@Override
	public void populate(final CartModel source, final CustomerShipToData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CartModel] source cannot be null");
		validateParameterNotNull(target, "Parameter [CustomerShipToData] target cannot be null");

		final AddressModel deliveryAddress = source.getDeliveryAddress();
		if (deliveryAddress != null)
		{
			target.setShipToCity(deliveryAddress.getTown());
			target.setShipToCompany(deliveryAddress.getCompany());
			target.setShipToCountry(deliveryAddress.getCountry().getIsocode());
			if (deliveryAddress.getRegion() != null)
			{
				target.setShipToState(deliveryAddress.getRegion().getIsocodeShort());
			}
			target.setShipToFirstName(deliveryAddress.getFirstname());
			target.setShipToLastName(deliveryAddress.getLastname());
			target.setShipToPhoneNumber(deliveryAddress.getPhone1());
			target.setShipToPostalCode(deliveryAddress.getPostalcode());
			target.setShipToStreet1(deliveryAddress.getLine1());
			target.setShipToStreet2(deliveryAddress.getLine2());
		}

		final DeliveryModeModel deliveryMode = source.getDeliveryMode();
		if (deliveryMode == null)
		{
			target.setShipToShippingMethod("none");
		}
		else
		{
			target.setShipToShippingMethod(deliveryMode.getCode());
		}
	}
}
