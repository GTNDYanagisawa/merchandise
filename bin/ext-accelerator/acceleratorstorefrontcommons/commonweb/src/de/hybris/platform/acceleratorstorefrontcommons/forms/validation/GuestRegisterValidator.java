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
package de.hybris.platform.acceleratorstorefrontcommons.forms.validation;

import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestRegisterForm;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates entries on Guest register user forms.
 */
@Component("guestRegisterValidator")
public class GuestRegisterValidator implements Validator
{

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return GuestRegisterForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final GuestRegisterForm guestRegisterForm = (GuestRegisterForm) object;
		final String newPasswd = guestRegisterForm.getPwd();
		final String checkPasswd = guestRegisterForm.getCheckPwd();

		if (StringUtils.isNotEmpty(newPasswd) && StringUtils.isNotEmpty(checkPasswd)
				&& !StringUtils.equals(newPasswd, checkPasswd))
		{
			errors.rejectValue("checkPwd", "validation.checkPwd.equals");
		}
		else
		{
			if (StringUtils.isEmpty(newPasswd))
			{
				errors.rejectValue("pwd", "register.pwd.invalid");
			}
			else if (StringUtils.length(newPasswd) < 6 || StringUtils.length(newPasswd) > 255)
			{
				errors.rejectValue("pwd", "register.pwd.invalid");
			}

			if (StringUtils.isEmpty(checkPasswd))
			{
				errors.rejectValue("checkPwd", "register.checkPwd.invalid");
			}
			else if (StringUtils.length(checkPasswd) < 6 || StringUtils.length(checkPasswd) > 255)
			{
				errors.rejectValue("checkPwd", "register.checkPwd.invalid");
			}
		}
	}
}
