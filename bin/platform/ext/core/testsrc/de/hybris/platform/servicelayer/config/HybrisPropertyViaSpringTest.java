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
package de.hybris.platform.servicelayer.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.util.Config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;


@IntegrationTest
public class HybrisPropertyViaSpringTest extends ServicelayerBaseTest
{
	private ConfigurableApplicationContext context;

	@Before
	public void setUp()
	{
		final GenericApplicationContext appContext = new GenericApplicationContext();
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(appContext);
		xmlReader.loadBeanDefinitions(new ClassPathResource("/servicelayer/test/properties-spring-test.xml"));
		appContext.refresh();
		context = appContext;
	}

	@After
	public void tearDown()
	{
		context.close();
	}

	@Test
	public void testProperty()
	{
		final PropertiesTestBean testBean = context.getBean(PropertiesTestBean.class);
		assertNotNull(testBean);
		assertNotNull(testBean.getTestAttribute());
		assertEquals(Config.getString("db.url", "xxx"), testBean.getTestAttribute());
	}
}
