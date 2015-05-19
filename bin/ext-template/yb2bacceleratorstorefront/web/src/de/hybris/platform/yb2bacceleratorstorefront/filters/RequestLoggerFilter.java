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
package de.hybris.platform.yb2bacceleratorstorefront.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;


/**
 * A filter that logs each request.
 */
public class RequestLoggerFilter extends GenericFilterBean
{
	private static final Logger LOG = Logger.getLogger(RequestLoggerFilter.class.getName());


	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException
	{
		if (servletRequest instanceof HttpServletRequest && LOG.isDebugEnabled())
		{
			final String requestDetails = buildRequestDetails((HttpServletRequest) servletRequest);

			if (LOG.isDebugEnabled())
			{
				LOG.debug(requestDetails + "Begin");
			}

			logCookies((HttpServletRequest) servletRequest);

			ServletResponse response = servletResponse;
			if (response instanceof HttpServletResponse)
			{
				response = new ResponseWrapper((HttpServletResponse) response);
			}

			final long startTime = System.currentTimeMillis();
			try
			{
				filterChain.doFilter(servletRequest, response);
			}
			finally
			{
				final long endTime = System.currentTimeMillis();
				final long duration = endTime - startTime;

				int status = 0;
				if (response instanceof ResponseWrapper)
				{
					status = ((ResponseWrapper) response).getStatus();
				}

				if (status != 0)
				{
					LOG.debug(requestDetails + duration + " ms (" + status + ")");
				}
				else
				{
					LOG.debug(requestDetails + duration + " ms");
				}
			}

			return;
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	protected void logCookies(final HttpServletRequest httpRequest)
	{
		if (LOG.isDebugEnabled())
		{
			final Cookie[] cookies = httpRequest.getCookies();
			if (cookies != null)
			{
				for (final Cookie cookie : cookies)
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("COOKIE Name: [" + cookie.getName() + "] Path: [" + cookie.getPath() + "] Value: ["
								+ cookie.getValue() + "]");
					}
				}
			}
		}
	}


	protected String buildRequestDetails(final HttpServletRequest request)
	{
		String queryString = request.getQueryString();
		if (queryString == null)
		{
			queryString = "";
		}

		final String requestUri = request.getRequestURI();

		final String securePrefix = request.isSecure() ? "s" : " ";
		final String methodPrefix = request.getMethod().substring(0, 1);

		return securePrefix + methodPrefix + " [" + requestUri + "] [" + queryString + "] ";
	}

	protected static class ResponseWrapper extends HttpServletResponseWrapper
	{
		private int status;

		public ResponseWrapper(final HttpServletResponse response)
		{
			super(response);
		}

		@Override
		public void setStatus(final int status)
		{
			super.setStatus(status);
			this.status = status;
		}

		public int getStatus()
		{
			return status;
		}

		@Override
		public void sendError(final int status, final String msg) throws IOException
		{
			super.sendError(status, msg);
			this.status = status;
		}

		@Override
		public void sendError(final int status) throws IOException
		{
			super.sendError(status);
			this.status = status;
		}
	}
}
