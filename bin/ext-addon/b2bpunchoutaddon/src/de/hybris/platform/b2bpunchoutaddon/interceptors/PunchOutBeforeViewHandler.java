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
package de.hybris.platform.b2bpunchoutaddon.interceptors;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;


public class PunchOutBeforeViewHandler implements BeforeViewHandlerAdaptee
{

	public static final String VIEW_NAME_MAP_KEY = "viewName";
	private Map<String, Map<String, String>> viewMap;

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			String viewName) throws PunchOutException
	{

		try
		{
			if (StringUtils.isNotBlank((String) request.getSession().getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)))
			{
				viewName = getPunchoutView(viewName);
				setPunchoutModeInModel(model);
			}

			return viewName;
		}
		catch (final Exception e)
		{
			throw new PunchOutException(PunchOutResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private String getPunchoutView(String viewName)
	{
		if (viewMap.containsKey(viewName))
		{
			viewName = B2bpunchoutaddonConstants.VIEW_PAGE_PREFIX + viewMap.get(viewName).get(VIEW_NAME_MAP_KEY);
		}

		return viewName;
	}

	private void setPunchoutModeInModel(final ModelMap model)
	{
		model.addAttribute("punchoutMode", Boolean.valueOf(true));
	}

	public Map<String, Map<String, String>> getViewMap()
	{
		return viewMap;
	}

	public void setViewMap(final Map<String, Map<String, String>> viewMap)
	{
		this.viewMap = viewMap;
	}

}
