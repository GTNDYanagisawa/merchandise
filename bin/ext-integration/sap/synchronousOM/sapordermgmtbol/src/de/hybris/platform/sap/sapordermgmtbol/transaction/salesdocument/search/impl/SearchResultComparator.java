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

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;

import java.util.Comparator;


/**
 * Comparing search results
 */
public class SearchResultComparator implements Comparator<SearchResult>
{

	private final String sortCode;

	/**
	 * Constructor
	 * 
	 * @param sortCode
	 */
	public SearchResultComparator(final String sortCode)
	{
		this.sortCode = sortCode;
	}


	@Override
	public int compare(final SearchResult o1, final SearchResult o2)
	{
		int result = 0;

		switch (this.sortCode)
		{
			case "creationDate":
				result = o2.getCreationDate().compareTo(o1.getCreationDate());
				break;
			case "purchaseOrderNumber":
				result = o1.getPurchaseOrderNumber().toLowerCase().compareTo(o2.getPurchaseOrderNumber().toLowerCase());
				break;
			case "overallStatus":
				result = o1.getOverallStatus().toLowerCase().compareTo(o2.getOverallStatus().toLowerCase());
				break;
			case "shippingStatus":
				result = o1.getShippingStatus().toLowerCase().compareTo(o2.getShippingStatus().toLowerCase());
				break;
			case "orderNumber":
				result = o2.getKey().getIdAsString().toLowerCase().compareTo(o1.getKey().getIdAsString().toLowerCase());
				break;
			//Creation Date as default value
			default:
				result = o2.getCreationDate().compareTo(o1.getCreationDate());
				break;
		}

		if (result == 0)
		{
			result = o2.getKey().getIdAsString().toLowerCase().compareTo(o1.getKey().getIdAsString().toLowerCase());
		}

		return result;
	}
}
