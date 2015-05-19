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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.sap.productconfig.frontend.validator.ConflictError;
import de.hybris.platform.sap.productconfig.frontend.validator.MandatoryFieldError;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;


public class ErrorResolver
{
	private static boolean isWarningMessage(final ObjectError objError)
	{
		return objError instanceof ConflictError || objError instanceof MandatoryFieldError;
	}

	private static ErrorMessage createErrorMessage(final ObjectError objError)
	{
		final String path = ((FieldError) objError).getField();
		final String defaultErrorMessage = objError.getDefaultMessage();
		final String code = objError.getCode();
		final Object[] args = objError.getArguments();

		final ErrorMessage errorMessage = new ErrorMessage();

		errorMessage.setPath(path);
		errorMessage.setMessage(defaultErrorMessage);
		errorMessage.setCode(code);
		errorMessage.setArgs(args);

		return errorMessage;
	}

	private static ErrorMessage createErrorMessageWithFieldName(final MandatoryFieldError objError)
	{
		final String path = ((FieldError) objError).getField();
		final String defaultErrorMessage = objError.getDefaultMessage();
		final String code = objError.getCodes()[0];
		final Object[] args =
		{ objError.getCstic().getLangdepname() };

		final ErrorMessage errorMessage = new ErrorMessage();

		errorMessage.setPath(path);
		errorMessage.setMessage(defaultErrorMessage);
		errorMessage.setCode(code);
		errorMessage.setArgs(args);

		return errorMessage;
	}

	public static List<ErrorMessage> getConflictErrors(final BindingResult bindResult)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors();
		final List<ErrorMessage> messages = new ArrayList<>();

		for (final FieldError objError : objErrors)
		{
			if (!(objError instanceof ConflictError))
			{
				continue;
			}

			final ErrorMessage errorMessage = createErrorMessage(objError);
			messages.add(errorMessage);
		}
		return messages;
	}

	public static List<ErrorMessage> getMandatoryFieldErrors(final BindingResult bindResult)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors();
		final List<ErrorMessage> messages = new ArrayList<>();

		for (final FieldError objError : objErrors)
		{
			if (!(objError instanceof MandatoryFieldError))
			{
				continue;
			}

			final ErrorMessage errorMessage = createErrorMessageWithFieldName((MandatoryFieldError) objError);
			messages.add(errorMessage);
		}
		return messages;
	}

	public static List<ErrorMessage> getWarnings(final BindingResult bindResult)
	{
		final List<ErrorMessage> messages = new ArrayList<>();
		messages.addAll(getConflictErrors(bindResult));
		messages.addAll(getMandatoryFieldErrors(bindResult));
		return messages;
	}



	public static List<ErrorMessage> getValidationErrors(final BindingResult bindResult)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors();
		final List<ErrorMessage> messages = new ArrayList<>();

		for (final FieldError objError : objErrors)
		{
			if (isWarningMessage(objError))
			{
				continue;
			}

			final ErrorMessage errorMessage = createErrorMessage(objError);
			messages.add(errorMessage);
		}
		return messages;
	}

	public static List<ErrorMessage> getWarningsForCstic(final BindingResult bindResult, final String path)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors(path);
		final List<ErrorMessage> messages = new ArrayList<>();
		for (final FieldError objError : objErrors)
		{
			if (!isWarningMessage(objError))
			{
				continue;
			}

			final ErrorMessage errorMessage = createErrorMessage(objError);
			messages.add(errorMessage);
		}
		return messages;
	}

	public static List<ErrorMessage> getValidationErrorsForCstic(final BindingResult bindResult, final String path)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors(path);
		final List<ErrorMessage> messages = new ArrayList<>();

		for (final FieldError objError : objErrors)
		{
			if (isWarningMessage(objError))
			{
				continue;
			}

			final ErrorMessage errorMessage = createErrorMessage(objError);
			messages.add(errorMessage);
		}
		return messages;
	}

	public static List<ErrorMessage> getWarningsForGroup(final BindingResult bindResult, final String path)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors();
		final List<ErrorMessage> messages = new ArrayList<>();

		for (final FieldError objError : objErrors)
		{
			if (!isWarningMessage(objError))
			{
				continue;
			}

			if (!isCorrectGroup(path, objError))
			{
				continue;
			}

			ErrorMessage errorMessage;
			if (objError instanceof MandatoryFieldError)
			{
				errorMessage = createErrorMessageWithFieldName((MandatoryFieldError) objError);
			}
			else
			{
				errorMessage = createErrorMessage(objError);
			}

			messages.add(errorMessage);
		}
		return messages;
	}

	private static boolean isCorrectGroup(final String path, final FieldError objError)
	{
		final String fieldName = objError.getField();

		if (!fieldName.startsWith(path))
		{
			return false;
		}

		if (fieldName.contains("subGroups"))
		{
			final int lastSubGroup = fieldName.lastIndexOf("subGroups") + 9;
			if (path.length() < lastSubGroup)
			{
				return false;
			}

			if (fieldName.substring(0, lastSubGroup).equals(path.substring(0, lastSubGroup)))
			{
				return true;
			}

			return false;
		}

		return true;
	}

	public static List<ErrorMessage> getErrorsForGroup(final BindingResult bindResult, final String path)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors();
		final List<ErrorMessage> messages = new ArrayList<>();

		for (final FieldError objError : objErrors)
		{
			if (isWarningMessage(objError))
			{
				continue;
			}

			if (!isCorrectGroup(path, objError))
			{
				continue;
			}

			final ErrorMessage errorMessage = createErrorMessage(objError);
			messages.add(errorMessage);
		}
		return messages;
	}

	public static boolean hasErrorMessages(final BindingResult bindResult)
	{
		final List<FieldError> objErrors = bindResult.getFieldErrors();

		for (final FieldError objError : objErrors)
		{
			if (isWarningMessage(objError))
			{
				continue;
			}

			return true;
		}

		return false;
	}
}
