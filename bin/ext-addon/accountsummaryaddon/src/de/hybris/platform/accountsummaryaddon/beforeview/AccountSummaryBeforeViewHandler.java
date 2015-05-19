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
package de.hybris.platform.accountsummaryaddon.beforeview;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;


public class AccountSummaryBeforeViewHandler implements BeforeViewHandlerAdaptee
{

	private static final String OVERRIDING_VIEW_WITH = "Overriding view with ";
	private static final String MY_COMPANY_HOME_PAGE = "pages/company/myCompanyHomePage";
	private static final String MY_COMPANY_OVERRIDE_HOME_PAGE = "addon:/accountsummaryaddon/pages/company/myCompanyHomePage";

	private static final Logger LOG = Logger.getLogger(AccountSummaryBeforeViewHandler.class);

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{
		if (MY_COMPANY_HOME_PAGE.equals(viewName))
		{
			LOG.info(OVERRIDING_VIEW_WITH + MY_COMPANY_OVERRIDE_HOME_PAGE);
			return MY_COMPANY_OVERRIDE_HOME_PAGE;
		}

		return viewName;

	}
}
