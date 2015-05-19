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
package de.hybris.platform.sap.core.configuration.global.dao.impl;

import de.hybris.platform.sap.core.configuration.exception.ConfigurationBaseRuntimeException;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Implementation for accessing the data of the global configuration.
 */
public class SAPGlobalConfigurationDAOImpl implements SAPGlobalConfigurationDAO
{
	/**
	 * {@link FlexibleSearchService} for running queries against the database.
	 * 
	 * @see "https://wiki.hybris.com/display/release5/FlexibleSearch"
	 */
	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Override
	public SAPGlobalConfigurationModel getSAPGlobalConfiguration()
	{
		final String queryString = "SELECT {p:" + SAPGlobalConfigurationModel.PK + "}" + "FROM {"
				+ SAPGlobalConfigurationModel._TYPECODE + " AS p}";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);

		final List<SAPGlobalConfigurationModel> result = flexibleSearchService.<SAPGlobalConfigurationModel> search(query)
				.getResult();
		if (result.size() > 1)
		{
			throw new ConfigurationBaseRuntimeException("There can't be more than one SAPGlobalConfigurationModel!");
		}
		if (result.size() == 0)
		{
			return null;
		}
		return result.get(0);
	}
}
