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
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointOfServicePageSiteMapGenerator extends AbstractSiteMapGenerator<PointOfServiceModel>
{
	@Override
	public List<SiteMapUrlData> getSiteMapUrlData(final List<PointOfServiceModel> models)
	{
		return Converters.convertAll(models, getSiteMapUrlDataConverter());
	}

	@Override
	protected List<PointOfServiceModel> getDataInternal(final CMSSiteModel siteModel)
	{
		final List<BaseStoreModel> stores = siteModel.getStores();
		final String query = "SELECT {ps.pk} FROM {PointOfService as ps} WHERE {ps.BaseStore} in (?stores)";

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("stores", stores);
		return doSearch(query, params, PointOfServiceModel.class);
	}
}
