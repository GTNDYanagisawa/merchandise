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
package de.hybris.platform.addonsupport.config.bundlesources;

import java.util.Locale;
import java.util.Map;


/**
 * Add-on resource bundle source interface. By default used by DefaultAddonResourceBundleSource.
 */
public interface JavaScriptMessageResourcesAccessor
{

	/**
	 * Getting messages from all sources
	 * 
	 * @return java.util.Map
	 */
	Map<String, String> getAllMessages(Locale locale);

	/**
	 * Getting addOnName for given Resources
	 * 
	 * @return String
	 */
	String getAddOnName();

}
