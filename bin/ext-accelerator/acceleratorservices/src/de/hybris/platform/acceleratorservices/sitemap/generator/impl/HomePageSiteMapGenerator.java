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
package de.hybris.platform.acceleratorservices.sitemap.generator.impl;

import de.hybris.platform.acceleratorservices.sitemap.data.SiteMapUrlData;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.converters.Converters;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Required;


public class HomePageSiteMapGenerator extends AbstractSiteMapGenerator<ContentPageModel>
{

	private CMSPageService cmsPageService;

	@Override
	public List<SiteMapUrlData> getSiteMapUrlData(final List<ContentPageModel> models)
	{
		return Converters.convertAll(models, getSiteMapUrlDataConverter());
	}

	@Override
	protected List<ContentPageModel> getDataInternal(final CMSSiteModel siteModel)
	{
		final ContentPageModel homepage = getCmsPageService().getHomepage();
		return Collections.singletonList(homepage);
	}

	protected CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	@Required
	public void setCmsPageService(final CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}
}
