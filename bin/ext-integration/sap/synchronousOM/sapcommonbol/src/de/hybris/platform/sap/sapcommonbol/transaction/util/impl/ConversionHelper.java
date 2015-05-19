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
package de.hybris.platform.sap.sapcommonbol.transaction.util.impl;

import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.LocaleUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Helper class for the data conversion for the Jco calls. <br>
 *
 */
public class ConversionHelper
{

	@Deprecated
	private static final char ABAP_FALSE = ' ';
	@Deprecated
	private static final char ABAP_TRUE = 'X';
	private static final String ABAP_COMMERCIAL_FORMAT_STR = "#0.0#;#0.0#-";
	private static final String DATE_STRING_FORMAT_STR = "yyyyMMdd";
	@Deprecated
	private static final String EMPTY_STRING = "";
	private static final String ZERO = "0";
	private static final String DATS_DATE_FORMAT_STR = "yyyy-MM-dd";
	private static final String DEC_DATE_FORMAT_STR = "yyyyMMddHHmmss";

	/**
	 * Time zone which is used as default.
	 */
	final public static String DEFAULT_TIMEZONE = "GMT";

	/**
	 * String which is used as empty date.
	 */
	public static final String NO_DATE = ZERO;

	/**
	 * ThreadLocal SimpleDateFormat "yyyy-MM-dd".<br>
	 * SimpleDateFormat is not thread safe and expensive to construct. By Wrapping into a ThreadLocal object we avoid
	 * synchronisation issues and still get a decent performance, as the object is only created once per thread.
	 */
	@Deprecated
	private static final ThreadLocal<SimpleDateFormat> TL_DATS_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>()
	{
		@Override
		protected SimpleDateFormat initialValue()
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATS_DATE_FORMAT_STR);
			// simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
			return simpleDateFormat;
		}
	};

	/**
	 * ThreadLocal SimpleDateFormat "yyyyMMddHHmmss".<br>
	 * SimpleDateFormat is not thread safe and expensive to construct. By Wrapping into a ThreadLocal object we avoid
	 * synchronisation issues and still get a decent performance, as the object is only created once per thread.
	 */
	@Deprecated
	private static final ThreadLocal<SimpleDateFormat> TL_DEC_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>()
	{
		@Override
		protected SimpleDateFormat initialValue()
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEC_DATE_FORMAT_STR);
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
			return simpleDateFormat;
		}
	};

	/**
	 * ThreadLocal SimpleDateFormat "yyyyMMdd".<br>
	 * SimpleDateFormat is not thread safe and expensive to construct. By Wrapping into a ThreadLocal object we avoid
	 * synchronisation issues and still get a decent performance, as the object is only created once per thread.
	 */
	private static final ThreadLocal<SimpleDateFormat> TL_DATE_STRING_FORMAT = new ThreadLocal<SimpleDateFormat>()
	{
		@Override
		protected SimpleDateFormat initialValue()
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_STRING_FORMAT_STR);
			// simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
			return simpleDateFormat;
		}
	};

	private static final ThreadLocal<ParsePosition> TL_UNUSED = new ThreadLocal<ParsePosition>()
	{
		@Override
		protected ParsePosition initialValue()
		{
			return new ParsePosition(0);
		}

	};

	/**
	 * ThreadLocal DecimalFormat "#0.0#;#0.0#-".<br>
	 * DecimalFormat is not thread safe and expensive to construct. By Wrapping into a ThreadLocal object we avoid
	 * synchronisation issues and still get a decent performance, as the object is only created once per thread.
	 */
	@Deprecated
	private static ThreadLocal<DecimalFormat> TL_ABAP_COMMERCIAL_FORMAT = new ThreadLocal<DecimalFormat>()
	{
		@Override
		protected DecimalFormat initialValue()
		{
			final DecimalFormat format = new DecimalFormat(ABAP_COMMERCIAL_FORMAT_STR);
			format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
			return format;
		}
	};

	/**
	 * Converts the ABAP type DEC (length 14) to a date. The expected format is YYYYMMDDHHMMSS.
	 *
	 * @param decAsString
	 *           decimal input used in ABAP system for date
	 * @return date date format
	 * @throws ParseException
	 *            something goes wrong during convention
	 */
	@Deprecated
	public static Date convertDECtoDate(final String decAsString) throws ParseException
	{
		Date date;
		if (decAsString == null || decAsString.equalsIgnoreCase(NO_DATE))
		{
			date = null;
		}
		else
		{
			final String parseAbleString = correctMessedUpFormat(decAsString);
			date = TL_DEC_DATE_FORMAT.get().parse(parseAbleString);
		}

		return date;
	}

	@Deprecated
	private static String correctMessedUpFormat(String decAsString)
	{
		// ABAP don't know leading zeros, so we add them if string is too short
		decAsString = decAsString.trim();
		final int expectedLength = DEC_DATE_FORMAT_STR.length();
		final int numMissingZeros = expectedLength - decAsString.length();
		if (numMissingZeros > 0)
		{
			final StringBuilder buf = new StringBuilder(expectedLength);
			for (int ii = 0; ii < numMissingZeros; ii++)
			{
				buf.append(ZERO);
			}
			buf.append(decAsString);
			decAsString = buf.toString();
		}

		return decAsString;
	}

	/**
	 * Converts date format to ABAP decimal for the date.<br>
	 *
	 * @param date
	 *           date in date format
	 * @return string contains decimal format of date
	 */
	@Deprecated
	public static String convertDateToDEC(final Date date)
	{

		// special case: null
		if (null == date)
		{
			return EMPTY_STRING;
		}

		// we convert this date to DEC like
		final String decAsString = TL_DEC_DATE_FORMAT.get().format(date);

		return decAsString;
	}

	/**
	 * Converts the ABAP type DATS (length 10) to a date. The expected format is YYYY-MM-DD.
	 *
	 * @param datsAsString
	 *           decimal input used in ABAP system for date
	 * @return date date in date format
	 */
	@Deprecated
	public static Date convertDATStoDate(final String datsAsString)
	{
		final ParsePosition pos = TL_UNUSED.get();
		// reset
		pos.setIndex(0);
		final Date date = TL_DATS_DATE_FORMAT.get().parse(datsAsString, pos);
		return date;
	}

	/**
	 * Converts a date string in format yyyyMMdd to a date.
	 *
	 * @param datsAsString
	 *           date string in format yyyyMMdd
	 * @return date
	 */
	public static Date convertDateStringToDate(final String datsAsString)
	{
		final ParsePosition pos = TL_UNUSED.get();
		// reset
		pos.setIndex(0);
		final Date date = TL_DATE_STRING_FORMAT.get().parse(datsAsString, pos);
		return date;
	}

	/**
	 * Converts a date into a date string in format yyyyMMdd .
	 *
	 * @param date
	 *           in date string format
	 * @return date string in format yyyyMMdd
	 */
	public static String convertDateToDateString(final Date date)
	{
		final String dateString = TL_DATE_STRING_FORMAT.get().format(date);
		return dateString;
	}

	/**
	 * Converts a date into a localized date string (which is formatted according to the session locale)
	 *
	 * @param date
	 *           Date in date format
	 * @return localised date string
	 */
	public static String convertDateToLocalizedString(final Date date)
	{
		final SimpleDateFormat formatter = ConversionTools.getSDF(LocaleUtil.getLocale());
		return formatter.format(date);
	}

	/**
	 * Converts the ABAP type DATS (length 10) to a date. The expected format is yyyy-MM-dd.
	 *
	 * @param date
	 *           Date in date format
	 * @return string date as string
	 */
	@Deprecated
	public static String convertDateToDATS(final Date date)
	{
		String datsAsString;
		if (null == date)
		{
			datsAsString = EMPTY_STRING;
		}
		else
		{
			datsAsString = TL_DATS_DATE_FORMAT.get().format(date);
		}
		return datsAsString;
	}

	/**
	 * Converts a BigDecimal to a String using the Locale defined in LocaleUtil. The fraction length is kept from the
	 * BigDecimal.
	 *
	 * @param bd
	 *           Big decimal
	 * @return String like "0.00"
	 */
	public static String convertBigDecimalToString(final BigDecimal bd)
	{
		if (bd == null)
		{
			return null;
		}
		final NumberFormat format = NumberFormat.getNumberInstance(LocaleUtil.getLocale());
		format.setMinimumFractionDigits(bd.scale());
		return format.format(bd);
	}

	/**
	 * Converts a BigDecimal to a string in ABAP commercial notation. That is an uninterrupted sequence of numbers with a
	 * maximum of one period (.) as a decimal separator.
	 *
	 * @param bd
	 *           the BigDecimal to be converted.
	 * @return String in APAP commercial notation like "2342.15".
	 */
	@Deprecated
	public static String convertBigDecimalToStringABAPCommercialNotation(final BigDecimal bd)
	{
		if (bd == null)
		{
			return null;
		}
		TL_ABAP_COMMERCIAL_FORMAT.get().setMaximumFractionDigits(bd.scale());
		return TL_ABAP_COMMERCIAL_FORMAT.get().format(bd);
	}



	/**
	 * Converts a String to a BigDecimal. In case the String is empty or null, the BigDecimal value is set to 0.
	 *
	 * @param number
	 *           Big decimal as string
	 * @return Big decimal
	 */
	@Deprecated
	public static BigDecimal convertStringToBigDecimal(final String number)
	{
		if (number == null || number.length() == 0)
		{
			return BigDecimal.ZERO;
		}
		final NumberFormat numberFormat = NumberFormat.getInstance(LocaleUtil.getLocale());
		// enforce direct parsing of big decimal
		if (numberFormat instanceof DecimalFormat)
		{
			final DecimalFormat decFormat = (DecimalFormat) numberFormat;
			decFormat.setParseBigDecimal(true);
		}
		BigDecimal result;
		try
		{
			result = convertStringToBigDecimal(number, numberFormat);
		}
		catch (final ParseException e)
		{
			throw new ApplicationBaseRuntimeException("Cannot parse '" + number + "' to BigDecimal", e);
		}
		return result;
	}

	/**
	 * Converts a String to a BigDecimal with the given format.
	 *
	 * @param number
	 *           Big decimal as string
	 * @param numberFormat
	 *           format to use
	 * @return Big decimal
	 * @throws ParseException
	 */
	@Deprecated
	public static BigDecimal convertStringToBigDecimal(final String number, final NumberFormat numberFormat) throws ParseException
	{
		BigDecimal result;
		final Number number1 = numberFormat.parse(number);
		if (number1 instanceof BigDecimal)
		{
			result = (BigDecimal) number1;
		}
		else
		{
			result = new BigDecimal(number1.doubleValue());
		}
		return result;
	}

	/**
	 * @param value
	 *           Character which is used in ABAP as boolean value
	 * @return boolean true if the value is 'X'
	 */
	@Deprecated
	public static boolean convertCharToBoolean(final char value)
	{

		return (value == ABAP_TRUE);
	}

	/**
	 * @param value
	 *           Boolean value
	 * @return 'X' if the value is true and space if value is false
	 */
	@Deprecated
	public static char convertBooleanToChar(final boolean value)
	{

		if (value)
		{
			return ABAP_TRUE;
		}
		else
		{
			return ABAP_FALSE;
		}

	}

	/**
	 * Adjusts the BigDecimal from JCo value to the correct customized one.<br>
	 *
	 * @param value
	 *           BigDecimal from JCO
	 * @param decimal
	 *           customized number of decimals
	 * @return corrected BigDecimal
	 */
	public static BigDecimal adjustCurrencyDecimalPoint(final BigDecimal value, final int decimal)
	{
		final long withoutDecimalPoint = value.unscaledValue().longValue();
		final BigDecimal newValue = BigDecimal.valueOf(withoutDecimalPoint, decimal);
		return newValue;
	}

}
