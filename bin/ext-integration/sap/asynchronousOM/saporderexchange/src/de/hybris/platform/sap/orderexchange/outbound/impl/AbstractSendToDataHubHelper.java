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

package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.sap.orderexchange.outbound.RawItemBuilder;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.servicelayer.model.AbstractItemModel;

import org.apache.poi.hssf.record.formula.functions.T;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;
import com.sun.jersey.api.client.ClientHandlerException;


/**
 * Helper for creating a raw items and sending it to the Data Hub
 * 
 * @param <T>
 */
public class AbstractSendToDataHubHelper<T extends AbstractItemModel> implements SendToDataHubHelper<T>
{
	private static final String DEFAULT_FEED = "DEFAULT_FEED";
	private String feed = DEFAULT_FEED;
	private DataHubOutboundService dataHubOutboundService;
	private String rawItemType;

	private RawItemBuilder<T> rawItemBuilder;

	protected RawItemBuilder<T> getRawItemBuilder()
	{
		return rawItemBuilder;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setRawItemBuilder(final RawItemBuilder<T> csvBuilder)
	{
		this.rawItemBuilder = csvBuilder;
	}

	@SuppressWarnings("javadoc")
	public String getFeed()
	{
		return feed;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setFeed(final String feed)
	{
		this.feed = feed;
	}

	@SuppressWarnings("javadoc")
	public DataHubOutboundService getDataHubOutboundService()
	{
		return dataHubOutboundService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService)
	{
		this.dataHubOutboundService = dataHubOutboundService;
	}

	@SuppressWarnings("javadoc")
	public String getRawItemType()
	{
		return rawItemType;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setRawItemType(final String rawItem)
	{
		this.rawItemType = rawItem;
	}

	@Override
	public SendToDataHubResult createAndSendRawItem(final T model)
	{
		try
		{
			getDataHubOutboundService().sendToDataHub(getFeed(), getRawItemType(), getRawItemBuilder().rowsAsNameValuePairs(model));
		}
		catch (final ClientHandlerException | DataHubOutboundException | DataHubCommunicationException e)
		{
			return new DefaultSendToDataHubResult(SendToDataHubResult.SENDING_FAILED_CODE, e.getMessage());
		}
		return DefaultSendToDataHubResult.OKAY;
	}

}
