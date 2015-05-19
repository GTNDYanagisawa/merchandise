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
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.interf.SearchBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.Search;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
@BackendInterface(SearchBackend.class)
public class SearchImpl extends BusinessObjectBase implements Search
{

	SearchBackend searchBackend;
	private boolean dirty = true;
	private SearchResultList searchResult;
	private List<SortData> sortOptions = getValidSortOptions();


	@Override
	public List<SortData> getSortOptions()
	{
		return sortOptions;
	}


	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	/**
	 * @return the searchBackend
	 * @throws BackendException
	 */
	public SearchBackend getSearchBackend() throws BackendException
	{
		if (searchBackend == null)
		{
			searchBackend = (SearchBackend) getBackendBusinessObject();
		}
		return searchBackend;
	}

	/**
	 * Can be used for testing. If not called, the backend object will be derived via anotations.
	 * 
	 * @param searchBackend
	 *           the searchBackend to set
	 */
	public void setSearchBackend(final SearchBackend searchBackend)
	{
		this.searchBackend = searchBackend;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.Search#getSearchResult()
	 */
	@Override
	public List<SearchResult> getSearchResult(final SearchFilter searchFilter, final PageableData pageableData)
	{
		if (isDirty())
		{
			SearchResultList searchFromBackend;
			try
			{
				searchFromBackend = getSearchBackend().getSearchResult(searchFilter);
				setDirty(false);
			}
			catch (final BackendException e)
			{
				throw new ApplicationBaseRuntimeException("getSearchResult failed", e);
			}
			this.searchResult = searchFromBackend;
		}

		searchResult.setPageableData(pageableData);
		setSortOptions(getCheckedSortOption(pageableData));
		return searchResult.getSearchResult();
	}


	@Override
	public void setDirty(final boolean b)
	{
		this.dirty = b;

	}

	@Override
	public int getSearchResultsTotalNumber()
	{
		if (searchResult == null)
		{
			return 0;
		}
		else
		{
			return searchResult.size();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.Search#getDateRange()
	 */
	@Override
	public int getDateRange()
	{
		try
		{
			return getSearchBackend().getDateRangeInDays();
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("getDateRange failed", e);
		}
	}

	List<SortData> getValidSortOptions()
	{
		final List<SortData> sortOptions = new ArrayList<SortData>();

		SortData sortData = new SortData();
		sortData.setCode("creationDate");
		sortOptions.add(sortData);

		sortData = new SortData();
		sortData.setCode("purchaseOrderNumber");
		sortOptions.add(sortData);

		return sortOptions;
	}

	List<SortData> getCheckedSortOption(final PageableData pageableData)
	{
		final List<SortData> sortOptions = getSortOptions();

		final String sort = pageableData.getSort();
		if (sort != null && (!sort.isEmpty()))
		{
			for (final SortData sortOption : sortOptions)
			{
				sortOption.setSelected(sort.equals(sortOption.getCode()));
			}
		}
		else
		{
			for (final SortData sortOption : sortOptions)
			{
				if (sortOption.isSelected())
				{
					pageableData.setSort(sortOption.getCode());
				}
			}
		}

		return sortOptions;
	}

	/**
	 * @param sortData
	 */
	protected void setSortOptions(final List<SortData> sortData)
	{
		this.sortOptions = sortData;
	}

}
