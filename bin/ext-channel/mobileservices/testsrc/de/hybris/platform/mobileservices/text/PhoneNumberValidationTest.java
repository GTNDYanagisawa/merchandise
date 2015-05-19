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
package de.hybris.platform.mobileservices.text;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import de.hybris.platform.mobileservices.text.phonenumber.NormalizedPhoneNumber;
import de.hybris.platform.mobileservices.text.phonenumber.PhoneNumberService;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * The Class TestShortUrlService.
 */
public class PhoneNumberValidationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(PhoneNumberValidationTest.class.getName()); // NOPMD by willy on 24/03/10 8:45

	@Resource
	PhoneNumberService phoneNumberService;

	@Test
	public void testPhoneFilter()
	{

		final String[] esPhonesOk =
		{ "647000000", "678000000" };
		final String[] esPhonesKo =
		{ "747000000", "67800000", "6780000000" };
		final String[] dePhonesOk =
		{ "01797777777", "015127777777", "01627777777" };
		final String[] dePhonesKo =
		{ "0257777777", "01888888888", "01790099999999", "01500000" };
		final String[] chPhonesOk =
		{ "0767777777", "0787777777", "0796666667" };
		final String[] chPhonesKo =
		{ "0166666", "0216667777666", "122666666", "023666666" };

		for (final String p : esPhonesOk)
		{
			final NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("ES", p);
			assertTrue("valid phone mark as invalid", normalizedPhoneNumber.isValid());
			LOG.info("International phone:" + normalizedPhoneNumber.getNormalizedNumber());
		}
		for (final String p : dePhonesOk)
		{
			final NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("DE", p);
			assertTrue("valid phone mark as invalid", normalizedPhoneNumber.isValid());
			LOG.info("International phone:" + normalizedPhoneNumber.getNormalizedNumber());
		}
		for (final String p : chPhonesOk)
		{
			final NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("CH", p);
			assertTrue("valid phone mark as invalid", normalizedPhoneNumber.isValid());
			LOG.info("International phone:" + normalizedPhoneNumber.getNormalizedNumber());
		}

		for (final String p : esPhonesKo)
		{
			final NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("ES", p);
			assertFalse("valid phone mark as valid", normalizedPhoneNumber.isValid());
			LOG.info("Phone is non valid(ES):" + p);
		}
		for (final String p : dePhonesKo)
		{
			final NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("DE", p);
			assertFalse("valid phone mark as valid", normalizedPhoneNumber.isValid());
			LOG.info("Phone is non valid(DE):" + p);
		}
		for (final String p : chPhonesKo)
		{
			final NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("CH", p);
			assertFalse("valid phone mark as valid", normalizedPhoneNumber.isValid());
			LOG.info("Phone is non valid(CH):" + p);
		}
	}
}
