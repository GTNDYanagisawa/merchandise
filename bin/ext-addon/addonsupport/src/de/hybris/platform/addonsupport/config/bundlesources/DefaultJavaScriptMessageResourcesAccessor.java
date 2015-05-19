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

import de.hybris.platform.util.Utilities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


/**
 * Downloading properties stored in addOn's js.messages.properties. Class extends ReloadableResourceBundleMessageSource.
 * Can use it as MessageSource
 */
public class DefaultJavaScriptMessageResourcesAccessor extends ReloadableResourceBundleMessageSource implements
		JavaScriptMessageResourcesAccessor
{


	/**
	 * Default accelerator addOn path, containing messages
	 */
	public static final String ACC_ADDON_PATH = "/acceleratoraddon/web/webroot/WEB-INF/messages/";

	/**
	 * base.js.properties file will be found by default
	 */
	public static final String ADDON_JS_PROPS_FILENAME = "base.js";

	/**
	 * AddOn name
	 */
	private String addOnName;

	/**
	 * Custom addOn messages path (if null, will be set in setter, and return in getter)
	 */
	private String accAddOnMsgsPath;

	/**
	 * Custom addOn properties file name (if null, will be set in setter, and return in getter)
	 */
	private String addOnPropsFilename;



	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Override
	public Map<String, String> getAllMessages(final Locale locale)
	{
		final String path = getMessageSourcePath(locale);
		setBasename(path);
		final PropertiesHolder propsHolder = getMergedProperties(locale);
		return new HashMap<String, String>((Map) propsHolder.getProperties());
	}

	/**
	 * Getting full path for addOn js messages properties file
	 * 
	 * @return String
	 */
	private String getMessageSourcePath(final Locale locale)
	{
		final String path = "file:///" + Utilities.getExtensionInfo(getAddOnName()).getExtensionDirectory() + getAccAddOnMsgsPath()
				+ getAddOnPropsFilename();
		return path;
	}

	/**
	 * @return the addOnName
	 */
	@Override
	public String getAddOnName()
	{
		return addOnName;
	}

	/**
	 * @return the accAddOnMsgsPath
	 */
	public String getAccAddOnMsgsPath()
	{
		return StringUtils.isBlank(accAddOnMsgsPath) ? ACC_ADDON_PATH : accAddOnMsgsPath;
	}

	/**
	 * @return the addOnPropsBasename
	 */
	public String getAddOnPropsFilename()
	{
		return StringUtils.isBlank(addOnPropsFilename) ? ADDON_JS_PROPS_FILENAME : addOnPropsFilename;
	}

	/**
	 * @param addOnName
	 *           the addOnName to set
	 */
	public void setAddOnName(final String addOnName)
	{
		this.addOnName = addOnName;
	}

	/**
	 * @param accAddOnMsgsPath
	 *           the accAddOnMsgsPath to set
	 */
	public void setAccAddOnMsgsPath(final String accAddOnMsgsPath)
	{
		this.accAddOnMsgsPath = StringUtils.isBlank(accAddOnMsgsPath) ? ACC_ADDON_PATH : accAddOnMsgsPath;
	}

	/**
	 * @param addOnPropsFilename
	 *           the addOnPropsFilename to set
	 */
	public void setAddOnPropsFilename(final String addOnPropsFilename)
	{
		this.addOnPropsFilename = StringUtils.isBlank(addOnPropsFilename) ? ADDON_JS_PROPS_FILENAME : addOnPropsFilename;
	}

}
