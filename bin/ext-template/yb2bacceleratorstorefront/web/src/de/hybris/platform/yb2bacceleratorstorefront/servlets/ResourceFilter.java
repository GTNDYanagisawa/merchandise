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
package de.hybris.platform.yb2bacceleratorstorefront.servlets;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * Filter which allows us to bypass all of the spring filters for requests to a given path.
 */
public class ResourceFilter implements Filter
{
	private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
	private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";
	private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";
	private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";
	private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";

	private RequestDispatcher defaultRequestDispatcher;

	protected RequestDispatcher getDefaultRequestDispatcher()
	{
		return defaultRequestDispatcher;
	}

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException
	{
		final ServletContext servletContext = filterConfig.getServletContext();
		
		if (servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null)
		{
			this.defaultRequestDispatcher = servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME);
		}
		else if (servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null)
		{
			this.defaultRequestDispatcher = servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME);
		}
		else if (servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null)
		{
			this.defaultRequestDispatcher = servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME);
		}
		else if (servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null)
		{
			this.defaultRequestDispatcher = servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME);
		}
		else if (servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null)
		{
			this.defaultRequestDispatcher = servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME);
		}
		else
		{
			throw new IllegalStateException(
					"Unable to locate the default servlet for serving static content. Please set the 'defaultServletName' property explicitly.");
		}
	}

	@Override
	public void destroy()
	{
		// No implementation
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		getDefaultRequestDispatcher().forward(request, response);
	}
}