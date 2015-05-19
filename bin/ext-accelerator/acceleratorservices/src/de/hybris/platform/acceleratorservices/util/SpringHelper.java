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
package de.hybris.platform.acceleratorservices.util;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Static class used to lookup a spring bean by name and type.
 */
public final class SpringHelper
{
	private SpringHelper()
	{
		// Empty private constructor
	}

	/**
	 * Returns the Spring bean with name <code>beanName</code> and of type <code>beanClass</code>. If the cacheInRequest
	 * flag is set to true then the bean is cached in the request attributes.
	 * 
	 * @param <T>
	 *           type of the bean
	 * @param request
	 *           the http request
	 * @param beanName
	 *           name of the bean
	 * @param beanClass
	 *           expected type of the bean
	 * @param cacheInRequest
	 *           flag, set to true to use the request attributes to cache the spring bean
	 * @return the bean matching the given arguments or <code>null</code> if no bean could be resolved
	 */
	public static <T> T getSpringBean(final ServletRequest request, final String beanName, final Class<T> beanClass,
			final boolean cacheInRequest)
	{
		validateParameterNotNull(request, "Parameter request must not be null");
		validateParameterNotNull(beanName, "Parameter beanName must not be null");
		validateParameterNotNull(beanClass, "Parameter beanClass must not be null");

		final String cacheBeanKey = SpringHelper.class.getName() + ".bean." + beanName;

		if (cacheInRequest)
		{
			// Lookup the bean by name in the request attributes
			final Object cachedBean = request.getAttribute(cacheBeanKey);
			if (cachedBean != null && beanClass.isInstance(cachedBean))
			{
				return (T) cachedBean;
			}
		}

		if (request instanceof HttpServletRequest)
		{
			// Lookup the bean in the current bean factory
			final HttpSession session = ((HttpServletRequest) request).getSession();
			final ServletContext servletContext = session.getServletContext();
			final WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

			T result = null;
			try
			{
				result = appContext.getBean(beanName, beanClass);
			}
			catch (final NoSuchBeanDefinitionException ignore)
			{
				// Ignore
			}

			if (cacheInRequest && result != null)
			{
				// Cache the bean in the request attributes
				request.setAttribute(cacheBeanKey, result);
			}
			return result;
		}
		return null;
	}
}
