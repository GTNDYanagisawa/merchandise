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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;

import junit.framework.Assert;
import ma.glasnost.orika.metadata.Type;

import org.junit.Test;


@UnitTest
public class ConsignmentStatusConverterTest
{
	ConsignmentStatusConverter converter = new ConsignmentStatusConverter();
	String stringStatus = ConsignmentStatus.PICKUP_COMPLETE.toString();
	ConsignmentStatus status = ConsignmentStatus.PICKUP_COMPLETE;

	@Test
	public void testConvertFrom()
	{
		final ConsignmentStatus result = converter.convertFrom(stringStatus, (Type<ConsignmentStatus>) null);
		Assert.assertEquals(status, result);
	}

	@Test
	public void testConvertTo()
	{
		final String result = converter.convertTo(status, (Type<String>) null);
		Assert.assertEquals(stringStatus, result);
	}
}
