/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;

import java.util.Date;
import java.util.List;


public interface ChinaOrderService extends OrderService
{
	/**
	 * Fetch orders for the given status and {@link OrderModel#getModifiedtime()} less than or equal to the expired date
	 * 
	 * @param orderStatus
	 * @param expiredDate
	 * @return The list of orders matching orderStatus and expiredDate
	 */
	List<OrderModel> getExpiredOrderForStatus(OrderStatus orderStatus, Date expiredDate);

	List<OrderModel> getUnexportedOrderForStatus(OrderStatus... orderStatus);

	/**
	 * @param code
	 * @return
	 */
	public OrderModel getOrderByCode(final String code);
}
