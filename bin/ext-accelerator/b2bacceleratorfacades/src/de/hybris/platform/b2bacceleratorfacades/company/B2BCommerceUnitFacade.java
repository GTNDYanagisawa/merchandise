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

import de.hybris.platform.b2bacceleratorfacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bacceleratorfacades.company.data.UserData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.List;


/**
 * A facade for unit management within b2b commerce
 */
public interface B2BCommerceUnitFacade
{
	/**
	 * Gets a paged list of approvers. Approvers already assigned to the business unit with <param>unitUid</param> are
	 * marked as selected.
	 * 
	 * @param pageableData
	 *           Pagination data
	 * @param unitUid
	 *           A unit id of the business unit from which to check selected approvers.
	 * @return A paged approver data.
	 */
	SearchPageData<UserData> getPagedApproversForUnit(PageableData pageableData, String unitUid);

	/**
	 * Get a list of customers associated with a give unit.
	 * 
	 * @param pageableData
	 * @param unitUid
	 *           A uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A paginated list of customers.
	 */
	SearchPageData<UserData> getPagedCustomersForUnit(PageableData pageableData, String unitUid);

	/**
	 * Get a list of administrators associated with a give unit.
	 * 
	 * @param pageableData
	 * @param unitUid
	 *           A uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A paginated list of customers.
	 */
	SearchPageData<UserData> getPagedAdministratorsForUnit(PageableData pageableData, String unitUid);

	/**
	 * Get a list of managers associated with a give unit.
	 * 
	 * @param pageableData
	 * @param unitUid
	 *           A uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A paginated list of customers.
	 */
	SearchPageData<UserData> getPagedManagersForUnit(PageableData pageableData, String unitUid);

	/**
	 * Disables a unit based on a uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * 
	 * @param uid
	 */
	void disableUnit(String uid);

	/**
	 * Gets a business unit as a B2BUnitNodeData assigned to the current session user with all the siblings retrieved via
	 * {@link de.hybris.platform.b2bacceleratorfacades.company.data.B2BUnitNodeData#getChildren()} which only has enough data to
	 * construct a tree view.
	 * 
	 * @return A business unit assigned to the session customer
	 */
	B2BUnitNodeData getParentUnitNode();

	/**
	 * A list of parent units for which the unit with <param>uid</param> can be assigned as a sibling
	 * 
	 * @param uid
	 *           An id of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A list of parent units that a given unit can be a child of
	 */
	List<B2BUnitNodeData> getAllowedParentUnits(String uid);

	/**
	 * Adds an approver to a unit.
	 * 
	 * @param unitUid
	 *           A unit to add an approver to
	 * @param approverUid
	 *           The approver to add to a unit's list of approvers
	 * @return An approver if added successfully otherwise null.
	 */
	B2BSelectionData addApproverToUnit(final String unitUid, final String approverUid);

	/**
	 * Removes an approver from a unit.
	 * 
	 * 
	 * @param unit
	 *           A business unit id
	 * @param approver
	 *           An approvers uid
	 * @return An approver
	 */
	B2BSelectionData removeApproverFromUnit(String unit, String approver);


	/**
	 * Get All units of organization which are enabled.
	 * 
	 * @return A collection of B2BUnit uids.
	 */
	List<String> getAllActiveUnitsOfOrganization();

	/**
	 * Get a list of customers directly associated to the unit plus all the customers who are members of given list of
	 * usergroups with the visible branch for the current session user. A list of {@link UserGroupModel#getUid()}
	 * 
	 * @param pageableData
	 *           Pagination data
	 * @param unit
	 *           A unit UID
	 * 
	 * @return A paginated list of {@link UserData}
	 */
	SearchPageData<UserData> getPagedUserDataForUnit(PageableData pageableData, String unit);

	/**
	 * Associates an address to a business unit
	 * 
	 * @param newAddress
	 *           Address data object
	 * @param unitUid
	 *           A unit uid
	 */
	void addAddressToUnit(AddressData newAddress, String unitUid);

	/**
	 * Remove an address from a unit
	 * 
	 * @param unitUid
	 * @param addressId
	 */
	void removeAddressFromUnit(String unitUid, String addressId);

	/**
	 * Edit address of a unit
	 * 
	 * @param newAddress
	 *           Address data
	 * @param unitUid
	 *           A unit UID
	 */
	void editAddressOfUnit(AddressData newAddress, String unitUid);

	/**
	 * Enable a Business Unit that is not active
	 * 
	 * @param unitUid
	 *           A unitUid uid
	 */
	void enableUnit(String unitUid);

	/**
	 * Updates {@link de.hybris.platform.b2b.model.B2BUnitModel} based on unit data if param originalUid is null the new
	 * unit is created
	 * 
	 * @param originalUid
	 *           the uid of {@link de.hybris.platform.b2b.model.B2BUnitModel} to update.
	 * @param unit
	 *           A unit data object
	 * @throws DuplicateUidException
	 *            If a unit being created has an uid already being used.
	 */
	void updateOrCreateBusinessUnit(String originalUid, B2BUnitData unit) throws DuplicateUidException;
}
