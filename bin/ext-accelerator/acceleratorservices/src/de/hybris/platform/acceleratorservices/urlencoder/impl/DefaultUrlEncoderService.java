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
package de.hybris.platform.acceleratorservices.urlencoder.impl;

import de.hybris.platform.acceleratorservices.constants.AcceleratorServicesConstants;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.UrlEncodingAttributeManager;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultUrlEncoderService implements UrlEncoderService
{
	private SessionService sessionService;
	private CMSSiteService cmsSiteService;
	private Map<String, UrlEncodingAttributeManager> urlEncodingAttributeManagerMap;

	@Override
	public Collection<String> getEncodingAttributesForSite()
	{

		return getCmsSiteService().getCurrentSite().getUrlEncodingAttributes();
	}

	@Override
	public boolean isUrlEncodingEnabledForCurrentSite()
	{
		return CollectionUtils.isNotEmpty(getEncodingAttributesForSite());
	}

	@Override
	public String getUrlEncodingPattern()
	{
		final String encodingAttributes = getSessionService().getAttribute(AcceleratorServicesConstants.URL_ENCODING_ATTRIBUTES);
		return StringUtils.defaultString(encodingAttributes, "");
	}

	@Override
	public boolean isLanguageEncodingEnabled()
	{
		return getEncodingAttributesForSite().contains(AcceleratorServicesConstants.LANGUAGE_ENCODING);
	}

	@Override
	public boolean isCurrencyEncodingEnabled()
	{
		return getEncodingAttributesForSite().contains(AcceleratorServicesConstants.CURRENCY_ENCODING);
	}

	@Override
	public Map<String, UrlEncodingAttributeManager> getUrlEncodingAttrManagerMap()
	{
		return getUrlEncodingAttributeManagerMap();
	}

	@Override
	public String getUrlEncodingPatternForEmail(final BusinessProcessModel businessProcessModel)
	{
		String urlPattern = null;

		for (final String attribute : getEncodingAttributesForSite())
		{
			final UrlEncodingAttributeManager urlEncodingAttributeManager = getUrlEncodingAttributeManagerMap().get(attribute);
			if (urlEncodingAttributeManager != null)
			{
				urlPattern = StringUtils.isBlank(urlPattern) ? urlEncodingAttributeManager
						.getAttributeValueForEmail(businessProcessModel) : urlPattern + "/"
						+ urlEncodingAttributeManager.getAttributeValueForEmail(businessProcessModel);
			}
		}

		return StringUtils.isNotEmpty(urlPattern) ? "/" + urlPattern : StringUtils.EMPTY;
	}

	@Override
	public String getCurrentUrlEncodingPattern()
	{
		String urlPattern = null;

		for (final String attribute : getEncodingAttributesForSite())
		{
			final UrlEncodingAttributeManager urlEncodingAttributeManager = getUrlEncodingAttributeManagerMap().get(attribute);
			if (urlEncodingAttributeManager != null)
			{
				urlPattern = StringUtils.isBlank(urlPattern) ? urlEncodingAttributeManager.getCurrentValue() : urlPattern + "/"
						+ urlEncodingAttributeManager.getCurrentValue();
			}
		}

		return StringUtils.isNotEmpty(urlPattern) ? "/" + urlPattern : StringUtils.EMPTY;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	protected Map<String, UrlEncodingAttributeManager> getUrlEncodingAttributeManagerMap()
	{
		return urlEncodingAttributeManagerMap;
	}

	@Required
	public void setUrlEncodingAttributeManagerMap(final Map<String, UrlEncodingAttributeManager> urlEncodingAttributeManagerMap)
	{
		this.urlEncodingAttributeManagerMap = urlEncodingAttributeManagerMap;
	}
}
