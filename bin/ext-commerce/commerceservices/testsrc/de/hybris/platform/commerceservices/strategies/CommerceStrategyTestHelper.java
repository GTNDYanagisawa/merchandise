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
package de.hybris.platform.commerceservices.strategies;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;


/**
 * Helper class for strategy tests.
 */
public class CommerceStrategyTestHelper
{

	private static final String BASE_SITE_MODEL = "baseSiteModel";

	public BaseSiteModel createSite(final ModelService modelService, final BaseSiteService siteService)
	{
		final BaseSiteModel siteModel = modelService.create(BaseSiteModel.class);
		siteModel.setName(BASE_SITE_MODEL);
		siteModel.setUid(BASE_SITE_MODEL);
		modelService.saveAll();
		siteService.setCurrentBaseSite(siteModel, false);
		return siteModel;
	}

	public BaseStoreModel createStore(final String name, final ModelService modelService)
	{
		final BaseStoreModel store1 = modelService.create(BaseStoreModel.class);
		store1.setUid(name);
		store1.setNet(true);
		return store1;
	}
}
