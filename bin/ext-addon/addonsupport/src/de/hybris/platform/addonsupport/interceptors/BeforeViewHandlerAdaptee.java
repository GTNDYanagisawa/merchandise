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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;


/**
 * Allows an AddOn to code up a BeforeViewHandler adapter without needing to implement the storefront specific
 * interface.
 * 
 * @author rmcotton
 */
public interface BeforeViewHandlerAdaptee
{
	String beforeView(HttpServletRequest request, HttpServletResponse response, ModelMap model, String viewName) throws Exception;

}