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
package de.hybris.platform.media.storage.impl;

import static org.fest.assertions.Assertions.assertThat;
import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

@UnitTest
public class BooleanValueConverterTest
{
	private final BooleanValueConverter converter = new BooleanValueConverter();

	@Test
	public void shouldConvertStringRepresentationOfTrueIntoBooleanTrue()
	{
		// given
		final String input = "true";

		// when
		final Boolean converted = converter.convert(input);

		// then
		assertThat(converted).isNotNull().isEqualTo(Boolean.TRUE);
	}

	@Test
	public void shouldConvertNullInputIntoBooleanFalse()
	{
		// given
		final String input = null;

		// when
		final Boolean converted = converter.convert(input);

		// then
		assertThat(converted).isNotNull().isEqualTo(Boolean.FALSE);
	}

	@Test
	public void shouldConvertEmptyStringInputIntoBooleanFalse()
	{
		// given
		final String input = " ";

		// when
		final Boolean converted = converter.convert(input);

		// then
		assertThat(converted).isNotNull().isEqualTo(Boolean.FALSE);
	}

	@Test
	public void shouldConvertNotValidTrueStringRepresentationIntoBooleanFalse()
	{
		// given
		final String input = "foobar";

		// when
		final Boolean converted = converter.convert(input);

		// then
		assertThat(converted).isNotNull().isEqualTo(Boolean.FALSE);
	}

}
