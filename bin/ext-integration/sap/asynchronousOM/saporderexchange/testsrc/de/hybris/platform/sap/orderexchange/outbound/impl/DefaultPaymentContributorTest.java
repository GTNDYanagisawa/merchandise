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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PaymentCsvColumns;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPaymentContributorTest
{
	private static final String SUBSCRIPTION_ID = "SubscriptionId";
	private static final String OWNER = "Owner";
	private static final String VALID_TO_YEAR = "ValidToYear";
	private static final String VALID_TO_MONTH = "ValidToMonth";
	private static final String REQUEST_ID = "RequestId";
	private static final String PAYMENT_PROVIDER = "PaymentProvider";
	private static final String CODE = "Code";
	private DefaultPaymentContributor cut;

	@Before
	public void setUp()
	{
		cut = new DefaultPaymentContributor();
	}

	@Test
	public void testGetColumns()
	{
		final Set<String> columns = cut.getColumns();
		assertTrue(columns.contains(OrderCsvColumns.ORDER_ID));
		assertTrue(columns.contains(PaymentCsvColumns.CC_OWNER));
		assertTrue(columns.contains(PaymentCsvColumns.VALID_TO_MONTH));
		assertTrue(columns.contains(PaymentCsvColumns.VALID_TO_YEAR));
		assertTrue(columns.contains(PaymentCsvColumns.SUBSCRIPTION_ID));
		assertTrue(columns.contains(PaymentCsvColumns.PAYMENT_PROVIDER));
		assertTrue(columns.contains(PaymentCsvColumns.REQUEST_ID));
	}

	@Test
	public void testCreateRow()
	{
		final OrderModel order = new OrderModel();
		final PaymentTransactionModel payment = new PaymentTransactionModel();
		final CreditCardPaymentInfoModel paymentInfo = new CreditCardPaymentInfoModel();

		order.setPaymentTransactions(Arrays.asList(payment));
		order.setPaymentInfo(paymentInfo);

		order.setCode(CODE);
		payment.setPaymentProvider(PAYMENT_PROVIDER);
		payment.setRequestId(REQUEST_ID);
		paymentInfo.setCcOwner(OWNER);
		paymentInfo.setValidToMonth(VALID_TO_MONTH);
		paymentInfo.setValidToYear(VALID_TO_YEAR);
		paymentInfo.setSubscriptionId(SUBSCRIPTION_ID);

		final List<Map<String, Object>> rows = cut.createRows(order);

		final Map<String, Object> row = rows.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(PAYMENT_PROVIDER, row.get(PaymentCsvColumns.PAYMENT_PROVIDER));
		assertEquals(REQUEST_ID, row.get(PaymentCsvColumns.REQUEST_ID));
		assertEquals(OWNER, row.get(PaymentCsvColumns.CC_OWNER));
		assertEquals(VALID_TO_MONTH, row.get(PaymentCsvColumns.VALID_TO_MONTH));
		assertEquals(VALID_TO_YEAR, row.get(PaymentCsvColumns.VALID_TO_YEAR));
		assertEquals(SUBSCRIPTION_ID, row.get(PaymentCsvColumns.SUBSCRIPTION_ID));
	}
}
