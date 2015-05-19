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
package de.hybris.platform.yb2bacceleratortest.orders;

import static de.hybris.platform.yb2bacceleratortest.constants.YB2BAcceleratorTestConstants.HISTORICAL_USER_UID;
import static de.hybris.platform.yb2bacceleratortest.constants.YB2BAcceleratorTestConstants.REPENISHMENT_USER_UID;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;


@IntegrationTest
public class B2BAcceleratorTestOrderDataTest extends ServicelayerTransactionalBaseTest
{
	public static final Logger LOG = Logger.getLogger(B2BAcceleratorTestOrderDataTest.class);

	private static final String quoteUserID = "QAQuoteUser@test.com";


	@Resource
	B2BAcceleratorTestOrderData b2BAcceleratorTestOrderData;
	@Resource
	B2BOrderFacade b2bOrderFacade;


	@Test
	@Ignore
	public void placeOrderWithInCustomerPermissions()
	{
		final String poNumber = "ORDER_SENT_NOTIFICATION_SENT paid" + System.currentTimeMillis();
		final String orderCode = b2BAcceleratorTestOrderData.placeOrderWithInCustomerPermissions(HISTORICAL_USER_UID, poNumber,
				true);

		final OrderData order = b2bOrderFacade.getOrderDetailsForCode(orderCode);

		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());
	}


	@Test
	@Ignore
	public void placeOrderRequiringApproval()
	{
		final String poNumber = "pendingApproval" + System.currentTimeMillis();

		final String orderCode = b2BAcceleratorTestOrderData.placeOrderRequiringApproval(HISTORICAL_USER_UID, poNumber);

		final OrderStatus status = b2bOrderFacade.getOrderDetailsForCode(orderCode).getStatus();
		LOG.info("Status is " + status);

		Assert.assertEquals(OrderStatus.PENDING_APPROVAL, status);

	}

	@Test
	@Ignore
	public void placeOrderAndApproveByB2BApprover()
	{
		final String poNumber = "pendingApproval" + System.currentTimeMillis();

		final String orderCode = b2BAcceleratorTestOrderData.placeOrderAndApproveByB2BApprover(HISTORICAL_USER_UID, poNumber);

		final OrderStatus status = b2bOrderFacade.getOrderDetailsForCode(orderCode).getStatus();
		LOG.info("Status is " + status);

		Assert.assertEquals(OrderStatus.APPROVED, status);

	}


	@Test
	@Ignore
	public void placeOrderForQuoteNegotiation()
	{
		final String poNumber = "pendingApproval" + System.currentTimeMillis();

		final String orderCode = b2BAcceleratorTestOrderData.placeOrderForQuoteNegotiation(HISTORICAL_USER_UID, poNumber);

		final OrderStatus status = b2bOrderFacade.getOrderDetailsForCode(orderCode).getStatus();
		LOG.info("Status is " + status);

		Assert.assertEquals(OrderStatus.PENDING_QUOTE, status);


	}

	@Test
	@Ignore
	public void placeOrderQuoteRequestInMerchantApprovedState()
	{
		final String poNumber = "pendingApproval" + System.currentTimeMillis();

		final String orderCode = b2BAcceleratorTestOrderData.placeOrderQuoteRequestWithMerchantResponse(quoteUserID, poNumber);

		final OrderStatus status = b2bOrderFacade.getOrderDetailsForCode(orderCode).getStatus();
		LOG.info("Status is " + status);

		Assert.assertEquals(OrderStatus.APPROVED_QUOTE, status);

	}

	@Test
	@Ignore
	public void placeOrderQuoteInMerchantRejectedState()
	{
		final String poNumber = "pendingApproval REJECTED " + System.currentTimeMillis();

		final String orderCode = b2BAcceleratorTestOrderData.placeOrderQuoteRequestWithMerchantResponse(quoteUserID, poNumber);

		final OrderStatus status = b2bOrderFacade.getOrderDetailsForCode(orderCode).getStatus();
		LOG.info("Status is " + status);

		Assert.assertEquals(OrderStatus.REJECTED_QUOTE, status);

	}


	@Test
	@Ignore
	public void createReplenishmentForTheFuture()
	{
		final String poNumber = "pendingApproval" + System.currentTimeMillis();
		final String jobCode = b2BAcceleratorTestOrderData
				.placeReplenishmentOrderScheduleForFuture(REPENISHMENT_USER_UID, poNumber);
		LOG.info("Job Code is " + jobCode);
	}



}
