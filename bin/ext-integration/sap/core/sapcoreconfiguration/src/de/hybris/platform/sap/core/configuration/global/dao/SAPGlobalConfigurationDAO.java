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
package de.hybris.platform.sap.core.configuration.global.dao;

import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/**
 * Interface for accessing the data of the global configuration.
 */
public interface SAPGlobalConfigurationDAO
{

	/**
	 * Finds the {@link SAPGlobalConfigurationModel} by performing a FlexibleSearch using the
	 * {@link FlexibleSearchService}.
	 * 
	 * @return {@link SAPGlobalConfigurationModel}
	 */
	public SAPGlobalConfigurationModel getSAPGlobalConfiguration();
}
