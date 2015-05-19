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
package de.hybris.platform.b2bacceleratorfacades.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bacceleratorfacades.company.B2BCommerceB2BUserGroupFacade;
import de.hybris.platform.b2bacceleratorfacades.company.data.UserData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;


public class DefaultB2BCommerceB2BUserGroupFacade extends DefaultCompanyB2BCommerceFacade implements
		B2BCommerceB2BUserGroupFacade
{

	@Override
	public SearchPageData<UserData> getPagedCustomersForUserGroup(final PageableData pageableData, final String usergroupUID)
	{
		final SearchPageData<UserData> searchPageData = this.getPagedUserData(pageableData);
		// update the results with users that already have been selected.
		final UserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(usergroupUID,
				UserGroupModel.class);
		validateParameterNotNull(userGroupModel, String.format("No usergroup found for uid %s", usergroupUID));
		for (final UserData userData : searchPageData.getResults())
		{
			final UserModel user = getUserService().getUserForUID(userData.getUid());
			userData.setSelected(CollectionUtils.find(user.getGroups(), new BeanPropertyValueEqualsPredicate(UserModel.UID,
					userGroupModel.getUid())) != null);
		}
		return searchPageData;
	}

	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissionsForUserGroup(final PageableData pageableData,
			final String usergroupUID)
	{
		final SearchPageData<B2BPermissionModel> permissions = getB2BCommercePermissionService().getPagedPermissions(pageableData);
		final SearchPageData<B2BPermissionData> searchPageData = convertPageData(permissions, getB2BPermissionConverter());
		final B2BUserGroupModel userGroupModel = getCompanyB2BCommerceService().getB2BUserGroupForUid(usergroupUID);
		validateParameterNotNull(userGroupModel, String.format("No usergroup found for uid %s", usergroupUID));
		for (final B2BPermissionData permissionData : searchPageData.getResults())
		{
			permissionData.setSelected(CollectionUtils.find(userGroupModel.getPermissions(), new BeanPropertyValueEqualsPredicate(
					B2BPermissionModel.CODE, permissionData.getCode())) != null);
		}

		return searchPageData;
	}

	@Override
	public B2BSelectionData addPermissionToUserGroup(final String userGroupUid, final String permission)
	{
		validateParameterNotNullStandardMessage("permission", permission);
		validateParameterNotNullStandardMessage("usergroup", userGroupUid);

		final B2BPermissionModel permissionModel = getB2BCommerceB2BUserGroupService().addPermissionToUserGroup(userGroupUid,
				permission);
		return createB2BSelectionData(permissionModel.getCode(), true, permissionModel.getActive().booleanValue());
	}

	@Override
	public B2BSelectionData removePermissionFromUserGroup(final String uid, final String permission)
	{
		validateParameterNotNullStandardMessage("permission", permission);
		validateParameterNotNullStandardMessage("usergroup", uid);

		final B2BPermissionModel permissionModel = getB2BCommerceB2BUserGroupService().removePermissionFromUserGroup(uid,
				permission);
		return createB2BSelectionData(permissionModel.getCode(), false, permissionModel.getActive().booleanValue());
	}

	@Override
	//TODO: REFACTOR into a ReverseB2BUserGroupPopulator
	public void updateUserGroup(final String userGroupUid, final B2BUserGroupData userGroupData) throws DuplicateUidException
	{
		B2BUserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(userGroupUid,
				B2BUserGroupModel.class);
		if (userGroupModel == null)
		{
			userGroupModel = this.getModelService().create(B2BUserGroupModel.class);
		}
		userGroupModel.setName(userGroupData.getName());
		userGroupModel.setLocName(userGroupData.getName());
		userGroupModel.setUid(userGroupData.getUid());
		if (userGroupData.getUnit() != null)
		{
			final B2BUnitModel unitModel = this.getCompanyB2BCommerceService().getUnitForUid(userGroupData.getUnit().getUid());
			userGroupModel.setUnit(unitModel);
		}

		this.getCompanyB2BCommerceService().saveModel(userGroupModel);
	}

	@Override
	public void disableUserGroup(final String userGroupUid)
	{
		validateParameterNotNullStandardMessage("usergroup id", userGroupUid);
		getB2BCommerceB2BUserGroupService().disableUserGroup(userGroupUid);
	}

	@Override
	public void removeUserGroup(final String userGroupUid)
	{
		validateParameterNotNullStandardMessage("usergroup id", userGroupUid);
		getB2BCommerceB2BUserGroupService().removeUserGroup(userGroupUid);
	}

	@Override
	public SearchPageData<UserData> getPagedUserData(final PageableData pageableData)
	{
		final SearchPageData<B2BCustomerModel> customers = getB2BCommerceUserService().getPagedCustomers(pageableData);
		return convertPageData(customers, getB2BUserConverter());
	}

	@Override
	public SearchPageData<B2BUserGroupData> getPagedB2BUserGroups(final PageableData pageableData)
	{
		final SearchPageData<B2BUserGroupModel> groups = getB2BCommerceB2BUserGroupService().getPagedB2BUserGroups(pageableData);
		final SearchPageData<B2BUserGroupData> searchPageData = convertPageData(groups, getB2BUserGroupConverter());
		return searchPageData;
	}

	@Override
	public B2BUserGroupData getB2BUserGroup(final String uid)
	{
		validateParameterNotNullStandardMessage("uid", uid);
		final B2BUserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(uid,
				B2BUserGroupModel.class);

		if (userGroupModel != null)
		{
			return this.getB2BUserGroupConverter().convert(userGroupModel);
		}
		return null;
	}

	@Override
	public UserData addMemberToUserGroup(final String usergroup, final String user)
	{
		validateParameterNotNullStandardMessage("usergroup", usergroup);
		validateParameterNotNullStandardMessage("user", user);

		final B2BCustomerModel customerModel = getB2BCommerceB2BUserGroupService().addMemberToUserGroup(usergroup, user);
		final UserData userData = this.getB2BUserConverter().convert(customerModel);
		userData.setSelected(true);
		return userData;
	}

	@Override
	public UserData removeMemberFromUserGroup(final String usergroup, final String user)
	{
		validateParameterNotNullStandardMessage("usergroup", usergroup);
		validateParameterNotNullStandardMessage("user", user);

		final B2BCustomerModel customerModel = getB2BCommerceB2BUserGroupService().removeMemberFromUserGroup(usergroup, user);
		final UserData userData = this.getB2BUserConverter().convert(customerModel);
		userData.setSelected(false);
		return userData;
	}

	@Override
	public UserGroupData getUserGroupDataForUid(final String uid)
	{
		final UserGroupModel userGroupForUID = getB2BCommerceB2BUserGroupService().getUserGroupForUID(uid, UserGroupModel.class);
		if (userGroupForUID != null)
		{
			final UserGroupData userGroupData = new UserGroupData();
			userGroupData.setUid(userGroupForUID.getUid());
			userGroupData.setName(userGroupForUID.getName());
			return userGroupData;
		}
		else
		{
			return null;
		}
	}
}
