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
package de.hybris.platform.sap.productconfig.frontend.beforeview;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.sap.productconfig.frontend.constants.Sapproductconfigb2baddonConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;


/**
 *
 */
public class ProductDetailsBeforeViewHandler implements BeforeViewHandlerAdaptee
{
	/**
	 *
	 */
	public static final String PRODUCT_CONFIG_PAGE = "addon:/" + Sapproductconfigb2baddonConstants.EXTENSIONNAME
			+ "/pages/configuration/configurationPage";
	private static final Logger LOG = Logger.getLogger(ProductDetailsBeforeViewHandler.class);


	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Intercepting view:" + viewName);
		}
		if (viewName.equals(PRODUCT_CONFIG_PAGE))
		{
			response.setHeader("Cache-control", "no-cache, no-store");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "-1");
		}

		return viewName;
	}

}
