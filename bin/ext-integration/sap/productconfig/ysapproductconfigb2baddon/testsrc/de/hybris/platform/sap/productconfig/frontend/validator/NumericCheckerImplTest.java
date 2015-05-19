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
package de.hybris.platform.sap.productconfig.frontend.validator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;


@UnitTest
public class NumericCheckerImplTest
{
	private final NumericCheckerImpl checker = new NumericCheckerImpl();
	@Mock
	private I18NService i18nService;
	@Mock
	private Errors errors;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);

		checker.setI18NService(i18nService);

	}

	@Test
	public void testValidWithPoint() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "123.41").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(2);

		checker.validate(csticData, errors);
		Mockito.verifyZeroInteractions(errors);
	}

	@Test
	public void testAdditionalValueValidWithPoint() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "123.41").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(2);
		csticData.setAdditionalValue(csticData.getValue());
		csticData.setValue("abc");

		checker.validateAdditionalValue(csticData, errors);
		Mockito.verifyZeroInteractions(errors);
	}


	@Test
	public void testValidErrorFlag() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "123.41").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(2);
		csticData.setCsticStatus(CsticStatusType.DEFAULT);

		checker.validate(csticData, errors);
		assertEquals("CStic should not have any error", CsticStatusType.DEFAULT, csticData.getCsticStatus());
	}


	@Test
	public void testInvalidErrorFlag() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "abc").getCstics().get(0);
		csticData.setCsticStatus(CsticStatusType.DEFAULT);

		checker.validate(csticData, errors);
		assertEquals("CStic should have an error", CsticStatusType.ERROR, csticData.getCsticStatus());

	}

	@Test
	public void testInvalidOnlyLetters() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "abc").getCstics().get(0);

		checker.validate(csticData, errors);
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("value"), Mockito.anyString(), Mockito.any(Object[].class),
				Mockito.anyString());
	}

	@Test
	public void testAdditionlaValueInvalidOnlyLetters() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "abc").getCstics().get(0);
		csticData.setAdditionalValue(csticData.getValue());
		csticData.setValue("123");

		checker.validateAdditionalValue(csticData, errors);
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("additionalValue"), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.anyString());
	}

	@Test
	public void testInvalidSomeLetters() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "123abc").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(0);

		checker.validate(csticData, errors);
		Mockito.verifyZeroInteractions(errors);
	}


	@Test
	public void testValidWithGrouping() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "12,000").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(0);

		checker.validate(csticData, errors);
		Mockito.verifyZeroInteractions(errors);
	}

	@Test
	public void testValidWithGroupingDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMANY);

		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "12.000").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(0);

		checker.validate(csticData, errors);
		Mockito.verifyZeroInteractions(errors);
	}


	@Test
	public void testInvalidTooLong() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "1234567").getCstics().get(0);
		csticData.setTypeLength(6);

		checker.validate(csticData, errors);
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("value"), Mockito.anyString(), Mockito.any(Object[].class),
				Mockito.anyString());
	}

	@Test
	public void testInvalidAdditionalValueTooLong() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "1234567").getCstics().get(0);
		csticData.setTypeLength(6);
		csticData.setAdditionalValue(csticData.getValue());
		csticData.setValue("1");

		checker.validateAdditionalValue(csticData, errors);
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("additionalValue"), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.anyString());
	}

	@Test
	public void testNumericWithoutEntryMask() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "-123456").getCstics().get(0);
		csticData.setTypeLength(6);
		csticData.setNumberScale(0);
		csticData.setEntryFieldMask(null);

		checker.validate(csticData, errors);
		Mockito.verifyZeroInteractions(errors);
	}

	@Test
	public void testInvalidTooMuchFractions() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "12.345").getCstics().get(0);
		csticData.setTypeLength(6);
		csticData.setNumberScale(2);

		checker.validate(csticData, errors);
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("value"), Mockito.anyString(), Mockito.any(Object[].class),
				Mockito.anyString());
	}

	@Test
	public void testNumberFormatDecimals() throws Exception
	{
		final String string = checker.createExpectedFormatAsString(3, 0, Locale.ENGLISH);

		assertEquals("###", string);
	}

	@Test
	public void testNumberFormatDecimalsWithGrouping() throws Exception
	{
		final String string = checker.createExpectedFormatAsString(4, 0, Locale.ENGLISH);

		assertEquals("#,###", string);
	}

	@Test
	public void testNumberFormatDecimalsWithGroupingDE() throws Exception
	{
		final String string = checker.createExpectedFormatAsString(4, 0, Locale.GERMAN);

		assertEquals("#.###", string);
	}


	@Test
	public void testNumberFormatDecimalsWithFractions() throws Exception
	{
		final String string = checker.createExpectedFormatAsString(1, 2, Locale.ENGLISH);

		assertEquals("#.##", string);
	}

	@Test
	public void testLongNumber() throws Exception
	{
		final String string = checker.createExpectedFormatAsString(10, 5, Locale.ENGLISH);

		assertEquals("#,###,###,###.#####", string);
	}

	@Test
	public void testNegativeNumber() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "-123").getCstics().get(0);
		csticData.setTypeLength(3);
		csticData.setNumberScale(0);

		checker.validate(csticData, errors);
		Mockito.verify(errors, Mockito.times(0)).rejectValue(Mockito.eq("value"), Mockito.anyString(), Mockito.any(Object[].class),
				Mockito.anyString());
	}

	@Test
	public void testNegativeNumberNotAllowed() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "-123").getCstics().get(0);
		csticData.setTypeLength(3);
		csticData.setNumberScale(0);
		csticData.setEntryFieldMask("___");

		checker.validate(csticData, errors);
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("value"), Mockito.anyString(), Mockito.any(Object[].class),
				Mockito.anyString());
	}


}
