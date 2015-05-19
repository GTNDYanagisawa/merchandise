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
package de.hybris.platform.b2bacceleratorfacades.company;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.B2BPermissionTypeEnum;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.Collection;


/**
 * A facade for permission management within b2b commerce
 */
public interface B2BCommercePermissionFacade
{
	SearchPageData<B2BPermissionData> getPagedPermissions(PageableData pageableData);

	B2BPermissionData getPermissionDetails(String uid);

	Collection<B2BPermissionTypeData> getB2BPermissionTypes();

	void updatePermissionDetails(B2BPermissionData b2BPermissionData) throws DuplicateUidException;

	/**
	 * Enable/disable a permission. active set to true denotes enabling permission and vice versa.
	 * 
	 * @param permissionCode
	 * @param active
	 * @throws DuplicateUidException
	 */
	void enableDisablePermission(String permissionCode, boolean active) throws DuplicateUidException;

	B2BPermissionTypeData getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum permissionTypeEnum);

	void addPermission(B2BPermissionData b2BPermissionData) throws DuplicateUidException;
}
