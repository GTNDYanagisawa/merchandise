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
package de.hybris.platform.acceleratorservices.urlresolver.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the UrlResolutionService.
 */
public class DefaultSiteBaseUrlResolutionService extends AbstractBusinessService implements SiteBaseUrlResolutionService
{
	private ConfigurationService configurationService;
	private Map<SiteChannel, String> contextRoots;
	private UrlEncoderService urlEncoderService;

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected Map<SiteChannel, String> getContextRoots()
	{
		return contextRoots;
	}

	@Required
	public void setContextRoots(final Map<SiteChannel, String> contextRoots)
	{
		this.contextRoots = contextRoots;
	}

	protected UrlEncoderService getUrlEncoderService()
	{
		return urlEncoderService;
	}

	@Required
	public void setUrlEncoderService(final UrlEncoderService urlEncoderService)
	{
		this.urlEncoderService = urlEncoderService;
	}


	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site,final String encodingAttributes, final boolean secure, final String path)
	{
		validateParameterNotNull(site, "CMS site model cannot be null");

		final String url = cleanupUrl(lookupConfig("website." + site.getUid() + (secure ? ".https" : ".http")));
		if (url != null)
		{
			// if url contains ? remove everything after ? then add path then add back the query string
			// this is so website urls in config files can have query strings and urls in emails will be
			// formatted correctly
			if (url.contains("?"))
			{
				final String queryString = url.substring(url.indexOf('?'));
				final String tmpUrl = url.substring(0, url.indexOf('?'));
				return cleanupUrl(tmpUrl) + (StringUtils.isNotBlank(encodingAttributes)? encodingAttributes:"") + (path == null ? "" : path) + "/" + queryString;
			}

			return url+ (StringUtils.isNotBlank(encodingAttributes)? encodingAttributes:"") + (path == null ? "" : path);
		}
		return getDefaultWebsiteUrlForSite(site, secure, path);
	}

	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site,final boolean secure, final String path)
	{
		return getWebsiteUrlForSite(site,getUrlEncoderService().getUrlEncodingPattern(), secure, path);
	}


	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site, final boolean secure, final String path, final String queryParams)
	{
		final String url = getWebsiteUrlForSite(site, secure, path);
		return appendQueryParams(url, queryParams);
	}

	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site, final String encodingAtrributes, final boolean secure, final String path, final String queryParams)
	{
		final String url = getWebsiteUrlForSite(site, encodingAtrributes, secure, path);
		return appendQueryParams(url, queryParams);
	}

	@Override
	public String getMediaUrlForSite(final BaseSiteModel site, final boolean secure, final String path)
	{
		validateParameterNotNull(site, "CMS site model cannot be null");

		final String url = getMediaUrlForSite(site, secure);
		if (url != null)
		{
			return url + (path == null ? "" : path);
		}
		return getDefaultMediaUrlForSite(site, secure) + (path == null ? "" : path);
	}

	@Override
	public String getMediaUrlForSite(final BaseSiteModel site, final boolean secure)
	{
		validateParameterNotNull(site, "CMS site model cannot be null");

		final String url = cleanupUrl(lookupConfig("media." + site.getUid() + (secure ? ".https" : ".http")));
		if (url != null)
		{
			return url;
		}
		return getDefaultMediaUrlForSite(site, secure);
	}


	protected String lookupConfig(final String key)
	{
		return getConfigurationService().getConfiguration().getString(key, null);
	}

	protected String cleanupUrl(final String url)
	{
		if (url != null && url.endsWith("/"))
		{
			return url.substring(0, url.length() - 1);
		}
		return url;
	}

	protected String getDefaultWebsiteUrlForSite(final BaseSiteModel site, final boolean secure, final String path)
	{
		final String contextRoot = getDefaultWebsiteContextRootForSite(site);
		if (contextRoot != null)
		{
			final String schemeHostAndPort;
			if (secure)
			{
				schemeHostAndPort = "https://localhost:" + lookupConfig("tomcat.ssl.port");
			}
			else
			{
				schemeHostAndPort = "http://localhost:" + lookupConfig("tomcat.http.port");
			}

			final String url = schemeHostAndPort + contextRoot + path;
			final String queryParams = "clear=true&site=" + site.getUid();
			return appendQueryParams(url, queryParams);
		}
		return null;
	}

	protected String getDefaultWebsiteContextRootForSite(final BaseSiteModel site)
	{
		final Map<SiteChannel, String> roots = getContextRoots();
		if (site.getChannel() != null && roots.containsKey(site.getChannel()))
		{
			return cleanupUrl(roots.get(site.getChannel()));
		}
		return null;
	}

	protected String appendQueryParams(final String url, final String params)
	{
		if (url.contains("?"))
		{
			return url + "&" + params;
		}
		else
		{
			return url + "?" + params;
		}
	}

	protected String getDefaultMediaUrlForSite(@SuppressWarnings("unused") final BaseSiteModel site, final boolean secure)
	{
		if (secure)
		{
			return "https://localhost:" + lookupConfig("tomcat.ssl.port");
		}
		else
		{
			return "http://localhost:" + lookupConfig("tomcat.http.port");
		}
	}
}
