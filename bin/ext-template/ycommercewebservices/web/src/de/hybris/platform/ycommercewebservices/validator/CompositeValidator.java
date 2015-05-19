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

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Implementation of {@link org.springframework.validation.Validator} that validates objects using composition of other
 * validators
 * 
 */
public class CompositeValidator implements Validator
{
	private Validator[] validators;

	@Override
	public boolean supports(final Class clazz)
	{
		for (final Validator v : validators)
		{
			if (v.supports(clazz))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		for (final Validator v : validators)
		{
			if (v.supports(object.getClass()))
			{
				v.validate(object, errors);
			}
		}
	}

	public Validator[] getValidators()
	{
		return validators;
	}

	@Required
	public void setValidators(final Validator[] validators)
	{
		this.validators = validators;
	}
}
