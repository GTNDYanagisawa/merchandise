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
package de.hybris.platform.ytelcoacceleratorstorefront.util;

import de.hybris.platform.util.Config;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * A Spring MVC <code>HandlerInterceptor</code> which is responsible to enforce CSRF token validity on incoming posts
 * requests. The interceptor should be registered with Spring MVC servlet using the following syntax:
 * 
 * <pre>
 *   &lt;mvc:interceptors&gt;
 *        &lt;bean class="com.eyallupu.blog.springmvc.controller.csrf.CSRFHandlerInterceptor"/&gt;
 *   &lt;/mvc:interceptors&gt;
 * </pre>
 * 
 * @author Eyal Lupu
 * @see CSRFRequestDataValueProcessor
 * 
 */
public class CSRFHandlerInterceptor extends HandlerInterceptorAdapter
{
	private static final Logger LOG = Logger.getLogger(CSRFHandlerInterceptor.class);
	private static final String CSRF_ALLOWED_URLS = "csrf.allowed.url.patterns";

	private String loginUrl;
	private String loginAndCheckoutUrl;

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		if (shouldCheckCSRFTokenForRequest(request))
		{
			// This is a POST request - need to check the CSRF token
			final String sessionToken = CSRFTokenManager.getTokenForSession(request.getSession());
			final String requestToken = CSRFTokenManager.getTokenFromRequest(request);
			if (sessionToken.equals(requestToken))
			{
				return true;
			}
			else
			{
				final String redirectUrl = getRedirectUrl(request);
				request.getSession().invalidate();
				LOG.error("Bad or missing CSRF value; redirecting to " + redirectUrl);
				final String encodedRedirectUrl = response.encodeRedirectURL(request.getContextPath() + redirectUrl);
				response.sendRedirect(encodedRedirectUrl);
				return false;
			}
		}
		else
		{
			// Not a POST - allow the request
			return true;
		}
	}

	protected boolean shouldCheckCSRFTokenForRequest(final HttpServletRequest request)
	{
		return ("POST").equalsIgnoreCase(request.getMethod()) && !isCSRFExemptUrl(request.getServletPath());
	}

	protected boolean isCSRFExemptUrl(final String servletPath)
	{
		if (servletPath != null)
		{
			final String allowedUrlPatterns = Config.getParameter(CSRF_ALLOWED_URLS);
			final Set<String> allowedUrls = StringUtils.commaDelimitedListToSet(allowedUrlPatterns);
			for (final String pattern : allowedUrls)
			{
				if (servletPath.matches(pattern))
				{
					return true;
				}
			}
		}

		return false;
	}

	protected String getRedirectUrl(final HttpServletRequest request)
	{
		if (request != null && request.getServletPath().contains("checkout"))
		{
			return getLoginAndCheckoutUrl();
		}
		else
		{
			return getLoginUrl();
		}
	}

	protected String getLoginUrl()
	{
		return loginUrl;
	}

	@Required
	public void setLoginUrl(final String loginUrl)
	{
		this.loginUrl = loginUrl;
	}

	protected String getLoginAndCheckoutUrl()
	{
		return loginAndCheckoutUrl;
	}

	@Required
	public void setLoginAndCheckoutUrl(final String loginAndCheckoutUrl)
	{
		this.loginAndCheckoutUrl = loginAndCheckoutUrl;
	}

}