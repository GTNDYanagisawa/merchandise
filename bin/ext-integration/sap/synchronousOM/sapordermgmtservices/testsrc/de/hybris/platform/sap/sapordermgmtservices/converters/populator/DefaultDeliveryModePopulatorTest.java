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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class DefaultDeliveryModePopulatorTest
{
	DefaultDeliveryModePopulator classUnderTest = new DefaultDeliveryModePopulator();

	@Test
	public void testPopulate()
	{

		final String value = "value";
		final String key = "key";
		final Entry<String, String> source = new AbstractMap.SimpleEntry<String, String>(key, value);
		final DeliveryModeData target = new DeliveryModeData();
		classUnderTest.populate(source, target);
		assertEquals(target.getCode(), key);
		assertEquals(target.getName(), value);
	}
}
