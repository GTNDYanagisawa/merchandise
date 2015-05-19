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
package de.hybris.platform.sap.core.test.util;

import de.hybris.platform.sap.core.common.util.DefaultGenericFactory;
import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Generic factory for tests with direct Spring access.
 */
public class TestGenericFactory extends DefaultGenericFactory implements ApplicationContextAware, DisposableBean
{

	/**
	 * Inject the Spring Application Context for testing.
	 * 
	 * @param applicationContext
	 *           Spring {@link ApplicationContext}
	 */
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		GenericFactoryProvider.setApplicationContext(applicationContext);
	}

	/**
	 * Resets the test Spring Application Context.
	 */
	@Override
	public void destroy()
	{
		GenericFactoryProvider.setApplicationContext(null);
	}

}
