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


import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.task.TaskConditionModel;


/**
 * Helper class for b2c customer processes within order
 */
public class DefaultB2CCustomerHelper implements B2CCustomerHelper

{
	private static final String SAP_ORDER_CUSTOMER_EVENT = "ERPCustomerReplicationEvent_";
	private FlexibleSearchService flexibleSearchService;
	private SAPGlobalConfigurationService sAPGlobalConfigurationService;
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
	public SAPGlobalConfigurationService getsAPGlobalConfigurationService()
	{
		return sAPGlobalConfigurationService;
	}

	@SuppressWarnings("javadoc")
	public void setsAPGlobalConfigurationService(final SAPGlobalConfigurationService sAPGlobalConfigurationService)
	{
		this.sAPGlobalConfigurationService = sAPGlobalConfigurationService;
	}

	public String determineB2CCustomer(final OrderModel order)
	{
		return Boolean.TRUE.equals(sAPGlobalConfigurationService.getProperty("replicateregistereduser")) ? ((CustomerModel) order
				.getUser()).getSapConsumerID() : null;
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

	@Override
	public void processWaitingCustomerOrders(final String customerID)
	{
		final String orderCustomerSearchExpression = SAP_ORDER_CUSTOMER_EVENT + customerID + "%";
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {o:pk} FROM {TaskCondition AS o} WHERE  {o.uniqueID} LIKE ?uniqueID");
		flexibleSearchQuery.addQueryParameter("uniqueID", orderCustomerSearchExpression);

		final SearchResult<TaskConditionModel> taskConditions = this.flexibleSearchService.search(flexibleSearchQuery);

		for (final TaskConditionModel taskConditionModel : taskConditions.getResult())
		{
			final String trigger = taskConditionModel.getUniqueID();
			businessProcessService.triggerEvent(trigger);
		}

	}
}
