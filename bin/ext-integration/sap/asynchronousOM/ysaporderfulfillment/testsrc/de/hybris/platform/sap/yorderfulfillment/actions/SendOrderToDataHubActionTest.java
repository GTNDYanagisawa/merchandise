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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendToDataHubResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;



@UnitTest
@SuppressWarnings("javadoc")
public class SendOrderToDataHubActionTest
{
	private static final String PROCESS_CODE = "4711";
	private static final String ORDER_CODE = "0815";
	private final OrderModel orderModel = new OrderModel();
	private final OrderProcessModel orderProcessModel = new OrderProcessModel();

	private ModelService modelService = EasyMock.createNiceMock(ModelService.class);
	private SendOrderToDataHubAction classUnderTest;
	private SendToDataHubHelper<OrderModel> helper;


	@Before
	public void setUp()
	{
		classUnderTest = new SendOrderToDataHubAction();
		MockitoAnnotations.initMocks(this);
		orderModel.setCode(ORDER_CODE);
		orderProcessModel.setOrder(orderModel);
		orderProcessModel.setCode(PROCESS_CODE);
		helper = EasyMock.createNiceMock(SendToDataHubHelper.class);
		classUnderTest.setSendOrderToDataHubHelper(helper);
		classUnderTest.setModelService(modelService);
		EasyMock.replay(modelService);
	}


	@Test
	public void testExecuteActionOK() throws Exception
	{
		EasyMock.expect(helper.createAndSendRawItem(orderModel)).andReturn(DefaultSendToDataHubResult.OKAY);
		EasyMock.replay(helper);
		Assert.assertEquals(Transition.OK, classUnderTest.executeAction(orderProcessModel));
	}

	@Test
	public void testExecuteActionNOK() throws Exception
	{
		EasyMock.expect(helper.createAndSendRawItem(orderModel)).andReturn(DefaultSendToDataHubResult.SENDING_FAILED);
		EasyMock.replay(helper);
		classUnderTest.setMaxRetries(0);
		Assert.assertEquals(Transition.NOK, classUnderTest.executeAction(orderProcessModel));
	}

	@Test(expected = RetryLaterException.class)
	public void testExecuteActionRetry() throws Exception
	{
		EasyMock.expect(helper.createAndSendRawItem(orderModel)).andReturn(DefaultSendToDataHubResult.SENDING_FAILED);
		EasyMock.replay(helper);
		classUnderTest.executeAction(orderProcessModel);
	}

	@Test
	public void testNoResetEndMessage()
	{
		final OrderProcessModel process = EasyMock.createMock(OrderProcessModel.class);
		EasyMock.expect(process.getEndMessage()).andReturn("Bla");
		EasyMock.replay(process);
		classUnderTest.resetEndMessage(process);
		EasyMock.verify(process);
	}

	@Test
	public void testResetEndMessage()
	{
		modelService = EasyMock.createNiceMock(ModelService.class);
		final OrderProcessModel process = EasyMock.createMock(OrderProcessModel.class);
		EasyMock.expect(process.getEndMessage()).andReturn(SendOrderToDataHubAction.ERROR_END_MESSAGE);
		process.setEndMessage("");
		EasyMock.expectLastCall();
		modelService.save(process);
		EasyMock.expectLastCall();
		EasyMock.replay(modelService, process);
		classUnderTest.resetEndMessage(process);
		EasyMock.verify(process);
	}

}
