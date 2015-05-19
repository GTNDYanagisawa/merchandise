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

import de.hybris.platform.b2bacceleratorfacades.company.data.UserData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


public interface B2BCommerceB2BUserGroupFacade
{
	/**
	 * Get paginated list of customers who are members of a given {@link de.hybris.platform.b2b.model.B2BUserGroupModel}
	 * 
	 * @param pageableData
	 *           Pagination data
	 * @param usergroupUID
	 *           A uid of a {@link de.hybris.platform.b2b.model.B2BUserGroupModel}
	 * @return A paginated list of users
	 */
	SearchPageData<UserData> getPagedCustomersForUserGroup(PageableData pageableData, String usergroupUID);

	/**
	 * Get paginated list of permissions associated to a {@link de.hybris.platform.b2b.model.B2BUserGroupModel}
	 * 
	 * @param pageableData
	 *           Pagination data
	 * @param usergroupUID
	 * @return A paginated list of permissions
	 */
	SearchPageData<B2BPermissionData> getPagedPermissionsForUserGroup(PageableData pageableData, String usergroupUID);

	/**
	 * Add a permission to a {@link de.hybris.platform.b2b.model.B2BUserGroupModel}
	 * 
	 * @param userGroupUid
	 *           A uid of a UserGroupModel
	 * @param permission
	 *           A permission code of B2BPermissionModel
	 * @return A data object with information about the selected permission
	 */
	B2BSelectionData addPermissionToUserGroup(String userGroupUid, String permission);

	/**
	 * Removes a permission to a {@link de.hybris.platform.b2b.model.B2BUserGroupModel}
	 * 
	 * @param userGroupUid
	 *           A uid of a UserGroupModel
	 * @param permission
	 *           A permission code of B2BPermissionModel
	 * @return A data object with information about the deselected permission
	 */
	B2BSelectionData removePermissionFromUserGroup(String userGroupUid, String permission);

	/**
	 * Updates B2BUserGroup based on passed in data object
	 * 
	 * @param userGroupUid
	 *           A uid of a UserGroupModel
	 * @param userGroupData
	 *           UserGroup data object
	 * @throws DuplicateUidException
	 *            Is thrown if a B2BUserGroup already exists
	 */
	void updateUserGroup(String userGroupUid, B2BUserGroupData userGroupData) throws DuplicateUidException;

	/**
	 * Marks a B2BUserGroup as inactive
	 * 
	 * @param userGroupUid
	 */
	void disableUserGroup(String userGroupUid);

	/**
	 * Remove B2BUserGroup
	 * 
	 * @param userGroupUid
	 */
	void removeUserGroup(String userGroupUid);

	/**
	 * Get paginated list of users who are members of a given {@link de.hybris.platform.b2b.model.B2BCustomerModel}
	 * 
	 * @param pageableData
	 * 
	 * @return A paginated list of users
	 */
	SearchPageData<UserData> getPagedUserData(PageableData pageableData);

	/**
	 * Get a paginated lists of User Group data of type {@link de.hybris.platform.b2b.model.B2BUserGroupModel}
	 * 
	 * @param pageableData
	 * @return A paginated list of B2BUser group data
	 */
	SearchPageData<B2BUserGroupData> getPagedB2BUserGroups(PageableData pageableData);

	/**
	 * Get the User Group Data with the uid
	 * 
	 * @param uid
	 *           A uid of the User Group Model
	 * @return The B2B User Group Data
	 */
	B2BUserGroupData getB2BUserGroup(String uid);

	/**
	 * Add a B2B user {@link de.hybris.platform.b2b.model.B2BCustomerModel} to the B2B user group
	 * 
	 * @param usergroup
	 *           A uid of the User Group Model
	 * @param user
	 *           A uid of the Customer Model
	 * @return The User Data
	 */
	UserData addMemberToUserGroup(String usergroup, String user);

	/**
	 * Remove the user from the User Group {@link de.hybris.platform.b2b.model.B2BUserGroupModel}
	 * 
	 * @param usergroup
	 *           A uid of the User Group Model
	 * @param user
	 *           A uid of the Customer Model
	 * @return The User Data
	 */
	UserData removeMemberFromUserGroup(String usergroup, String user);

	/**
	 * Get the user group data {@link de.hybris.platform.b2b.model.B2BUserGroupModel} for the uid
	 * 
	 * @param uid
	 *           A uid of the User Group Model
	 * @return The User group Data
	 */
	UserGroupData getUserGroupDataForUid(String uid);
}
