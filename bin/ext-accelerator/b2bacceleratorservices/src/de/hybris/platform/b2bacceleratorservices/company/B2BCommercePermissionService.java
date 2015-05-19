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
package de.hybris.platform.b2bacceleratorservices.company;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * A service for permission management within b2b commerce
 */
public interface B2BCommercePermissionService
{
	/**
	 * Gets list of {@link SearchPageData} B2BPermissionModel for pagination given the required pagination parameters
	 * with {@link PageableData}
	 * 
	 * @param pageableData
	 *           Pagination information
	 * @return Collection of paginated {@link B2BPermissionModel} objects
	 */
	SearchPageData<B2BPermissionModel> getPagedPermissions(PageableData pageableData);
}
