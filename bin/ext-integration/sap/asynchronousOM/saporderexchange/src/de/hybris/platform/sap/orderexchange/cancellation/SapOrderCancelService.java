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
package de.hybris.platform.sap.orderexchange.cancellation;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;


/**
 * This interface provides methods to cancel a hybris order and to restore an order status if cancellation failed
 * 
 */
public interface SapOrderCancelService
{

	/**
	 * Cancel a hybris order,
	 * 
	 * @param order
	 *           the order to be cancelled
	 * @param erpRejectionReason
	 *           rejection reason coming from ERP
	 * @throws OrderCancelException
	 */
	void cancelOrder(OrderModel order, String erpRejectionReason) throws OrderCancelException;

	/**
	 * restore the order status after a failed cancel request
	 * 
	 * @param order
	 * @throws OrderCancelException
	 */
	void restoreAfterCancelFailed(OrderModel order) throws OrderCancelException;

}
