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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Implementation of {@link org.springframework.validation.Validator} that validates one specific string property
 * specified by {@link #fieldPath} property in any object if it is not null and not blank
 * 
 */
public class FieldNotEmptyValidator implements Validator
{
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

	private String fieldPath;

	@Override
	public boolean supports(final Class clazz)
	{
		return true;
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		Assert.notNull(errors, "Errors object must not be null");
		final Object fieldValue = errors.getFieldValue(fieldPath);

		if (fieldValue == null || (fieldValue instanceof String && StringUtils.isBlank((String) fieldValue)))
		{
			errors.rejectValue(fieldPath, FIELD_REQUIRED_MESSAGE_ID, new String[]
			{ fieldPath }, null);
		}
	}

	public String getFieldPath()
	{
		return fieldPath;
	}

	@Required
	public void setFieldPath(final String fieldPath)
	{
		this.fieldPath = fieldPath;
	}
}
