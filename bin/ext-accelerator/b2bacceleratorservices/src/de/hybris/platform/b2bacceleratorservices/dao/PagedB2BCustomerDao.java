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
package de.hybris.platform.b2bacceleratorservices.dao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


public interface PagedB2BCustomerDao<M> extends PagedGenericDao<M>
{
	/**
	 * Finds all visible users within a sessions branch 2 sorts are available by default, sortCode "byName" and "byDate"
	 * 
	 * @param sortCode
	 * @param pageableData
	 * @return A paged result of customers
	 */
	SearchPageData<B2BCustomerModel> findPagedCustomers(String sortCode, PageableData pageableData);

	SearchPageData<B2BCustomerModel> findPagedCustomersByGroupMembership(String sortCode, PageableData pageableData,
			String... userGroupId);

	SearchPageData<B2BCustomerModel> findPagedCustomersForUnitByGroupMembership(String sortCode, PageableData pageableData,
			String unit, String... userGroupId);

	SearchPageData<B2BCustomerModel> findPagedApproversForUnitByGroupMembership(String sortCode, PageableData pageableData,
			String unit, String... userGroupId);

	SearchPageData<B2BCustomerModel> findPagedCustomersForUnit(String sortCode, PageableData pageableData, String unit);
}
