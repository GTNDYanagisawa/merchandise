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
package de.hybris.platform.acceleratorcms.component.renderer.impl;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * GenericViewCMSComponentRenderer.
 * 
 * Generic CMS component renderer. Looks for a controller bean named with the typecode of the CMS component (with suffix
 * 'Controller'), if a controller exists with that name then the component is rendered by a server side include to
 * '/view/[controllerName]'. If there is no controller defined then the default include is
 * '/view/DefaultCMSComponentController'.
 */
public class GenericViewCMSComponentRenderer implements CMSComponentRenderer<AbstractCMSComponentModel>, BeanFactoryAware
{
	public static final String DEFAULT_CONTROLLER = "DefaultCMSComponentController";

	private static final Logger LOG = Logger.getLogger(GenericViewCMSComponentRenderer.class);

	private BeanFactory beanFactory;

	protected BeanFactory getBeanFactory()
	{
		return beanFactory;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
	{
		this.beanFactory = beanFactory;
	}

	@Override
	public void renderComponent(final PageContext pageContext, final AbstractCMSComponentModel component) throws ServletException,
			IOException
	{

		final Object existingComponentInRequest = pageContext.getAttribute("component", PageContext.REQUEST_SCOPE);

		pageContext.setAttribute("component", component, PageContext.REQUEST_SCOPE);

		try
		{
			final String typeCode = component.getTypeCode();
			String controllerName = typeCode + "Controller";
			if (!getBeanFactory().containsBean(controllerName))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("No controller defined for ContentElement [" + typeCode + "]. Using default Controller");
				}
				controllerName = DEFAULT_CONTROLLER;
			}

			final String includePath = "/view/" + controllerName;

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Rendering CMS Component type [" + typeCode + "] uid [" + component.getUid() + "], include path ["
						+ includePath + "]");
			}
			pageContext.include(includePath);
		}
		finally
		{
			pageContext.removeAttribute("component", PageContext.REQUEST_SCOPE);
			if (existingComponentInRequest != null)
			{
				pageContext.setAttribute("component", existingComponentInRequest, PageContext.REQUEST_SCOPE);

			}
		}
	}
}
