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

/**
 * 
 */
package de.hybris.platform.ycommercewebservices.resolver;

import de.hybris.platform.commercewebservicescommons.dto.error.ErrorListWsDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.web.servlet.ModelAndView;


/**
 * OAuth2ExceptionHandlerExceptionResolver is used to grab and handle
 * {@link org.springframework.security.oauth2.common.exceptions.OAuth2Exception} before
 * {@link org.springframework.security.oauth2.provider.endpoint.TokenEndpoint} handling. The main purpose is to convert
 * {@link org.springframework.security.oauth2.common.exceptions.OAuth2Exception} object to
 * {@link de.hybris.platform.commercewebservicescommons.dto.error.ErrorListWsDTO}.
 * <p>
 * Exceptions are translated in the same way as TokenEndpoint does - the
 * {@link org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator} is used for that purpose.
 */
public class OAuth2ExceptionHandlerExceptionResolver extends AbstractRestHandlerExceptionResolver
{
	private static final int DEFAULT_ORDER = -1;
	protected final Log logger = LogFactory.getLog(getClass());
	private WebResponseExceptionTranslator webResponseExceptionTranslator = new DefaultWebResponseExceptionTranslator();

	public OAuth2ExceptionHandlerExceptionResolver()
	{
		setOrder(DEFAULT_ORDER);
	}

	@Override
	protected ModelAndView doResolveException(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final Exception ex)
	{
		if (!OAuth2Exception.class.isAssignableFrom(ex.getClass()))
		{
			return null;
		}

		//TokenEndpoint exception handlers logic
		ResponseEntity<OAuth2Exception> responseEntity = null;
		final Exception exceptionToTranslate;
		if (ClientRegistrationException.class.isAssignableFrom(ex.getClass()))
		{
			exceptionToTranslate = new BadClientCredentialsException();
		}
		else
		{
			exceptionToTranslate = ex;
		}

		//Exception translation by WebResponseExceptionTranslator
		try
		{
			responseEntity = webResponseExceptionTranslator.translate(exceptionToTranslate);
		}
		catch (final Exception e)
		{
			logger.error("Translating of [" + exceptionToTranslate.getClass().getName() + "] resulted in Exception", e);
			return null;
		}

		final ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
		final ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);

		//get translated headers
		outputMessage.getHeaders().putAll(responseEntity.getHeaders());

		//set status
		outputMessage.setStatusCode(responseEntity.getStatusCode());

		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		errorListDto.setErrors(getWebserviceErrorFactory().createErrorList(ex));

		try
		{
			return writeWithMessageConverters(errorListDto, inputMessage, outputMessage);
		}
		catch (final Exception handlerException)
		{
			logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
		}
		return null;
	}

	/**
	 * @return the webResponseExceptionTranslator
	 */
	public WebResponseExceptionTranslator getWebResponseExceptionTranslator()
	{
		return webResponseExceptionTranslator;
	}

	/**
	 * @param webResponseExceptionTranslator
	 *           the webResponseExceptionTranslator to set
	 */
	public void setWebResponseExceptionTranslator(final WebResponseExceptionTranslator webResponseExceptionTranslator)
	{
		this.webResponseExceptionTranslator = webResponseExceptionTranslator;
	}
}
