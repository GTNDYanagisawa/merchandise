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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 */
public class SearchResultListImpl implements SearchResultList
{

	List<SearchResult> results = new ArrayList<SearchResult>();
	private PageableData pageableData;


	PageableData getPageableData()
	{
		return pageableData;
	}

	@Override
	public void add(final SearchResult result)
	{
		this.results.add(result);

	}

	@Override
	public int size()
	{
		return results.size();
	}


	@Override
	public void clear()
	{
		results.clear();

	}


	@Override
	public void setPageableData(final PageableData pageableData)
	{
		this.pageableData = pageableData;
	}

	private void sortResultList(final String sort)
	{
		final SearchResultComparator comparator = new SearchResultComparator(sort);
		Collections.sort(results, comparator);
	}

	@Override
	public List<SearchResult> getSearchResult()
	{
		if (results.isEmpty())
		{
			return Collections.emptyList();
		}
		if (pageableData == null)
		{
			throw new ApplicationBaseRuntimeException("No pageable data provided!");
		}

		if (this.pageableData.getSort() == null)
		{
			this.pageableData.setSort("creationDate");
		}

		this.sortResultList(this.pageableData.getSort());

		final int startIndex = determineStartIndex();
		final int endIndex = determineEndIndex(startIndex);

		return this.results.subList(startIndex, endIndex);
	}

	protected int determineStartIndex()
	{
		final int startIndex = pageableData.getCurrentPage() * pageableData.getPageSize();
		if (startIndex >= results.size())
		{
			throw new ApplicationBaseRuntimeException("Pageable data does not match results size");
		}
		return startIndex;
	}



	protected int determineEndIndex(final int startIndex)
	{
		int endIndex = startIndex + pageableData.getPageSize();
		if (endIndex > results.size())
		{
			endIndex = results.size();
		}
		return endIndex;
	}
}
