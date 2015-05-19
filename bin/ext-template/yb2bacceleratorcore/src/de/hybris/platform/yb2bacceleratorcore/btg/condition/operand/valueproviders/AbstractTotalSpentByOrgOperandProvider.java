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
package de.hybris.platform.yb2bacceleratorcore.btg.condition.operand.valueproviders;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.btg.condition.operand.valueproviders.AbstractOrderOperandValueProvider;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.yb2bacceleratorcore.model.btg.OrganizationOrderStatisticsModel;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class AbstractTotalSpentByOrgOperandProvider extends AbstractOrderOperandValueProvider
{

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(AbstractTotalSpentByOrgOperandProvider.class);

	protected double getTotalSpentByBranch(final B2BUnitModel unit, final CurrencyModel currency, final Date startDateInclusive,
			final Date endDateNonInclusive, final String catalogId, final String categoryCode)
	{

		final Map<String, Object> attr = new HashMap<String, Object>(4);
		attr.put(OrderModel.CURRENCY, currency);
		attr.put("unit", unit);

		final StringBuilder queryBuilder = new StringBuilder(150);
		queryBuilder.append(" SELECT sum({").append(OrganizationOrderStatisticsModel.ORDERTOTAL).append("}) ");
		queryBuilder.append(" FROM   {OrganizationOrderStatistics}	");
		queryBuilder.append(" WHERE  {currency}	= 	?currency			");
		queryBuilder.append(" AND    {unit} 		= 	?unit					");

		if (startDateInclusive != null)
		{
			queryBuilder.append(" AND 	 {date} 		>= ?startdate			");
			attr.put("startdate", startDateInclusive);
		}

		if (endDateNonInclusive != null)
		{
			queryBuilder.append(" AND    {date} 		< 	?enddate 			");
			attr.put("enddate", endDateNonInclusive);
		}

		if (catalogId != null)
		{
			queryBuilder.append(" AND {").append(OrganizationOrderStatisticsModel.CATALOG).append("} = ?catalogId");
			attr.put("catalogId", catalogId);
		}

		if (categoryCode != null)
		{

			queryBuilder.append(" AND {").append(OrganizationOrderStatisticsModel.CATEGORY).append("} = ?categoryCode");
			attr.put("categoryCode", categoryCode);
		}

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.addQueryParameters(attr);
		query.setResultClassList(Collections.singletonList(Double.class));
		final SearchResult<Double> result = getFlexibleSearchService().search(query);
		if (result.getResult().isEmpty() || result.getResult().get(0) == null)
		{
			return 0;
		}
		else
		{
			return result.getResult().get(0).doubleValue();
		}

	}
}
