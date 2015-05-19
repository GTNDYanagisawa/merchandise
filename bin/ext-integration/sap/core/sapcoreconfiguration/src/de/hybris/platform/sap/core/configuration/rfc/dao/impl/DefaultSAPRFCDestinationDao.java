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
package de.hybris.platform.sap.core.configuration.rfc.dao.impl;

import de.hybris.platform.sap.core.configuration.jalo.SAPRFCDestination;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.rfc.dao.SAPRFCDestinationDao;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;


/**
 * Default implementation of the DAO for SAP RFC Destinations.
 */
public class DefaultSAPRFCDestinationDao extends AbstractItemDao implements SAPRFCDestinationDao
{

	@Override
	public List<SAPRFCDestinationModel> findRfcDestinations()
	{
		final String queryString = //
		"SELECT {p:" + SAPRFCDestinationModel.PK + "} "//
				+ "FROM {" + SAPRFCDestinationModel._TYPECODE + " AS p} ";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		final SearchResult<SAPRFCDestinationModel> result = search(query);
		return result.getResult();

	}

	@Override
	public List<SAPRFCDestinationModel> findRfcDestinationByName(final String destName)
	{
		final String queryString = String.format("SELECT {%s} FROM {%s} WHERE {%s} = ?rfcDestinationName", SAPRFCDestination.PK,
				"SAPRFCDestination", SAPRFCDestination.RFCDESTINATIONNAME);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("rfcDestinationName", destName);
		final SearchResult<SAPRFCDestinationModel> result = search(query);
		return result.getResult();
	}

}
