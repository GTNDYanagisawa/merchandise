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
package de.hybris.platform.b2bacceleratorservices.dao.impl;

import de.hybris.platform.b2bacceleratorservices.dao.B2BAcceleratorCartToOrderCronJobModelDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BAcceleratorCartToOrderCronJobModelDao extends DefaultGenericDao<CartToOrderCronJobModel> implements
		B2BAcceleratorCartToOrderCronJobModelDao
{

	private static final String FIND_CARTTOORDERCRONJOB_BY_USER_QUERY = "SELECT {" + CartToOrderCronJobModel._TYPECODE + ":"
			+ CartToOrderCronJobModel.PK + "} FROM { " + CartToOrderCronJobModel._TYPECODE + " as "
			+ CartToOrderCronJobModel._TYPECODE + " JOIN " + CartModel._TYPECODE + " as " + CartModel._TYPECODE + " ON {"
			+ CartToOrderCronJobModel._TYPECODE + ":" + CartToOrderCronJobModel.CART + "} = {" + CartModel._TYPECODE + ":"
			+ CartModel.PK + "}} WHERE {" + CartModel._TYPECODE + ":" + CartModel.USER + "} = ?user";

	private static final String FIND_CARTTOORDERCRONJOB_BY_CODE_AND_USER_QUERY =  FIND_CARTTOORDERCRONJOB_BY_USER_QUERY +
		" AND {" + CartToOrderCronJobModel._TYPECODE + ":" + CartToOrderCronJobModel.CODE + "} = ?code";

	private static final String FIND_ORDERS_FOR_SCHEDULE_BY_CODE_QUERY = "SELECT {" + OrderModel._TYPECODE + ":" + OrderModel.PK
			+ "} FROM { " + OrderModel._TYPECODE + " as " + OrderModel._TYPECODE + " JOIN " + CartToOrderCronJobModel._TYPECODE
			+ " as " + CartToOrderCronJobModel._TYPECODE + " ON {" + OrderModel._TYPECODE + ":" + OrderModel.SCHEDULINGCRONJOB
			+ " } = {" + CartToOrderCronJobModel._TYPECODE + ":" + CartToOrderCronJobModel.PK + " }} WHERE {"
			+ CartToOrderCronJobModel._TYPECODE + ":" + CartToOrderCronJobModel.CODE + "} = ?code"
			+ " AND {" + OrderModel._TYPECODE + ":" + OrderModel.VERSIONID + "} IS NULL";


	private static final String SORT_JOBS_BY_DATE = " ORDER BY {" + CartToOrderCronJobModel._TYPECODE + ":"
			+ CartToOrderCronJobModel.CREATIONTIME + "} DESC, {" + CartToOrderCronJobModel._TYPECODE + ":"
			+ CartToOrderCronJobModel.PK + "}";

	private static final String SORT_JOBS_BY_CODE = " ORDER BY {" + CartToOrderCronJobModel._TYPECODE + ":"
			+ CartToOrderCronJobModel.CODE + "}, {" + CartToOrderCronJobModel._TYPECODE + ":" + CartToOrderCronJobModel.CREATIONTIME
			+ "} DESC, {" + CartToOrderCronJobModel._TYPECODE + ":" + CartToOrderCronJobModel.PK + "}";


	private static final String SORT_ORDERS_BY_DATE = " ORDER BY {" + OrderModel._TYPECODE + ":" + OrderModel.CREATIONTIME
			+ "} DESC, {" + OrderModel._TYPECODE + ":" + OrderModel.PK + "}";

	private static final String SORT_ORDERS_BY_CODE = " ORDER BY {" + OrderModel._TYPECODE + ":" + OrderModel.CODE + "},{"
			+ OrderModel._TYPECODE + ":" + OrderModel.CREATIONTIME + "} DESC, {" + OrderModel._TYPECODE + ":" + OrderModel.PK + "}";

	public DefaultB2BAcceleratorCartToOrderCronJobModelDao()
	{
		super(CartToOrderCronJobModel._TYPECODE);
	}

	@Override
	public CartToOrderCronJobModel findCartToOrderCronJobByCode(final String code, final UserModel user)
	{
		final Map<String, Object> attr = new HashMap<String, Object>(2);
		attr.put(CartToOrderCronJobModel.CODE, code);
		attr.put(CartModel.USER, user);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CARTTOORDERCRONJOB_BY_CODE_AND_USER_QUERY);
		query.getQueryParameters().putAll(attr);
		final SearchResult<CartToOrderCronJobModel> jobs = this.getFlexibleSearchService().search(query);
		final List<CartToOrderCronJobModel> result = jobs.getResult();
		return (result.iterator().hasNext() ? result.iterator().next() : null);
	}

	@Override
	public List<CartToOrderCronJobModel> findCartToOrderCronJobsByUser(final UserModel user)
	{
		final Map<String, Object> attr = new HashMap<String, Object>(1);
		attr.put(OrderModel.USER, user);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CARTTOORDERCRONJOB_BY_USER_QUERY + SORT_JOBS_BY_DATE);
		query.getQueryParameters().putAll(attr);
		final SearchResult<CartToOrderCronJobModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}


	@Override
	public SearchPageData<CartToOrderCronJobModel> findPagedCartToOrderCronJobsByUser(final UserModel user,
			final PageableData pageableData)
	{
		final Map<String, Object> queryParams = new HashMap<String, Object>(1);
		queryParams.put(OrderModel.USER, user);

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byDate", FIND_CARTTOORDERCRONJOB_BY_USER_QUERY + SORT_JOBS_BY_DATE),
				createSortQueryData("byReplenishmentNumber", FIND_CARTTOORDERCRONJOB_BY_USER_QUERY + SORT_JOBS_BY_CODE));

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}

	@Override
	public SearchPageData<OrderModel> findOrderByJob(final String jobCode, final PageableData pageableData)
	{
		final Map<String, Object> queryParams = new HashMap<String, Object>(1);
		queryParams.put(CartToOrderCronJobModel.CODE, jobCode);

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byDate", FIND_ORDERS_FOR_SCHEDULE_BY_CODE_QUERY + SORT_ORDERS_BY_DATE),
				createSortQueryData("byOrderNumber", FIND_ORDERS_FOR_SCHEDULE_BY_CODE_QUERY + SORT_ORDERS_BY_CODE));

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}


	@Override
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		super.setFlexibleSearchService(flexibleSearchService);
	}

	private PagedFlexibleSearchService pagedFlexibleSearchService;

	protected PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	@Required
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}
}
