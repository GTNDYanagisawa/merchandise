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
 * specified by {@link #fieldPath} property in any object if it is not null, not blank and shorter than maxLength
 * 
 */
public class FieldNotEmptyOrTooLongValidator implements Validator
{
	private static final String FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID = "field.requiredAndNotTooLong";

	private String fieldPath;
	private int maxLength;
	private String errorMessageId;

	@Override
	public boolean supports(final Class clazz)
	{
		return true;
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		Assert.notNull(errors, "Errors object must not be null");
		final String fieldValue = (String) errors.getFieldValue(fieldPath);
		final String resultErrorMessageId = errorMessageId != null ? errorMessageId : FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID;

		if (StringUtils.isBlank(fieldValue) || StringUtils.length(fieldValue) > maxLength)
		{
			errors.rejectValue(fieldPath, resultErrorMessageId, new String[]
			{ String.valueOf(maxLength) }, null);
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


	public int getMaxLength()
	{
		return maxLength;
	}

	@Required
	public void setMaxLength(final int maxLength)
	{
		this.maxLength = maxLength;
	}

	public String getErrorMessageId()
	{
		return errorMessageId;
	}

	public void setErrorMessageId(final String errorMessageId)
	{
		this.errorMessageId = errorMessageId;
	}
}
