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
package de.hybris.platform.acceleratorservices.web.payment.validation;


import de.hybris.platform.acceleratorservices.web.payment.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorservices.web.payment.forms.SopPaymentDetailsForm;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component("sopPaymentDetailsValidator")
public class SopPaymentDetailsValidator implements Validator
{
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return PaymentDetailsForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final SopPaymentDetailsForm form = (SopPaymentDetailsForm) object;

		final Calendar startOfCurrentMonth = getCalendarResetTime();
		startOfCurrentMonth.set(Calendar.DAY_OF_MONTH, 1);

		final Calendar startOfNextMonth = getCalendarResetTime();
		startOfNextMonth.set(Calendar.DAY_OF_MONTH, 1);
		startOfNextMonth.add(Calendar.MONTH, 1);

		final Calendar start = parseDate(form.getCard_startMonth(), form.getCard_startYear());
		final Calendar expiration = parseDate(form.getCard_expirationMonth(), form.getCard_expirationYear());

		if (start != null && !start.before(startOfNextMonth))
		{
			errors.rejectValue("card_startMonth", "payment.startDate.invalid");
		}
		if (expiration != null && expiration.before(startOfCurrentMonth))
		{
			errors.rejectValue("card_expirationMonth", "payment.startDate.invalid");
		}
		if (start != null && expiration != null && start.after(expiration))
		{
			errors.rejectValue("card_startMonth", "payment.startDate.invalid");
		}

		if (StringUtils.isBlank(form.getBillTo_country()))
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_country", "address.country.invalid");
		}
		else
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_firstName", "address.firstName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_lastName", "address.lastName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_street1", "address.line1.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_city", "address.city.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_postalCode", "address.postcode.invalid");
		}
	}

	protected Calendar parseDate(final String month, final String year)
	{
		if (StringUtils.isNotBlank(month) && StringUtils.isNotBlank(year))
		{
			final Integer yearInt = getIntegerForString(year);
			final Integer monthInt = getIntegerForString(month);

			if (yearInt != null && monthInt != null)
			{
				final Calendar date = getCalendarResetTime();
				date.set(Calendar.YEAR, yearInt.intValue());
				date.set(Calendar.MONTH, monthInt.intValue() - 1);
				date.set(Calendar.DAY_OF_MONTH, 1);
				return date;
			}
		}
		return null;
	}

	protected Calendar getCalendarResetTime()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	/**
	 * Common method to convert a String to an Integer.
	 * 
	 * @param value
	 *           - the String value to be converted.
	 * @return - an Integer object.
	 */
	protected Integer getIntegerForString(final String value)
	{
		if (value != null && !value.isEmpty())
		{
			try
			{
				return Integer.valueOf(value);
			}
			catch (final Exception ignore)
			{
				// Ignore
			}
		}

		return null;
	}
}
