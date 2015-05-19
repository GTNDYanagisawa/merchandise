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




import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;



/**
 * Class for setting the Hybris order status after receiving an order confirmation from the SAP ERP backend .
 * 
 */
public class SetConfirmationStatusAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();
		order.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
		setOrderStatus(order, OrderStatus.CREATED);
		return Transition.OK;
	}
}
