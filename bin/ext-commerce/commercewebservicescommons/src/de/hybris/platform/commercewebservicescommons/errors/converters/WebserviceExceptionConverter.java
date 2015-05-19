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

import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.WebserviceException;

import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Converts {@link WebserviceException} to a list of {@link de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO}.
 */
public class WebserviceExceptionConverter extends AbstractErrorConverter
{
	@Override
	public boolean supports(final Class clazz)
	{
		return WebserviceException.class.isAssignableFrom(clazz);
	}

	@Override
	public void populate(final Object o, final List<ErrorWsDTO> webserviceErrorList)
	{
		final WebserviceException ex = (WebserviceException) o;
		final ErrorWsDTO error = createTargetElement();

		if (StringUtils.isNotEmpty(ex.getSubject()))
		{
			error.setSubject(ex.getSubject());
			if (StringUtils.isNotEmpty(ex.getSubjectType()))
			{
				error.setSubjectType(ex.getSubjectType());
			}
		}
		if (StringUtils.isNotEmpty(ex.getReason()))
		{
			error.setReason(ex.getReason());
		}
		if (StringUtils.isNotBlank(ex.getType()))
		{
			error.setType(ex.getType());
		}
		error.setMessage(ex.getMessage());
		webserviceErrorList.add(error);
	}
}
