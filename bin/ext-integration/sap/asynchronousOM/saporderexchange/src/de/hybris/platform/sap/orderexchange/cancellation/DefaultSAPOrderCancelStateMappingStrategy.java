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

import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelStateMappingStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.Collection;


/**
 * This class overwrites the hybris mapping strategy to ensure a fix strategy to enable system to cancel data via cs
 * cockpit and sending this information to ERP
 * 
 */
public class DefaultSAPOrderCancelStateMappingStrategy implements OrderCancelStateMappingStrategy
{

	public OrderCancelState getOrderCancelState(final OrderModel order)
	{
		final OrderStatus orderStatus = order.getStatus();
		if ((OrderStatus.CANCELLED.equals(orderStatus)) || (OrderStatus.COMPLETED.equals(orderStatus)))
		{
			return OrderCancelState.CANCELIMPOSSIBLE;
		}

		final Collection<ConsignmentModel> consignments = order.getConsignments();
		if ((consignments != null) && (!consignments.isEmpty()))
		{
			return OrderCancelState.CANCELIMPOSSIBLE;
		}

		return OrderCancelState.SENTTOWAREHOUSE;
	}
}
