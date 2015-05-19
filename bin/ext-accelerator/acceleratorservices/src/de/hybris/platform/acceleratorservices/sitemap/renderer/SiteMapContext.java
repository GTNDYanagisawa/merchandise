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
package de.hybris.platform.acceleratorservices.sitemap.renderer;

import de.hybris.platform.acceleratorservices.enums.SiteMapChangeFrequencyEnum;
import de.hybris.platform.acceleratorservices.enums.SiteMapPageEnum;
import de.hybris.platform.acceleratorservices.model.SiteMapPageModel;
import de.hybris.platform.acceleratorservices.sitemap.data.SiteMapUrlData;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.velocity.VelocityContext;


public class SiteMapContext extends VelocityContext
{
	public static final String BASE_URL = "baseUrl";
	public static final String MEDIA_URL = "mediaUrl";
	public static final String SITE_MAP_URL_DATA_COLLECTION = "siteMapUrlDataCollection";
	public static final String CHANGE_FREQ = "changeFreq";
	public static final String PRIORITY = "priority";


	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	private BaseSiteService baseSiteService;
	private UrlEncoderService urlEncoderService;


	public String getBaseUrl()
	{
		return (String) this.get(BASE_URL);
	}

	public String getChangeFreq()
	{
		return (String) this.get(CHANGE_FREQ);
	}

	public Double getPriority()
	{
		return (Double) this.get(PRIORITY);
	}

	public List<SiteMapUrlData> getSiteMapData()
	{
		return (List<SiteMapUrlData>) this.get(SITE_MAP_URL_DATA_COLLECTION);
	}

	public void setSiteMapUrlData(final List<SiteMapUrlData> data)
	{
		this.put(SITE_MAP_URL_DATA_COLLECTION, data);
	}

	public void init(final CMSSiteModel site, final SiteMapPageEnum siteMapPageEnum)
	{
		final String currentUrlEncodingPattern = getUrlEncoderService().getCurrentUrlEncodingPattern();
		this.put(BASE_URL, getSiteBaseUrlResolutionService().getWebsiteUrlForSite(site, currentUrlEncodingPattern, false, ""));
		this.put(MEDIA_URL, getSiteBaseUrlResolutionService().getMediaUrlForSite(site, false, ""));


		final Collection<SiteMapPageModel> siteMapPages = site.getSiteMapConfig().getSiteMapPages();
		final SiteMapPageModel siteMapPageModel = (SiteMapPageModel) CollectionUtils.find(siteMapPages, new Predicate()
		{
			@Override
			public boolean evaluate(final Object o)
			{
				return ((SiteMapPageModel) o).getCode().equals(siteMapPageEnum);
			}
		});

		if (siteMapPageModel != null)
		{
			this.put(CHANGE_FREQ, siteMapPageModel.getFrequency().getCode());
			this.put(PRIORITY, siteMapPageModel.getPriority());
		}
		else
		{
			this.put(CHANGE_FREQ, SiteMapChangeFrequencyEnum.DAILY.getCode());
			this.put(PRIORITY, Double.valueOf(0.5D));
		}
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	public UrlEncoderService getUrlEncoderService()
	{
		return urlEncoderService;
	}

	public void setUrlEncoderService(final UrlEncoderService urlEncoderService)
	{
		this.urlEncoderService = urlEncoderService;
	}
}
