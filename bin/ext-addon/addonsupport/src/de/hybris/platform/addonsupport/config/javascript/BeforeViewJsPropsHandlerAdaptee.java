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
package de.hybris.platform.addonsupport.config.javascript;

import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.addonsupport.config.bundlesources.JavaScriptMessageResourcesAccessor;
import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;


/**
 * Before view handler. All properties from addon (base.properties) are mapped to 'jsVariables' model property.
 * 
 */
public abstract class BeforeViewJsPropsHandlerAdaptee implements BeforeViewHandlerAdaptee
{

	public static final String JS_VARIABLES_MODEL_NAME = "jsAddOnsVariables";


	private JavaScriptMessageResourcesAccessor messageSource;

	private I18NService i18NService;

	/**
	 * Custom variable node name. If you do not want to use name in JS_VARIABLES_MODEL_NAME, this custom name will be
	 * used.
	 */
	private String jsVariableModelName;


	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName) throws Exception
	{
		//Adding JS variables on the end, then abstract method
		attachJSVariablesToModel(model);
		return beforeViewJsProps(request, response, model, viewName);
	}

	/**
	 * Method create Map of JavaScript variables. Format of map: index => addOn name value =>
	 * List<JavaScriptVariableData>
	 * 
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	protected void attachJSVariablesToModel(final ModelMap model)
	{
		if (model != null)
		{
			Map<String, List<JavaScriptVariableData>> jsVariables = (Map<String, List<JavaScriptVariableData>>) model
					.get(detectJsModelName());
			if (jsVariables == null)
			{
				jsVariables = new HashMap<String, List<JavaScriptVariableData>>();
				model.addAttribute(detectJsModelName(), jsVariables);
			}
			final Locale currentLocale = getI18NService().getCurrentLocale();
			final List<JavaScriptVariableData> jsVariablesList = JavaScriptVariableDataFactory.createFromMap(getMessageSource()
					.getAllMessages(currentLocale));
			jsVariables.put(getMessageSource().getAddOnName(), jsVariablesList);
		}
	}

	/**
	 * Abstract method, insert your logic here
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param viewName
	 * @return String viewName
	 */
	public abstract String beforeViewJsProps(final HttpServletRequest request, final HttpServletResponse response,
			final ModelMap model, final String viewName);

	/**
	 * Detecting js model name
	 * 
	 * @return String
	 */
	protected String detectJsModelName()
	{
		return StringUtils.isBlank(getJsVariableModelName()) ? JS_VARIABLES_MODEL_NAME : getJsVariableModelName();
	}

	/**
	 * @return the addonResourceBundleSource
	 */
	public JavaScriptMessageResourcesAccessor getMessageSource()
	{
		return messageSource;
	}

	/**
	 * @param addonResourceBundleSource
	 *           the addonResourceBundleSource to set
	 */
	@Required
	public void setMessageSource(final JavaScriptMessageResourcesAccessor addonResourceBundleSource)
	{
		this.messageSource = addonResourceBundleSource;
	}

	/**
	 * @return the jsVariableModelName
	 */
	public String getJsVariableModelName()
	{
		return jsVariableModelName;
	}

	/**
	 * @param jsVariableModelName
	 *           the jsVariableModelName to set
	 */
	public void setJsVariableModelName(final String jsVariableModelName)
	{
		this.jsVariableModelName = jsVariableModelName;
	}

	/**
	 * @return the i18NService
	 */
	public I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18NService
	 *           the i18NService to set
	 */
	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}

}
