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
package de.hybris.platform.b2b.punchout.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for {@link CXmlDateUtil}.
 */
@UnitTest
public class CXmlDateUtilTest
{
	private static final String DATE_STRING = "2001-12-20T09:25:57+01:00";
	private static final String INCORRECT_DATE_STRING = "2000-3-18T11:33:32";

	private final CXmlDateUtil dateUtil = new CXmlDateUtil();

	private final TimeZone timeZone = TimeZone.getTimeZone("GMT+1");

	/**
	 * Sets a pre-defined time zone for the {@link CXmlDateUtil}.
	 */
	@Before
	public void setUp()
	{
		dateUtil.DATE_FORMAT.setTimeZone(timeZone);
	}

	/**
	 * Tests that parsing a date is correct when it is in the correct ISO 8601 format.
	 */
	@Test
	public void testParseStringHappyCase() throws Exception
	{
		final Date result = dateUtil.parseString(DATE_STRING);
		assertNotNull(result);
	}

	/**
	 * Tests that parsing a wrong date string not in accordance with the ISO 8601 format fails.
	 */
	@Test(expected = ParseException.class)
	public void testParseIncorrectString() throws Exception
	{
		dateUtil.parseString(INCORRECT_DATE_STRING);
	}

	/**
	 * Tests that the output of a formatted date is the expected ISO 8601 string.
	 */
	@Test
	public void testFormatDate() throws Exception
	{
		final Calendar calendar = Calendar.getInstance(timeZone);
		calendar.set(2001, 11, 20, 9, 25, 57);
		assertEquals(DATE_STRING, dateUtil.formatDate(calendar.getTime()));
	}

}
