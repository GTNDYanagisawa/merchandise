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
package de.hybris.platform.sap.core.test;

import de.hybris.platform.sap.core.test.beans.ApplicationContextProvider;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Base test class for junit test in the hybris environment without starting the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(locations =
{ "classpath:sapcoretest-test-spring.xml", "classpath:converters-spring.xml" })
public abstract class SapcoretestSpringJUnitTest
{

	@Resource(name = "sapCoreApplicationContextProvider")
	private ApplicationContextProvider applicationContextProvider;

	/**
	 * Standard test setUp method.
	 */
	@Before
	public void setUp()
	{
	}

	/**
	 * Standard test tearDown method.
	 */
	@After
	public void tearDown()
	{
	}

	/**
	 * Returns the Spring ApplicationContext e.g. for getting beans
	 * 
	 * @return ApplicationContext
	 */
	protected ApplicationContext getApplicationContext()
	{
		return applicationContextProvider.getApplicationContext();
	}

}
