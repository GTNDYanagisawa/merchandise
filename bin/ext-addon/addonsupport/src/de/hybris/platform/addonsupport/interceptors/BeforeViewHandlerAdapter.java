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
package de.hybris.platform.addonsupport.interceptors;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;


public class BeforeViewHandlerAdapter implements FactoryBean<Object>, InitializingBean
{

	private Method getModelMapMethod;
	private Method getViewNameMethod;
	private Method setViewNameMethod;

	private BeforeViewHandlerAdaptee adaptee;

	private String proxyInterface;
	private Class<?> proxyObjectType;

	@Override
	public Object getObject() throws Exception
	{

		final ProxyFactory factory = new ProxyFactory(proxyObjectType, new org.aopalliance.intercept.MethodInterceptor()
		{
			@Override
			public Object invoke(final org.aopalliance.intercept.MethodInvocation arg0) throws Throwable

			{
				final ModelMap modelMap = (ModelMap) ReflectionUtils.invokeMethod(getModelMapMethod, arg0.getArguments()[2]);
				final String view = (String) ReflectionUtils.invokeMethod(getViewNameMethod, arg0.getArguments()[2]);
				final Object result = getAdaptee().beforeView((HttpServletRequest) arg0.getArguments()[0],
						(HttpServletResponse) arg0.getArguments()[1], modelMap, view);

				if (result != view)
				{
					ReflectionUtils.invokeMethod(setViewNameMethod, arg0.getArguments()[2], result);
				}
				return null;
			}
		});
		return factory.getProxy();
	}


	@Override
	public Class<?> getObjectType()
	{
		return this.proxyObjectType;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

	protected void init() throws ClassNotFoundException
	{
		this.proxyObjectType = ClassUtils.resolveClassName(getProxyInterfaceName(), ClassUtils.getDefaultClassLoader());
		final Class<?> modelParameterClazz = ClassUtils.resolveClassName("org.springframework.web.servlet.ModelAndView",
				ClassUtils.getDefaultClassLoader());
		this.getModelMapMethod = ReflectionUtils.findMethod(modelParameterClazz, "getModelMap");
		this.getViewNameMethod = ReflectionUtils.findMethod(modelParameterClazz, "getViewName");
		this.setViewNameMethod = ReflectionUtils.findMethod(modelParameterClazz, "setViewName", String.class);

	}

	/**
	 * @return the adaptee
	 */
	public BeforeViewHandlerAdaptee getAdaptee()
	{
		return adaptee;
	}

	/**
	 * @param adaptee
	 *           the adaptee to set
	 */
	@Required
	public void setAdaptee(final BeforeViewHandlerAdaptee adaptee)
	{
		this.adaptee = adaptee;
	}


	/**
	 * @return the interfaceClassName
	 */
	public String getProxyInterfaceName()
	{
		return proxyInterface;
	}


	/**
	 * @param interfaceClassName
	 *           the interfaceClassName to set
	 */
	@Required
	public void setProxyInterfaceName(final String interfaceClassName)
	{
		this.proxyInterface = interfaceClassName;
	}


	@Override
	public void afterPropertiesSet() throws Exception
	{
		init();
	}

}