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
package de.hybris.platform.addonsupport.interceptors;

import de.hybris.platform.acceleratorservices.addonsupport.RequiredAddOnsNameProvider;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.SiteTheme;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;


/**
 * Allows an AddOn to Include Java Script and CSS conditionally.
 * 
 * @author rmcotton
 */
public abstract class AbstractConditionalResourceBeforeHandler implements BeforeViewHandlerAdaptee
{

	protected static final String COMMON = "common";
	protected static final String SHARED = "shared";
	protected static final String RESOURCE_TYPE_JAVASCRIPT = "javascript";
	protected static final String RESOURCE_TYPE_CSS = "css";

	private String defaultThemeName;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "uiExperienceService")
	private UiExperienceService uiExperienceService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource(name = "requiredAddOnsNameProvider")
	private RequiredAddOnsNameProvider requiredAddOnsNameProvider;


	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName) throws Exception
	{

		if (isIncludeResource(request, response, model, viewName))
		{
			final String contextPath = (String) model.get("contextPath");

			final CMSSiteModel currentSite = cmsSiteService.getCurrentSite();

			final String siteName = currentSite.getUid();
			final String themeName = getThemeNameForSite(currentSite);

			final String uiExperienceCode = uiExperienceService.getUiExperienceLevel().getCode();
			final String uiExperienceCodeLower = uiExperienceCode.toLowerCase();


			final List<String> dependantAddOns = requiredAddOnsNameProvider.getAddOns(request.getSession().getServletContext()
					.getServletContextName());

			addOrAppendListAttribute(model, getCommonCssPathKey(),
					getAddOnCommonCSSPaths(contextPath, uiExperienceCodeLower, dependantAddOns));
			addOrAppendListAttribute(model, getThemeCssPathKey(),
					getAddOnThemeCSSPaths(contextPath, themeName, uiExperienceCodeLower, dependantAddOns));
			addOrAppendListAttribute(model, getJavaScriptPathsKey(),
					getAddOnJSPaths(contextPath, siteName, uiExperienceCodeLower, dependantAddOns));
		}
		return viewName;
	}

	protected void addOrAppendListAttribute(final ModelMap model, final String key, final List newVals)
	{
		final List vals;
		if (model.containsAttribute(key))
		{
			vals = (List) model.get(key);
			vals.addAll(newVals);
		}
		else
		{
			vals = newVals;
		}
		model.addAttribute(key, vals);
	}

	protected String getCommonCssPathKey()
	{
		return "addOnCommonCssPaths";
	}

	protected String getThemeCssPathKey()
	{
		return "addOnThemeCssPaths";
	}

	protected String getJavaScriptPathsKey()
	{
		return "addOnJavaScriptPaths";
	}

	protected abstract boolean isIncludeResource(HttpServletRequest request, HttpServletResponse response, ModelMap model,
			String viewName);


	protected List getAddOnResourcePaths(final String contextPath, final List<String> addOnNames, final String[] propertyNames)
	{
		final List<String> addOnResourcePaths = new ArrayList<String>();

		for (final String addon : addOnNames)
		{
			for (final String propertyName : propertyNames)
			{
				final String addOnResourcePropertyValue = siteConfigService.getProperty(addon + "." + propertyName);
				if (addOnResourcePropertyValue != null)
				{
					final String[] propertyPaths = addOnResourcePropertyValue.split(";");
					for (final String propertyPath : propertyPaths)
					{
						addOnResourcePaths.add(contextPath + "/_ui/addons/" + addon + propertyPath);
					}
				}
			}
		}
		return addOnResourcePaths;
	}

	protected List getAddOnCommonCSSPaths(final String contextPath, final String uiExperience, final List<String> addOnNames)
	{
		final String[] propertyNames = new String[]
		{ getPathPropertyPrefix() + "." + RESOURCE_TYPE_CSS + ".paths", //
				getPathPropertyPrefix() + "." + RESOURCE_TYPE_CSS + ".paths." + uiExperience //
		};

		return getAddOnResourcePaths(contextPath, addOnNames, propertyNames);
	}

	protected List getAddOnThemeCSSPaths(final String contextPath, final String themeName, final String uiExperience,
			final List<String> addOnNames)
	{
		final String[] propertyNames = new String[]
		{ getPathPropertyPrefix() + "." + RESOURCE_TYPE_CSS + ".paths." + uiExperience + "." + themeName };

		return getAddOnResourcePaths(contextPath, addOnNames, propertyNames);
	}

	protected List getAddOnJSPaths(final String contextPath, final String siteName, final String uiExperience,
			final List<String> addOnNames)
	{
		final String[] propertyNames = new String[]
		{ getPathPropertyPrefix() + "." + RESOURCE_TYPE_JAVASCRIPT + ".paths", //
				getPathPropertyPrefix() + "." + RESOURCE_TYPE_JAVASCRIPT + ".paths." + uiExperience //
		};

		return getAddOnResourcePaths(contextPath, addOnNames, propertyNames);
	}

	protected String getThemeNameForSite(final CMSSiteModel site)
	{
		final SiteTheme theme = site.getTheme();
		if (theme != null)
		{
			final String themeCode = theme.getCode();
			if (themeCode != null && !themeCode.isEmpty())
			{
				return themeCode;
			}
		}
		return getDefaultThemeName();
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


	protected String getDefaultThemeName()
	{
		return defaultThemeName;
	}

	@Required
	public void setDefaultThemeName(final String defaultThemeName)
	{
		this.defaultThemeName = defaultThemeName;
	}

	public abstract String getPathPropertyPrefix();


}
