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
package de.hybris.platform.yb2bacceleratorcore.event;

import de.hybris.platform.b2b.event.OrderRejectedEvent;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;


public class OrderRejectedEventListener extends AbstractOrderEventListener<OrderRejectedEvent>
{
	@Override
	protected void onEvent(final OrderRejectedEvent event)
	{
		final OrderModel order = event.getOrder();
		final UserModel user = (UserModel) event.getApprover();
		setSessionLocaleForOrder(order);

		String comment = "";
		for (final B2BPermissionResultModel permissionResult : order.getPermissionResults())
		{
			if (permissionResult.getApprover().equals(user))
			{
				if ((comment = permissionResult.getNote(getI18NService().getCurrentLocale())) != null)
				{
					break;
				}
			}
		}
		createOrderHistoryEntry(user, order, OrderStatus.REJECTED, comment);
	}
}
