package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercewebservicescommons.dto.queues.OrderStatusUpdateElementListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.queues.OrderStatusUpdateElementWsDTO;
import de.hybris.platform.ycommercewebservices.formatters.WsDateFormatter;
import de.hybris.platform.ycommercewebservices.queues.data.OrderStatusUpdateElementData;
import de.hybris.platform.ycommercewebservices.queues.impl.OrderStatusUpdateQueue;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/{baseSiteId}/feeds")
public class FeedsController extends BaseController
{
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
	@Resource(name = "orderStatusUpdateQueue")
	private OrderStatusUpdateQueue orderStatusUpdateQueue;


	/**
	 * Order status updates the feed. Returns only elements from the current baseSite, newer than the specified
	 * timestamp.
	 *
	 * @queryparam timestamp Only items newer than the given parameter are retrieved. This parameter should be in
	 *             RFC-8601 format.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of order status update
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/orders/statusfeed", method = RequestMethod.GET)
	@ResponseBody
	public OrderStatusUpdateElementListWsDTO orderStatusFeed(@RequestParam final String timestamp,
			@PathVariable final String baseSiteId,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final Date timestampDate = wsDateFormatter.toDate(timestamp);
		final List<OrderStatusUpdateElementData> orders = orderStatusUpdateQueue.getItems(timestampDate);
		filterOrderStatusQueue(orders, baseSiteId);
		final List<OrderStatusUpdateElementWsDTO> orderStatusUpdateElements = dataMapper.mapAsList(orders,
				OrderStatusUpdateElementWsDTO.class, fields);
		final OrderStatusUpdateElementListWsDTO result = new OrderStatusUpdateElementListWsDTO();
		result.setOrderStatusUpdateElements(orderStatusUpdateElements);
		return result;
	}

	private void filterOrderStatusQueue(final List<OrderStatusUpdateElementData> orders, final String baseSiteId)
	{
		final Iterator<OrderStatusUpdateElementData> dataIterator = orders.iterator();
		while (dataIterator.hasNext())
		{
			final OrderStatusUpdateElementData orderStatusUpdateData = dataIterator.next();
			if (!baseSiteId.equals(orderStatusUpdateData.getBaseSiteId()))
			{
				dataIterator.remove();
			}
		}
	}
}
