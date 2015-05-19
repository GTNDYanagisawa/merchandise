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

package de.hybris.platform.sap.yorderfulfillment.actions;


import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 * Class for setting the Hybris order and consignment status after receiving an goods issue information from the SAP ERP
 * backend
 * 
 */
public class SetCompletionStatusAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();
		setOrderStatus(order);
		setConsignmentStatus(order);
		save(order);

		return Transition.OK;
	}

	protected void setOrderStatus(final OrderModel order)
	{
		setOrderStatus(order, OrderStatus.COMPLETED);
		order.setDeliveryStatus(DeliveryStatus.SHIPPED);
	}

	protected void setConsignmentStatus(final OrderModel order)
	{
		for (final ConsignmentModel consignment : order.getConsignments())
		{
			consignment.setStatus(ConsignmentStatus.SHIPPED);
			save(consignment);
		}
	}
}
