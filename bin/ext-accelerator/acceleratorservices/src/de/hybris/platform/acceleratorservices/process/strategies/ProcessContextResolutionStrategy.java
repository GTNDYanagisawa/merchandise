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
package de.hybris.platform.acceleratorservices.process.strategies;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Strategy to impersonate site and initialize session context from the process model.
 */
public interface ProcessContextResolutionStrategy<T extends BaseSiteModel>
{
	/**
	 * Impersonate site, and initializes session currency and session language for the given business process.
	 * 
	 * @param businessProcessModel
	 *           the business process
	 */
	void initializeContext(BusinessProcessModel businessProcessModel);

	/**
	 * Resolves content catalog version to be used for the given business process.
	 * 
	 * @param businessProcessModel
	 *           the business process
	 * @return the content catalog version
	 */
	CatalogVersionModel getContentCatalogVersion(BusinessProcessModel businessProcessModel);

	/**
	 * Resolves site to be used for the given business process.
	 * 
	 * @param businessProcessModel
	 *           the business process
	 * @return the cms site
	 */
	T getCmsSite(BusinessProcessModel businessProcessModel);
}
