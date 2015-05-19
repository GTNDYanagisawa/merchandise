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

import de.hybris.platform.b2b.event.OrderApprovedEvent;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class OrderApprovedEventListener extends AbstractOrderEventListener<OrderApprovedEvent>
{
	private static final Logger LOG = Logger.getLogger(OrderApprovedEventListener.class);

	@Override
	protected void onEvent(final OrderApprovedEvent event)
	{
		final OrderModel order = event.getOrder();
		final UserModel user = (UserModel) event.getApprover();
		setSessionLocaleForOrder(order);

		String comment = "";
		for (final B2BPermissionResultModel permissionResult : order.getPermissionResults())
		{
			if (permissionResult.getApprover().equals(user)
					&& (comment = permissionResult.getNote(getI18NService().getCurrentLocale())) != null)
			{
				break;
			}
		}
		createOrderHistoryEntry(user, order, OrderStatus.APPROVED, comment);

		final String submitOrderProcessCode = order.getStore().getSubmitOrderProcessCode();
		if (!StringUtils.isEmpty(submitOrderProcessCode))
		{
			final OrderProcessModel businessProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
					submitOrderProcessCode + "-" + order.getCode() + "-" + System.currentTimeMillis(), submitOrderProcessCode);
			businessProcessModel.setOrder(order);
			getBusinessProcessService().startProcess(businessProcessModel);
		}
		else
		{
			LOG.error("Unable to start fulfilment process for order [" + order.getCode() + "]. Store [" + order.getStore().getUid()
					+ "] has missing SubmitOrderProcessCode");
		}
	}

}
