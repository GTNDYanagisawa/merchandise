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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.jalo.BusinessProcess;
import de.hybris.platform.sap.orderexchange.outbound.OrderExchangeRepair;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class is to repair order information (creation oder or cancelling) from hybris to ERP by a cron job. It provides
 * service implementations that can be used to realize this.
 * 
 */
public class DefaultOrderExchangeRepair implements OrderExchangeRepair
{
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;

	public List<OrderProcessModel> findAllProcessModelsToRepair(final String processName, final String endMessage)
	{
		final String query = "select {PK} from {OrderProcess} WHERE {" + BusinessProcess.PROCESSDEFINITIONNAME
				+ "} = ?processDefinitionName " + "and {" + BusinessProcess.STATE + "} = ?statusValue and {"
				+ BusinessProcess.ENDMESSAGE + "} = ?processResult";

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("processDefinitionName", processName);
		searchQuery.addQueryParameter("processResult", endMessage);
		searchQuery.addQueryParameter("statusValue", ProcessState.ERROR);
		final SearchResult<OrderProcessModel> processes = flexibleSearchService.search(searchQuery);
		return processes.getResult();
	}


	@Override
	public List<OrderProcessModel> findProcessesByActionIds(final String processName, final String processCurrentActions[])
	{
		final String query = "select {bp.PK} " + "from {OrderProcess AS bp  JOIN ProcessTask AS pt ON {bp.pk} = {pt.process} } "
				+ "WHERE {bp.processDefinitionName} = ?processDefinitionName and {pt.action} in (?processCurrentActions)";

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("processDefinitionName", processName);
		searchQuery.addQueryParameter("processCurrentActions", Arrays.asList(processCurrentActions));
		final SearchResult<OrderProcessModel> processes = flexibleSearchService.search(searchQuery);
		return processes.getResult();
	}


	@Override
	public List<OrderModel> findAllOrdersInStatus(final OrderStatus orderStatus)
	{
		final String query = "SELECT {pk} FROM {Order} WHERE  {status} = ?status AND {versionID} IS NULL";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("status", orderStatus);
		final SearchResult<OrderModel> orderList = flexibleSearchService.search(searchQuery);
		return orderList.getResult();
	}



	@SuppressWarnings("javadoc")
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@SuppressWarnings("javadoc")
	public ModelService getModelService()
	{
		return modelService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
