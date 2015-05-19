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
package de.hybris.platform.accountsummaryaddon.document;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.HashMap;
import java.util.Map;


/**
 * Used to build A B2BDocument query.
 */
public class B2BDocumentQueryBuilder
{

	private final int currentPage;
	private final int pageSize;
	private final String sort;
	private final boolean isAsc;
	private final Map<String, Object> criterias = new HashMap<String, Object>();


	public B2BDocumentQueryBuilder(final int currentPage, final int pageSize, final String sort, final boolean isAsc)
	{
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.sort = sort;
		this.isAsc = isAsc;
	}

	public B2BDocumentQueryBuilder addCriteria(final String criteriaName, final Object criteriaValue)
	{
		criterias.put(criteriaName, criteriaValue);
		return this;
	}

	public AccountSummaryDocumentQuery build()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);

		final AccountSummaryDocumentQuery query = new AccountSummaryDocumentQuery(pageableData, this.isAsc);

		for (final String criteriaName : criterias.keySet())
		{
			query.addCriteria(criteriaName, criterias.get(criteriaName));
		}

		return query;
	}

	public void addAllCriterias(final Map<String, Object> criterias)
	{
		this.criterias.putAll(criterias);
	}
}
