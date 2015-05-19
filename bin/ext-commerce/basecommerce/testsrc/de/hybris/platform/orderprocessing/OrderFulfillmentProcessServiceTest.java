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
package de.hybris.platform.orderprocessing;

import de.hybris.basecommerce.jalo.AbstractOrderManagementTest;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.impl.BusinessProcessServiceDao;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Test is strongly time related and need to be rewritten
 */
public class OrderFulfillmentProcessServiceTest extends AbstractOrderManagementTest
{

	private static final Logger LOG = Logger.getLogger(OrderFulfillmentProcessServiceTest.class);
	@Resource
	private OrderFulfillmentProcessService orderFulfillmentProcessService;
	@Resource
	private BusinessProcessServiceDao businessProcessServiceDao;
	@Resource
	private ModelService modelService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * Tests proper launch of business process for a given order
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartFulfillmentProcess() throws Exception
	{


		final OrderProcessModel process = orderFulfillmentProcessService.startFulfillmentProcessForOrder(order);
		process.getCode();
		final BusinessProcessModel bussinessProcess = businessProcessServiceDao.getProcess(process.getCode());
		Assert.assertEquals("OrderProcess is not as expected", process, bussinessProcess);
		Assert.assertEquals("Order related to process is not as expected", order, ((OrderProcessModel) bussinessProcess).getOrder());

		final Date start = new Date();
		//maximum waiting time limited to 40 sec.
		long seconds = 0;
		while (!isFinished(bussinessProcess) && seconds < 40)
		{
			Thread.sleep(100);
			modelService.refresh(bussinessProcess);
			seconds = (new Date().getTime() - start.getTime()) / 1000;
			LOG.info("sec: " + seconds);
		}

		if (seconds >= 40)
		{
			// businessProcess didn't end properly
			Assert.fail("Business Process did not end properly within 40 seconds!");
		}

	}

	@Override
	public Logger getLogger()
	{
		return LOG;
	}

	private boolean isFinished(final BusinessProcessModel process)
	{
		switch (process.getState())
		{
			case SUCCEEDED:
				return true;
			case FAILED:
				return true;
			case ERROR:
				return true;
			default:
				return false;
		}
	}
}
