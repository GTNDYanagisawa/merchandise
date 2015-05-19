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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.TimeData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TimeDataPopulatorTest
{
	private final DateFormat dateFormat = new SimpleDateFormat("hh --- mm");

	private final DateFormat dfKorea = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.KOREA);

	private TimeDataPopulator timeDataPopulator;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testDataConvertWithCustomFormat()
	{
		timeDataPopulator = new TimeDataPopulator()
		{
			@Override
			protected DateFormat getDateFormat()
			{
				return dateFormat;
			}
		};

		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 45);
		final Date someTime = cal.getTime();
		final TimeData data = new TimeData();
		timeDataPopulator.populate(someTime, data);

		Assert.assertEquals(10, data.getHour());
		Assert.assertEquals(45, data.getMinute());
		Assert.assertEquals(dateFormat.format(someTime), data.getFormattedHour());
	}

	@Test
	public void testDataConvertWithCustomLocale()
	{
		timeDataPopulator = new TimeDataPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return Locale.KOREA;
			}
		};

		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 45);
		final Date someTime = cal.getTime();
		final TimeData data = new TimeData();
		timeDataPopulator.populate(someTime, data);

		Assert.assertEquals(10, data.getHour());
		Assert.assertEquals(45, data.getMinute());
		Assert.assertEquals(dfKorea.format(someTime), data.getFormattedHour());
	}
}
