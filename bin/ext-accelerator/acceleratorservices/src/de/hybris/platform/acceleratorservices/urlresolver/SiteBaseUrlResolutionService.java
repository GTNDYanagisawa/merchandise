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
package de.hybris.platform.acceleratorservices.urlresolver;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;


/**
 * CMS Site URL resolution service interface. This service is used to lookup the fully qualified URL to the root of the
 * website for a Site. Supports separate URLs for the website and for the media root. Supports secure and insecure URLs.
 * 
 * @spring.bean siteBaseUrlResolutionService
 */
public interface SiteBaseUrlResolutionService
{
	/**
	 * Resolves website base url for the given site.
	 * 
	 * @param site
	 *           the base site
	 * @param secure
	 *           flag to indicate is HTTPS url is required
	 * @param path
	 *           the path to include in the url
	 * @return The URL for the website
	 */
	String getWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path);

	/**
	 * Resolves media base url for the given site.
	 * 
	 * @param site
	 *           the base site
	 * @param secure
	 *           flag to indicate is HTTPS url is required
	 * @return The URL for the media root
	 */
	String getMediaUrlForSite(BaseSiteModel site, boolean secure);

	/**
	 * Resolves media base url for the given site.
	 * 
	 * @param site
	 *           the base site
	 * @param secure
	 *           flag to indicate is HTTPS url is required
	 * @param path
	 *           the path to include in the url
	 * @return The URL for the media root
	 */
	String getMediaUrlForSite(BaseSiteModel site, boolean secure, String path);

	/**
	 *
	 * @param site
	 * @param secure
	 * @param path
	 * @param queryParams
	 * @return
	 */
	String getWebsiteUrlForSite(final BaseSiteModel site, final boolean secure, final String path, final String queryParams);

	/**
	 *
	 * @param site
	 * @param encodingAttributes
	 * @param secure
	 * @param path
	 * @return
	 */
	String getWebsiteUrlForSite(BaseSiteModel site, String encodingAttributes, boolean secure, String path);

	/**
	 *
	 * @param site
	 * @param encodingAtrributes
	 * @param secure
	 * @param path
	 * @param queryParams
	 * @return
	 */
	String getWebsiteUrlForSite(BaseSiteModel site, String encodingAtrributes, boolean secure, String path, String queryParams);
}
