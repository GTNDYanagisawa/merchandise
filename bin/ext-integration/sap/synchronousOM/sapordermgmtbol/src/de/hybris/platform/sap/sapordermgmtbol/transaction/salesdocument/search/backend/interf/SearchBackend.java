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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.interf;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;


/**
 * 
 */
public interface SearchBackend extends BackendBusinessObject
{
	/**
	 * Performs a search in backend, according to the filter settings
	 * 
	 * @param searchFilter
	 *           Filter settings
	 * @return Search result list
	 * @throws BackendException
	 */
	SearchResultList getSearchResult(SearchFilter searchFilter) throws BackendException;

	/**
	 * Retrieves date range
	 * 
	 * @return Date range in days
	 */
	int getDateRangeInDays();
}
