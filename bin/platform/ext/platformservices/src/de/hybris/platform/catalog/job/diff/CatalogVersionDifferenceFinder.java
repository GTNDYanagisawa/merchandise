/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.catalog.job.diff;

import de.hybris.platform.catalog.model.CatalogVersionDifferenceModel;
import de.hybris.platform.catalog.model.CompareCatalogVersionsCronJobModel;


/**
 * Abstract difference finder for {@link CompareCatalogVersionsCronJobModel} between two <code>TYPE</code>s.
 */
public interface CatalogVersionDifferenceFinder<TYPE, DIFF extends CatalogVersionDifferenceModel>
{
	/**
	 * Processes difference for a given {@link CompareCatalogVersionsCronJobModel} model.
	 */
	int processDifferences(final CompareCatalogVersionsCronJobModel model);

}
