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

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.Collection;


/**
 * A service for unit management within b2b commerce
 */
public interface B2BCommerceUnitService
{
	/**
	 * A collection of business units based on root unit of an organization to which the parent business unit of the
	 * currently logged in customer belongs to
	 * 
	 * @return A collection of units where the root unit is the parent business unit of the currently logged in customer.
	 */
	Collection<? extends B2BUnitModel> getOrganization();

	/**
	 * A branch of business units based on the parent unit of the current session user.
	 * 
	 * @return A collection of units where the root unit is the parent business unit of the currently logged in customer.
	 */
	Collection<? extends B2BUnitModel> getBranch();

	/**
	 * Gets a Root unit of the organization based on the parent business unit of the session user
	 * 
	 * @see #getParentUnit()
	 * @return A root unit of an organization a session customer belongs to
	 */
	<T extends B2BUnitModel> T getRootUnit();

	/**
	 * Gets a parent unit of the current session user
	 * 
	 * @return The business unit assigned to the current session user.
	 */
	<T extends B2BUnitModel> T getParentUnit();

	/**
	 * Assign a parent unit to unitModel
	 * 
	 * @param unitModel
	 *           A unit to assign a parent for {@link B2BUnitModel}
	 * @param parentUnit
	 *           The parent unit {@link B2BUnitModel}
	 */
	void setParentUnit(B2BUnitModel unitModel, B2BUnitModel parentUnit);

	/**
	 * Get all the units of a organization for current logged in user
	 * 
	 * @return Collection of units for the organization of the current user
	 */
	Collection<? extends B2BUnitModel> getAllUnitsOfOrganization();

	/**
	 * Gets a customer for a given uid
	 *
	 * @param uid
	 *           A unique identifier for {@link B2BCustomerModel}
	 * @return A {@link B2BCustomerModel} for the given uid
	 */
	<T extends B2BCustomerModel> T getCustomerForUid(String uid);

	/**
	 * Gets all the allowed parent units for a given {@link B2BUnitModel}
	 * 
	 * @param unit
	 *           A unique identifier for a unit
	 * @return A collection of {@link B2BUnitModel} for the given uid
	 */
	Collection<? extends B2BUnitModel> getAllowedParentUnits(B2BUnitModel unit);

	/**
	 * Updates the branch collection in the session for the current user. Should be called after a new unit creation so
	 * that its does not get filter out by the unit branch search restriction.
	 */
	void updateBranchInSession();

	/**
	 * Add approver to given unit
	 * 
	 * @param unitId
	 *           A unique identifier for {@link B2BUnitModel}
	 * @param approverId
	 *           A unique identifier for {@link B2BCustomerModel} who is a approver
	 * @return Updated {@link B2BCustomerModel} object after adding approver to customer.
	 */
	B2BCustomerModel addApproverToUnit(String unitId, String approverId);

	/**
	 * If an approver is a member of the B2BUnit remove b2bapprovergroup role, if the approver is a member of the current
	 * branch of units the approver will be removed from {@link de.hybris.platform.b2b.model.B2BUnitModel#getApprovers()}
	 * relationship
	 * 
	 * @param unitUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param approverUid
	 *           A unique identifier of {@link B2BCustomerModel}
	 * @return An approver who was removed from a {@link de.hybris.platform.b2b.model.B2BUnitModel#getApprovers()}
	 */
	B2BCustomerModel removeApproverFromUnit(String unitUid, String approverUid);

	/**
	 * Disable unit based on the given uid
	 * 
	 * @param uid
	 *           A unique identifier of {@link B2BUnitModel}
	 */
	void disableUnit(String uid);

	/**
	 * Enable unit based on the given uid
	 * 
	 * @param unit
	 *           A unique identifier of {@link B2BUnitModel}
	 */
	void enableUnit(String unit);

	/**
	 * Gets parent unit based on the given unit
	 * 
	 * @param unit
	 *           A {@link B2BUnitModel} object
	 * @return A {@link B2BUnitModel} object which denotes the parent unit of teh given unit
	 */
	<T extends B2BUnitModel> T getParentUnit(B2BUnitModel unit);

	/**
	 * Removes the address from a given unit
	 * 
	 * @param unitUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param addressId
	 *           A unique identifier of {@link AddressModel}
	 */
	void removeAddressEntry(String unitUid, String addressId);

	/**
	 * Sets a given address for a unit
	 * 
	 * @param unitForUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param addressModel
	 *           {@link AddressModel} object which is getting added to unit
	 */
	void saveAddressEntry(B2BUnitModel unitForUid, AddressModel addressModel);

	/**
	 * Gets a {@link AddressModel} object for a given unit
	 * 
	 * @param unit
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param id
	 *           A unique identifier of {@link AddressModel}
	 * @return {@link AddressModel} object
	 */
	AddressModel getAddressForCode(B2BUnitModel unit, String id);

	/**
	 * Save updated {@link AddressModel} object to a unit
	 * 
	 * @param unitModel
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param addressModel
	 *           {@link AddressModel} object for given unit
	 */
	void editAddressEntry(B2BUnitModel unitModel, AddressModel addressModel);

	/**
	 * Gets list of {@link SearchPageData} {@link B2BCustomerModel} for a given unit for pagination provided with
	 * required pagination parameters with {@link PageableData}
	 * 
	 * @param pageableData
	 *           Pagination information
	 * @param unit
	 *           A unique identifier of {@link B2BUnitModel}
	 * @return Collection of paginated {@link B2BCostCenterModel} objects
	 */
	SearchPageData<B2BCustomerModel> getPagedUsersForUnit(PageableData pageableData, String unit);

	/**
	 * Gets list of {@link SearchPageData} {@link B2BCustomerModel} provided with required pagination parameters with
	 * {@link PageableData}
	 * 
	 * @param pageableData
	 *           Pagination information
	 * @param unitUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param userGroupUid
	 *           A unique identifier of {@link B2BUserGroupModel}
	 * @return Collection of paginated {@link B2BCostCenterModel} objects
	 */
	SearchPageData<B2BCustomerModel> findPagedApproversForUnitByGroupMembership(PageableData pageableData, String unitUid,
			String... userGroupUid);
}
