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
package de.hybris.platform.acceleratorservices.order.dao.impl;

import de.hybris.platform.acceleratorservices.order.dao.AcceleratorConsignmentDao;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Default implementation
 */
public class DefaultAcceleratorConsignmentDao extends DefaultGenericDao<ConsignmentModel> implements AcceleratorConsignmentDao
{
	private static final Logger LOG = Logger.getLogger(DefaultAcceleratorConsignmentDao.class);//NOPMD

	/**
	 * DefaultGenericDao is only usable when typecode is set.
	 */
	public DefaultAcceleratorConsignmentDao()
	{
		super(ConsignmentModel._TYPECODE);
	}

	@Override
	public List<ConsignmentModel> findConsignmentsForStatus(final List<ConsignmentStatus> statuses,
			final Collection<BaseSiteModel> sites)
	{
		final Map<String, Object> attributes = new HashMap();
		for (final ConsignmentStatus status : statuses)
		{
			attributes.put("key" + Integer.toString(status.hashCode()), status);
		}
		for (final BaseSiteModel siteModel : sites)
		{
			attributes.put("key" + Integer.toString(siteModel.hashCode()), siteModel);
		}

		final StringBuilder queryString = new StringBuilder(100);
		queryString.append("SELECT {c:").append(ConsignmentModel.PK).append("} FROM { ").append(ConsignmentModel._TYPECODE)
				.append(" AS c ");
		queryString.append("JOIN ").append(AbstractOrderModel._TYPECODE).append(" AS ao ON ");
		queryString.append("{c:").append(ConsignmentModel.ORDER).append("}={ao:").append(AbstractOrderModel.PK).append('}');
		queryString.append("} WHERE (");
		for (final ConsignmentStatus status : statuses)
		{
			queryString.append("{c:status} = ?").append("key" + Integer.toString(status.hashCode())).append(" OR ");
		}
		queryString.replace(queryString.length() - 3, queryString.length() + 1, ")");
		queryString.append(" AND (");
		for (final BaseSiteModel siteModel : sites)
		{
			queryString.append("{ao:site} = ?").append("key" + Integer.toString(siteModel.hashCode())).append(" OR ");
		}
		queryString.replace(queryString.length() - 3, queryString.length() + 1, ")");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
		query.getQueryParameters().putAll(attributes);
		final SearchResult<ConsignmentModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}
}
