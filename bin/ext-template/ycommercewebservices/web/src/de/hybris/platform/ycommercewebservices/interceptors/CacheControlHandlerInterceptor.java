/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.ycommercewebservices.interceptors;

import de.hybris.platform.commercewebservicescommons.cache.CacheControl;
import de.hybris.platform.commercewebservicescommons.cache.CacheControlDirective;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Handler interceptor that adds a Cache-Control header to the response based on @link{CacheControl} annotation.
 */
public class CacheControlHandlerInterceptor extends HandlerInterceptorAdapter
{
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";

	@Override
	public final boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		if (isEligibleForCacheControl(request, response, handler))
		{
			final CacheControl cacheAnnotation = getCacheControlAnnotation(request, response, handler);
			if (cacheAnnotation != null)
			{
				response.setHeader(HEADER_CACHE_CONTROL, createCacheControlHeaderField(cacheAnnotation));
			}
		}

		return super.preHandle(request, response, handler);
	}

	/**
	 * Indicates whether the given request and response are eligible for Cache-Control generation.
	 * 
	 * @param request
	 *           the HTTP request
	 * @param response
	 *           the HTTP response
	 * @param handler
	 *           handler (or {@link HandlerMethod})
	 * @return {@code true} if eligible; {@code false} otherwise
	 */
	protected boolean isEligibleForCacheControl(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler)
	{
		return HttpMethod.GET.name().equals(request.getMethod()) || HttpMethod.HEAD.name().equals(request.getMethod());
	}

	protected CacheControl getCacheControlAnnotation(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler)
	{
		if (handler == null || !(handler instanceof HandlerMethod))
		{
			return null;
		}

		final HandlerMethod handlerMethod = (HandlerMethod) handler;
		CacheControl cacheAnnotation = handlerMethod.getMethodAnnotation(CacheControl.class);

		if (cacheAnnotation == null)
		{
			cacheAnnotation = handlerMethod.getBeanType().getAnnotation(CacheControl.class);
		}

		return cacheAnnotation;
	}

	protected String createCacheControlHeaderField(final CacheControl cacheAnnotation)
	{
		final CacheControlDirective[] directives = cacheAnnotation.directive();
		final StringBuilder cacheHeader = new StringBuilder();

		if (directives != null)
		{
			for (final CacheControlDirective directive : directives)
			{
				if (cacheHeader.length() > 0)
				{
					cacheHeader.append(", ");
				}
				cacheHeader.append(directive.toString());
			}
		}

		if (cacheAnnotation.maxAge() >= 0)
		{
			if (cacheHeader.length() > 0)
			{
				cacheHeader.append(", ");
			}
			cacheHeader.append("max-age=").append(cacheAnnotation.maxAge());
		}

		if (cacheAnnotation.sMaxAge() >= 0)
		{
			if (cacheHeader.length() > 0)
			{
				cacheHeader.append(", ");
			}
			cacheHeader.append("s-maxage=").append(cacheAnnotation.sMaxAge());
		}

		return cacheHeader.toString();
	}
}
