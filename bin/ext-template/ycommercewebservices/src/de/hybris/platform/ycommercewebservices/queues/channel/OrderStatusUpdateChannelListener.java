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
 */
package de.hybris.platform.ycommercewebservices.queues.channel;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ycommercewebservices.queues.UpdateQueue;
import de.hybris.platform.ycommercewebservices.queues.data.OrderStatusUpdateElementData;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class OrderStatusUpdateChannelListener
{
	private static final Logger LOG = Logger.getLogger(OrderStatusUpdateChannelListener.class);
	private UpdateQueue<OrderStatusUpdateElementData> orderStatusUpdateQueue;
	private Converter<OrderModel, OrderStatusUpdateElementData> orderStatusUpdateElementConverter;

	public void onMessage(final OrderModel order)
	{
		LOG.debug("OrderStatusUpdateChannelListener got new status for order with code " + order.getCode());
		final OrderStatusUpdateElementData orderStatusUpdateElementData = getOrderStatusUpdateElementConverter().convert(order);
		getOrderStatusUpdateQueue().addItem(orderStatusUpdateElementData);
	}

	public UpdateQueue<OrderStatusUpdateElementData> getOrderStatusUpdateQueue()
	{
		return orderStatusUpdateQueue;
	}

	@Required
	public void setOrderStatusUpdateQueue(final UpdateQueue<OrderStatusUpdateElementData> orderStatusUpdateQueue)
	{
		this.orderStatusUpdateQueue = orderStatusUpdateQueue;
	}

	public Converter<OrderModel, OrderStatusUpdateElementData> getOrderStatusUpdateElementConverter()
	{
		return orderStatusUpdateElementConverter;
	}

	@Required
	public void setOrderStatusUpdateElementConverter(
			final Converter<OrderModel, OrderStatusUpdateElementData> orderStatusUpdateElementConverter)
	{
		this.orderStatusUpdateElementConverter = orderStatusUpdateElementConverter;
	}

}
