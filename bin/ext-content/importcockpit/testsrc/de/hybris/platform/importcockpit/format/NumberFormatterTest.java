/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.importcockpit.format;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.format.model.impl.ImportNumberFormat;

import java.util.Collections;
import java.util.Locale;

import org.junit.Test;


@UnitTest
public class NumberFormatterTest
{

	@Test
	public void testGetFormattedNumberString()
	{
		NumberFormatter formatter = prepareFormatter("###,###.##", ',', '.', 0, 8, Locale.US);
		assertThat(formatter.getFormattedNumberString("123,456.78")).isEqualTo("123,456.78");

		formatter = prepareFormatter("###,###.##", ' ', '.', 0, 8, Locale.US);
		assertThat(formatter.getFormattedNumberString("123 456.78")).isEqualTo("123,456.78");

		formatter = prepareFormatter("###,###.##", ' ', ',', 0, 8, Locale.US);
		assertThat(formatter.getFormattedNumberString("123 456,78")).isEqualTo("123,456.78");

		formatter = prepareFormatter("###,###.##", 'x', 'y', 0, 8, Locale.US);
		assertThat(formatter.getFormattedNumberString("123x456y78")).isEqualTo("123,456.78");

		formatter = prepareFormatter("###,###.##", ',', '.', 0, 8, Locale.JAPAN);
		assertThat(formatter.getFormattedNumberString("123,456.78")).isEqualTo("123,456.78");

		formatter = prepareFormatter("###,###.##", ' ', '.', 0, 8, Locale.JAPAN);
		assertThat(formatter.getFormattedNumberString("123 456.78")).isEqualTo("123,456.78");

		formatter = prepareFormatter("###,###.##", ' ', ',', 0, 8, Locale.JAPAN);
		assertThat(formatter.getFormattedNumberString("123 456,78")).isEqualTo("123,456.78");

		formatter = prepareFormatter("###,###.##", 'x', 'y', 0, 8, Locale.JAPAN);
		assertThat(formatter.getFormattedNumberString("123x456y78")).isEqualTo("123,456.78");
	}

	private NumberFormatter prepareFormatter(final String pattern, final char groupSep, final char decimalSep,
			final int minFrDigits, final int maxFrDigits, final Locale locale)
	{
		final ImportNumberFormat numberFormat = new ImportNumberFormat(pattern, pattern, groupSep, decimalSep, minFrDigits,
				maxFrDigits,
				locale);
		final FormatBuilder formatBuilder = mock(FormatBuilder.class);
		when(formatBuilder.getNumberFormats()).thenReturn(Collections.singletonList(numberFormat));
		return new NumberFormatter(numberFormat, formatBuilder);
	}

}
