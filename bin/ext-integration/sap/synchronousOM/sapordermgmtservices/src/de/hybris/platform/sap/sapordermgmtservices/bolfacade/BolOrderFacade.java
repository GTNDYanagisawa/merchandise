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
package de.hybris.platform.sap.sapordermgmtservices.bolfacade;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;

import java.util.List;


/**
 * Serves as facade to the BOL layer, allowing access to the order related business objects. Covers the access to a
 * single order and the order search functions.
 */
public interface BolOrderFacade
{
	/**
	 * Get total number of orders found in the last back end call, not taking any pagination into account. The call does
	 * not perform a search itself, but refers to the results of the last call of
	 * {@link BolOrderFacade#performSearch(SearchFilter, PageableData)}
	 * 
	 * @return Total number of orders
	 */
	public Integer getSearchResultsTotalNumber();


	/**
	 * Return order details for an order existing in the back end persistence. Cannot be called for non-persisted orders
	 * in checkout.
	 * 
	 * @param orderId
	 *           Back end ID of the order
	 * @return BOL representation of order
	 */
	public Order getSavedOrder(final String orderId);

	/**
	 * Performs search for orders. Will either access the back end, if no search has been performed so far or the search
	 * result is dirty (see {@link #setSearchDirty()}, or will perform paging and sorting on the existing search result,
	 * without doing a back end call
	 * 
	 * @param searchFilter
	 *           Filter data
	 * @param pageableData
	 *           Paging data as requested in the hybris service layer
	 * @return List of BOL search results
	 */
	public List<SearchResult> performSearch(SearchFilter searchFilter, PageableData pageableData);

	/**
	 * Returning the date range for order search from configuration, also see
	 * {@link SAPConfigurationModel#getSapordermgmt_dateRange()}. This range specifies the number of days the search will
	 * cover. <br>
	 * Example: 365 means search is done for the last year
	 * 
	 * @return Date range in days
	 */
	public int getDateRange();

	/**
	 * Returning the current search sort options, allowing to access them even if the UI does not specify them in each
	 * call.
	 * 
	 * @return The current sort options
	 */
	public List<SortData> getSearchSort();


	/**
	 * Allows to state that the search result is dirty, i.e. search results need to be re-populated from the back end. If
	 * the search is not dirty, calls to the BOL layer will just perform pagination and sorting.
	 * 
	 */
	public void setSearchDirty();
}
