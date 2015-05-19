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
package de.hybris.platform.ycommercewebservices.conv;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.thoughtworks.xstream.XStream;


/**
 * Sets default converter as a target of converter redirection in converters implementing {@link RedirectableConverter}
 * interface.
 */
public class ConverterPostProcessor implements BeanPostProcessor
{
	private final XStream xStream;

	public ConverterPostProcessor(final XStream xStream)
	{
		this.xStream = xStream;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException
	{
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException
	{
		if (bean instanceof RedirectableConverter)
		{
			((RedirectableConverter) bean).setTargetConverter((getxStream().getConverterLookup()
					.lookupConverterForType(((RedirectableConverter) bean).getConvertedClass())));

		}
		return bean;
	}

	public XStream getxStream()
	{
		return xStream;
	}

}
