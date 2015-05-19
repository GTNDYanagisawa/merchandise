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

package de.hybris.platform.ycommercewebservices.resolver;

import de.hybris.platform.commercewebservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.WebserviceValidationException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * Exception resolver implementation for ycommercewebservices extension.
 */
public class RestHandlerExceptionResolver extends AbstractRestHandlerExceptionResolver
{
	private static final int DEFAULT_ORDER = 0;
	private static final int DEFAULT_STATUS_CODE = 400;
	private Map<String, Integer> statusCodeMappings;

	public RestHandlerExceptionResolver()
	{
		setOrder(DEFAULT_ORDER);
	}

	@Required
	public void setStatusCodeMappings(final Map<String, Integer> statusCodeMappings)
	{
		this.statusCodeMappings = statusCodeMappings;
	}

	@Override
	protected ModelAndView doResolveException(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final Exception ex)
	{
		if (statusCodeMappings.containsKey(ex.getClass().getSimpleName()))
		{
			response.setStatus(statusCodeMappings.get(ex.getClass().getSimpleName()).intValue());
		}
		else
		{
			response.setStatus(DEFAULT_STATUS_CODE);
		}
		logger.info("Translating exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
		logger.error(ex.toString());
		ex.printStackTrace();

		final List<ErrorWsDTO> errorList;
		if (ex instanceof WebserviceValidationException)
		{
			errorList = getWebserviceErrorFactory().createErrorList(((WebserviceValidationException) ex).getValidationObject());
		}
		else
		{
			errorList = getWebserviceErrorFactory().createErrorList(ex);
		}
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		errorListDto.setErrors(errorList);

		final ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
		final ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);

		try
		{
			return writeWithMessageConverters(errorListDto, inputMessage, outputMessage);
		}
		catch (final Exception handlerException)
		{
			logger.error("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
		}
		return null;
	}
}
