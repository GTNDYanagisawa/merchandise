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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PaymentCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Builds the Row map for the CSV files for the Payment in an Order
 */
public class DefaultPaymentContributor implements RawItemContributor<OrderModel>
{

	@Override
	public Set<String> getColumns()
	{
		return new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, PaymentCsvColumns.CC_OWNER, PaymentCsvColumns.VALID_TO_MONTH,
				PaymentCsvColumns.VALID_TO_YEAR, PaymentCsvColumns.SUBSCRIPTION_ID, PaymentCsvColumns.PAYMENT_PROVIDER,
				PaymentCsvColumns.REQUEST_ID));
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final List<Map<String, Object>> result = new ArrayList<>();

		for (final PaymentTransactionModel payment : order.getPaymentTransactions())
		{
			final PaymentInfoModel paymentInfo = order.getPaymentInfo();

			final Map<String, Object> row = new HashMap<>();
			row.put(OrderCsvColumns.ORDER_ID, order.getCode());
			row.put(PaymentCsvColumns.PAYMENT_PROVIDER, payment.getPaymentProvider());
			if (payment.getRequestId() == null || payment.getRequestId().isEmpty())
			{
				row.put(PaymentCsvColumns.REQUEST_ID, "1");
			}
			else
			{
				row.put(PaymentCsvColumns.REQUEST_ID, payment.getRequestId());
			}

			if (paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				final CreditCardPaymentInfoModel ccPaymentInfo = (CreditCardPaymentInfoModel) paymentInfo;
				row.put(PaymentCsvColumns.CC_OWNER, ccPaymentInfo.getCcOwner());
				row.put(PaymentCsvColumns.VALID_TO_MONTH, ccPaymentInfo.getValidToMonth());
				row.put(PaymentCsvColumns.VALID_TO_YEAR, ccPaymentInfo.getValidToYear());
				row.put(PaymentCsvColumns.SUBSCRIPTION_ID, ccPaymentInfo.getSubscriptionId());
			}

			result.add(row);
		}

		return result;
	}

}
