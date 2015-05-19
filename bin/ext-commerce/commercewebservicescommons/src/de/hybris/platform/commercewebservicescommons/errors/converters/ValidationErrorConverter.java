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

package de.hybris.platform.commercewebservicescommons.errors.converters;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;


/**
 * Converts Spring's {@link Errors} to a list of {@link de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO}.
 */
public class ValidationErrorConverter extends AbstractLocalizedErrorConverter
{
	private static final String TYPE = "ValidationError";
	private static final String SUBJECT_TYPE = "parameter";
	private static final String REASON_INVALID = "invalid";
	private static final String REASON_MISSING = "missing";
	private CommerceCommonI18NService commerceCommonI18NService;

	@Override
	public boolean supports(final Class clazz)
	{
		return Errors.class.isAssignableFrom(clazz);
	}

	@Override
	public void populate(final Object o, final List<ErrorWsDTO> webserviceErrorList)
	{
		final Errors errors = (Errors) o;
		final Locale currentLocale = commerceCommonI18NService.getCurrentLocale();
		for (final ObjectError error : errors.getGlobalErrors())
		{
			ErrorWsDTO errorDto = createTargetElement();
			errorDto.setType(TYPE);
			errorDto.setSubjectType(SUBJECT_TYPE);
			errorDto.setMessage(getMessage(error.getCode(), error.getArguments(), currentLocale));
			errorDto.setSubject(error.getObjectName());
			errorDto.setReason(REASON_INVALID);
			webserviceErrorList.add(errorDto);
		}
		for (final FieldError error : errors.getFieldErrors())
		{
			ErrorWsDTO errorDto = createTargetElement();
			errorDto.setType(TYPE);
			errorDto.setSubjectType(SUBJECT_TYPE);
			errorDto.setMessage(getMessage(error.getCode(), error.getArguments(), currentLocale));
			errorDto.setSubject(error.getField());
			errorDto.setReason(error.getRejectedValue() == null ? REASON_MISSING : REASON_INVALID);
			webserviceErrorList.add(errorDto);
		}
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}
}
