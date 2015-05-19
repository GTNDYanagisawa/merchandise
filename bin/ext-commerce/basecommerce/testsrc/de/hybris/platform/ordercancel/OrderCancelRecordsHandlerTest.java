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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class OrderCancelRecordsHandlerTest extends AbstractOrderCancelTest
{

	@Resource
	OrderCancelRecordsHandler orderCancelRecordsHandler;

	OrderCancelRequest fullCancelRequest = null;
	OrderCancelRequest partialCancelRequest = null;

	@Before
	public void createRequest()
	{
		fullCancelRequest = new OrderCancelRequest(order);

		final List<OrderCancelEntry> entries = new ArrayList<OrderCancelEntry>();
		entries.add(new OrderCancelEntry(order.getEntries().get(1), 1));
		entries.add(new OrderCancelEntry(order.getEntries().get(2), 2));

		partialCancelRequest = new OrderCancelRequest(order, entries);
	}

	@Test
	public void testCreateRecordEntriesFullCancell() throws Exception
	{
		final OrderCancelRecordEntryModel entry = orderCancelRecordsHandler.createRecordEntry(fullCancelRequest);
		modelService.refresh(order);
		assertNotNull("Order Should have modification record", order.getModificationRecords());
		final Collection<OrderCancelRecordEntryModel> resultingEntries = getOrderCancelEntries(order);
		assertTrue("resulting Cancel Entry is not as expected", resultingEntries.contains(entry));

	}

	@Test
	public void testCreateRecordEntriesPartialCancell() throws Exception
	{
		final OrderCancelRecordEntryModel entry = orderCancelRecordsHandler.createRecordEntry(partialCancelRequest);
		modelService.refresh(order);
		assertNotNull("Order Should have modification record", order.getModificationRecords());
		final Collection<OrderCancelRecordEntryModel> resultingEntries = getOrderCancelEntries(order);
		assertTrue("resulting Cancel Entry is not as expected", resultingEntries.contains(entry));

	}

	protected Collection<OrderCancelRecordEntryModel> getOrderCancelEntries(final OrderModel order)
	{
		for (final Iterator<OrderModificationRecordModel> iter = order.getModificationRecords().iterator(); iter.hasNext();)
		{
			final OrderModificationRecordModel modificationRecord = iter.next();
			if (modificationRecord instanceof OrderCancelRecordModel)
			{
				final OrderCancelRecordModel cancelRecord = (OrderCancelRecordModel) modificationRecord;
				return transform2CancelEntries(cancelRecord.getModificationRecordEntries());
			}
		}
		return null;
	}

	protected Collection<OrderCancelRecordEntryModel> transform2CancelEntries(
			final Collection<OrderModificationRecordEntryModel> input)
	{
		final List<OrderCancelRecordEntryModel> result = new ArrayList<OrderCancelRecordEntryModel>();
		for (final OrderModificationRecordEntryModel entry : input)
		{
			if (entry instanceof OrderCancelRecordEntryModel)
			{
				result.add((OrderCancelRecordEntryModel) entry);
			}
		}
		return result;
	}

}
