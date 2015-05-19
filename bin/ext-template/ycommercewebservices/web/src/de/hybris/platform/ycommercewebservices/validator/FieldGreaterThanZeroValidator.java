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

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Implementation of {@link org.springframework.validation.Validator} that validates one number property specified by
 * {@link #fieldPath} in any object if it is not null and greater than zero
 * 
 */
public class FieldGreaterThanZeroValidator implements Validator
{
	private static final String FIELD_GREATER_THAN_ZERO_MESSAGE_ID = "field.greaterThanZero";

	private String fieldPath;

	@Override
	public boolean supports(final Class clazz)
	{
		return true;
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		Assert.notNull(errors, "Errors object must not be null");
		final Object fieldValue = fieldPath == null ? target : errors.getFieldValue(fieldPath);

		if (fieldValue instanceof Number)
		{
			validateNumber((Number) fieldValue, fieldPath, errors);
		}
		else if (fieldValue instanceof String)
		{
			validateString((String) fieldValue, fieldPath, errors);
		}
		else
		{
			errors.rejectValue(fieldPath, FIELD_GREATER_THAN_ZERO_MESSAGE_ID, new String[]
			{ fieldPath }, null);
		}
	}

	private void validateNumber(final Number value, final String fieldPath, final Errors errors)
	{
		if (value.longValue() <= 0)
		{
			errors.rejectValue(fieldPath, FIELD_GREATER_THAN_ZERO_MESSAGE_ID, new String[]
			{ fieldPath }, null);
		}
	}

	private void validateString(final String value, final String fieldPath, final Errors errors)
	{
		try
		{
			if (Long.parseLong(value) <= 0)
			{
				errors.rejectValue(fieldPath, FIELD_GREATER_THAN_ZERO_MESSAGE_ID, new String[]
				{ fieldPath }, null);
			}
		}
		catch (final NumberFormatException e)
		{
			errors.rejectValue(fieldPath, FIELD_GREATER_THAN_ZERO_MESSAGE_ID, new String[]
			{ fieldPath }, null);
		}
	}

	public String getFieldPath()
	{
		return fieldPath;
	}

	public void setFieldPath(final String fieldPath)
	{
		this.fieldPath = fieldPath;
	}
}
