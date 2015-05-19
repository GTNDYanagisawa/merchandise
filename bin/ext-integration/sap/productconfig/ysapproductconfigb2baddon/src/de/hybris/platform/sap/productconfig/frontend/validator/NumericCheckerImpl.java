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

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;


public class NumericCheckerImpl implements NumericChecker
{

	private static final String TOO_MANY_DIGITS = "Too many digits";
	private static final String TOO_MANY_FRACTIONS = "Too many fractions";
	private static final String SOURCE_FIELD_VALUE = "value";
	private static final String SOURCE_FIELD_ADDITIONAL_VALUE = "additionalValue";
	private static final String NOT_A_NUMBER = "Not a number";
	private static final char PATTERN_CHAR = '#';
	private static final char DEFAULT_DECIMAL_SEPARATOR = '.';
	private static final String MSG_KEY_NOT_NUMERIC_ERROR = "sapproductconfig.value.not.numeric";
	private static final String MSG_KEY_TOO_LONG_FRACTION = "sapproductconfig.value.too.long.fraction";
	private static final String MSG_KEY_TOO_MANY_DIGITS = "sapproductconfig.value.too.many.digits";
	private static final String MSG_KEY_NO_NEGATIVE_NUMBER = "sapproductconfig.value.no.negative";

	@Autowired
	private I18NService i18NService;
	private Locale locale;


	@Override
	public void validate(final CsticData cstic, final Errors errorObj)
	{
		final String value = cstic.getValue();
		validateValue(cstic, errorObj, value);
	}

	@Override
	public void validateAdditionalValue(final CsticData cstic, final Errors errorObj)
	{
		final String value = cstic.getAdditionalValue();
		validateValue(cstic, errorObj, value);
	}

	protected void validateValue(final CsticData cstic, final Errors errorObj, final String value)
	{
		this.locale = i18NService.getCurrentLocale();
		final DecimalFormat numberFormatter = (DecimalFormat) NumberFormat.getInstance(locale);
		numberFormatter.setParseBigDecimal(true);

		BigDecimal number;
		try
		{
			number = (BigDecimal) numberFormatter.parse(value);
		}
		catch (final ParseException e)
		{
			createFieldError(cstic, value, errorObj, new Object[]
			{ value }, NOT_A_NUMBER, MSG_KEY_NOT_NUMERIC_ERROR);
			return;
		}

		final boolean isNegativeNumber = BigDecimal.ZERO.compareTo(number) > 0;
		if (isNegativeNumber && !isNegativeNumberAllowed(cstic.getEntryFieldMask()))
		{
			createFieldError(cstic, value, errorObj, new Object[] {}, NOT_A_NUMBER, MSG_KEY_NO_NEGATIVE_NUMBER);
			return;
		}

		checkLength(number, value, cstic, errorObj);
	}

	private boolean isNegativeNumberAllowed(final String entryFieldMask)
	{
		if (entryFieldMask == null || entryFieldMask.isEmpty())
		{
			return true;
		}
		final boolean canBeNegative = entryFieldMask.charAt(0) == '-';
		return canBeNegative;
	}

	private void checkLength(final BigDecimal numberToCheck, final String value, final CsticData cstic, final Errors errorObj)
	{
		final int maxFractions = cstic.getNumberScale();
		final int maxDecimals = cstic.getTypeLength() - maxFractions;
		final String expectedFormat = createExpectedFormatAsString(maxDecimals, maxFractions, this.locale);


		final int actualFraction = numberToCheck.scale();
		if (actualFraction > maxFractions)
		{
			final Object[] args =
			{ value, Integer.valueOf(actualFraction), Integer.valueOf(maxFractions), expectedFormat };
			createFieldError(cstic, value, errorObj, args, TOO_MANY_FRACTIONS, MSG_KEY_TOO_LONG_FRACTION);
		}

		final int numberOfDecimals = countDecimals(numberToCheck);
		if (numberOfDecimals > maxDecimals)
		{
			final Object[] args =
			{ value, Integer.valueOf(numberOfDecimals), Integer.valueOf(maxDecimals), expectedFormat };
			createFieldError(cstic, value, errorObj, args, TOO_MANY_DIGITS, MSG_KEY_TOO_MANY_DIGITS);
		}

	}

	protected void createFieldError(final CsticData cstic, final String errorValue, final Errors errorObj, final Object[] args,
			final String defaultMsg, final String msgKey)
	{
		final String source;
		if (errorValue.equals(cstic.getValue()))
		{
			source = SOURCE_FIELD_VALUE;
		}
		else
		{
			source = SOURCE_FIELD_ADDITIONAL_VALUE;
		}
		errorObj.rejectValue(source, msgKey, args, defaultMsg);
		cstic.setCsticStatus(CsticStatusType.ERROR);
	}

	protected String createExpectedFormatAsString(final int maxIntegerPlaces, final int maxDecimalPlaces, final Locale locale)
	{
		final DecimalFormat numberFormatter = (DecimalFormat) NumberFormat.getInstance(locale);
		final DecimalFormatSymbols symbols = numberFormatter.getDecimalFormatSymbols();

		final char groupingSeparator = symbols.getGroupingSeparator();
		final char decimalSeparator = symbols.getDecimalSeparator();

		final StringBuffer buffer = createTemplateForDecimals(maxIntegerPlaces, groupingSeparator);

		final String decimalTemplate = createTemplateForFractions(maxDecimalPlaces, decimalSeparator);
		buffer.append(decimalTemplate);

		final String pattern = buffer.toString();
		return pattern;
	}

	private String createTemplateForFractions(final int maxDecimalPlaces, final char decimalSeparator)
	{
		final StringBuffer buffer = new StringBuffer();
		if (maxDecimalPlaces > 0)
		{
			buffer.append(decimalSeparator);
		}
		for (int ii = 0; ii < maxDecimalPlaces; ++ii)
		{
			buffer.append(PATTERN_CHAR);
		}

		return buffer.toString();
	}

	private StringBuffer createTemplateForDecimals(final int maxIntegerPlaces, final char groupingSeparator)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < maxIntegerPlaces; i++)
		{
			if ((i % 3) == 0 && i > 0)
			{
				buffer.append(groupingSeparator);
			}

			buffer.append(PATTERN_CHAR);
		}

		buffer = buffer.reverse();
		return buffer;
	}

	private int countDecimals(final BigDecimal numberToCheck)
	{
		String plainString = numberToCheck.toPlainString();
		plainString = plainString.replaceFirst("-", "");


		int pos = plainString.indexOf(DEFAULT_DECIMAL_SEPARATOR);
		final boolean separatorNotFound = pos == -1;
		if (separatorNotFound)
		{
			pos = plainString.length();
		}

		return pos;
	}

	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}
}
