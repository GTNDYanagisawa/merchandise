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
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.converters.Converters;
import java.util.List;


public class CustomPageSiteMapGenerator extends AbstractSiteMapGenerator<String>
{
	@Override
	public List<SiteMapUrlData> getSiteMapUrlData(final List<String> models)
	{
		return Converters.convertAll(models, getSiteMapUrlDataConverter());
	}

	@Override
	protected List<String> getDataInternal(final CMSSiteModel siteModel)
	{
		return (List<String>) siteModel.getSiteMapConfig().getCustomUrls();
	}
}
