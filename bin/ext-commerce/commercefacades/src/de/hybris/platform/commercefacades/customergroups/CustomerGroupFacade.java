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
package de.hybris.platform.commercefacades.customergroups;

import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;

import java.util.List;
import java.util.Set;


/**
 * Facade for management of customer groups - that is user groups which are sub group of user group with id defined via
 * {@link #setBaseCustomerGroupId(String)}. Typically customer group id = 'customergroup'
 * 
 */
public interface CustomerGroupFacade
{
	/**
	 * Create customer group (direct sub group of 'customergroup') with given uid and localized name in current locale
	 */
	void createCustomerGroup(String uid, String localizedName);

	/**
	 * Assign user to customer group
	 * 
	 * @param customerGroupid
	 *           - customer group uid
	 * @param userId
	 *           - user uid
	 */
	void addUserToCustomerGroup(String customerGroupid, String userId);

	/**
	 * Remove user from customer group
	 * 
	 * @param customerGroupid
	 * @param userId
	 */
	void removeUserFromCustomerGroup(String customerGroupid, String userId);

	/**
	 * 
	 * @return all customer groups of a current customer
	 */
	List<UserGroupData> getCustomerGroupsForCurrentUser();

	/**
	 * 
	 * @return all customer groups of a given customer
	 */
	List<UserGroupData> getCustomerGroupsForUser(String uid);

	/**
	 * Returns user group with uid 'customergroup' and all it's direct subgroups
	 * 
	 * @param pageOption
	 *           - result paging option.
	 * @return All customer groups as {@link UserGroupDataList}.
	 */
	UserGroupDataList getAllCustomerGroups(PageOption pageOption);

	/**
	 * return customer group (a sub-group of 'cutomergroup') by uid.
	 * 
	 * @param customerGroupId
	 *           - customer group id
	 * @param options
	 *           - collection of required {@link UserGroupOption}s
	 * 
	 */
	UserGroupData getCustomerGroup(String uid, Set<UserGroupOption> options);
}
