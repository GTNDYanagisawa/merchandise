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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;

import java.util.List;


/**
 * BO representation of Order Search
 */
public interface Search
{
	/**
	 * Triggers a search. In case the search is not dirty, no back end call will be made but paging will be done in the
	 * BOL layer
	 * 
	 * @param searchFilter
	 *           Filter settings
	 * @param pageableData
	 *           Paging settings
	 * @return List of search result entries according to filter and paging settings
	 */
	public List<SearchResult> getSearchResult(SearchFilter searchFilter, PageableData pageableData);

	/**
	 * Sets state to dirty, i.e. the next search call needs to access the back end
	 * 
	 * @param b
	 *           State is dirty?
	 */
	void setDirty(boolean b);

	/**
	 * Is state dirty, i.e. do we need to access the back end for the next search call?
	 * 
	 * @return Dirty state
	 */
	boolean isDirty();

	/**
	 * @return Total number of search results, taking no paging into account
	 */
	int getSearchResultsTotalNumber();

	/**
	 * Retrieves the date range (i.e. the number of days the system will search for in the past) from customizing
	 * 
	 * @return Date range in days
	 */
	public int getDateRange();

	/**
	 * Sort options which are stored in the session. The UI might not always send the sort options, so we need a session
	 * storage
	 * 
	 * @return Current sort options
	 */
	List<SortData> getSortOptions();
}
