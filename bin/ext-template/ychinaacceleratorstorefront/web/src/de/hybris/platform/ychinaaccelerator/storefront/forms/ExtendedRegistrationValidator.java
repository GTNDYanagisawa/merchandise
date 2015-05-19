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
package de.hybris.platform.ychinaaccelerator.storefront.forms;


import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.RegistrationValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


/**
 * Validates registration forms.
 */
@Component("extendedRegistrationValidator")
public class ExtendedRegistrationValidator extends RegistrationValidator
{	
	public static final String MOBILE_REGEX = "\\d{11}";

	@Override
	public void validate(final Object object, final Errors errors)
	{
		super.validate(object, errors);

		final RegisterForm registerForm = (RegisterForm) object;
		final String mobileNumber = registerForm.getMobileNumber();
			
		if (!StringUtils.isBlank(mobileNumber) && !isMobileNumberValid(mobileNumber))
		{
			errors.rejectValue("mobileNumber", "register.mobileNumber.invalid");
		}									
	}

	public boolean isMobileNumberValid(final String mobileNumber){
		final Pattern pattern = Pattern.compile(MOBILE_REGEX);
		final Matcher matcher = pattern.matcher(mobileNumber);
		return matcher.matches();
	}
}
