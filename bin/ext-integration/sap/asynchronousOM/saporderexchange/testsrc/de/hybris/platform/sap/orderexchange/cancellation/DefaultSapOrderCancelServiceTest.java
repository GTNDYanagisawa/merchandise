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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.ordercancel.OrderCancelCallbackService;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.OrderCancelResponse.ResponseStatus;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultSapOrderCancelServiceTest
{
	private static final String ERP_REJECTION_REASON = "00";

	@InjectMocks
	private DefaultSapOrderCancelService classUnderTest;
	@Mock
	private UserService userService;
	@Mock
	private OrderCancelService orderCancelService;
	@Mock
	private OrderCancelRecordsHandler orderCancelRecordsHandler;
	@Mock
	private OrderCancelCallbackService orderCancelCallbackService;

	private OrderModel order;

	@Before
	public void setUp()
	{
		classUnderTest = new DefaultSapOrderCancelService();
		MockitoAnnotations.initMocks(this);

		final EmployeeModel adminUser = new EmployeeModel();
		Mockito.when(userService.getAdminUser()).thenReturn(adminUser);

		order = buildOrder();
	}

	private OrderModel buildOrder()
	{
		final OrderModel order = new OrderModel();
		final AbstractOrderEntryModel entry = new OrderEntryModel();
		entry.setQuantity(Long.parseLong("99"));
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entry);
		order.setEntries(entries);
		return order;
	}




	@Test
	public void testOrderCancelRecordIsNotCreatedIfAlreadyExisting() throws OrderCancelException
	{
		final OrderCancelRecordModel orderCancelRecordModel = new OrderCancelRecordModel();
		Mockito.when(orderCancelService.getCancelRecordForOrder(order)).thenReturn(orderCancelRecordModel);

		classUnderTest.cancelOrder(order, ERP_REJECTION_REASON);

		Mockito.verify(orderCancelRecordsHandler, Mockito.times(0)).createRecordEntry(Mockito.any(OrderCancelRequest.class),
				Mockito.any(EmployeeModel.class));
	}



	@Test
	public void testRestoreAfterCancelFailedUnderlyingServiceIsCalledCorrectly() throws Exception
	{
		final ArgumentCaptor<OrderCancelResponse> orderCancelResponseCapture = ArgumentCaptor.forClass(OrderCancelResponse.class);

		classUnderTest.restoreAfterCancelFailed(order);

		Mockito.verify(orderCancelCallbackService).onOrderCancelResponse(orderCancelResponseCapture.capture());
		final OrderCancelResponse capturedResponse = orderCancelResponseCapture.getValue();

		Assert.assertSame(order, capturedResponse.getOrder());
		Assert.assertEquals(ResponseStatus.denied, capturedResponse.getResponseStatus());
	}



}
