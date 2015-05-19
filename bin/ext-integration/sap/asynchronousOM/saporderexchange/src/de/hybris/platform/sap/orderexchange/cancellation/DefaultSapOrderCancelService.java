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

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.ordercancel.OrderCancelCallbackService;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.OrderCancelResponse.ResponseStatus;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.beans.factory.annotation.Required;


/**
 * This Class provides several services to cancel a hybris order and do fulfillment on ERP side. It is used when doing a
 * cancellation on ERP side and to set the corresponding order status on hybris side.
 * 
 */
public class DefaultSapOrderCancelService implements SapOrderCancelService
{
	private UserService userService;
	private OrderCancelService orderCancelService;
	private OrderCancelRecordsHandler orderCancelRecordsHandler;
	private OrderCancelCallbackService orderCancelCallbackService;


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.orderexchange.cancellation.OrderCancellation#cancelOrder(de.hybris.platform.core.model.
	 * order .OrderModel)
	 */
	public void cancelOrder(final OrderModel order, final String erpRejectionReason) throws OrderCancelException
	{
		final OrderCancelResponse cancelResponse = new OrderCancelResponse(order, ResponseStatus.full, "");
		cancelResponse.setCancelReason(CancelReason.valueOf(erpRejectionReason));
		createOrderCancelEntryIfNecessary(order, cancelResponse);
		orderCancelCallbackService.onOrderCancelResponse(cancelResponse);
	}

	public void restoreAfterCancelFailed(final OrderModel order) throws OrderCancelException
	{
		final OrderCancelResponse cancelResponse = new OrderCancelResponse(order, ResponseStatus.denied, "");
		orderCancelCallbackService.onOrderCancelResponse(cancelResponse);
	}

	protected void createOrderCancelEntryIfNecessary(final OrderModel order, final OrderCancelResponse cancelResponse)
			throws OrderCancelException
	{
		final EmployeeModel adminUser = userService.getAdminUser();
		if (orderCancelService.getCancelRecordForOrder(order) == null)
		{
			this.orderCancelRecordsHandler.createRecordEntry(cancelResponse, adminUser);
		}
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setOrderCancelRecordsHandler(final OrderCancelRecordsHandler orderCancelRecordsHandler)
	{
		this.orderCancelRecordsHandler = orderCancelRecordsHandler;

	}

	@SuppressWarnings("javadoc")
	@Required
	public void setOrderCancelCallbackService(final OrderCancelCallbackService orderCancelCallbackService)
	{
		this.orderCancelCallbackService = orderCancelCallbackService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setOrderCancelService(final OrderCancelService orderCancelService)
	{
		this.orderCancelService = orderCancelService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
