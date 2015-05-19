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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.entitlementservices.data.EmsGrantData;
import de.hybris.platform.entitlementservices.enums.EntitlementTimeUnit;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import com.hybris.services.entitlements.condition.ConditionData;
import com.hybris.services.entitlements.conversion.DateTimeConverter;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/emsclient/test/emsclient-timeframe-test-spring.xml")
public class EmsGrantDataTimeConditionPopulatorTest
{
	@Autowired
	EmsGrantDataTimeConditionPopulator populator;

	@Autowired
	DateTimeConverter dateTimeConverter;

	@Test
	public void shouldCountStartDateFromOne()
	{
		final ConditionData conditionData = new ConditionData();
		final EmsGrantData emsData = new EmsGrantData();
		final Date date = new Date();
		emsData.setTimeUnitStart(1);
		emsData.setTimeUnitDuration(1);
		emsData.setTimeUnit(EntitlementTimeUnit.DAY);
		emsData.setCreatedAt(date);

		populator.populate(emsData, conditionData);

		assertEquals(
				conditionData.getProperty(EmsGrantDataTimeConditionPopulator.GRANT_PARAMETER_START),
				dateTimeConverter.convertDateToString(date));
	}

	@Test
	public void shouldUseDuration() throws ParseException
	{
		final ConditionData conditionData = new ConditionData();
		final EmsGrantData emsData = new EmsGrantData();
		final Date date = new Date();
		emsData.setTimeUnitStart(1);
		emsData.setTimeUnitDuration(1);
		emsData.setTimeUnit(EntitlementTimeUnit.DAY);
		emsData.setCreatedAt(date);

		populator.populate(emsData, conditionData);

		assertEquals(
				conditionData.getProperty(EmsGrantDataTimeConditionPopulator.GRANT_PARAMETER_START),
				dateTimeConverter.convertDateToString(date));
		assertTrue(date.before(dateTimeConverter.convertStringToDate(
				conditionData.getProperty(EmsGrantDataTimeConditionPopulator.GRANT_PARAMETER_END))));
	}

	@Test(expected = ConversionException.class)
	public void shouldRejectNullStartTime()
	{
		final ConditionData conditionData = new ConditionData();
		final EmsGrantData emsData = new EmsGrantData();
		final Date date = new Date();
		emsData.setTimeUnitStart(null);
		emsData.setTimeUnitDuration(1);
		emsData.setTimeUnit(EntitlementTimeUnit.DAY);
		emsData.setCreatedAt(date);

		populator.populate(emsData, conditionData);
	}

	@Test(expected = ConversionException.class)
	public void shouldRejectInvalidStartTime()
	{
		final ConditionData conditionData = new ConditionData();
		final EmsGrantData emsData = new EmsGrantData();
		final Date date = new Date();
		emsData.setTimeUnitStart(0);
		emsData.setTimeUnitDuration(1);
		emsData.setTimeUnit(EntitlementTimeUnit.DAY);
		emsData.setCreatedAt(date);

		populator.populate(emsData, conditionData);
	}

	/**
	 * Null duration means open end date
	 */
	@Test
	public void shouldAcceptNullDuration()
	{
		final ConditionData conditionData = new ConditionData();
		final EmsGrantData emsData = new EmsGrantData();
		final Date date = new Date();
		emsData.setTimeUnitStart(1);
		emsData.setTimeUnitDuration(null);
		emsData.setTimeUnit(EntitlementTimeUnit.DAY);
		emsData.setCreatedAt(date);

		populator.populate(emsData, conditionData);

		assertNotNull(conditionData.getProperty(EmsGrantDataTimeConditionPopulator.GRANT_PARAMETER_START));
		assertNull(conditionData.getProperty(EmsGrantDataTimeConditionPopulator.GRANT_PARAMETER_END));
	}

	/**
	 * Duration of 0 means open end date
	 */
	@Test
	public void shouldAcceptZeroDuration()
	{
		final ConditionData conditionData = new ConditionData();
		final EmsGrantData emsData = new EmsGrantData();
		final Date date = new Date();
		emsData.setTimeUnitStart(1);
		emsData.setTimeUnitDuration(0);
		emsData.setTimeUnit(EntitlementTimeUnit.DAY);
		emsData.setCreatedAt(date);

		populator.populate(emsData, conditionData);

		assertNotNull(conditionData.getProperty(EmsGrantDataTimeConditionPopulator.GRANT_PARAMETER_START));
		assertNull(conditionData.getProperty(EmsGrantDataTimeConditionPopulator.GRANT_PARAMETER_END));
	}

	@Test(expected = ConversionException.class)
	public void shouldRejectInvalidDuration()
	{
		final ConditionData conditionData = new ConditionData();
		final EmsGrantData emsData = new EmsGrantData();
		final Date date = new Date();
		emsData.setTimeUnitStart(1);
		emsData.setTimeUnitDuration(-1);
		emsData.setTimeUnit(EntitlementTimeUnit.DAY);
		emsData.setCreatedAt(date);

		populator.populate(emsData, conditionData);
	}
}
