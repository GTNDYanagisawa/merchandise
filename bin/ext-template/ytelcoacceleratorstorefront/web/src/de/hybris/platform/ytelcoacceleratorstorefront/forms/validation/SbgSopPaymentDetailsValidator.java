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
package de.hybris.platform.ytelcoacceleratorstorefront.forms.validation;

import de.hybris.platform.ytelcoacceleratorstorefront.forms.SopPaymentDetailsForm;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component("sbgSopPaymentDetailsValidator")
public class SbgSopPaymentDetailsValidator implements Validator
{
	private static final Logger LOG = Logger.getLogger(SbgSopPaymentDetailsValidator.class);

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return SopPaymentDetailsForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final SopPaymentDetailsForm form = (SopPaymentDetailsForm) object;

		final Calendar start = parseDate(form.getStartMonth(), form.getStartYear());
		final Calendar expiration = parseDate(form.getExpiryMonth(), form.getExpiryYear());
		final Calendar current = Calendar.getInstance();

		if (start != null)
		{
			if (start.after(current))
			{
				errors.rejectValue("startMonth", "payment.startDate.past.invalid");
			}

			if (expiration != null)
			{
				if (start.after(expiration))
				{
					errors.rejectValue("startMonth", "payment.startDate.invalid");
				}
			}
		}

		if (expiration != null)
		{
			if (expiration.before(current))
			{
				errors.rejectValue("expiryMonth", "payment.expiryDate.future.invalid");
			}
		}

		if (Boolean.TRUE.equals(form.getNewBillingAddress()))
		{
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.titleCode", "address.title.invalid");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.firstName", "address.firstName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.lastName", "address.lastName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.line1", "address.line1.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.townCity", "address.townCity.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.postcode", "address.postcode.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.countryIso", "address.country.invalid");
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
		if (StringUtils.isNotEmpty(value))
		{
			try
			{
				return Integer.valueOf(value);
			}
			catch (final Exception e)
			{
				LOG.warn(String.format("String value '%s' cannot be converted to Integer", value), e);
			}
		}

		return null;
	}
}
