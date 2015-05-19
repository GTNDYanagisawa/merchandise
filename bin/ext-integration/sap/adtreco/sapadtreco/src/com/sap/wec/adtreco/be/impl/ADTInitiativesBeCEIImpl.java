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
package com.sap.wec.adtreco.be.impl;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObjectBase;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.configuration.http.HTTPDestination;
import de.hybris.platform.sap.core.configuration.http.impl.HTTPDestinationServiceImpl;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.wec.adtreco.be.ODataClientService;
import com.sap.wec.adtreco.be.intf.ADTInitiativesBE;


/**
 *
 */
@BackendType("CEI")
public class ADTInitiativesBeCEIImpl extends BackendBusinessObjectBase implements ADTInitiativesBE
{
	@Resource(name = "sapCoreHTTPDestinationService")
	private HTTPDestinationServiceImpl httpDestinationService;
	private HTTPDestination httpDestination;

	private static final String SERVICE_URL = "/sap/opu/odata/sap/CUAN_COMMON_SRV";
	protected String path;
	protected ODataClientService clientService;
	protected String httpDestinationId;

	public String getHttpDestinationId()
	{
		return httpDestinationId;
	}

	public void setHttpDestinationId(final String httpDestinationId)
	{
		this.httpDestinationId = httpDestinationId;
	}

	public ODataClientService getClientService()
	{
		return clientService;
	}

	public void setClientService(final ODataClientService clientService)
	{
		this.clientService = clientService;
	}

	/**
	 * @return the path
	 */
	public String getPath()
	{
		return this.path;
	}

	/**
	 * @param path
	 *           the path to set
	 */
	public void setPath(final String path)
	{
		this.path = path;
	}

	/**
	 * @return the user
	 */
	public String getUser()
	{
		return this.httpDestination.getUserid();
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return this.httpDestination.getPassword();
	}

	public ODataFeed getInitiatives(final String select, final String filter, final String entitySetName, final String expand)
			throws ODataException, URISyntaxException
	{
		loadDestinations();
		ODataFeed feed = null;
		this.clientService.createService(this.httpDestination.getUserid(), this.httpDestination.getPassword());
		feed = this.clientService.readFeed(path, ODataClientService.APPLICATION_XML, entitySetName, select, filter, expand);
		return feed;
	}

	public ODataEntry getInitiative(final String select, final String keyValue, final String entitySetName)
	{
		loadDestinations();
		ODataEntry entry = null;
		this.clientService.createService(this.httpDestination.getUserid(), this.httpDestination.getPassword());
		try
		{
			entry = this.clientService.readEntry(path, ODataClientService.APPLICATION_XML, entitySetName, select, null, keyValue);
		}
		catch (final URISyntaxException ex)
		{
			(new Exception("Connection to CEI system failed due to wrong URI syntax" + path, ex)).printStackTrace();
		}
		catch (final RuntimeException ex)
		{

		}
		catch (final ODataException ex)
		{
			(new Exception("HTTP Destination is not configured correctly", ex)).printStackTrace();
		}
		catch (final IOException ex)
		{
			(new Exception("Error closing connection to the backend", ex)).printStackTrace();
		}
		return entry;
	}

	public void loadDestinations()
	{

		if (this.httpDestinationService != null)
		{
			this.httpDestination = this.httpDestinationService.getHTTPDestination(httpDestinationId);
			if (this.httpDestination != null)
			{
				this.path = this.httpDestination.getTargetURL() + SERVICE_URL;
			}
		}
	}

}
