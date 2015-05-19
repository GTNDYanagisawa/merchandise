/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.integration.oms.order.dataimport.task;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.integration.oms.order.service.OmsOrderService;
import de.hybris.platform.omsorders.notification.ModelChangeNotifier;
import de.hybris.platform.omsorders.services.query.daos.SyncDao;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.oms.domain.order.Order;
import com.hybris.oms.domain.order.OrderLine;
import com.hybris.oms.domain.order.OrderLineQuantity;
import com.hybris.oms.domain.types.Quantity;


@RunWith(MockitoJUnitRunner.class)
public class OmsOrderSourcingCheckTaskRunnerTest
{
	private OmsOrderService omsOrderService;
	private ModelChangeNotifier<OrderModel> orderTicketSystemNotifier;
	private SyncDao<OrderModel> orderSyncDao;

	private OmsOrderSourcingCheckTaskRunner taskRunner;
	private TaskModel taskModel;

	private final String orderId = "1234567890";
	private final String orderId2 = "1234567891";
	private Order order;
	private Order order2;

	@Before
	public void setup()
	{
		omsOrderService = BDDMockito.mock(OmsOrderService.class);
		orderTicketSystemNotifier = BDDMockito.mock(ModelChangeNotifier.class);
		orderSyncDao = BDDMockito.mock(SyncDao.class);

		taskRunner = new OmsOrderSourcingCheckTaskRunner();
		taskModel = new FakeTaskModel();

		taskRunner.setOmsOrderService(omsOrderService);
		taskRunner.setOrderTicketSystemNotifier(orderTicketSystemNotifier);
		taskRunner.setOrderSyncDao(orderSyncDao);

		order = createFakeOrderWithEmptyOrderLineQuantity();
		order2 = createFakeOrderWithSomeOrderLineQuantity();
	}

	@Test
	public void notifyOrderFailed() throws Exception
	{
		final OrderModel orderModel = new OrderModel();

		BDDMockito.when(omsOrderService.getOrderByOrderId(orderId)).thenReturn(order);
		BDDMockito.when(orderSyncDao.findById(OrderModel.CODE, orderId)).thenReturn(orderModel);

		taskModel.setContext(orderId);
		taskRunner.run(null, taskModel);

		BDDMockito.verify(orderTicketSystemNotifier, Mockito.atLeastOnce()).notify(orderModel);
	}

	@Test(expected = RetryLaterException.class)
	public void notifyOrderFailed_RetryWhenExceptionOccours() throws Exception
	{
		final OrderModel orderModel = new OrderModel();

		BDDMockito.when(omsOrderService.getOrderByOrderId(orderId)).thenReturn(order);
		BDDMockito.when(orderSyncDao.findById(OrderModel.CODE, orderId)).thenReturn(orderModel);
		BDDMockito.doThrow(new RuntimeException()).when(orderTicketSystemNotifier).notify(orderModel);

		taskModel.setContext(orderId);
		taskRunner.run(null, taskModel);
	}

	@Test(expected = RetryLaterException.class)
	public void notifyOrderFailed_RetryWhenExceptionOccurs() throws Exception
	{
		final OrderModel orderModel = new OrderModel();

		BDDMockito.when(omsOrderService.getOrderByOrderId(orderId2)).thenReturn(order2);
		BDDMockito.when(orderSyncDao.findById(OrderModel.CODE, orderId2)).thenReturn(orderModel);
		BDDMockito.doThrow(new RuntimeException()).when(orderTicketSystemNotifier).notify(orderModel);

		taskModel.setContext(orderId2);
		taskRunner.run(null, taskModel);
	}

	private Order createFakeOrderWithEmptyOrderLineQuantity()
	{
		final List<OrderLine> orderLines = new ArrayList<>();
		final OrderLine orderLine = new OrderLine();
		orderLine.setOrderLineQuantities(new ArrayList<OrderLineQuantity>());
		orderLines.add(orderLine);

		final Order order = new Order();
		order.setOrderId(orderId);
		order.setOrderLines(orderLines);

		return order;
	}


	private Order createFakeOrderWithSomeOrderLineQuantity()
	{
		final List<OrderLine> orderLines = new ArrayList<>();
		final OrderLine orderLine1 = new OrderLine();
		orderLine1.setOrderLineQuantities(new ArrayList<OrderLineQuantity>());
		orderLines.add(orderLine1);
		final OrderLine orderLine2 = new OrderLine();
		final List<OrderLineQuantity> orderLineQuantities = new ArrayList<>();
		final OrderLineQuantity orderLineQuantity = new OrderLineQuantity();
		orderLineQuantity.setQuantity(new Quantity());
		orderLineQuantities.add(orderLineQuantity);
		orderLine2.setOrderLineQuantities(orderLineQuantities);
		orderLines.add(orderLine2);

		final Order order = new Order();
		order.setOrderId(orderId2);
		order.setOrderLines(orderLines);

		return order;
	}

}

class FakeTaskModel extends TaskModel
{
	@Override
	public Integer getRetry()
	{
		return Integer.valueOf(10);
	}
}
