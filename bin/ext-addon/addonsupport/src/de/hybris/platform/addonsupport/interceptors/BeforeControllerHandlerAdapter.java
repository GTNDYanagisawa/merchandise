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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;


public class BeforeControllerHandlerAdapter implements FactoryBean<Object>, InitializingBean
{


	private String proxyInterface;
	private Class<?> proxyObjectType;

	private BeforeControllerHandlerAdaptee adaptee;


	@Override
	public Object getObject() throws Exception
	{
		final ProxyFactory factory = new ProxyFactory(getObjectType(), new org.aopalliance.intercept.MethodInterceptor()
		{
			public java.lang.Object invoke(final org.aopalliance.intercept.MethodInvocation arg0) throws java.lang.Throwable
			{
				return Boolean.valueOf(getAdaptee().beforeController((HttpServletRequest) arg0.getArguments()[0],
						(HttpServletResponse) arg0.getArguments()[1], (HandlerMethod) arg0.getArguments()[2]));
			}
		});
		return factory.getProxy();
	}

	@Override
	public Class<?> getObjectType()
	{
		return proxyObjectType;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

	@Required
	public void setProxyInterfaceName(final String interfaceName)
	{
		this.proxyInterface = interfaceName;
	}


	/**
	 * @return the interfaceClassName
	 */
	public String getProxyInterfaceName()
	{
		return proxyInterface;
	}

	/**
	 * @return the adaptee
	 */
	public BeforeControllerHandlerAdaptee getAdaptee()
	{
		return adaptee;
	}

	/**
	 * @param adaptee
	 *           the adaptee to set
	 */
	@Required
	public void setAdaptee(final BeforeControllerHandlerAdaptee adaptee)
	{
		this.adaptee = adaptee;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		this.proxyObjectType = ClassUtils.resolveClassName(getProxyInterfaceName(), ClassUtils.getDefaultClassLoader());

	}



}