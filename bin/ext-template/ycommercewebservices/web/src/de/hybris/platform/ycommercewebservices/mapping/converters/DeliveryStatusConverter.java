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
 */
package de.hybris.platform.ycommercewebservices.mapping.converters;

import de.hybris.platform.core.enums.DeliveryStatus;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link DeliveryStatus} and String
 */
public class DeliveryStatusConverter extends BidirectionalConverter<DeliveryStatus, String>
{
	@Override
	public DeliveryStatus convertFrom(final String source, final Type<DeliveryStatus> destinationType)
	{
		return DeliveryStatus.valueOf(source);
	}

	@Override
	public String convertTo(final DeliveryStatus source, final Type<String> destinationType)
	{
		return source.toString();
	}
}
