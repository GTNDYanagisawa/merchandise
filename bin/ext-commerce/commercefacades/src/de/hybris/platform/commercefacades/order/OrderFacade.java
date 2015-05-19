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
package de.hybris.platform.commercefacades.order;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.List;


/**
 * Order facade interface. An Order Facade should provide access to a user's order history and full details of an order.
 */
public interface OrderFacade
{
	/**
	 * Returns the detail of an Order.
	 *
	 * @param code
	 *           The code of the Order for which to retrieve the detail.
	 * @return The detail of the order with matching code
	 */
	OrderData getOrderDetailsForCode(String code);

	/**
	 *@param guid
	 *         The guid of the Order for which to retrieve the detail.
	 * @return
	 */
	OrderData getOrderDetailsForGUID(final String guid);

	/**
	 * Returns the order history of the current user for given statuses.
	 * 
	 * @param statuses
	 *           array of order statuses to filter the results
	 * @return The order history of the current user.
	 */
	List<OrderHistoryData> getOrderHistoryForStatuses(OrderStatus... statuses);

	/**
	 * Returns the order history of the current user for given statuses.
	 * 
	 * @param pageableData
	 *           paging information
	 * @param statuses
	 *           array of order statuses to filter the results
	 * @return The order history of the current user.
	 */
	SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(PageableData pageableData, OrderStatus... statuses);

    /**
     * Returns the order details for order code
     *
     * @param code
     *          order code
     * @return
     */
    OrderData getOrderDetailsForCodeWithoutUser(String code);
}
