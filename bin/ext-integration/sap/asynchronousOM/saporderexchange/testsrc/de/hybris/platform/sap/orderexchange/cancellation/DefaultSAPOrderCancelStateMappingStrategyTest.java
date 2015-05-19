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


import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultSAPOrderCancelStateMappingStrategyTest
{
	private final DefaultSAPOrderCancelStateMappingStrategy classUnderTest = new DefaultSAPOrderCancelStateMappingStrategy();
	private final OrderModel orderModel = new OrderModel();

	@Before
	public void setUp()
	{
		orderModel.setCode("0815");
	}

	@Test
	public void testOrderStateCancelled() throws Exception
	{
		orderModel.setStatus(OrderStatus.CANCELLED);
		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderStateCompleted() throws Exception
	{
		orderModel.setStatus(OrderStatus.COMPLETED);
		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderStateCancelling() throws Exception
	{
		orderModel.setStatus(OrderStatus.CANCELLING);
		Assert.assertEquals(OrderCancelState.SENTTOWAREHOUSE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderStatePending() throws Exception
	{
		orderModel.setStatus(OrderStatus.CHECKED_VALID);
		Assert.assertEquals(OrderCancelState.SENTTOWAREHOUSE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderWithConsignment()
	{
		final ConsignmentModel consignment = mock(ConsignmentModel.class);
		final Set<ConsignmentModel> consignmentSet = new HashSet<ConsignmentModel>();

		consignmentSet.add(consignment);
		orderModel.setStatus(OrderStatus.CHECKED_VALID);
		orderModel.setConsignments(consignmentSet);

		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, classUnderTest.getOrderCancelState(orderModel));
	}
}
