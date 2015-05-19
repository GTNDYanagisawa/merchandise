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
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserGroupModel;


/**
 * A service for user management within b2b commerce
 */
public interface B2BCommerceUserService
{
	/**
	 * Gets list of {@link SearchPageData} for pagination given the required pagination parameters with
	 * {@link PageableData}
	 * 
	 * @param pageableData
	 *           Pagination information
	 * @return List of paginated {@link B2BCustomerModel} objects
	 */
	SearchPageData<B2BCustomerModel> getPagedCustomers(PageableData pageableData);

	/**
	 * Gets list of {@link SearchPageData} for pagination given the required pagination parameters with
	 * {@link PageableData}
	 * 
	 * @param pageableData
	 *           Pagination information
	 * @param userGroupUid
	 *           Unique identifiers for {@link B2BUserGroupModel}
	 * @return List of paginated {@link B2BCustomerModel} objects
	 */
	SearchPageData<B2BCustomerModel> getPagedCustomersByGroupMembership(PageableData pageableData, String... userGroupUid);

	/**
	 * Add an approver for a given user and return the updated {@link B2BCustomerModel} object updated with approver
	 * details
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param approver
	 *           A unique identifier for {@link B2BCustomerModel} representing a approver
	 * @return Updated {@link B2BCustomerModel} object updated with approvers
	 */
	B2BCustomerModel addApproverToCustomer(String user, String approver);

	/**
	 * Remove an approver for a given user and return the updated {@link B2BCustomerModel} object updated with approver
	 * details
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param approver
	 *           A unique identifier for {@link B2BCustomerModel} representing a approver
	 * @return Updated {@link B2BCustomerModel} object removed with approver
	 */
	B2BCustomerModel removeApproverFromCustomer(String user, String approver);

	/**
	 * Remove user role for a given user and return the updated {@link B2BCustomerModel} object
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param role
	 *           A unique identifier for {@link UserGroupModel} representing a user groups to which the user belongs
	 * @return Updated {@link B2BCustomerModel} object with user groups removed matching the role
	 */
	B2BCustomerModel removeUserRole(String user, String role);

	/**
	 * Add user role for a given user and return the updated {@link B2BCustomerModel} object
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param role
	 *           A unique identifier for {@link UserGroupModel} representing a user groups to which the user belongs
	 * @return Updated {@link B2BCustomerModel} object with user groups added matching the role
	 */
	B2BCustomerModel addUserRole(String user, String role);

	/**
	 * Add permission for a given user and return the updated {@link B2BCustomerModel} object
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param permission
	 *           A unique identifier for {@link B2BPermissionModel} which is added to the given user
	 * @return Updated {@link B2BPermissionModel} object with permissions added
	 */
	B2BPermissionModel addPermissionToCustomer(String user, String permission);

	/**
	 * Remove permission for a given user and return the updated {@link B2BCustomerModel} object
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param permission
	 *           A unique identifier for {@link B2BPermissionModel} which is removed from the given user
	 * @return Updated {@link B2BPermissionModel} object with permissions removed
	 */
	B2BPermissionModel removePermissionFromCustomer(String user, String permission);

	/**
	 * Add Usergroups @link B2BCustomerModel} object for a given unique id of a customer
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a customer
	 * @param usergroup
	 *           A unique identifier for {@link B2BUserGroupModel} representing a user group
	 * @return Updated {@link B2BUserGroupModel} object with user group added for the given unique identifier for
	 *         {@link B2BUserGroupModel}
	 */
	B2BUserGroupModel addB2BUserGroupToCustomer(String user, String usergroup);

	/**
	 * @deprecated Use deselectB2BUserGroupFromCustomer(String user, String usergroup) or
	 *             removeB2BUserGroupToCustomer(String user, String usergroup) instead.
	 */
	@Deprecated
	B2BUserGroupModel removeB2BUserGroupToCustomer(String user, String usergroup);

	/**
	 * Deselects usergroup from a customer.
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a customer
	 * @param usergroup
	 *           A unique identifier for {@link B2BUserGroupModel} representing a user group
	 * @return Updated {@link B2BUserGroupModel} object with user group.
	 */
	B2BUserGroupModel deselectB2BUserGroupFromCustomer(String user, String usergroup);

	/**
	 * Removes usergroup from a customer.
	 * 
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a customer
	 * @param usergroup
	 *           A unique identifier for {@link B2BUserGroupModel} representing a user group
	 */
	void removeB2BUserGroupFromCustomerGroups(String user, String usergroup);

	/**
	 * Get parent unit {@link B2BUnitModel} for a given unique id of a customer
	 * 
	 * @param uid
	 *           A unique id for @link B2BCustomerModel} object of a customer
	 * @return Parent unit {@link B2BUnitModel} object for a given unique id of customer
	 * 
	 * @throws IllegalArgumentException
	 *            if uid is null
	 */
	<T extends B2BUnitModel> T getParentUnitForCustomer(String uid);

	/**
	 * Disable a customer given its unique id
	 * 
	 * @param uid
	 *           A unique id for @link B2BCustomerModel} representing a user
	 * @throws DuplicateUidException
	 */
	void disableCustomer(String uid) throws DuplicateUidException;

	/**
	 * Enable customer given its unique id
	 * 
	 * @param uid
	 *           A unique id for @link B2BCustomerModel} representing a user
	 * @throws DuplicateUidException
	 */
	void enableCustomer(String uid) throws DuplicateUidException;
}
