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
package de.hybris.platform.sap.core.configuration.http.impl;

import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.http.HTTPDestination;
import de.hybris.platform.sap.core.configuration.http.HTTPDestinationService;
import de.hybris.platform.sap.core.configuration.http.dao.SAPHttpDestinationDao;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the HTTP destination service.
 */
public class HTTPDestinationServiceImpl implements HTTPDestinationService
{

	/**
	 * The HTTP destination DAO.
	 */
	private SAPHttpDestinationDao httpDestinationDao;

	/**
	 * @param httpDestinationDao
	 *           the HTTP destination DAO
	 */
	public void setHttpDestinationDao(final SAPHttpDestinationDao httpDestinationDao)
	{
		this.httpDestinationDao = httpDestinationDao;
	}

	@Override
	public HTTPDestination getHTTPDestination(final String destinationName)
	{

		return new HTTPDestinationImpl(getHttpDestinationsByName(destinationName));

	}

	/**
	 * @return return list of http destinations
	 */
	public List<SAPHTTPDestinationModel> getHttpDestinations()
	{
		return httpDestinationDao.findHttpDestinations();
	}

	/**
	 * @param destinationName
	 *           the name of the destination
	 * @return the http destination model
	 */
	public SAPHTTPDestinationModel getHttpDestinationsByName(final String destinationName)
	{

		final List<SAPHTTPDestinationModel> httpDestinationModels = httpDestinationDao.findHttpDestinationsByName(destinationName);

		if (httpDestinationModels.size() == 0)
		{
			throw new UnknownIdentifierException(String.format("No HTTP destination found for HTTP destination name {%s}",
					destinationName));
		}
		else if (httpDestinationModels.size() > 1)
		{
			throw new AmbiguousIdentifierException(String.format(
					"More than one HTTP destination found for HTTP destination name {%s} ", destinationName));
		}
		else
		{
			return httpDestinationModels.get(0);
		}

	}

	@Override
	public String toString()
	{
		final List<HTTPDestinationImpl> httpDestinations = new ArrayList<HTTPDestinationImpl>();
		final List<SAPHTTPDestinationModel> httpDestinationModels = httpDestinationDao.findHttpDestinations();
		for (final SAPHTTPDestinationModel saphttpDestinationModel : httpDestinationModels)
		{
			httpDestinations.add(new HTTPDestinationImpl(saphttpDestinationModel));
		}
		return super.toString() + SapcoreconfigurationConstants.CRLF + "- HTTP Destinations: " + httpDestinations;
	}

}
