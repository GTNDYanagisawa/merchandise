/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.core.initialization;

import org.junit.Before;
import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


@Ignore
public abstract class AbstractSystemSetupTest
{
	protected ApplicationContext applicationContext;
	protected SystemSetupCollector systemSetupCollector;

	@Before
	public void setUp()
	{
		applicationContext = new ClassPathXmlApplicationContext("core/systemsetup/systemsetup-test-applicationcontext.xml");
		systemSetupCollector = applicationContext.getBean("systemSetupCollector", SystemSetupCollector.class);
	}
}
