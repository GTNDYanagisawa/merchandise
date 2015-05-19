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

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class OrderCancelHistoryEntriesTest extends AbstractOrderCancelTest
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(OrderCancelHistoryEntriesTest.class);

	@Resource
	private OrderCancelStateMappingStrategy defaultOrderCancelStateMappingStrategy;

	@Before
	public void customSetUp() throws InvalidCartException
	{
		//clearMockedDependencied();
		currentPrincipal = REQUEST_ORIGIN_STRATEGY_CUSTOMER.getCallingPrincipal();
		injectStateMappingStrategy(defaultOrderCancelStateMappingStrategy);
		configuration = getOrCreateOrderCancelConfig(3600);
		configuration.setPartialCancelAllowed(true);
		configuration.setPartialOrderEntryCancelAllowed(true);
		configuration.setOrderCancelAllowed(true);
		configuration.setCancelAfterWarehouseAllowed(true);
		configuration.setCompleteCancelAfterShippingStartedAllowed(true);
	}

	@Test
	public void testCompleteCancel() throws Exception
	{

		final CancelReason reason = CancelReason.LATEDELIVERY;
		final String notes = "Delivery was too late";
		final OrderCancelRequest completeCancelRequest = createCompleteRequest(order, reason, notes);

		final IMocksControl mockCreator = EasyMock.createStrictControl();
		final OrderCancelPaymentServiceAdapter paymentAdapterMock = mockCreator.createMock(OrderCancelPaymentServiceAdapter.class);
		final OrderCancelNotificationServiceAdapter notificationsAdapterMock = mockCreator
				.createMock(OrderCancelNotificationServiceAdapter.class);
		final Capture<OrderCancelRecordEntryModel> capturedArgument = new Capture<OrderCancelRecordEntryModel>();
		paymentAdapterMock.recalculateOrderAndModifyPayments(order);
		notificationsAdapterMock.sendCancelFinishedNotifications(and(capture(capturedArgument),
				isA(OrderCancelRecordEntryModel.class)));
		injectAdaptersDependencies(notificationsAdapterMock, paymentAdapterMock, null, orderCancelService);

		mockCreator.replay();

		final OrderCancelRecordEntryModel historyEntry = orderCancelService.requestOrderCancel(completeCancelRequest,
				currentPrincipal);

		Assert.assertEquals("The cancel reason is not as expected", reason, historyEntry.getCancelReason());
		Assert.assertEquals("The cancel notes are not as expected", notes, historyEntry.getNotes());
	}

	@Test
	public void testPartialCancel() throws Exception
	{

		final AbstractOrderEntryModel orderEntry0 = order.getEntries().get(0);
		final AbstractOrderEntryModel orderEntry1 = order.getEntries().get(1);

		final Map<PK, CancelReason> reasons = new HashMap<PK, CancelReason>(2);
		reasons.put(orderEntry0.getPk(), CancelReason.OUTOFSTOCK);
		reasons.put(orderEntry1.getPk(), CancelReason.OTHER);

		final Map<PK, String> notes = new HashMap<PK, String>(2);
		notes.put(orderEntry0.getPk(), "Product out of stock");
		notes.put(orderEntry1.getPk(), "Product had wrong color");

		final String commonNotes = "Order cancel entries have different cancel reasoning";
		final List<OrderCancelEntry> cancelRequestEntries = new ArrayList<OrderCancelEntry>();
		//first order entry canceled due to : out of stock
		cancelRequestEntries.add(createOrderCancelRequestEntry(orderEntry0, "Product out of stock", CancelReason.OUTOFSTOCK));
		//the second one (but only 1 item) due to : Other -> Wrong color;  
		cancelRequestEntries.add(createOrderCancelRequestEntry(orderEntry1, "Product had wrong color", CancelReason.OTHER, 1));

		final OrderCancelRequest partialOrderRequest = createPartialRequest(order, cancelRequestEntries, commonNotes);

		final IMocksControl mockCreator = EasyMock.createStrictControl();
		final OrderCancelPaymentServiceAdapter paymentAdapterMock = mockCreator.createMock(OrderCancelPaymentServiceAdapter.class);
		final OrderCancelNotificationServiceAdapter notificationsAdapterMock = mockCreator
				.createMock(OrderCancelNotificationServiceAdapter.class);

		final Capture<OrderCancelRecordEntryModel> capturedArgument = new Capture<OrderCancelRecordEntryModel>();

		//record mock objects behavior
		paymentAdapterMock.recalculateOrderAndModifyPayments(order);
		notificationsAdapterMock.sendCancelFinishedNotifications(and(capture(capturedArgument),
				isA(OrderCancelRecordEntryModel.class)));

		injectAdaptersDependencies(notificationsAdapterMock, paymentAdapterMock, null, orderCancelService);

		mockCreator.replay();

		final OrderCancelRecordEntryModel historyEntry = orderCancelService.requestOrderCancel(partialOrderRequest,
				currentPrincipal);

		//check the order cancel entry
		Assert.assertEquals("The cancel reason on the whole entry should be NA", CancelReason.NA, historyEntry.getCancelReason());
		Assert.assertEquals("The cancel notes are not as expected", commonNotes, historyEntry.getNotes());

		//...and consecutive order entries cancel entries
		final List<OrderEntryModificationRecordEntryModel> modificationRecords = (List<OrderEntryModificationRecordEntryModel>) historyEntry
				.getOrderEntriesModificationEntries();
		final int size = modificationRecords.size();

		Assert.assertEquals("record entries number not as expexted", 2, size);

		for (final OrderEntryModificationRecordEntryModel modificationEntry : modificationRecords)
		{
			final OrderEntryCancelRecordEntryModel cancelEntry = (OrderEntryCancelRecordEntryModel) modificationEntry;
			final PK orderEntryPK = cancelEntry.getOrderEntry().getPk();

			Assert.assertEquals("Cancel reason was not as expected", reasons.get(orderEntryPK), cancelEntry.getCancelReason());
			Assert.assertEquals("Notes were not as expected", notes.get(orderEntryPK), cancelEntry.getNotes());
		}
	}

	private OrderCancelRequest createPartialRequest(final OrderModel order, final List<OrderCancelEntry> cancelRequestEntries,
			final String commonNotes)
	{
		return new OrderCancelRequest(order, cancelRequestEntries, commonNotes);
	}

	private OrderCancelRequest createCompleteRequest(final OrderModel order, final CancelReason reason, final String notes)
	{
		return new OrderCancelRequest(order, reason, notes);
	}

	private OrderCancelEntry createOrderCancelRequestEntry(final AbstractOrderEntryModel orderEntry, final String notes,
			final CancelReason cancelReason)
	{
		return new OrderCancelEntry(orderEntry, notes, cancelReason);
	}

	private OrderCancelEntry createOrderCancelRequestEntry(final AbstractOrderEntryModel orderEntry, final String notes,
			final CancelReason cancelReason, final long cancelQuantity)
	{
		return new OrderCancelEntry(orderEntry, cancelQuantity, notes, cancelReason);
	}
}
