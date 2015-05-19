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
package de.hybris.platform.b2bacceleratorservices.company.impl;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceBudgetService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * 
 * Default implementation of {@link B2BCommerceBudgetService }
 * 
 */
public class DefaultB2BCommerceBudgetService extends DefaultCompanyB2BCommerceService implements B2BCommerceBudgetService
{
	@Override
	public <T extends B2BBudgetModel> T getBudgetModelForCode(final String code)
	{
		return (T) getB2BBudgetService().getB2BBudgetForCode(code);
	}

	@Override
	public SearchPageData<B2BBudgetModel> findPagedBudgets(final PageableData pageableData)
	{
		return getPagedB2BBudgetDao().findPagedBudgets("byName", pageableData);
	}
}
