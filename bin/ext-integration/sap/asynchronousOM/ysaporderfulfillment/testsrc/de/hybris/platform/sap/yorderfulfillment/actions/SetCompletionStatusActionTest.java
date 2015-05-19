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


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.model.MockModelService;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class SetCompletionStatusActionTest
{
	private SetCompletionStatusAction classUnderTest;

	class OrderModelForTest extends OrderModel
	{
		@Override
		public de.hybris.platform.core.PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(9999l);
		}
	}

	class ConsignmentModelForTest extends ConsignmentModel
	{
		@Override
		public de.hybris.platform.core.PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(9999l);
		}
	}

	@Before
	public void setUp()
	{
		classUnderTest = new SetCompletionStatusAction();
		classUnderTest.setModelService(new MockModelService());
	}

	@Test
	public void testExecuteAction() throws Exception
	{
		final OrderProcessModel process = new OrderProcessModel();
		final OrderModel order = new OrderModelForTest();
		final ConsignmentModel consignment = new ConsignmentModelForTest();
		final Set<ConsignmentModel> consignments = new HashSet<ConsignmentModel>();
		consignments.add(consignment);
		order.setConsignments(consignments);
		process.setOrder(order);
		final Transition transition = classUnderTest.executeAction(process);
		Assert.assertEquals(Transition.OK, transition);
		final OrderStatus status = order.getStatus();
		Assert.assertEquals(OrderStatus.COMPLETED, status);
		Assert.assertEquals(DeliveryStatus.SHIPPED, order.getDeliveryStatus());
		for (final ConsignmentModel consignmentOut : order.getConsignments())
		{
			Assert.assertEquals(ConsignmentStatus.SHIPPED, consignmentOut.getStatus());
		}
	}
}
