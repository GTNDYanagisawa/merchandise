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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.mobileservices.enums.PhoneNumberFormat;
import de.hybris.platform.mobileservices.jalo.text.PhoneNumber;
import de.hybris.platform.mobileservices.jalo.text.PhoneNumberValidator;
import de.hybris.platform.mobileservices.model.text.PhoneNumberModel;
import de.hybris.platform.mobileservices.text.phonenumber.NormalizedPhoneNumber;
import de.hybris.platform.mobileservices.text.phonenumber.PhoneNumberService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * 
 */
public class PhoneNumberServiceTest extends ServicelayerTest
{
	@Resource
	I18NService i18nService;

	@Resource
	PhoneNumberService phoneNumberService;

	@Resource
	ModelService modelService;

	protected void assertValidAndNormalized(final String expected, final String countryIso, final String number)
	{
		final NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber(countryIso, number);
		assertTrue(normalizedPhoneNumber.getErrorMessage(), normalizedPhoneNumber.isValid());
		assertEquals(expected, normalizedPhoneNumber.getNormalizedNumber());
	}

	@Test
	public void testPhoneNumberCreation() throws JaloGenericCreationException, JaloAbstractTypeException,
			JaloItemNotFoundException
	{
		CountryModel countryModel;
		try
		{
			countryModel = i18nService.getCountry("DE");
		}
		catch (final UnknownIdentifierException e)
		{
			countryModel = modelService.create(CountryModel.class);
			countryModel.setIsocode("DE");
			modelService.save(countryModel);
		}

		PhoneNumberModel phoneNumberModel = new PhoneNumberModel();
		phoneNumberModel.setNumber("+491771111111");
		phoneNumberModel.setFormat(PhoneNumberFormat.INTERNATIONAL);
		modelService.save(phoneNumberModel);
		assertEquals("+491771111111", phoneNumberModel.getNumber());
		assertEquals("491771111111", phoneNumberModel.getNormalizedNumber());
		assertEquals(countryModel, phoneNumberModel.getCountry());

		phoneNumberModel = new PhoneNumberModel();
		phoneNumberModel.setNumber("(0177) 1234567");
		phoneNumberModel.setFormat(PhoneNumberFormat.LOCAL);
		phoneNumberModel.setCountry(countryModel);
		modelService.save(phoneNumberModel);
		assertEquals("(0177) 1234567", phoneNumberModel.getNumber());
		assertEquals("491771234567", phoneNumberModel.getNormalizedNumber());
		assertEquals(countryModel, phoneNumberModel.getCountry());

		phoneNumberModel = new PhoneNumberModel();
		phoneNumberModel.setNumber("07797806542");
		phoneNumberModel.setFormat(PhoneNumberFormat.LOCAL);
		try
		{
			modelService.save(phoneNumberModel);
			fail("ModelSavingException expected");
		}
		catch (final ModelSavingException e)
		{
			assertTrue(e.getCause() != null ? e.getCause().getClass().getName() : "null",
					e.getCause() instanceof InterceptorException);
			final InterceptorException interceptorException = (InterceptorException) e.getCause();
			assertTrue(interceptorException.getInterceptor() instanceof PhoneNumberValidator);
		}

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(PhoneNumber.NUMBER, "07797806542");

		try
		{
			ComposedType.newInstance(null, PhoneNumber.class, params);
			fail("JaloInvalidParameterException expected");
		}
		catch (final JaloInvalidParameterException e)
		{
			// as expected
		}
	}

	@Test
	public void testValidation()
	{
		assertValidAndNormalized("491771234567", "DE", "+491771234567");
		assertValidAndNormalized("491771234567", "DE", "00491771234567");
		assertValidAndNormalized("491771234567", null, "+491771234567");
		assertValidAndNormalized("491771234567", "DE", "01771234567");
		assertValidAndNormalized("491771234567", "DE", "0177-1234567");
		assertValidAndNormalized("491771234567", "DE", "0177-1234567");

		NormalizedPhoneNumber normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber(null, "+491771234567");
		assertNotNull(normalizedPhoneNumber);
		assertTrue(normalizedPhoneNumber.isValid());
		assertEquals("491771234567", normalizedPhoneNumber.getNormalizedNumber());
		assertEquals("DE", normalizedPhoneNumber.getCountryIsoCode());
		assertEquals(49, normalizedPhoneNumber.getRegionCode());

		normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber(null, "491771234567");
		assertNotNull(normalizedPhoneNumber);
		assertTrue(normalizedPhoneNumber.isValid());
		assertEquals("491771234567", normalizedPhoneNumber.getNormalizedNumber());
		assertEquals("DE", normalizedPhoneNumber.getCountryIsoCode());
		assertEquals(49, normalizedPhoneNumber.getRegionCode());

		normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("DE", "0177-1234567");
		assertNotNull(normalizedPhoneNumber);
		assertTrue(normalizedPhoneNumber.isValid());
		assertEquals("491771234567", normalizedPhoneNumber.getNormalizedNumber());
		assertEquals("DE", normalizedPhoneNumber.getCountryIsoCode());
		assertEquals(49, normalizedPhoneNumber.getRegionCode());

		normalizedPhoneNumber = phoneNumberService.validateAndNormalizePhoneNumber("DE", "0111-1234567");
		assertNotNull(normalizedPhoneNumber);
		assertFalse(normalizedPhoneNumber.isValid());
		assertEquals("491111234567", normalizedPhoneNumber.getNormalizedNumber());
		assertEquals("DE", normalizedPhoneNumber.getCountryIsoCode());
		assertEquals(49, normalizedPhoneNumber.getRegionCode());
	}

	@Test
	public void testAutomaticNumberNormalization()
	{
		CountryModel countryModelDE = null;
		try
		{
			countryModelDE = i18nService.getCountry("DE");
		}
		catch (final UnknownIdentifierException e)
		{
			countryModelDE = new CountryModel();
			countryModelDE.setIsocode("DE");
			modelService.save(countryModelDE);
		}
		assertNotNull(countryModelDE);

		final PhoneNumberModel phoneNumberModel = new PhoneNumberModel();
		phoneNumberModel.setNumber("01771234567");
		phoneNumberModel.setCountry(countryModelDE);

		modelService.save(phoneNumberModel);

		assertEquals("491771234567", phoneNumberModel.getNormalizedNumber());
	}
}
