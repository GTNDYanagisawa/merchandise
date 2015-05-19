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
package de.hybris.platform.cockpit.services.label.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.label.LabelService;
import de.hybris.platform.cockpit.services.label.impl.DefaultLabelService.StaticLanguageProvider;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.ctx.ScopeTenantIgnoreDocReader;

import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;


@IntegrationTest
public class DefaultLabelServiceIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultLabelServiceIntegrationTest.class);
	private GenericApplicationContext applicationContext;

	@Resource
	private UserService userService;

	private LabelService labelService;
	private TypeService typeService;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		initApplicationContext();
		labelService = (LabelService) applicationContext.getBean("labelService");
		typeService = (TypeService) applicationContext.getBean("cockpitTypeService");
		if (labelService instanceof DefaultLabelService)
		{
			((DefaultLabelService) labelService).setLanguageProvider(new StaticLanguageProvider("en"));

		}

		// Create data for tests
		LOG.info("Creating data for labels ..");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/test/testLabels.csv", "utf-8");

		LOG.info("Finished data for labels " + (System.currentTimeMillis() - startTime) + "ms");
	}

	private void initApplicationContext() throws Exception
	{
		final GenericApplicationContext context = new GenericApplicationContext();
		context.setResourceLoader(new DefaultResourceLoader(Registry.class.getClassLoader()));
		context.setClassLoader(Registry.class.getClassLoader());
		context.getBeanFactory().setBeanClassLoader(Registry.class.getClassLoader());
		context.setParent(Registry.getGlobalApplicationContext());
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
		xmlReader.setDocumentReaderClass(ScopeTenantIgnoreDocReader.class);
		xmlReader.setBeanClassLoader(Registry.class.getClassLoader());
		xmlReader.loadBeanDefinitions(getSpringConfigurationLocations());
		context.refresh();
		final AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
		this.applicationContext = context;
	}


	protected String[] getSpringConfigurationLocations()
	{
		return new String[]
		{ "classpath:/cockpit/cockpit-spring-services.xml", "classpath:/cockpit/cockpit-spring-services-test.xml" };
	}



	@Test
	public void testGetObjectTextLabelForTypedObject()
	{
		final TypedObject ahertz = typeService.wrapItem(userService.getUserForUID("ahertz"));
		Assert.assertNotNull(ahertz);

		final String textLabel = labelService.getObjectTextLabelForTypedObject(ahertz);
		Assert.assertNotNull(textLabel);
		Assert.assertEquals("Wrong customer retrieved", "Anja Hertz", textLabel);
	}


	@Test
	public void testGetObjectDescriptionForTypedObject()
	{
		final TypedObject abrode = typeService.wrapItem(userService.getUserForUID("abrode"));
		Assert.assertNotNull(abrode);

		final String descriptionLabel = labelService.getObjectDescriptionForTypedObject(abrode);
		Assert.assertNotNull(descriptionLabel);
		Assert.assertEquals("Wrong customer retrieved", "Super Arin", descriptionLabel);
	}


	@Test
	public void testGetObjectIconPathForTypedObject()
	{
		final TypedObject ahertz = typeService.wrapItem(userService.getUserForUID("ahertz"));
		Assert.assertNotNull(ahertz);

		final String iconPath = labelService.getObjectIconPathForTypedObject(ahertz);
		Assert.assertNotNull(iconPath);
		Assert.assertTrue("Wrong icon path retrieved", iconPath.contains("TestBasePrincipal.xml"));
	}

}
