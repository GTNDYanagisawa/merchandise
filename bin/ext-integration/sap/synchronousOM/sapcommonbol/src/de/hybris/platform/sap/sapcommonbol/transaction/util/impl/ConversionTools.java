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

import de.hybris.platform.sap.core.common.TechKey;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



/**
 * Common conversion tools
 *
 */
public class ConversionTools
{

	private static Map<String, String> theLanguageMapISO2ToR3 = new HashMap<String, String>();

	/**
	 * Retrieves the pattern string for the decimal point representation, using Java standard functionality.
	 *
	 * @param locale
	 *           the session locale
	 * @return the pattern string
	 */
	public static String getDecimalPointFormat(final Locale locale)
	{
		final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(locale);
		return formatter.toPattern();
	}

	/**
	 * Gets the decimal separator for a given locale, using Java standard functionality.
	 *
	 * @param locale
	 *           the current locale
	 * @return the decimal separator
	 */
	public static char getDecimalSeparator(final Locale locale)
	{
		final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(locale);
		return formatter.getDecimalFormatSymbols().getDecimalSeparator();
	}

	/**
	 * Gets the grouping separator for a given locale, using Java standard functionality.
	 *
	 * @param locale
	 *           the current locale
	 * @return the grouping separator
	 */
	public static char getGroupingSeparator(final Locale locale)
	{
		final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(locale);
		return formatter.getDecimalFormatSymbols().getGroupingSeparator();
	}

	/**
	 * Retrieves the date format pattern string for a given locale.
	 *
	 * @param locale
	 *           the session loacale
	 * @return the pattern string
	 */
	public static String getDateFormat(final Locale locale)
	{
		final SimpleDateFormat newFormatter = doShortTo4((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale));
		newFormatter.setLenient(false);

		return newFormatter.toPattern();
	}

	/**
	 * Retrieves a new instance of SimpleDateFormat for the given locale. Note: Although the result is in short format,
	 * still 4 digits are displayed for year.
	 *
	 * @param locale
	 * @return new instance of date formatter.
	 */
	public static SimpleDateFormat getSDF(final Locale locale)
	{
		final SimpleDateFormat newFormatter = doShortTo4((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale));
		return newFormatter;
	}

	/**
	 * Converts a short date format into the standard date format which means adding some year, month or date characters.
	 *
	 * @param sdf
	 *           simple date format
	 * @return the converted simple date format
	 */
	static SimpleDateFormat doShortTo4(final SimpleDateFormat sdf)
	{

		int i;

		int iLen;
		String sTemp;

		sTemp = sdf.toPattern();
		iLen = sTemp.length();

		// year conversion
		if (sTemp.indexOf("yyyy") == -1)
		{
			i = sTemp.lastIndexOf('y') + 1;
			sTemp = sTemp.substring(0, i) + "yy" + (i < iLen ? sTemp.substring(i, iLen) : "");
		}

		// month conversions:
		iLen = sTemp.length();
		i = sTemp.lastIndexOf('M') + 1;
		if (i == 1 || sTemp.charAt(i - 2) != 'M')
		{
			sTemp = sTemp.substring(0, i - 1) + "MM" + (i < iLen ? sTemp.substring(i, iLen) : "");
		}
		// day conversions:
		iLen = sTemp.length();
		i = sTemp.lastIndexOf('d') + 1;
		if (i == 1 || sTemp.charAt(i - 2) != 'd')
		{
			sTemp = sTemp.substring(0, i - 1) + "dd" + (i < iLen ? sTemp.substring(i, iLen) : "");
		}

		sdf.applyPattern(sTemp);

		return sdf;
	}

	/**
	 * Cuts of the leading zeros for document ID display and document item display. If an format exception occurs, the
	 * input is returned.
	 *
	 * @param argument
	 *           the String with leading zeros
	 * @return the String without leading zeros
	 */
	public static String cutOffZeros(final String argument)
	{
		final int size = argument.length();
		int firstNonZeroIndex = 0;
		boolean nonZeroReached = false;

		for (int i = 0; i < size; i++)
		{
			final char ch = argument.charAt(i);
			if (!Character.isDigit(ch))
			{
				return argument;
			}
			if ('0' == ch && !nonZeroReached)
			{
				firstNonZeroIndex = i + 1;
			}
			else
			{
				nonZeroReached = true;
			}
		}
		if (firstNonZeroIndex == 0)
		{
			return argument;
		}
		if (!nonZeroReached)
		{
			return argument;
		}
		return argument.substring(firstNonZeroIndex);
	}


	/**
	 * This method is needed when accessing the catalog which currently cannot deal with leading zeros. The leading zeros
	 * might come from an ERP system when using numerical Ids for products. In that case the key contains the id with
	 * additional leading zeros
	 *
	 * @param key
	 * @param id
	 * @return a new techKey based on the id
	 */
	@Deprecated
	public static TechKey cutOffZerosFromKey(final TechKey key, final String id)
	{
		final String idAsString = key.getIdAsString();
		if (idAsString != null && id != null && idAsString.endsWith(id))
		{
			final int endIndex = idAsString.length() - id.length();
			if (endIndex > 0 && containsOnlyZeros(idAsString.substring(0, endIndex)))
			{
				return new TechKey(id);
			}
		}
		return key;
	}

	@Deprecated
	private static boolean containsOnlyZeros(final String s)
	{
		final int size = s.length();
		for (int i = 0; i < size; i++)
		{
			if (s.charAt(i) != '0')
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the single character r3 language code for a given String.
	 *
	 * @param langu
	 *           a <code>String</code> value
	 * @return a <code>the language in R/3 format. Case sensitive!</code> value
	 */
	public static String getR3LanguageCode(final String langu)
	{

		if (langu != null)
		{
			return theLanguageMapISO2ToR3.get(langu.toLowerCase(Locale.ENGLISH));
		}
		return null;
	}

	static
	{
		theLanguageMapISO2ToR3.put("en", "E");
		theLanguageMapISO2ToR3.put("de", "D");
		theLanguageMapISO2ToR3.put("fr", "F");
		theLanguageMapISO2ToR3.put("es", "S");
		theLanguageMapISO2ToR3.put("pt", "P");
		theLanguageMapISO2ToR3.put("it", "I");
		theLanguageMapISO2ToR3.put("da", "K");
		theLanguageMapISO2ToR3.put("fi", "U");
		theLanguageMapISO2ToR3.put("nl", "N");
		theLanguageMapISO2ToR3.put("no", "O");
		theLanguageMapISO2ToR3.put("sv", "V");
		// ----------------------- -2
		theLanguageMapISO2ToR3.put("sk", "Q");
		theLanguageMapISO2ToR3.put("cs", "C");
		theLanguageMapISO2ToR3.put("hu", "H");
		theLanguageMapISO2ToR3.put("pl", "L");
		// ----------------------- -5
		theLanguageMapISO2ToR3.put("ru", "R");
		theLanguageMapISO2ToR3.put("bg", "W");
		// ----------------------- -9
		theLanguageMapISO2ToR3.put("tr", "T");
		// ----------------------- -7
		theLanguageMapISO2ToR3.put("el", "G");
		// ----------------------- -8
		theLanguageMapISO2ToR3.put("he", "B");
		// -----------------------
		theLanguageMapISO2ToR3.put("ja", "J");
		theLanguageMapISO2ToR3.put("zf", "M");
		theLanguageMapISO2ToR3.put("zh", "1");
		theLanguageMapISO2ToR3.put("ko", "3");
		// ----missing ISA languages
		theLanguageMapISO2ToR3.put("hr", "6");
		theLanguageMapISO2ToR3.put("sl", "5");
		theLanguageMapISO2ToR3.put("th", "2");

		// ---missing languages, non ISA
		theLanguageMapISO2ToR3.put("sr", "0");
		theLanguageMapISO2ToR3.put("ro", "4");
		theLanguageMapISO2ToR3.put("ms", "7");
		theLanguageMapISO2ToR3.put("uk", "8");
		theLanguageMapISO2ToR3.put("et", "9");
		theLanguageMapISO2ToR3.put("ar", "A");
		theLanguageMapISO2ToR3.put("lt", "X");
		theLanguageMapISO2ToR3.put("lv", "Y");
		theLanguageMapISO2ToR3.put("af", "a");
		theLanguageMapISO2ToR3.put("id", "i");
	}


	/**
	 * Method adds leading zeros to a numeric string based on a specified total length
	 *
	 * @param inputString
	 *           contains the numeric String
	 * @param desiredLength
	 *           specifies the total desired length of the string
	 * @return String with leading zeroes appended
	 *
	 */
	public static String addLeadingZerosToNumericID(final String inputString, final int desiredLength)
	{
		int size = inputString.length();

		//check if inputString is numeric
		for (int i = 0; i < size; i++)
		{
			final char ch = inputString.charAt(i);
			if (!Character.isDigit(ch))
			{
				return inputString;
			}
		}
		//if inputString is already the desired length, keep as is
		if (size >= desiredLength)
		{
			return inputString;
		}

		//pad with missing zeroes on the left
		final StringBuffer buffer = new StringBuffer(size);
		while (size++ < desiredLength)
		{
			buffer.append("0");
		}
		buffer.append(inputString);
		return buffer.toString();
	}
}