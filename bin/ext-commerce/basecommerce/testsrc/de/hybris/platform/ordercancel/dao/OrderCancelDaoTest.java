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
package de.hybris.platform.ordercancel.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.ordercancel.AbstractOrderCancelTest;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class OrderCancelDaoTest extends AbstractOrderCancelTest
{
	private static final Logger LOG = Logger.getLogger(OrderCancelDaoTest.class.getName());

	@Resource
	protected OrderCancelDao orderCancelDao;



	@Before
	public void createEntries() throws Exception
	{
		LOG.info("Setting up OrderCancelDaoTest");


	}

	/**
	 * Test case: test fetching order cancel record for a given order
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetOrderCancelRecord() throws Exception
	{
		assertNull("Order Cancel record should be null", orderCancelDao.getOrderCancelRecord(order));
		createCancelRecord(order);
		final OrderCancelRecordModel record = orderCancelDao.getOrderCancelRecord(order);
		assertNotNull("Order Cancel record should not be null", record);
		assertEquals("Order Cancel record should have reference to original order", record.getOrder(), order);
		assertNotNull("Order Cancel identifier should not be null", record.getIdentifier());

	}

	/**
	 * Test case: test fetching order cancel record entries for a given order
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetOrderCancelRecordEntries() throws Exception
	{
		final OrderCancelRecordEntryModel entry = createCancelRecordEntry(order);

		assertNotNull("Order cancel entries cannot be null", orderCancelDao.getOrderCancelRecordEntries(order));
		final List<OrderCancelRecordEntryModel> result = (List<OrderCancelRecordEntryModel>) orderCancelDao
				.getOrderCancelRecordEntries(order);
		assertTrue("Order cancel entries - unexpected size", result.size() == 1);
		assertEquals("Order cancel entries - not equal to expected entry", entry, result.get(0));
		assertNotNull("Order cancel entry: must have original order snaphot", result.get(0).getOriginalVersion());
	}

}
