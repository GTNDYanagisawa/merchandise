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
package de.hybris.platform.b2bacceleratorservices.btg.condition.operand.valueproviders;

import de.hybris.platform.b2bacceleratorservices.model.btg.BTGNumberOfOrdersAboveAmountRelativeDateOperandModel;
import de.hybris.platform.btg.condition.operand.OperandValueProvider;
import de.hybris.platform.btg.condition.operand.valueproviders.AbstractOrderOperandValueProvider;
import de.hybris.platform.btg.condition.operand.valueproviders.OperandValueProviderUtils;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.Order;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NumberOfOrdersAboveAmountRelativeDateOperandValueProvider extends AbstractOrderOperandValueProvider implements
		OperandValueProvider<BTGNumberOfOrdersAboveAmountRelativeDateOperandModel>
{
	@Override
	public Object getValue(final BTGNumberOfOrdersAboveAmountRelativeDateOperandModel operand, final UserModel user,
			final BTGConditionEvaluationScope evaluationScope)
	{
		Date fromDate = null;
		if ((operand.getUnit() != null) && (operand.getValue() != null))
		{
			fromDate = OperandValueProviderUtils.getDateFromInterval(operand.getUnit(), operand.getValue().intValue());
		}
		final int total = getNumberOfOrdersAboveThreshold(user, fromDate, null, operand.getCurrency(), operand.getAmount(),
				operand.getOrderStatus());
		return Integer.valueOf(total);
	}

	@Override
	public Class getValueType(final BTGNumberOfOrdersAboveAmountRelativeDateOperandModel operand)
	{
		return Integer.class;
	}

	protected int getNumberOfOrdersAboveThreshold(final UserModel user, final Date fromDate, final Date toDate,
			final CurrencyModel currency, final Double amount, final OrderStatus orderStatus)
	{
		final Map params = new HashMap();
		final StringBuilder querryBuffer = new StringBuilder(150);
		querryBuffer.append(" SELECT  COUNT({pk}) 				");
		querryBuffer.append(" FROM   {Order as o} 				");
		querryBuffer.append(" WHERE  {o:versionID} IS NULL 	");
		if (fromDate != null)
		{
			querryBuffer.append(" AND {o:creationtime} >= ?startDate");
			params.put("startDate", fromDate);
		}
		if (toDate != null)
		{
			querryBuffer.append(" AND {o:creationtime} <= ?endDate ");
			params.put("endDate", toDate);
		}
		querryBuffer.append(" AND   {o:").append(Order.USER).append("} 	   = ?user 		");
		if (currency != null)
		{
			querryBuffer.append(" AND   {o:").append(Order.CURRENCY).append("}   = ?currency	");
			params.put("currency", currency);
		}
		querryBuffer.append(" AND   {o:").append(Order.TOTALPRICE).append("}	> ?threshold");
		params.put("user", user);
		params.put("threshold", amount);
		if (orderStatus != null)
		{
			querryBuffer.append(" AND   {o:").append(Order.STATUS).append("}	= ?status");
			params.put("status", orderStatus);
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(querryBuffer.toString());
		query.addQueryParameters(params);
		query.setResultClassList(Collections.singletonList(Integer.class));
		final SearchResult result = getFlexibleSearchService().search(query);
		return ((result.getResult().isEmpty()) ? 0 : ((Integer) result.getResult().get(0)).intValue());
	}


}
