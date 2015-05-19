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


import java.text.SimpleDateFormat;
import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Assert;


@SuppressWarnings("javadoc")
public class ConversionToolsTest extends TestCase
{

	public void testConstructor()
	{
		final ConversionTools tools = new ConversionTools();
		// we just expect that instantiation is possible
		assertNotNull(tools);
	}

	public void testCutOfZeros()
	{
		Assert.assertEquals("1", ConversionTools.cutOffZeros("0001"));
		Assert.assertEquals("3456", ConversionTools.cutOffZeros("3456"));
		Assert.assertEquals("345", ConversionTools.cutOffZeros("0345"));
	}

	public void testCutOfZeros_Alphanumeric()
	{
		Assert.assertEquals("A1", ConversionTools.cutOffZeros("A1"));
		Assert.assertEquals("1A", ConversionTools.cutOffZeros("1A"));
		Assert.assertEquals("A", ConversionTools.cutOffZeros("A"));
		Assert.assertEquals("0A", ConversionTools.cutOffZeros("0A"));
		Assert.assertEquals("/005", ConversionTools.cutOffZeros("/005"));
		Assert.assertEquals("0001A", ConversionTools.cutOffZeros("0001A"));
	}

	public void testCutOfZeros_NegativeSign()
	{
		Assert.assertEquals("-01", ConversionTools.cutOffZeros("-01"));
	}

	public void testCutOfZeros_Zeros()
	{
		Assert.assertEquals("000", ConversionTools.cutOffZeros("000"));
	}

	public void testDoShortTo4()
	{
		assertEquals(new SimpleDateFormat("yyyyMMdd"), ConversionTools.doShortTo4(new SimpleDateFormat("yyMMdd")));
		assertEquals(new SimpleDateFormat("yyyyMMdd"), ConversionTools.doShortTo4(new SimpleDateFormat("yyyyMMdd")));
		assertEquals(new SimpleDateFormat("ddMMyyyy"), ConversionTools.doShortTo4(new SimpleDateFormat("ddMMyy")));
		assertEquals(new SimpleDateFormat("dd-MM-yyyy"), ConversionTools.doShortTo4(new SimpleDateFormat("dd-MM-yy")));
	}

	public void testGetDateFormat()
	{
		assertEquals("MM/dd/yyyy", ConversionTools.getDateFormat(Locale.US));
		assertEquals("dd.MM.yyyy", ConversionTools.getDateFormat(Locale.GERMANY));
	}

	public void testGetDecimalFormat()
	{
		assertEquals("#,##0.###", ConversionTools.getDecimalPointFormat(Locale.US));

	}

	public void testGetDecimalSeparator()
	{
		assertEquals('.', ConversionTools.getDecimalSeparator(Locale.US));
		assertEquals(',', ConversionTools.getDecimalSeparator(Locale.GERMANY));
		assertEquals('.', ConversionTools.getDecimalSeparator(Locale.ENGLISH));
	}

	public void testGetGroupingSeparator()
	{
		assertEquals(',', ConversionTools.getGroupingSeparator(Locale.US));
		assertEquals('.', ConversionTools.getGroupingSeparator(Locale.GERMANY));
		assertEquals(',', ConversionTools.getGroupingSeparator(Locale.ENGLISH));
	}

	public void testGetR3LanguageCode()
	{
		assertEquals("E", ConversionTools.getR3LanguageCode("EN"));
		assertEquals("D", ConversionTools.getR3LanguageCode("DE"));
		assertEquals("F", ConversionTools.getR3LanguageCode("FR"));
		// case insensitive!
		assertEquals("E", ConversionTools.getR3LanguageCode("en"));
	}

	public void testGetR3LanguageNonExistingCode()
	{
		assertEquals(null, ConversionTools.getR3LanguageCode("E"));
	}

	public void testGetSDF()
	{
		assertEquals((new SimpleDateFormat("MM/dd/yyyy")).toPattern(), (ConversionTools.getSDF(Locale.US)).toPattern());
		assertEquals((new SimpleDateFormat("dd.MM.yyyy")).toPattern(), (ConversionTools.getSDF(Locale.GERMANY)).toPattern());

	}

}
