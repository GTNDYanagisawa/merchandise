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

import de.hybris.platform.sap.core.common.TechKey;

import java.math.BigInteger;
import java.util.Locale;


/**
 * Contains static methods for converting guids in string format into those in byte[] format and vice versa. <br>
 * We use this since we operate on IPC structures directly. For performance reasons, we don't want to create our own
 * structures with character guids and map them in the backend.
 */
public class GuidConversionUtil
{

	private static BigInteger maxVal = new BigInteger("100000000000000000000000000000000", 16);
	private static BigInteger halfOfMaxVal = maxVal.shiftRight(1);

	/**
	 * Converts a guid in string format into byte format.
	 * 
	 * @param arg
	 *           the guid in string format. Must be of length 32 and must contain valid hex numbers 0,1,..F, otherwise a
	 *           runtime exception might be thrown
	 * @return the guid in byte[] format. The length of the array is 16
	 */
	public static byte[] convertToByteArray(final String arg)
	{

		BigInteger value = new BigInteger(arg, 16);

		if (value.compareTo(halfOfMaxVal) >= 0)
		{
			value = value.subtract(maxVal);
		}
		final byte[] result = value.toByteArray();

		return result;
	}

	/**
	 * Converts a guid in TechKey format into byte format.
	 * 
	 * @param arg
	 *           the guid. The underlying string be of length 32 and must contain valid hex numbers 0,1,..F, otherwise a
	 *           runtime exception might be thrown
	 * @return the guid in byte[] format. The length of the array is 16
	 */
	public static byte[] convertToByteArray(final TechKey arg)
	{
		return convertToByteArray(arg.getIdAsString());
	}

	/**
	 * Converts a guid in byte format into string format.
	 * 
	 * @param arg
	 *           the guid as byte[] of length 16
	 * @return the string representation. Characters in upper case
	 */
	public static String convertToString(final byte[] arg)
	{
		BigInteger value = new BigInteger(arg);

		if (value.compareTo(new BigInteger("0")) < 0)
		{
			value = value.add(maxVal);
		}

		final StringBuffer sb = new StringBuffer(value.toString(16));
		while (sb.length() < 32)
		{
			sb.insert(0, "0");
		}
		return sb.toString().toUpperCase(Locale.ENGLISH);
	}

	/**
	 * Converts a guid in byte format into an ISA TechKey.
	 * 
	 * @param arg
	 *           the guid as byte[] of length 16
	 * @return the techkey. Characters in underlying string in upper case
	 */
	public static TechKey convertToTechKey(final byte[] arg)
	{
		return new TechKey(convertToString(arg));

	}

}
