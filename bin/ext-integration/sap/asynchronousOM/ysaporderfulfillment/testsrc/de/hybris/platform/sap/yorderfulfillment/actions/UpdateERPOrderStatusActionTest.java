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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class UpdateERPOrderStatusActionTest
{
	private static final String PROCESS_CODE = "4711";
	private static final String ORDER_CODE = "0815";
	private final OrderModel orderModel = new OrderModel();
	private final OrderProcessModel orderProcessModel = new OrderProcessModel();

	@Mock
	private ModelService modelService;


	@InjectMocks
	private final SetConfirmationStatusAction updateAction = new SetConfirmationStatusAction();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		orderModel.setCode(ORDER_CODE);

		orderProcessModel.setOrder(orderModel);
		orderProcessModel.setCode(PROCESS_CODE);
	}

	@Test
	public void testUpdateERPOrderStatus() throws Exception
	{
		final Transition transition = updateAction.executeAction(orderProcessModel);
		Assert.assertEquals("OK", transition.name().toString());
		// NOTOK status need to be considered afterwards as well
		Assert.assertEquals(OrderStatus.CREATED, orderModel.getStatus());
	}

	@Test
	public void testSaveWasCalled() throws Exception
	{
		updateAction.executeAction(orderProcessModel);
		Mockito.verify(modelService).save(orderModel);
	}


}
