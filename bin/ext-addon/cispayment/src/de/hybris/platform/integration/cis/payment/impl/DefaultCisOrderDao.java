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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.daos.impl.DefaultOrderDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class DefaultCisOrderDao extends DefaultOrderDao
{
	private static final String FIND_INACTIVE_SUSPENEDED_ORDERS_QUERY = "SELECT {" + OrderModel.PK + "} FROM {"
			+ OrderModel._TYPECODE + "} WHERE {" + OrderModel.STATUS + "} = ?orderStatus" + " AND {" + OrderModel.MODIFIEDTIME
			+ "} < ?inactiveDate";

	private static final String FIND_ORDER_BY_CODE_QUERY = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE {" + OrderModel.CODE + "} IN (?orderIds)";

	/**
	 * Find all orders that were placed in Review and have been inactive for more than 12 hours
	 */
	public List<OrderModel> findInactiveOrders()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -12);

		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("orderStatus", OrderStatus.SUSPENDED);
		queryParams.put("inactiveDate", calendar.getTime());

		return getFlexibleSearchResult(new FlexibleSearchQuery(FIND_INACTIVE_SUSPENEDED_ORDERS_QUERY, queryParams));
	}

	/**
	 * Find all orders with the given orderIds
	 */
	public List<OrderModel> findOrdersByCode(final List<String> orderIds)
	{
		validateParameterNotNull(orderIds, "orderIds must not be null!");

		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("orderIds", StringUtils.join(orderIds, ","));

		return getFlexibleSearchResult(new FlexibleSearchQuery(FIND_ORDER_BY_CODE_QUERY, queryParams));
	}

	private List<OrderModel> getFlexibleSearchResult(final FlexibleSearchQuery query)
	{
		query.setResultClassList(Collections.singletonList(OrderModel.class));
		final SearchResult<OrderModel> res = getFlexibleSearchService().search(query);
		final List<OrderModel> result = res.getResult();
		return result == null ? Collections.EMPTY_LIST : result;
	}
}
