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

package de.hybris.platform.emsclient.populators;

import static junit.framework.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.entitlementservices.data.EmsGrantData;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import com.hybris.services.entitlements.condition.ConditionData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/emsclient/metered-populator-test-spring.xml")
public class EmsGrantDataMeteredConditionPopulatorTest
{
	@Autowired
	Populator<EmsGrantData, ConditionData> populator;

	@Test
	public void shouldConvert()
	{
		final EmsGrantData source = new EmsGrantData();
		source.setMaxQuantity(10);
		final ConditionData target = new ConditionData();
		populator.populate(source,target);
		assertEquals("metered", target.getType());
		assertEquals(1, target.getProperties().size());
		assertEquals("10", target.getProperty("maxQuantity"));
	}

	@Test
	public void shouldEnableOverageForZero()
	{
		final EmsGrantData source = new EmsGrantData();
		source.setMaxQuantity(0);
		final ConditionData target = new ConditionData();
		populator.populate(source,target);
		assertEquals("true", target.getProperty("allowOverage"));
		assertEquals("0", target.getProperty("maxQuantity"));
	}

	@Test
	public void shouldEnableOverageForMinusOne()
	{
		final EmsGrantData source = new EmsGrantData();
		source.setMaxQuantity(-1);
		final ConditionData target = new ConditionData();
		populator.populate(source,target);
		assertEquals("true", target.getProperty("allowOverage"));
		assertEquals("0", target.getProperty("maxQuantity"));
	}

	@Test(expected = ConversionException.class)
	public void shouldRejectNegativeValues()
	{
		final EmsGrantData source = new EmsGrantData();
		source.setMaxQuantity(-2);
		final ConditionData target = new ConditionData();
		populator.populate(source, target);
	}
}
