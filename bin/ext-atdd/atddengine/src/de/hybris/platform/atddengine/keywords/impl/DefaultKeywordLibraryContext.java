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
package de.hybris.platform.atddengine.keywords.impl;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.atddengine.keywords.ImpExAdaptor;
import de.hybris.platform.atddengine.keywords.ImpExAdaptorAware;
import de.hybris.platform.atddengine.keywords.KeywordLibraryContext;
import de.hybris.platform.atddengine.keywords.RobotTestContext;
import de.hybris.platform.atddengine.keywords.RobotTestContextAware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;


public class DefaultKeywordLibraryContext implements KeywordLibraryContext, BeanFactoryAware, ImpExAdaptorAware,
		RobotTestContextAware
{
	private final AutowiredAnnotationBeanPostProcessor beanPostProcessor;

	private ImpExAdaptor impExAdaptor;

	private RobotTestContext robotTestContext;

	public DefaultKeywordLibraryContext()
	{
		beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
	}

	@Override
	public void initializeKeywordLibrary(final AbstractKeywordLibrary keywordLibrary)
	{
		beanPostProcessor.processInjection(keywordLibrary);

		processInterfaces(keywordLibrary);
	}

	private void processInterfaces(final AbstractKeywordLibrary keywordLibrary)
	{
		if (keywordLibrary instanceof ImpExAdaptorAware)
		{
			((ImpExAdaptorAware) keywordLibrary).setImpExAdaptor(impExAdaptor);
		}

		if (keywordLibrary instanceof RobotTestContextAware)
		{
			((RobotTestContextAware) keywordLibrary).setRobotTestContext(robotTestContext);
		}
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
	{
		beanPostProcessor.setBeanFactory(beanFactory);
	}

	@Override
	public void setImpExAdaptor(final ImpExAdaptor impExAdaptor)
	{
		this.impExAdaptor = impExAdaptor;
	}

	@Override
	public void setRobotTestContext(final RobotTestContext robotTestContext)
	{
		this.robotTestContext = robotTestContext;
	}

}
