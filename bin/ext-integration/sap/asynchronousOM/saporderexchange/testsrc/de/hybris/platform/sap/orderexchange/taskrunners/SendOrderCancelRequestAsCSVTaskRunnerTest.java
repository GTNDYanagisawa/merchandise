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
package de.hybris.platform.sap.orderexchange.taskrunners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendOrderCancelRequestToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendToDataHubResult;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;


@SuppressWarnings("javadoc")
@UnitTest
public class SendOrderCancelRequestAsCSVTaskRunnerTest
{

	private SendOrderCancelRequestAsCSVTaskRunner cut;

	@Before
	public void setUp()
	{
		cut = new SendOrderCancelRequestAsCSVTaskRunner();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testRunSuccess() throws RetryLaterException
	{
		final TaskModel taskModelMock = mock(TaskModel.class);
		final OrderCancelRecordEntryModel orderCancelRecordEntryMock = mock(OrderCancelRecordEntryModel.class);
		final OrderModificationRecordModel modificationRecordMock = mock(OrderModificationRecordModel.class);
		final DefaultSendOrderCancelRequestToDataHubHelper cSVHelperMock = mock(DefaultSendOrderCancelRequestToDataHubHelper.class);
		final TaskService paramTaskService = null;

		when(taskModelMock.getContext()).thenReturn(orderCancelRecordEntryMock);
		when(orderCancelRecordEntryMock.getModificationRecord()).thenReturn(modificationRecordMock);
		when(cSVHelperMock.createAndSendRawItem(orderCancelRecordEntryMock)).thenReturn(
				new DefaultSendToDataHubResult(HttpStatus.OK, ""));

		cut.setOrderCancelRequestCSVHelper(cSVHelperMock);
		cut.run(paramTaskService, taskModelMock);

		verify(cSVHelperMock).createAndSendRawItem(orderCancelRecordEntryMock);
	}

	@Test
	public void testRunFailWithRetry()
	{
		final TaskModel taskModelMock = mock(TaskModel.class);
		final OrderCancelRecordEntryModel orderCancelRecordEntryMock = mock(OrderCancelRecordEntryModel.class);
		final OrderModificationRecordModel modificationRecordMock = mock(OrderModificationRecordModel.class);
		final DefaultSendOrderCancelRequestToDataHubHelper cSVHelperMock = mock(DefaultSendOrderCancelRequestToDataHubHelper.class);
		final TaskService paramTaskService = null;
		final OrderModel order = new OrderModel();
		final ModelService modelService = mock(ModelService.class);

		when(taskModelMock.getContext()).thenReturn(orderCancelRecordEntryMock);
		when(orderCancelRecordEntryMock.getModificationRecord()).thenReturn(modificationRecordMock);
		when(cSVHelperMock.createAndSendRawItem(orderCancelRecordEntryMock)).thenReturn(
				new DefaultSendToDataHubResult(HttpStatus.FORBIDDEN, ""));
		when(modificationRecordMock.getOrder()).thenReturn(order);

		cut.setModelService(modelService);
		cut.setOrderCancelRequestCSVHelper(cSVHelperMock);
		try
		{
			cut.run(paramTaskService, taskModelMock);
		}
		catch (final RetryLaterException e)
		{
			verify(cSVHelperMock).createAndSendRawItem(orderCancelRecordEntryMock);
			verify(modelService).save(order);
			assertEquals(OrderStatus.CANCELLING, order.getStatus());
			assertEquals(SendOrderCancelRequestAsCSVTaskRunner.DEFAULT_RETRY_DELAY, e.getDelay());
			return;
		}
		fail("RetryLaterException missing");
	}

	@Test
	public void testRunFailWithoutRetry() throws RetryLaterException
	{
		final TaskModel taskModelMock = mock(TaskModel.class);
		final OrderCancelRecordEntryModel orderCancelRecordEntryMock = mock(OrderCancelRecordEntryModel.class);
		final OrderModificationRecordModel modificationRecordMock = mock(OrderModificationRecordModel.class);
		final DefaultSendOrderCancelRequestToDataHubHelper cSVHelperMock = mock(DefaultSendOrderCancelRequestToDataHubHelper.class);
		final TaskService paramTaskService = null;
		final OrderModel order = new OrderModel();
		final ModelService modelService = mock(ModelService.class);

		when(taskModelMock.getContext()).thenReturn(orderCancelRecordEntryMock);
		when(orderCancelRecordEntryMock.getModificationRecord()).thenReturn(modificationRecordMock);
		when(cSVHelperMock.createAndSendRawItem(orderCancelRecordEntryMock)).thenReturn(
				new DefaultSendToDataHubResult(HttpStatus.FORBIDDEN, ""));
		when(modificationRecordMock.getOrder()).thenReturn(order);
		when(taskModelMock.getRetry()).thenReturn(Integer.valueOf(11));

		cut.setModelService(modelService);
		cut.setOrderCancelRequestCSVHelper(cSVHelperMock);
		cut.run(paramTaskService, taskModelMock);

		verify(cSVHelperMock).createAndSendRawItem(orderCancelRecordEntryMock);
		verify(modelService).save(order);
		assertEquals(OrderStatus.CANCELLING, order.getStatus());
	}

	@Test
	public void testHandleError()
	{
		final OrderCancelRecordEntryModel orderCancelRecordEntryMock = mock(OrderCancelRecordEntryModel.class);
		final OrderModificationRecordModel modificationRecordMock = mock(OrderModificationRecordModel.class);
		final OrderModel order = new OrderModel();
		final ModelService modelService = mock(ModelService.class);
		final TaskModel taskModelMock = mock(TaskModel.class);

		when(orderCancelRecordEntryMock.getModificationRecord()).thenReturn(modificationRecordMock);
		when(modificationRecordMock.getOrder()).thenReturn(order);
		when(taskModelMock.getContext()).thenReturn(orderCancelRecordEntryMock);

		cut.setModelService(modelService);
		cut.handleError(null, taskModelMock, null);

		verify(modelService).save(order);
		assertEquals(OrderStatus.CANCELLING, order.getStatus());
	}

	@Test
	public void testSetGet()
	{
		cut.setMaxRetries(10);
		assertEquals(10, cut.getMaxRetries());

		cut.setRetryDelay(3600);
		assertEquals(3600, cut.getRetryDelay());

		final ModelService modelService = new DefaultModelService();
		cut.setModelService(modelService);
		assertSame(modelService, cut.getModelService());
	}
}
