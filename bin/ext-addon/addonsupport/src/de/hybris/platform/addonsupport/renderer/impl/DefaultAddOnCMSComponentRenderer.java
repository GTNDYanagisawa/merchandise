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
package de.hybris.platform.addonsupport.renderer.impl;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultAddOnCMSComponentRenderer<C extends AbstractCMSComponentModel> implements CMSComponentRenderer<C>
{
	private TypeService typeService;
	private UiExperienceService uiExperienceService;
	private ModelService modelService;
	private CMSComponentService cmsComponentService;
	private final String cmsComponentFolder = "cms";

	protected TypeService getTypeService()
	{
		return this.typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CMSComponentService getCmsComponentService()
	{
		return cmsComponentService;
	}

	@Required
	public void setCmsComponentService(final CMSComponentService cmsComponentService)
	{
		this.cmsComponentService = cmsComponentService;
	}

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}

	@Override
	public void renderComponent(final PageContext pageContext, final C component) throws ServletException, IOException
	{
		final Map<String, Object> exposedVariables = exposeVariables(pageContext, component);
		pageContext.include(getView(component));
		unExposeVariables(pageContext, component, exposedVariables);
	}

	protected Map<String, Object> exposeVariables(final PageContext pageContext, @SuppressWarnings("unused") final C component)
	{
		final Map<String, Object> variables = getVariablesToExpose(pageContext, component);
		if (variables != null)
		{
			for (final Entry<String, Object> entry : variables.entrySet())
			{
				pageContext.setAttribute(entry.getKey(), entry.getValue(), getScopeForVariableName(entry.getKey()));
			}
		}
		return variables;
	}

	protected int getScopeForVariableName(@SuppressWarnings("unused") final String variableName)
	{
		return PageContext.REQUEST_SCOPE;
	}

	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final Map<String, Object> variables = new HashMap<String, Object>();
		for (final String property : getCmsComponentService().getEditorProperties(component))
		{
			try
			{
				final Object value = modelService.getAttributeValue(component, property);
				variables.put(property, value);
			}
			catch (final AttributeNotSupportedException ignore)
			{
				// ignore
			}
		}
		return variables;
	}


	protected void unExposeVariables(final PageContext pageContext, @SuppressWarnings("unused") final C component,
			final Map<String, Object> exposedVariables)
	{
		if (exposedVariables != null)
		{
			for (final String variableName : exposedVariables.keySet())
			{
				pageContext.removeAttribute(variableName, getScopeForVariableName(variableName));
			}
		}
	}

	protected String getView(final C component)
	{
		return "/WEB-INF/views/addons/" + getAddonUiExtensionName(component) + "/" + getUIExperienceFolder() + "/"
				+ getCmsComponentFolder() + "/" + getViewResourceName(component) + ".jsp";
	}

	protected String getAddonUiExtensionName(final C component)
	{
		return getTypeService().getComposedTypeForCode(component.getItemtype()).getExtensionName();
	}

	protected String getCmsComponentFolder()
	{
		return this.cmsComponentFolder;
	}

	protected String getUIExperienceFolder()
	{
		return StringUtils.lowerCase(getUiExperienceService().getUiExperienceLevel().getCode());
	}

	protected String getViewResourceName(final C component)
	{
		return StringUtils.lowerCase(getTypeCode(component));
	}

	protected String getTypeCode(final C component)
	{
		return component.getItemtype();
	}

	/**
	 * Helper method to lookup a spring bean in the context of a request. This should only be used to lookup beans that
	 * are request scoped. The looked up bean is cached in the request attributes so it should not have a narrower scope
	 * than request scope. This method should not be used for beans that could be injected into this bean.
	 * 
	 * @param request
	 *           the current request
	 * @param beanName
	 *           the name of the bean to lookup
	 * @param beanType
	 *           the expected type of the bean
	 * @param <T>
	 *           the expected type of the bean
	 * @return the bean found or <tt>null</tt>
	 */
	protected <T> T getBean(final HttpServletRequest request, final String beanName, final Class<T> beanType)
	{
		return SpringHelper.getSpringBean(request, beanName, beanType, true);
	}

	protected RequestContextData getRequestContextData(final HttpServletRequest request)
	{
		return getBean(request, "requestContextData", RequestContextData.class);
	}
}
