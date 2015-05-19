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
public class ProductSerachBeforeViewHandler implements BeforeViewHandlerAdaptee
{

	public static final String PRODUCT_CONFIG_PRODUCT_LISTER_FRAGMENT_VIEW_NAME = "addon:/"
			+ Sapproductconfigb2baddonConstants.EXTENSIONNAME + "/fragments/product/productLister";
	public static final String PRODUCT_LISTER_FRAGMENT_VIEW_NAME = "fragments/product/productLister";
	private static final Logger LOG = Logger.getLogger(ProductSerachBeforeViewHandler.class);


	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			String viewName)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Intercepting view:" + viewName);
		}

		if (PRODUCT_LISTER_FRAGMENT_VIEW_NAME.equals(viewName))
		{
			viewName = PRODUCT_CONFIG_PRODUCT_LISTER_FRAGMENT_VIEW_NAME;
		}

		return viewName;
	}

}
