package de.hybris.platform.ycommercewebservices.validator;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates one specific string property specified by {@link #fieldPath} in any object if it is valid email address
 */
public class EmailValidator implements Validator
{
	private static final String INVALID_EMAIL_MESSAGE_ID = "field.invalidEmail";
	private String fieldPath;

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return true;
	}

	@Override
	public void validate(final Object o, final Errors errors)
	{
		Assert.notNull(errors, "Errors object must not be null");
		final String fieldValue = (String) errors.getFieldValue(fieldPath);

		if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(fieldValue))
		{
			errors.rejectValue(fieldPath, INVALID_EMAIL_MESSAGE_ID, new String[]
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
