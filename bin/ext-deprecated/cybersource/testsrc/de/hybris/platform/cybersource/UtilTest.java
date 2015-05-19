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
package de.hybris.platform.cybersource;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.cybersource.adapter.Util;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Various tests for {@link Util} class
 */
@ManualTest
public class UtilTest
{

	private Calendar cal;

	@Before
	public void setUp()
	{
		cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2010);
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		cal.set(Calendar.DAY_OF_MONTH, 23);
		cal.set(Calendar.HOUR_OF_DAY, 13);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 41);
		cal.set(Calendar.MILLISECOND, 0);
	}


	/**
	 * Tests {@link Util#parseXmlSchemaDateTime(String)} method with null argument. It should not throw any exception but
	 * return null.
	 */
	@Test
	public void testPoliteNullHandling()
	{
		Assert.assertNull(Util.parseXmlSchemaDateTime(null));
	}

	/**
	 * Tests {@link Util#parseXmlSchemaDateTime(String)} method with UTC zero-length time zone canonical representation
	 * (this is also commonly called GMT in UK which is not 100% accurate).
	 */
	@Test
	public void parseUTCZeroTest()
	{
		final String dateTime = "2010-02-23T13:30:41Z";
		final TimeZone timeZone = TimeZone.getTimeZone("GMT+00");
		cal.setTimeZone(timeZone);
		Assert.assertEquals(cal.getTime(), Util.parseXmlSchemaDateTime(dateTime));
	}

	/**
	 * Tests {@link Util#parseXmlSchemaDateTime(String)} method with UTC +01:00 time zone (Central Europe Time)
	 */
	@Test
	public void parseUTCPlus01Test()
	{
		final String dateTime = "2010-02-23T13:30:41+01:00";
		final TimeZone timeZone = TimeZone.getTimeZone("GMT+01");
		cal.setTimeZone(timeZone);
		Assert.assertEquals(cal.getTime(), Util.parseXmlSchemaDateTime(dateTime));
	}

	/**
	 * Tests {@link Util#parseXmlSchemaDateTime(String)} method with UTC -05:00 time zone (called ET-Eastern Time Zone in
	 * US, this is for example the time zone for New York city)
	 */
	@Test
	public void parseUTCMinus05Test()
	{
		final String dateTime = "2010-02-23T13:30:41-05:00";
		final TimeZone timeZone = TimeZone.getTimeZone("GMT-05");
		cal.setTimeZone(timeZone);
		Assert.assertEquals(cal.getTime(), Util.parseXmlSchemaDateTime(dateTime));
	}

}
