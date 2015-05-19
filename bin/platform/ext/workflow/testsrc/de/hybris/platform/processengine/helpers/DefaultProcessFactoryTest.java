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
package de.hybris.platform.processengine.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.definition.DefaultProcessDefinitionFactory;
import de.hybris.platform.processengine.definition.ProcessDefinitionResource;
import de.hybris.platform.processengine.definition.ProcessDefinitionsCache;
import de.hybris.platform.processengine.definition.TestProcessDefinitionsProvider;
import de.hybris.platform.processengine.definition.XMLProcessDefinitionsReader;
import de.hybris.platform.processengine.helpers.impl.DefaultProcessFactory;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.standard.NoAction;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Test for process factory. It test if loading of correct or incorrect process definitions is working well.
 */
@IntegrationTest
public class DefaultProcessFactoryTest
{
	private final String process = "process1";
	private final String processDefinition = "process1";

	private DefaultProcessFactory test;

	@Before
	public void setUp()
	{
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Registry.getApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

		beanFactory.registerBeanDefinition("DefaultProcessFactoryTest.noAction",
				BeanDefinitionBuilder.rootBeanDefinition(NoAction.class.getName()).getBeanDefinition());

		final MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue("resource", "classpath:/processengine/test/valid.xml");
		beanFactory.registerBeanDefinition("DefaultProcessFactoryTest.testProcess1Definition", new RootBeanDefinition(
				ProcessDefinitionResource.class, null, pvs));

		beanFactory.registerBeanDefinition("DefaultProcessFactoryTest.xmlDefinitionsReader", BeanDefinitionBuilder
				.rootBeanDefinition(XMLProcessDefinitionsReader.class).addConstructorArgReference("scriptingLanguagesService")
				.getBeanDefinition());

		beanFactory.registerBeanDefinition(
				"DefaultProcessFactoryTest.processDefinitionsProvider",
				BeanDefinitionBuilder.rootBeanDefinition(TestProcessDefinitionsProvider.class)
						.addConstructorArgReference("DefaultProcessFactoryTest.xmlDefinitionsReader").getBeanDefinition());

		beanFactory.registerBeanDefinition(
				"DefaultProcessFactoryTest.definitionsCache",
				BeanDefinitionBuilder.rootBeanDefinition(ProcessDefinitionsCache.class)
						.addPropertyReference("processDefinitionsProvider", "DefaultProcessFactoryTest.processDefinitionsProvider")
						.getBeanDefinition());

		beanFactory.registerBeanDefinition(
				"DefaultProcessFactoryTest.processDefinitionFactoryTest",
				BeanDefinitionBuilder.rootBeanDefinition(DefaultProcessDefinitionFactory.class)
						.addPropertyReference("definitionsCache", "DefaultProcessFactoryTest.definitionsCache")
						.addPropertyReference("xmlDefinitionsReader", "DefaultProcessFactoryTest.xmlDefinitionsReader")
						.getBeanDefinition());

		beanFactory.getBean("DefaultProcessFactoryTest.testProcess1Definition");
		beanFactory.getBean("DefaultProcessFactoryTest.noAction");



		test = new DefaultProcessFactory();
		test.setProcessDefinitionFactory((DefaultProcessDefinitionFactory) beanFactory
				.getBean("DefaultProcessFactoryTest.processDefinitionFactoryTest"));
	}

	@After
	public void down()
	{
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Registry.getApplicationContext();

		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

		beanFactory.removeBeanDefinition("DefaultProcessFactoryTest.processDefinitionFactoryTest");
		beanFactory.removeBeanDefinition("DefaultProcessFactoryTest.noAction");
		beanFactory.removeBeanDefinition("DefaultProcessFactoryTest.testProcess1Definition");
	}

	@Test
	public void createProcessModelTest()
	{
		final BusinessProcessModel testmodel = test.createProcessModel(process, processDefinition, Collections.EMPTY_MAP);
		assertNotNull("testmodel", testmodel);
		assertEquals("processDefinition", processDefinition, testmodel.getProcessDefinitionName());
	}

}
