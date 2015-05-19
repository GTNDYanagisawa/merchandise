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

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link ConsignmentStatus} and String
 */
public class ConsignmentStatusConverter extends BidirectionalConverter<ConsignmentStatus, String>
{
	@Override
	public ConsignmentStatus convertFrom(final String source, final Type<ConsignmentStatus> destinationType)
	{
		return ConsignmentStatus.valueOf(source);
	}

	@Override
	public String convertTo(final ConsignmentStatus source, final Type<String> destinationType)
	{
		return source.toString();
	}
}
