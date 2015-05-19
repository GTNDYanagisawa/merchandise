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
import de.hybris.platform.converters.Converters;
import de.hybris.platform.util.FlexibleSearchUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContentPageModelSiteMapGenerator extends AbstractSiteMapGenerator<ContentPageModel>
{

	@Override
	public List<SiteMapUrlData> getSiteMapUrlData(final List<ContentPageModel> models)
	{
		return Converters.convertAll(models, getSiteMapUrlDataConverter());
	}

	@Override
	protected List<ContentPageModel> getDataInternal(final CMSSiteModel siteModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(ContentPageModel.DEFAULTPAGE, Boolean.TRUE);

		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT {cp." + ContentPageModel.PK + "} FROM {" + ContentPageModel._TYPECODE
				+ " AS cp JOIN CMSLinkComponent AS clc ON {cp.pk}={clc.contentPage}} WHERE ");
		queryBuilder
				.append(FlexibleSearchUtils.buildOracleCompatibleCollectionStatement("{cp." + ContentPageModel.CATALOGVERSION
						+ "} in (?catalogVersions)", "catalogVersions", "OR", getCatalogVersionService().getSessionCatalogVersions(),
						params));
		queryBuilder.append(" AND {cp." + ContentPageModel.DEFAULTPAGE + "}  = ?defaultPage");


		return doSearch(queryBuilder.toString(), params, ContentPageModel.class);

	}
}
