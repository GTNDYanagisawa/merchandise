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
package de.hybris.platform.ychinaaccelerator.storefront.forms.validation;

import de.hybris.platform.ychinaaccelerator.storefront.forms.UpdateMobileNumberForm;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * MobileNumberValidator
 */
@Component("mobileNumberValidator")
public class MobileNumberValidator implements Validator
{
	public static final String MOBILE_REGEX = "\\d{11}";

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return UpdateMobileNumberForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final UpdateMobileNumberForm updateMobileNumberForm = (UpdateMobileNumberForm) object;
		final String mobileNumber = updateMobileNumberForm.getMobileNumber();
		final String password = updateMobileNumberForm.getPassword();

		if (!StringUtils.isEmpty(mobileNumber) && !Pattern.matches(MOBILE_REGEX, mobileNumber))
		{
			errors.rejectValue("mobileNumber", "profile.mobileNumber.invalid");
		}

		if (StringUtils.isEmpty(password))
		{
			errors.rejectValue("password", "profile.pwd.invalid");
		}
	}
}
