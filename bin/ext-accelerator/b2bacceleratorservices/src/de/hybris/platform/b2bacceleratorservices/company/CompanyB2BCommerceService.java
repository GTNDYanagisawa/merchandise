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
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * A service which services organization management.
 */
public interface CompanyB2BCommerceService
{
	/**
	 * Get a {@link B2BUnitModel} for a unique identifier <param>unitUid</param>
	 * 
	 * @param unitUid
	 *           A unique identifier for a {@link B2BUnitModel}
	 * @return A unit if exist or null if not found
	 */
	B2BUnitModel getUnitForUid(String unitUid);

	/**
	 * Gets a current session user as a {@link B2BCustomerModel}
	 * 
	 * @return A customer in the session.
	 */
	<T extends B2BCustomerModel> T getCurrentUser();

	/**
	 * Searches for users
	 * 
	 * @param pageableData
	 *           to use in narrowing result
	 * @param userGroupUID
	 *           uids of groups to search against
	 * @return users which belong to groups with given uids
	 */
	SearchPageData<B2BCustomerModel> getPagedUsersForUserGroups(PageableData pageableData, String... userGroupUID);

	/**
	 * Get members who are assigned to a unit with a specified user group
	 * 
	 * @param unit
	 *           A business unit
	 * @param userGroupId
	 *           UserGroup id
	 * @return A collection of users assigned to the unit who are members of the specified group
	 */

	<T extends B2BCustomerModel> List<T> getMembersOfUnitForUserGroup(B2BUnitModel unit, String userGroupId);


	/**
	 * Get all currencies for the current store
	 * 
	 * @return Collection of currencyModels
	 */
	Collection<? extends CurrencyModel> getAllCurrencies();

	/**
	 * Get the currencyModel for the given ISO code
	 * 
	 * @param isoCode
	 *           ISO Code of the {@link CurrencyModel}
	 * @return A {@link CurrencyModel} object that matches the given ISO code
	 */
	<T extends CurrencyModel> T getCurrencyForIsoCode(String isoCode);

	/**
	 * Persists the model delegating to {@link de.hybris.platform.servicelayer.model.ModelService#save(Object)}
	 * 
	 * @param model
	 *           A model to save.
	 * 
	 * @throws DuplicateUidException
	 *            Is thrown if a model is not unique based on its unique identifiers.
	 * 
	 */
	<T extends ItemModel> void saveModel(T model) throws DuplicateUidException;

	/**
	 * Gets a customer for the given uid
	 * 
	 * @param uid
	 *           A unique identifier for {@link B2BCustomerModel}
	 * @return A {@link B2BCustomerModel} for uid
	 */
	<T extends B2BCustomerModel> T getCustomerForUid(String uid);


	/**
	 * Gets the parent unit given a unit object.
	 * 
	 * @param unit
	 *           A unique identifier for {@link B2BUnitModel}
	 * @return A {@link B2BUnitModel} for given unit
	 */
	<T extends B2BUnitModel> T getParentUnit(B2BUnitModel unit);

	/**
	 * Gets a b2b user group given its uid
	 * 
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @return A {@link B2BUserGroupModel} for given unit
	 */
	B2BUserGroupModel getB2BUserGroupForUid(String uid);

	/**
	 * Gets a map object containing the business processes for the current store
	 * 
	 * @return A {@link Map} containing the business processes information
	 */
	Map<String, String> getBusinessProcesses();

	/**
	 * Gets {@link B2BPermissionModel } for a given permission code
	 * 
	 * @param permissionCode
	 *           A unique identifier for {@link B2BPermissionModel}
	 * @return {@link B2BPermissionModel } object
	 */
	B2BPermissionModel getPermissionForCode(String permissionCode);
}
