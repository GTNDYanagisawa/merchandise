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

package de.hybris.platform.sap.orderexchange.outbound;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import java.util.List;


/**
 * This interface provides methods to support to repair and resend failed order cancellations trying to send to ERP
 */
@SuppressWarnings("javadoc")
public interface OrderExchangeRepair
{
	List<OrderProcessModel> findAllProcessModelsToRepair(String processName, String endMessage);

	List<OrderProcessModel> findProcessesByActionIds(String processName, String processActions[]);

	List<OrderModel> findAllOrdersInStatus(OrderStatus orderStatus);
}
