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
package de.hybris.platform.sap.sapcommonbol.transaction.util;

import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class ConversionHelperTest extends TestCase
{

	@Override
	public void setUp()
	{
		LocaleUtil.setLocale(Locale.ENGLISH);
	}

	@SuppressWarnings("deprecation")
	public void testConvertStringToBigDecimal_simple()
	{
		final BigDecimal expected = new BigDecimal("123456.78");
		final BigDecimal actual = ConversionHelper.convertStringToBigDecimal("123,456.78");
		assertTrue("expected " + expected.toPlainString() + ", but saw " + actual.toPlainString(), expected.compareTo(actual) == 0);
	}

	@SuppressWarnings("deprecation")
	public void testConvertStringToBigDecimal_simple_de()
	{
		LocaleUtil.setLocale(Locale.GERMAN);
		final BigDecimal expected = new BigDecimal("123456.78");
		final BigDecimal actual = ConversionHelper.convertStringToBigDecimal("123.456,78");
		assertTrue("expected " + expected.toPlainString() + ", but saw " + actual.toPlainString(), expected.compareTo(actual) == 0);
	}

	@SuppressWarnings("deprecation")
	public void testConvertDecToDate_valid() throws ParseException
	{
		final Date date = ConversionHelper.convertDECtoDate("20101230112233");
		assertNotNull("Date must not be null", date);

		final Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.setTime(date);

		assertEquals(2010, calendar.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
		assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(22, calendar.get(Calendar.MINUTE));

	}

	@SuppressWarnings("deprecation")
	public void testConvertDecToDate_invalid()
	{
		try
		{
			ConversionHelper.convertDECtoDate("20XX1230112233");
			fail("The invalid string must not be parsed");
		}
		catch (final ParseException expected)
		{
			// $JL-EXC$ expected
		}

	}

	@SuppressWarnings("deprecation")
	public void testConverDecToDate_shortenedByBackend() throws ParseException
	{
		final Date date = ConversionHelper.convertDECtoDate("101230112233");
		assertNotNull("Date must not be null", date);

		final Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.setTime(date);

		assertEquals(10, calendar.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
		assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(22, calendar.get(Calendar.MINUTE));

	}



	public void testConvertDateStringToDate()
	{
		final String dateAsString = "19991231";
		final Date date = ConversionHelper.convertDateStringToDate(dateAsString);

		final Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.setTime(date);

		assertEquals(1999, calendar.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
		assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, calendar.get(Calendar.MINUTE));
	}

	@SuppressWarnings("deprecation")
	public void testConvertStringToBigDecimal()
	{

		final String string = "10";
		LocaleUtil.setLocale(Locale.ENGLISH);

		final BigDecimal bigDecimal = ConversionHelper.convertStringToBigDecimal(string);

		assertEquals(BigDecimal.TEN, bigDecimal);
	}

	public void testConstructor()
	{
		assertNotNull(new ConversionHelper());
	}

	public void testAdjustCurrencyScale_5_2() throws Exception
	{
		final BigDecimal value = BigDecimal.valueOf(500, 2);// 5.00

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 2);

		assertEquals("5.00", newValue.toPlainString());
	}

	public void testAdjustCurrencyScale_5_3() throws Exception
	{
		final BigDecimal value = BigDecimal.valueOf(500, 2);

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 3);

		assertEquals("0.500", newValue.toPlainString());
	}

	public void testAdjustCurrencyScale_50_3() throws Exception
	{
		final BigDecimal value = BigDecimal.valueOf(500, 1);// 50.0

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 3);

		assertEquals("0.500", newValue.toPlainString());

	}

	public void testAdjustCurrencyScale_500_0() throws Exception
	{
		final BigDecimal value = BigDecimal.valueOf(500, 0);// 500

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 2);

		assertEquals("5.00", newValue.toPlainString());

	}

	public void testAdjustCurrencyScale_max() throws Exception
	{
		final BigDecimal value = BigDecimal.valueOf(99999999999998888l, 2);// 999999999999988,88

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 4);

		assertEquals("9999999999999.8888", newValue.toPlainString());

	}

	public void testOverflowProblem() throws Exception
	{
		final BigDecimal value = BigDecimal.valueOf(3599964000l, 2);// 3599964000,00

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 2);

		assertEquals("35999640.00", newValue.toPlainString());

	}

}
