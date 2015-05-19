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
package de.hybris.platform.sap.core.common.util;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;


/**
 * Default implementation of the GenericFactory.
 */
public class DefaultGenericFactory implements GenericFactory
{

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(final String name)
	{
		return (T) GenericFactoryProvider.getApplicationContext().getBean(name);

	}

	@Override
	public <T> T getBean(final Class<T> type)
	{
		return GenericFactoryProvider.getApplicationContext().getBean(type);
	}

	@Override
	public Object getBean(final String name, final Object... args)
	{
		return GenericFactoryProvider.getApplicationContext().getBean(name, args);
	}

	@Override
	public void removeBean(final String beanName)
	{
		ApplicationContext context = GenericFactoryProvider.getApplicationContext();
		boolean done = false;
		// Try to find bean in whole application context hierarchy
		while (context != null && !done)
		{
			final DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
			try
			{
				factory.destroyScopedBean(beanName);
				done = true;
			}
			catch (final NoSuchBeanDefinitionException e)
			{
				context = GenericFactoryProvider.getApplicationContext().getParent();
			}
		}
	}

	@Override
	public String[] getBeanNamesForType(final Class<?> type)
	{
		ApplicationContext context = GenericFactoryProvider.getApplicationContext();
		String[] beanNames = new String[0];
		// Try to find bean in whole application context hierarchy
		while (beanNames.length == 0 && context != null)
		{
			beanNames = context.getBeanNamesForType(type);
			context = context.getParent();
		}
		return beanNames;
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType)
	{
		return GenericFactoryProvider.getApplicationContext().findAnnotationOnBean(beanName, annotationType);
	}

}
