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
package de.hybris.platform.ordercancel;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.isA;

import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordercancel.OrderCancelResponse.ResponseStatus;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests complete cancel execution in different scenarios.
 */
public class OrderCancelCompleteTest extends AbstractOrderCancelTest
{
	private static final Logger LOG = Logger.getLogger(OrderCancelCompleteTest.class);



	@Before
	public void customSetUp() throws InvalidCartException
	{
		LOG.info("Setting up OrderCancelDaoTest");
		order = placeTestOrder();
		currentPrincipal = REQUEST_ORIGIN_STRATEGY_CUSTOMER.getCallingPrincipal();
		injectStateMappingStrategy(STATE_STRATEGY_HOLDING_AREA_QUEUE_WAITING);

		configuration = getOrCreateOrderCancelConfig(3600);
		configuration.setPartialCancelAllowed(true);
		configuration.setPartialOrderEntryCancelAllowed(true);
		configuration.setOrderCancelAllowed(true);
		configuration.setCancelAfterWarehouseAllowed(true);
		configuration.setCompleteCancelAfterShippingStartedAllowed(true);

		final OrderCancelDaoMock ocdMock = new OrderCancelDaoMock(configuration);
		this.orderCancelService.setOrderCancelDao(ocdMock);
	}


	/**
	 * Exception should be throws when issuing a complete cancel request when cancel is not possible.
	 */
	@Test(expected = OrderCancelException.class)
	public void testExceptionOnCompleteCancelImpossible() throws OrderCancelException
	{
		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(order);
		configuration.setOrderCancelAllowed(false);
		orderCancelService.requestOrderCancel(completeCancelRequest, currentPrincipal);
	}

	/**
	 * Exception should be throws when issuing a partial cancel request when cancel is not possible.
	 */
	@Test(expected = OrderCancelException.class)
	public void testExceptionOnPartialCancelImpossible() throws OrderCancelException
	{
		final OrderCancelRequest partialCancelRequest = preparePartialCancelRequest(order, 1);
		configuration.setOrderCancelAllowed(false);
		orderCancelService.requestOrderCancel(partialCancelRequest, currentPrincipal);
	}

	@Test
	public void testCompleteCancelInHoldingArea() throws OrderCancelException
	{
		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(order);

		final int numberOfEntries = order.getEntries().size();

		final double originalOrderTotalsSum = calculateOrderTotalSum(order);

		final IMocksControl mockCreator = EasyMock.createStrictControl();
		final OrderCancelPaymentServiceAdapter paymentAdapterMock = mockCreator.createMock(OrderCancelPaymentServiceAdapter.class);
		final OrderCancelNotificationServiceAdapter notificationsAdapterMock = mockCreator
				.createMock(OrderCancelNotificationServiceAdapter.class);

		final Capture<OrderCancelRecordEntryModel> capturedArgument = new Capture<OrderCancelRecordEntryModel>();

		//record mock objects behavior
		paymentAdapterMock.recalculateOrderAndModifyPayments(order);
		notificationsAdapterMock.sendCancelFinishedNotifications(and(capture(capturedArgument),
				isA(OrderCancelRecordEntryModel.class)));
		//notificationsAdapterMock.sendCancelFinishedNotifications(completeCancelRequest);

		injectAdaptersDependencies(notificationsAdapterMock, paymentAdapterMock, null, orderCancelService);
		//		orderCancelService.setNotificationsAdapter(notificationsAdapterMock);
		//		orderCancelService.setPaymentsAdapter(paymentAdapterMock);

		mockCreator.replay();

		final OrderCancelRecordEntryModel cancelRecord = orderCancelService.requestOrderCancel(completeCancelRequest,
				currentPrincipal);

		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
		{
			modelService.refresh(oemrem);
		}

		final double cancelSnapshotTotalsSum = calculateSnapshotTotal(cancelRecord);

		mockCreator.verify();

		Assert.assertEquals("Newly created cancel record should have been passed into notificationServiceAdapter", cancelRecord,
				capturedArgument.getValue());

		Assert.assertEquals("Order should have 3 entries", 3, completeCancelRequest.getOrder().getEntries().size());

		Assert.assertEquals("Order cancel record entry should have " + numberOfEntries + " entries ", numberOfEntries, cancelRecord
				.getOrderEntriesModificationEntries().size());

		Assert.assertEquals(
				"Cancel request order entries snapshot must reflect original order entries (that does no longer exist)",
				originalOrderTotalsSum, cancelSnapshotTotalsSum, 0.00001);

		Assert.assertEquals("Order cancel record should NOT be in progress ", false, cancelRecord.getModificationRecord()
				.isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.SUCCESSFULL,
				OrderModificationEntryStatus.SUCCESSFULL, cancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.FULL.getCode(),
				OrderCancelEntryStatus.FULL, cancelRecord.getCancelResult());

		Assert.assertEquals("Order should be in " + OrderStatus.CANCELLED.getCode() + " state", OrderStatus.CANCELLED,
				completeCancelRequest.getOrder().getStatus());

	}

	private double calculateSnapshotTotal(final OrderCancelRecordEntryModel cancelRecord)
	{
		final double cancelSnapshotTotalsSum;
		{
			double tmp = 0;
			for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
			{
				tmp += oemrem.getOriginalOrderEntry().getTotalPrice().doubleValue();
			}
			cancelSnapshotTotalsSum = tmp;
		}
		return cancelSnapshotTotalsSum;
	}

	private double calculateOrderTotalSum(final OrderModel order)
	{
		final double originalOrderTotalsSum;
		{
			double tmp = 0;
			for (final AbstractOrderEntryModel orderEntry : order.getEntries())
			{
				tmp += orderEntry.getTotalPrice().doubleValue();
			}
			originalOrderTotalsSum = tmp;
		}
		return originalOrderTotalsSum;
	}

	private double calculateOrderBaseSum(final OrderModel order)
	{
		final double originalOrderBaseSum;
		{
			double tmp = 0;
			for (final AbstractOrderEntryModel orderEntry : order.getEntries())
			{
				tmp += orderEntry.getBasePrice().doubleValue() * orderEntry.getQuantity().doubleValue();
			}
			originalOrderBaseSum = tmp;
		}
		return originalOrderBaseSum;
	}

	private OrderCancelRecordEntryModel performCompleteCancelWarehouseRequest(final OrderModel order) throws OrderCancelException
	{

		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);

		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(order);

		final int numberOfEntries = order.getEntries().size();

		final double originalOrderTotalsSum = calculateOrderTotalSum(order);

		final IMocksControl mockCreator = EasyMock.createStrictControl();
		final OrderCancelWarehouseAdapter warehouseAdapterMock = mockCreator.createMock(OrderCancelWarehouseAdapter.class);
		final OrderCancelNotificationServiceAdapter notificationsAdapterMock = mockCreator
				.createMock(OrderCancelNotificationServiceAdapter.class);

		final Capture<OrderCancelRecordEntryModel> capturedArgument = new Capture<OrderCancelRecordEntryModel>();

		///////////////////////////////////////////////////////
		//record mock objects behavior

		warehouseAdapterMock.requestOrderCancel(completeCancelRequest);
		//notificationsAdapterMock.sendCancelPendingNotifications(completeCancelRequest);
		notificationsAdapterMock.sendCancelPendingNotifications(and(capture(capturedArgument),
				isA(OrderCancelRecordEntryModel.class)));

		injectAdaptersDependencies(notificationsAdapterMock, null, warehouseAdapterMock, orderCancelService);

		mockCreator.replay();

		final OrderCancelRecordEntryModel cancelRecord = orderCancelService.requestOrderCancel(completeCancelRequest,
				currentPrincipal);

		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
		{
			modelService.refresh(oemrem);
		}

		final double cancelSnapshotTotalsSum = calculateSnapshotTotal(cancelRecord);

		mockCreator.verify();

		Assert.assertEquals("Newly created cancel record should have been passed into notificationServiceAdapter", cancelRecord,
				capturedArgument.getValue());

		Assert.assertEquals("Order should have" + numberOfEntries + " entries ", numberOfEntries, completeCancelRequest.getOrder()
				.getEntries().size());

		Assert.assertEquals("Order cancel record entry should have " + numberOfEntries + " entries ", numberOfEntries, cancelRecord
				.getOrderEntriesModificationEntries().size());

		Assert.assertEquals("Cancel request order entries snapshot must reflect original order entries", originalOrderTotalsSum,
				cancelSnapshotTotalsSum, 0.00001);

		Assert.assertEquals("Order cancel record should be in progress ", true, cancelRecord.getModificationRecord().isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.INPROGRESS,
				OrderModificationEntryStatus.INPROGRESS, cancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.FULL.getCode(),
				OrderCancelEntryStatus.FULL, cancelRecord.getCancelResult());

		Assert.assertEquals("Order should be in CANCELLING state", OrderStatus.CANCELLING, order.getStatus());

		return cancelRecord;
	}

	@Test
	public void testCompleteCancelWarehouseNotShipping() throws OrderCancelException
	{

		//Perform customer cancel request part
		performCompleteCancelWarehouseRequest(order);

		//Perform warehouse complete cancel successful response part
		{
			//Warehouse response: Complete Cancel (All order entries)
			final OrderCancelResponse completeCancelResponse = new OrderCancelResponse(order);

			final int numberOfEntries = order.getEntries().size();

			final double originalOrderTotalsSum;
			{
				double tmp = 0;
				for (final AbstractOrderEntryModel orderEntry : order.getEntries())
				{
					tmp += orderEntry.getTotalPrice().doubleValue();
				}
				originalOrderTotalsSum = tmp;
			}

			OrderCancelRecordEntryModel cancelRecord = null;
			final OrderCancelRecordModel ocrm = orderCancelService.getCancelRecordForOrder(order);
			for (final OrderModificationRecordEntryModel omrem : ocrm.getModificationRecordEntries())
			{
				if (OrderModificationEntryStatus.INPROGRESS == omrem.getStatus())
				{
					cancelRecord = (OrderCancelRecordEntryModel) omrem;
				}
			}

			final IMocksControl mockCreator = EasyMock.createStrictControl();
			final OrderCancelPaymentServiceAdapter paymentAdapterMock = mockCreator
					.createMock(OrderCancelPaymentServiceAdapter.class);
			final OrderCancelNotificationServiceAdapter notificationsAdapterMock = mockCreator
					.createMock(OrderCancelNotificationServiceAdapter.class);

			//record mock objects behavior
			paymentAdapterMock.recalculateOrderAndModifyPayments(order);
			notificationsAdapterMock.sendCancelFinishedNotifications(cancelRecord);

			injectAdaptersDependencies(notificationsAdapterMock, paymentAdapterMock, null, orderCancelService);

			mockCreator.replay();

			orderCancelService.onOrderCancelResponse(completeCancelResponse);

			//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
			for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
			{
				modelService.refresh(oemrem);
			}

			final double cancelSnapshotTotalsSum;
			{
				double tmp = 0;
				for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
				{
					tmp += oemrem.getOriginalOrderEntry().getTotalPrice().doubleValue();
				}
				cancelSnapshotTotalsSum = tmp;
			}

			mockCreator.verify();

			Assert.assertEquals("Order should have 3 entries", 3, completeCancelResponse.getOrder().getEntries().size());

			Assert.assertEquals("Order cancel record entry should have " + numberOfEntries + " entries ", numberOfEntries,
					cancelRecord.getOrderEntriesModificationEntries().size());

			Assert.assertEquals(
					"Cancel request order entries snapshot must reflect original order entries (that does no longer exist)",
					originalOrderTotalsSum, cancelSnapshotTotalsSum, 0.00001);

			Assert.assertEquals("Order cancel record should NOT be in progress ", false, cancelRecord.getModificationRecord()
					.isInProgress());

			Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.SUCCESSFULL,
					OrderModificationEntryStatus.SUCCESSFULL, cancelRecord.getStatus());

			Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.FULL.getCode(),
					OrderCancelEntryStatus.FULL, cancelRecord.getCancelResult());

			Assert.assertEquals("Order should be in " + OrderStatus.CANCELLED.getCode() + " state", OrderStatus.CANCELLED,
					order.getStatus());

		}

	}

	@Test
	public void testWarehouseResponseCancelDenied() throws Exception
	{
		final OrderStatus previousOrderStatus = order.getStatus();

		//Perform customer cancel request part
		final OrderCancelRecordEntryModel cancelRecord = performCompleteCancelWarehouseRequest(order);

		//response :  denied
		final OrderCancelResponse cancelDeniedResponse = new OrderCancelResponse(order, ResponseStatus.denied,
				"Order already shipped");
		orderCancelService.onOrderCancelResponse(cancelDeniedResponse);

		modelService.refresh(cancelRecord);
		modelService.refresh(order);

		Assert.assertEquals("Cancel Record Entry should have status SUCCESSFULL", OrderModificationEntryStatus.SUCCESSFULL,
				cancelRecord.getStatus());
		Assert.assertEquals("Cancel Record Entry should have cancel status DENIED", OrderCancelEntryStatus.DENIED,
				cancelRecord.getCancelResult());
		Assert.assertEquals("Order should be returned to previous state", previousOrderStatus, order.getStatus());
	}


	/**
	 * This test case tests scenario when FULL order cancel was requested, however warehouse performed PARTIAL cancel -
	 * due to some internal issues (partial shipment already done?)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWarehouseResponsePartialCancelOK() throws Exception
	{

		final List<AbstractOrderEntryModel> originalEntries = order.getEntries();
		final OrderStatus previousOrderState = order.getStatus();
		final double intitalOrderEntrySum = calculateOrderBaseSum(order);

		//Perform customer cancel request part
		final OrderCancelRecordEntryModel cancelRecord = performCompleteCancelWarehouseRequest(order);

		//response
		{
			final List<OrderCancelEntry> responseEntries = new ArrayList<OrderCancelEntry>();

			double priceDifference = 0.0;

			//first entry cancelled fully
			responseEntries.add(new OrderCancelEntry(originalEntries.get(0), originalEntries.get(0).getQuantity().longValue()));

			priceDifference += originalEntries.get(0).getTotalPrice().doubleValue();

			//second entry cancel only 1 product
			responseEntries.add(new OrderCancelEntry(originalEntries.get(1), 1));
			priceDifference += originalEntries.get(1).getBasePrice().doubleValue();

			final OrderCancelResponse partialResponse = new OrderCancelResponse(order, responseEntries);

			////////////////////////////////////////

			final IMocksControl mockCreator = EasyMock.createStrictControl();
			final OrderCancelPaymentServiceAdapter paymentAdapterMock = mockCreator
					.createMock(OrderCancelPaymentServiceAdapter.class);
			final OrderCancelNotificationServiceAdapter notificationsAdapterMock = mockCreator
					.createMock(OrderCancelNotificationServiceAdapter.class);

			//record mock objects behavior
			paymentAdapterMock.recalculateOrderAndModifyPayments(order);
			notificationsAdapterMock.sendCancelFinishedNotifications(cancelRecord);

			injectAdaptersDependencies(notificationsAdapterMock, paymentAdapterMock, null, orderCancelService);

			mockCreator.replay();

			orderCancelService.onOrderCancelResponse(partialResponse);

			modelService.refresh(cancelRecord);
			modelService.refresh(order);

			final double finalOrderEntrySum = calculateOrderBaseSum(order);

			Assert.assertEquals("Order Entries totals sum should be decreased after partial cancel: ", intitalOrderEntrySum
					- finalOrderEntrySum, priceDifference, 0.00001);

			Assert.assertEquals("Cancel Record Entry should have status SUCCESSFULL", OrderModificationEntryStatus.SUCCESSFULL,
					cancelRecord.getStatus());
			Assert.assertEquals("Cancel Record Entry should have cancel status PARTIAL", OrderCancelEntryStatus.PARTIAL,
					cancelRecord.getCancelResult());
			Assert.assertEquals("Order should be returned to previous state", previousOrderState, order.getStatus());

			Assert.assertEquals("Order should have 3 entries after partial cancel", 3, order.getEntries().size());

		}

	}

	/**
	 * Tests consecutive cancel requests when the first one didnt finish
	 * 
	 * @throws OrderCancelException
	 */
	@Test
	public void testSecondCancelRequest() throws OrderCancelException
	{

		boolean passed = false;
		performCompleteCancelWarehouseRequest(order);
		try
		{
			performCompleteCancelWarehouseRequest(order);
		}
		catch (final OrderCancelDeniedException e)
		{
			passed = true;
		}
		Assert.assertTrue(
				"Second order cancel request should have failed due to OrderCancelDeniedException (Cancel already in progress)",
				passed);


	}



	private OrderCancelRequest prepareCompleteCancelRequest(final OrderModel order)
	{
		return new OrderCancelRequest(order);
	}

	private OrderCancelRequest preparePartialCancelRequest(final OrderModel order, final int entryIndex)
	{
		final List<OrderCancelEntry> entries = new ArrayList<OrderCancelEntry>();
		entries.add(new OrderCancelEntry(order.getEntries().get(entryIndex), order.getEntries().get(entryIndex).getQuantity()
				.longValue() - 1));
		return new OrderCancelRequest(order, entries);
	}
}
