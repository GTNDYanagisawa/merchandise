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
 */
package de.hybris.platform.ycommercewebservices.v1.controller;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;
import de.hybris.platform.ycommercewebservices.formatters.WsDateFormatter;
import de.hybris.platform.ycommercewebservices.queues.data.OrderStatusUpdateElementData;
import de.hybris.platform.ycommercewebservices.queues.data.OrderStatusUpdateElementDataList;
import de.hybris.platform.ycommercewebservices.queues.impl.OrderStatusUpdateQueue;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Web Service Controller for the ORDERS resource. All methods check orders of the current user. Methods require basic
 * authentication and are restricted to https channel.
 */
@Controller("orderControllerV1")
@RequestMapping(value = "/{baseSiteId}/orders")
public class OrderController extends BaseController
{
	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
	@Resource(name = "orderStatusUpdateQueue")
	private OrderStatusUpdateQueue orderStatusUpdateQueue;

	/**
	 * Web service for getting current user's order information by order code.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/orders/1234 <br>
	 * This method requires authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 * 
	 * @param code
	 *           - order code - must be given as path variable.
	 * 
	 * @return {@link OrderData} as response body.
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	public OrderData getOrder(@PathVariable final String code)
	{
		return orderFacade.getOrderDetailsForCode(code);
	}

	/**
	 * Web service for getting order information by order GUID.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/orders/byGuid/ea134b389f3d04b4821f51aa55c79f0766974f5f<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 * 
	 * @param guid
	 *           - order guid - must be given as path variable.
	 * 
	 * @return {@link OrderData} as response body.
	 */
	@Secured("ROLE_CLIENT")
	@RequestMapping(value = "/byGuid/{guid}", method = RequestMethod.GET)
	@ResponseBody
	public OrderData getOrderByGuid(@PathVariable final String guid)
	{
		return orderFacade.getOrderDetailsForGUID(guid);
	}

	/**
	 * Web service for getting current user's order history data.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/orders?statuses=COMPLETED,CANCELLED&pageSize=5&currentPage=0 <br>
	 * This method requires authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 * 
	 * @param statuses
	 *           - filter for order statuses- optional
	 * @param currentPage
	 *           - pagination parameter- optional
	 * @param pageSize
	 *           - {@link PaginationData} parameter - optional
	 * @param sort
	 *           - sort criterion
	 * 
	 * @return {@link OrderData} as response body.
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public OrderHistoriesData getPagedOrdersForStatuses(@RequestParam(required = false) final String statuses,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(required = false) final String sort)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);

		final OrderHistoriesData orderHistoriesData;
		if (statuses != null)
		{
			final Set<OrderStatus> statusSet = extractOrderStatuses(statuses);
			orderHistoriesData = createOrderHistoriesData(orderFacade.getPagedOrderHistoryForStatuses(pageableData,
					statusSet.toArray(new OrderStatus[statusSet.size()])));
		}
		else
		{
			orderHistoriesData = createOrderHistoriesData(orderFacade.getPagedOrderHistoryForStatuses(pageableData));
		}
		return orderHistoriesData;
	}

	/**
	 * Web service handler for order status update feed. Returns only elements from the current baseSite newer than
	 * specified timestamp. Sample Call: http://localhost:9001/rest/v1/{SITE}/orders/statusFeed<br>
	 * This method requires trusted client authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 * 
	 * @param timestamp
	 *           - time in ISO-8601 format
	 * @return {@link de.hybris.platform.ycommercewebservices.queues.data.OrderStatusUpdateElementDataList}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/statusFeed", method = RequestMethod.GET)
	@ResponseBody
	public OrderStatusUpdateElementDataList expressUpdate(@RequestParam final String timestamp,
			@PathVariable final String baseSiteId)
	{
		final Date timestampDate = wsDateFormatter.toDate(timestamp);
		final OrderStatusUpdateElementDataList orderStatusUpdateDataList = new OrderStatusUpdateElementDataList();
		orderStatusUpdateDataList.setOrderStatusUpdateElements(orderStatusUpdateQueue.getItems(timestampDate));
		filterOrderStatusQueue(orderStatusUpdateDataList, baseSiteId);
		return orderStatusUpdateDataList;
	}

	private void filterOrderStatusQueue(final OrderStatusUpdateElementDataList orderStatusUpdateDataList, final String baseSiteId)
	{
		final Iterator<OrderStatusUpdateElementData> dataIterator = orderStatusUpdateDataList.getOrderStatusUpdateElements()
				.iterator();
		while (dataIterator.hasNext())
		{
			final OrderStatusUpdateElementData orderStatusUpdateData = dataIterator.next();
			if (!baseSiteId.equals(orderStatusUpdateData.getBaseSiteId()))
			{
				dataIterator.remove();
			}
		}
	}

	protected Set<OrderStatus> extractOrderStatuses(final String statuses)
	{
		final String statusesStrings[] = statuses.split(YcommercewebservicesConstants.OPTIONS_SEPARATOR);

		final Set<OrderStatus> statusesEnum = new HashSet<>();
		for (final String status : statusesStrings)
		{
			statusesEnum.add(OrderStatus.valueOf(status));
		}
		return statusesEnum;
	}

	protected OrderHistoriesData createOrderHistoriesData(final SearchPageData<OrderHistoryData> result)
	{
		final OrderHistoriesData orderHistoriesData = new OrderHistoriesData();

		orderHistoriesData.setOrders(result.getResults());
		orderHistoriesData.setSorts(result.getSorts());
		orderHistoriesData.setPagination(result.getPagination());

		return orderHistoriesData;
	}

}
