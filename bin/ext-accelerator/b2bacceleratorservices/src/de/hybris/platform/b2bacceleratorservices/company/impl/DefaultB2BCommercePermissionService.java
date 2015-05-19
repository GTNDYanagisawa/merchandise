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

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommercePermissionService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


public class DefaultB2BCommercePermissionService extends DefaultCompanyB2BCommerceService implements B2BCommercePermissionService
{
	@Override
	public SearchPageData<B2BPermissionModel> getPagedPermissions(final PageableData pageableData)
	{
		return getPagedB2BPermissionDao().findPagedPermissions("byName", pageableData);
	}
}
