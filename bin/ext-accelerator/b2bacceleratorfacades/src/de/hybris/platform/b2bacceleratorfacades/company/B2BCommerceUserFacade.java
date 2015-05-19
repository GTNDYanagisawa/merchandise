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
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * A facade for user management within b2b commerce
 */
public interface B2BCommerceUserFacade
{
	/**
	 * Returns {@link B2BUnitData} for given customer uid.
	 * 
	 * @param uid
	 *           of customer
	 * @return found {@link B2BUnitData}
	 */
	B2BUnitData getParentUnitForCustomer(String uid);

	/**
	 * Get Paginated list of customers
	 * 
	 * @param pageableData
	 *           Pagination Data
	 * @return A paginated list of customers
	 */
	SearchPageData<CustomerData> getPagedCustomers(PageableData pageableData);

	/**
	 * Gets the list of approvers for the customers
	 * 
	 * @param pageableData
	 *           Pagination Data
	 * @param uid
	 *           of customer
	 * @return Get Paginated list found approvers
	 */
	SearchPageData<UserData> getPagedApproversForCustomer(PageableData pageableData, String uid);

	/**
	 * Add an approver to the customer
	 * 
	 * @param user
	 *           the uid of the customer
	 * @param approver
	 *           the approver uid
	 * @return Returns the {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData addApproverForCustomer(String user, String approver);

	/**
	 * Remove an existing approver to the customer
	 * 
	 * @param user
	 *           the uid of the customer
	 * @param approver
	 *           the approver uid
	 * @return Returns the {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData removeApproverFromCustomer(String user, String approver);

	/**
	 * Gets the list of permissions of the customers
	 * 
	 * @param pageableData
	 *           Pagination Data
	 * @param uid
	 *           the uid of the customer
	 * @return Returns the {@link de.hybris.platform.commerceservices.search.pagedata.SearchPageData}
	 */
	SearchPageData<B2BPermissionData> getPagedPermissionsForCustomer(PageableData pageableData, String uid);

	/**
	 * Update customer model {@link de.hybris.platform.b2b.model.B2BCustomerModel}
	 * 
	 * @param customer
	 *           The Customer Data {@link CustomerData}
	 * @throws DuplicateUidException
	 */
	void updateCustomer(CustomerData customer) throws DuplicateUidException;

	/**
	 * Enable a customer
	 * 
	 * @param uid
	 *           The uid of the customer of {@link de.hybris.platform.b2b.model.B2BCustomerModel}
	 * @throws DuplicateUidException
	 */
	void enableCustomer(String uid) throws DuplicateUidException;

	/**
	 * Disable a customer
	 * 
	 * @param uid
	 *           The uid of the customer of {@link de.hybris.platform.b2b.model.B2BCustomerModel}
	 * @throws DuplicateUidException
	 */
	void disableCustomer(String uid) throws DuplicateUidException;


	/**
	 * Reset the customer password
	 * 
	 * @param uid
	 *           the uid of the customer
	 * @param updatedPassword
	 */
	void resetCustomerPassword(String uid, String updatedPassword);

	/**
	 * Remove the role from a user
	 * 
	 * @param user
	 *           the uid of the user
	 * @param role
	 *           the uid of the role to be removed
	 * @return Returns the {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData removeUserRole(String user, String role);

	/**
	 * Remove the role from a user
	 * 
	 * @param user
	 *           the uid of the user
	 * @param role
	 *           the uid of the role to be removed
	 * @return Returns the {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData addUserRole(String user, String role);

	/**
	 * Add a permission to a customer
	 * 
	 * @param user
	 *           the uid of the customer
	 * @param permission
	 *           the code of the permission
	 * @return Returns the {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData addPermissionToCustomer(String user, String permission);

	/**
	 * Remove a permission from a customer
	 * 
	 * @param user
	 *           the uid of the customer
	 * @param permission
	 *           the code of the permission
	 * @return Returns the {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData removePermissionFromCustomer(String user, String permission);

	/**
	 * Get Paginated list of B2B User groups the customer belongs to.
	 * 
	 * @param pageableData
	 *           Pageable Data
	 * @param user
	 *           the uid of the customer
	 * @return Returns the {@link de.hybris.platform.commerceservices.search.pagedata.SearchPageData}
	 */
	SearchPageData<B2BUserGroupData> getPagedB2BUserGroupsForCustomer(PageableData pageableData, String user);

	/**
	 * Add b2b user group to a customer
	 * 
	 * @param user
	 *           the uid of the customer
	 * @param usergroup
	 *           the usergroup name
	 * @return Returns {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData addB2BUserGroupToCustomer(String user, String usergroup);


	/**
	 * @Deprecated Use deselectB2BUserGroupFromCustomer(String user, String usergroup) or
	 *             removeB2BUserGroupFromCustomerGroups(String user, String usergroup) instead.
	 * 
	 */
	@Deprecated
	B2BSelectionData removeB2BUserGroupFromCustomer(String user, String usergroup);

	/**
	 * Remove b2b user group from a customer
	 * 
	 * @param user
	 *           the uid of the customer
	 * @param usergroup
	 *           the usergroup name
	 */
	void removeB2BUserGroupFromCustomerGroups(String user, String usergroup);

	/**
	 * Deselects b2b user group from a customer
	 * 
	 * @param user
	 *           the uid of the customer
	 * @param usergroup
	 *           the usergroup name
	 * @return Returns {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData}
	 */
	B2BSelectionData deselectB2BUserGroupFromCustomer(String user, String usergroup);
}
