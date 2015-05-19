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
package de.hybris.platform.payment.commands.impl;

import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.commands.factory.CommandFactory;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.dto.BasicCardInfo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 *
 */
public class CommandFactoryRegistryMockImpl implements ApplicationContextAware, CommandFactoryRegistry
{
	public static final String MOCKUP_PAYMENT_PROVIDER = "Mockup"; // As defined in payment-spring-test.xml

	private ApplicationContext applicationContext;

	@Override
	public CommandFactory getFactory(final String paymentProvider) throws AdapterException
	{
		if (MOCKUP_PAYMENT_PROVIDER.equals(paymentProvider))
		{
			return applicationContext.getBean("mockupCommandFactory", CommandFactory.class);
		}
		else
		{
			throw new AdapterException("The requested paymentProvider should be <" + MOCKUP_PAYMENT_PROVIDER + "> instead of <"
					+ paymentProvider + ">");
		}
	}

	@Override
	public CommandFactory getFactory(final BasicCardInfo card, final boolean threeD) throws AdapterException
	{
		return applicationContext.getBean("mockupCommandFactory", CommandFactory.class);
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

}
