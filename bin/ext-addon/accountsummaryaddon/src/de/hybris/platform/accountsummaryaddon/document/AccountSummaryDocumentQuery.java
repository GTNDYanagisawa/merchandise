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

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;

public class AccountSummaryDocumentQuery
{
	private final Map<String, Object> searchCriteria = new HashMap<String, Object>();

	private final PageableData pageableData;

	private final boolean sortAsc;

	private static final String WILD_CARD = "%";


	public AccountSummaryDocumentQuery(final PageableData pageableData, final boolean sortAsc)
	{
		this.pageableData = pageableData;
		this.sortAsc = sortAsc;
	}

	public boolean isAsc()
	{
		return sortAsc;
	}

	public void addCriteria(final String criteriaName, final Object criteriaValue)
	{
		if (criteriaValue instanceof String && !StringUtils.equals(criteriaName, B2BDocumentModel.UNIT))
		{
			this.searchCriteria.put(criteriaName, WILD_CARD + criteriaValue + WILD_CARD);
		}
		else
		{
			this.searchCriteria.put(criteriaName, criteriaValue);
		}
	}

	public Map<String, Object> getSearchCriteria()
	{
		return this.searchCriteria;
	}

	public PageableData getPageableData()
	{
		return pageableData;
	}
}
