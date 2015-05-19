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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.Collection;
import java.util.Set;


/**
 * A service for budget management within b2b commerce
 */
public interface B2BCommerceB2BUserGroupService
{
	/**
	 * Gets list of {@link SearchPageData} for pagination given the required pagination parameters with
	 * {@link PageableData}
	 * 
	 * @param pageableData
	 *           Pagination information
	 * @return List of paginated {@link B2BUserGroupModel} objects
	 */
	SearchPageData<B2BUserGroupModel> getPagedB2BUserGroups(PageableData pageableData);

	/**
	 * Gets updated collection of user groups
	 * 
	 * @param userGroups
	 *           Collection of user groups that has to be updated customer
	 * @param roles
	 *           Collection of roles
	 * @param customerModel
	 *           Customer object for which the user group has to be updated
	 * @return Updated {@link PrincipalGroupModel} object with given user groups.
	 */
	Set<PrincipalGroupModel> updateUserGroups(Collection<String> userGroups, Collection<String> roles,
			B2BCustomerModel customerModel);

	/**
	 * Gets a user group for given uid and user group type
	 * 
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @param userGroupType
	 *           User group type
	 * @return Usergroup object based on the given uid and user group type
	 */
	<T extends UserGroupModel> T getUserGroupForUID(String uid, Class<T> userGroupType);

	/**
	 * Gets updated permission, after adding permission to given user group
	 * 
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @param permission
	 *           Permission that has to be added to user group
	 * @return Updated {@link B2BPermissionModel} object
	 * 
	 */
	B2BPermissionModel addPermissionToUserGroup(String uid, String permission);

	/**
	 * Gets updated permission, after removing permission from a given user group
	 * 
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @param permission
	 *           Permission that has to be added to user group
	 * @return Updated {@link B2BPermissionModel} object
	 * 
	 */
	B2BPermissionModel removePermissionFromUserGroup(String uid, String permission);

	/**
	 * Disable a user group given its uid
	 * 
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 */
	void disableUserGroup(String uid);

	/**
	 * Remove user group given its uid
	 * 
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 */
	void removeUserGroup(String uid);

	/**
	 * Gets updated customer, after adding customer to given user group
	 * 
	 * @param userGroup
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel}
	 * @return Updated {@link B2BCustomerModel} object
	 * 
	 */
	B2BCustomerModel addMemberToUserGroup(String userGroup, String user);

	/**
	 * Gets updated customer, after removing customer from a given user group
	 * 
	 * @param userGroup
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel}
	 * @return Updated {@link B2BCustomerModel} object
	 * 
	 */
	B2BCustomerModel removeMemberFromUserGroup(String userGroup, String user);
}
