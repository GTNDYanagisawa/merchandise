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
package de.hybris.platform.sap.core.common.util.conv;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;


/**
 * Test for the conversion utility.
 */
@UnitTest
public class GuidConversionUtilTest
{
	private final static String fifteen = "0000000000000000000000000000000F";

	/**
	 * Calls conversion methods with empty input.
	 */
	@Test
	public void testEmptyInput()
	{
		try
		{
			GuidConversionUtil.convertToByteArray("");
			Assert.fail("Empty string should raise a NumberFormatException");
		}
		catch (final NumberFormatException e)
		{
			// ok
		}

		try
		{
			final TechKey tk = new TechKey("");
			GuidConversionUtil.convertToByteArray(tk);
			Assert.fail("Empty string should raise a NumberFormatException");
		}
		catch (final NumberFormatException e)
		{
			// ok
		}

		try
		{
			GuidConversionUtil.convertToString(new byte[] {});
			Assert.fail("Empty byte array should raise a NumberFormatException");
		}
		catch (final NumberFormatException e)
		{
			// ok
		}

		try
		{
			GuidConversionUtil.convertToTechKey(new byte[] {});
			Assert.fail("Empty byte array should raise a NumberFormatException");
		}
		catch (final NumberFormatException e)
		{
			// ok
		}
	}

	/**
	 * Calls conversion methods with correct input.
	 */
	@Test
	public void testCorrectInput()
	{
		final byte[] b = GuidConversionUtil.convertToByteArray(fifteen);
		Assert.assertEquals(fifteen, GuidConversionUtil.convertToString(b));

		final byte[] b2 = GuidConversionUtil.convertToByteArray(new TechKey(fifteen));
		Assert.assertEquals(fifteen, GuidConversionUtil.convertToString(b2));

		final TechKey tk = GuidConversionUtil.convertToTechKey(b);
		Assert.assertEquals(new TechKey(fifteen), tk);
	}
}
