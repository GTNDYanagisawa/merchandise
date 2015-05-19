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
package de.hybris.platform.sap.sapordermgmtservices.order;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.List;


/**
 * Service representation of an order and order history for SAP synchronous order management
 */
public interface OrderService
{
	/**
	 * Fetches an order from the back end
	 * 
	 * @param code
	 *           Technical ID of the order
	 * @return hybris order representation
	 */
	OrderData getOrderForCode(String code);

	/**
	 * Perform an order search, taking pagination and sorting into account
	 * 
	 * @param pageableData
	 *           Contains paging and sorting attributes
	 * @param statuses
	 *           Order statuses the search should be performed for
	 * @return Search result, including sorting and pagination
	 */
	SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
			final OrderStatus... statuses);

	/**
	 * Perform an order search without pagination and sorting
	 * 
	 * @param statuses
	 *           Order statuses the search should be performed for
	 * @return List of orders
	 */
	List<OrderHistoryData> getOrderHistoryForStatuses(final OrderStatus... statuses);



}
