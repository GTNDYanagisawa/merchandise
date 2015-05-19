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

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import org.springframework.context.ApplicationContext;


/**
 * GenericFactory provider for usage outside Spring.
 */
public class GenericFactoryProvider
{

	private static ApplicationContext localApplicationContext;

	/**
	 * Get GenericFactory instance.
	 * 
	 * @return GenericFactory instance
	 */
	public static GenericFactory getInstance()
	{
		return (GenericFactory) getApplicationContext().getBean("sapCoreGenericFactory");
	}

	/**
	 * Sets the Spring Application Context.
	 * 
	 * @param applicationContext
	 *           Application Context
	 */
	public static void setApplicationContext(final ApplicationContext applicationContext)
	{
		localApplicationContext = applicationContext;
	}

	/**
	 * Gets the Spring ApplicationContext.
	 * 
	 * @return Application Context
	 */
	public static ApplicationContext getApplicationContext()
	{
		if (localApplicationContext != null)
		{
			return localApplicationContext;
		}
		final ApplicationContext applicationContext = ServicelayerUtils.getApplicationContext();
		if (applicationContext == null)
		{
			throw new CoreBaseRuntimeException("Application Context not available yet since Registry is still in startup!"
					+ " Possible error is the injection of bean with scope 'sapSession' into a bean with scope 'singleton'.");
		}
		return applicationContext;
	}

	/**
	 * Checks if a local Spring Application Context is set.
	 * 
	 * @return true if local Spring Application Context is set
	 */
	public boolean isLocalApplicationContext()
	{
		return (localApplicationContext != null);
	}

}
