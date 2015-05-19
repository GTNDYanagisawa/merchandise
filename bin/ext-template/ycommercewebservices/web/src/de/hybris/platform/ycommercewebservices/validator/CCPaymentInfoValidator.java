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
 */
package de.hybris.platform.ycommercewebservices.validator;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;

import java.util.Calendar;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates instances of {@link CCPaymentInfoData}.
 * 
 * @author KKW
 */
@Component("ccPaymentInfoValidator")
public class CCPaymentInfoValidator implements Validator
{
	@Resource(name = "paymentAddressValidator")
	private Validator paymentAddressValidator;

	@Override
	public boolean supports(final Class clazz)
	{
		return CCPaymentInfoData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final CCPaymentInfoData ccPaymentData = (CCPaymentInfoData) target;

		if (StringUtils.isNotBlank(ccPaymentData.getStartMonth()) && StringUtils.isNotBlank(ccPaymentData.getStartYear())
				&& StringUtils.isNotBlank(ccPaymentData.getExpiryMonth()) && StringUtils.isNotBlank(ccPaymentData.getExpiryYear()))
		{
			final Calendar start = Calendar.getInstance();
			start.set(Calendar.DAY_OF_MONTH, 0);
			start.set(Calendar.MONTH, Integer.parseInt(ccPaymentData.getStartMonth()) - 1);
			start.set(Calendar.YEAR, Integer.parseInt(ccPaymentData.getStartYear()) - 1);

			final Calendar expiration = Calendar.getInstance();
			expiration.set(Calendar.DAY_OF_MONTH, 0);
			expiration.set(Calendar.MONTH, Integer.parseInt(ccPaymentData.getExpiryMonth()) - 1);
			expiration.set(Calendar.YEAR, Integer.parseInt(ccPaymentData.getExpiryYear()) - 1);

			if (start.after(expiration))
			{
				errors.rejectValue("startMonth", "payment.startDate.invalid");
			}
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountHolderName", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardType", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardNumber", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiryMonth", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiryYear", "field.required");

		paymentAddressValidator.validate(ccPaymentData, errors);
	}
}
