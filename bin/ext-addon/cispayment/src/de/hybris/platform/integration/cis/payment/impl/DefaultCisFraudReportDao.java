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
package de.hybris.platform.integration.cis.payment.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.integration.cis.payment.model.CisFraudReportCronJobModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultCisFraudReportDao extends AbstractItemDao
{
	private static final String FIND_LAST_FRAUD_REPORT_END_TIME_QUERY = "SELECT MAX({"
			+ CisFraudReportCronJobModel.LASTFRAUDREPORTENDTIME + "}) FROM {" + CisFraudReportCronJobModel._TYPECODE + "}";

	private final static String FIND_PAYMENT_TRANSACTION_ENTRIES_QUERY = "SELECT {" + PaymentTransactionEntryModel.PK + "} FROM {"
			+ PaymentTransactionEntryModel._TYPECODE + "} WHERE {" + PaymentTransactionEntryModel.CODE + "} IN (?transactionIds)";

	/**
	 * Find the max fraud report end time stored in the database
	 */
	public Date findLastFraudReportEndTime()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_LAST_FRAUD_REPORT_END_TIME_QUERY);

		query.setResultClassList(Collections.singletonList(Date.class));
		final SearchResult<Date> res = getFlexibleSearchService().search(query);
		final List<Date> result = res.getResult();

		return result.iterator().next();
	}

	/**
	 * Find all payment transaction entries with the given transactionIds
	 */
	public List<PaymentTransactionEntryModel> findTransactionsByCode(final List<String> transactionIds)
	{
		validateParameterNotNull(transactionIds, "transactionIds must not be null!");

		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("transactionIds", transactionIds);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PAYMENT_TRANSACTION_ENTRIES_QUERY, queryParams);
		query.setResultClassList(Collections.singletonList(PaymentTransactionEntryModel.class));
		final SearchResult<OrderModel> res = getFlexibleSearchService().search(query);
		final List<OrderModel> result = res.getResult();
		return result == null ? Collections.EMPTY_LIST : result;
	}
}
