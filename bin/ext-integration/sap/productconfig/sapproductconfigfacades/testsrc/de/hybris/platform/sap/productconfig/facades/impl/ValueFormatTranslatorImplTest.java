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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ValueFormatTranslatorImplTest
{

	private ValueFormatTranslatorImpl cut;

	@Mock
	private I18NService i18nService;

	@Before
	public void setup()
	{
		cut = new ValueFormatTranslatorImpl();
		MockitoAnnotations.initMocks(this);
		cut.setI18NService(i18nService);
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
	}

	@Test
	public void testStringStaysTheSame() throws Exception
	{
		final String value = "value";
		final String formattedValue = cut.format(UiType.STRING, value);
		final String parsedValue = cut.parse(UiType.STRING, formattedValue);

		assertEquals("Must be the same", value, parsedValue);

	}

	@Test
	public void testParseNumeric() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, "123,999.123");

		assertEquals("Must be in simple format", "123999.123", parsedString);
	}

	@Test
	public void testParseNumeric_empty() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, "");

		assertTrue("empty string should not remain as empty", parsedString.isEmpty());
	}

	@Test
	public void testParseNumeric_null() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, null);

		assertTrue("null value should be parsed as empty string", parsedString.isEmpty());
	}


	@Test
	public void testParseNumeric_invalid() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, "abc");

		assertTrue("invalid value should be parsed as empty string", parsedString.isEmpty());
	}


	@Test
	public void testParseNumericDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		final String parsedString = cut.parse(UiType.NUMERIC, "123.999,123");

		assertEquals("Must be in simple and english format", "123999.123", parsedString);
	}

	@Test
	public void testFormatNumeric() throws Exception
	{
		final String formattedString = cut.format(UiType.NUMERIC, "123999.123");

		assertEquals("Must be in nice format", "123,999.123", formattedString);
	}

	@Test
	public void testFormatNumericExponent() throws Exception
	{
		final String formattedString = cut.format(UiType.NUMERIC, "1.23999123E05");

		assertEquals("Must be in nice format", "123,999.123", formattedString);
	}

	@Test
	public void testFormatNumericDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		final String formattedString = cut.format(UiType.NUMERIC, "123999.123");

		assertEquals("Must be in nice format", "123.999,123", formattedString);
	}

	@Test
	public void testFormatNumericExponentDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		final String formattedString = cut.format(UiType.NUMERIC, "1.23999123E05");

		assertEquals("Must be in nice format", "123.999,123", formattedString);
	}

	@Test
	public void testFormatNumericNull() throws Exception
	{
		final String formattedString = cut.format(UiType.NUMERIC, null);

		assertEquals("Must be in nice format", "", formattedString);
	}

	@Test
	public void testFormatNumericEmpty() throws Exception
	{
		final String formattedString = cut.format(UiType.NUMERIC, "");

		assertEquals("Must be in nice format", "", formattedString);
	}

	@Test
	public void testFormatNumericLargeNumber() throws Exception
	{
		final String formattedString = cut.format(UiType.NUMERIC, "9999999999.99999");

		assertEquals("Must be unchanged", "9,999,999,999.99999", formattedString);
	}

}
