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

import de.hybris.platform.commercewebservicescommons.errors.factory.WebserviceErrorFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;


/**
 * Base class for {@link org.springframework.web.servlet.HandlerExceptionResolver} that converts objects using provided
 * list of {@link org.springframework.http.converter.HttpMessageConverter}
 */
public abstract class AbstractRestHandlerExceptionResolver extends AbstractHandlerExceptionResolver
{
	private HttpMessageConverter<?>[] messageConverters;
	private WebserviceErrorFactory webserviceErrorFactory;

	/**
	 * @return the messageConverters
	 */
	public HttpMessageConverter<?>[] getMessageConverters()
	{
		return messageConverters;
	}

	@Required
	public void setMessageConverters(final HttpMessageConverter<?>[] messageConverters)
	{
		this.messageConverters = messageConverters;
	}

	protected ModelAndView writeWithMessageConverters(final Object returnValue, final HttpInputMessage inputMessage,
			final HttpOutputMessage outputMessage) throws ServletException, IOException
	{
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty())
		{
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		final Class<?> returnValueType = returnValue.getClass();
		if (this.messageConverters != null)
		{
			for (final MediaType acceptedMediaType : acceptedMediaTypes)
			{
				for (final HttpMessageConverter messageConverter : this.messageConverters)
				{
					if (messageConverter.canWrite(returnValueType, acceptedMediaType))
					{
						messageConverter.write(returnValue, acceptedMediaType, outputMessage);
						return new ModelAndView();
					}
				}
			}
		}
		if (logger.isWarnEnabled())
		{
			logger.warn("Could not find HttpMessageConverter that supports return type [" + returnValueType + "] and "
					+ acceptedMediaTypes);
		}
		return null;
	}

	protected WebserviceErrorFactory getWebserviceErrorFactory()
	{
		return webserviceErrorFactory;
	}

	@Required
	public void setWebserviceErrorFactory(WebserviceErrorFactory webserviceErrorFactory)
	{
		this.webserviceErrorFactory = webserviceErrorFactory;
	}
}
