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
package de.hybris.platform.sap.orderexchange.datahub.inbound.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/**
 * Abstract root class for inbound processing of order related notifications from Data Hub
 */
public abstract class AbstractDataHubInboundHelper
{
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@SuppressWarnings("javadoc")
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@SuppressWarnings("javadoc")
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@SuppressWarnings("javadoc")
	public ModelService getModelService()
	{
		return modelService;
	}

	@SuppressWarnings("javadoc")
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@SuppressWarnings("javadoc")
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@SuppressWarnings("javadoc")
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected OrderModel readOrder(final String orderCode)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {o:pk} FROM {Order AS o} WHERE  {o.code} = ?code AND {o.versionID} IS NULL");
		flexibleSearchQuery.addQueryParameter("code", orderCode);

		final OrderModel order = getFlexibleSearchService().searchUnique(flexibleSearchQuery);
		if (order == null)
		{
			final String msg = "Error while IDoc processing. Called with not existing order for order code : " + orderCode;
			throw new IllegalArgumentException(msg);
		}
		return order;
	}
}
