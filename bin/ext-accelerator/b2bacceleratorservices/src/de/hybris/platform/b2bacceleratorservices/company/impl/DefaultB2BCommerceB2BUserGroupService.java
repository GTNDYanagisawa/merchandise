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
package de.hybris.platform.b2bacceleratorservices.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DefaultB2BCommerceB2BUserGroupService extends DefaultCompanyB2BCommerceService implements
		B2BCommerceB2BUserGroupService
{
	@Override
	public SearchPageData<B2BUserGroupModel> getPagedB2BUserGroups(final PageableData pageableData)
	{
		return getPagedB2BUserGroupDao().findPagedB2BUserGroup("byName", pageableData);
	}

	@Override
	public Set<PrincipalGroupModel> updateUserGroups(final Collection<String> availableUserGroups,
			final Collection<String> selectedUserGroups, final B2BCustomerModel customerModel)
	{
		final Set<PrincipalGroupModel> customerGroups = new HashSet<PrincipalGroupModel>(customerModel.getGroups());

		// If you pass in NULL then nothing will happen
		if (selectedUserGroups != null)
		{
			for (final String group : availableUserGroups)
			{
				// add a group
				final UserGroupModel userGroupModel = getUserService().getUserGroupForUID(group);
				if (selectedUserGroups.contains(group))
				{
					customerGroups.add(userGroupModel);
				}
				else
				{ // remove a group
					customerGroups.remove(userGroupModel);
				}
			}
			customerModel.setGroups(customerGroups);
		}

		return customerGroups;
	}

	@Override
	public <T extends UserGroupModel> T getUserGroupForUID(final String uid, final Class<T> userGroupType)
	{
		try
		{
			return getUserService().getUserGroupForUID(uid, userGroupType);
		}
		catch (final UnknownIdentifierException uie)
		{
			return null;
		}
	}

	@Override
	public B2BPermissionModel addPermissionToUserGroup(final String uid, final String permission)
	{
		final B2BUserGroupModel userGroupModel = this.getB2BUserGroupForUid(uid);
		final List<B2BPermissionModel> permissionModels = new ArrayList<B2BPermissionModel>(userGroupModel.getPermissions());
		final B2BPermissionModel permissionModel = getPermissionForCode(permission);
		permissionModels.add(permissionModel);
		userGroupModel.setPermissions(permissionModels);
		this.getModelService().save(userGroupModel);
		return permissionModel;
	}

	@Override
	public B2BPermissionModel removePermissionFromUserGroup(final String uid, final String permission)
	{
		final B2BUserGroupModel userGroupModel = this.getB2BUserGroupForUid(uid);
		final List<B2BPermissionModel> permissionModels = new ArrayList<B2BPermissionModel>(userGroupModel.getPermissions());
		final B2BPermissionModel permissionModel = getPermissionForCode(permission);
		permissionModels.remove(permissionModel);
		userGroupModel.setPermissions(permissionModels);
		this.getModelService().save(userGroupModel);
		return permissionModel;
	}

	@Override
	public void disableUserGroup(final String uid)
	{
		final B2BUserGroupModel userGroupModel = this.getB2BUserGroupForUid(uid);
		validateParameterNotNullStandardMessage("B2BUserGroupModel", uid);
		userGroupModel.setMembers(Collections.<PrincipalModel> emptySet());
		getModelService().save(userGroupModel);
	}

	@Override
	public void removeUserGroup(final String uid)
	{
		final B2BUserGroupModel userGroupModel = this.getB2BUserGroupForUid(uid);
		validateParameterNotNullStandardMessage("B2BUserGroupModel", uid);
		getModelService().remove(userGroupModel);
	}


	@Override
	public B2BCustomerModel addMemberToUserGroup(final String usergroup, final String user)
	{
		final B2BUserGroupModel userGroupModel = this.getUserService().getUserGroupForUID(usergroup, B2BUserGroupModel.class);
		final B2BCustomerModel customer = getCustomerForUid(user);
		addMemberToUserGroup(userGroupModel, customer);
		this.getModelService().save(userGroupModel);
		return customer;
	}

	@Override
	public B2BCustomerModel removeMemberFromUserGroup(final String usergroup, final String user)
	{
		final B2BUserGroupModel userGroupModel = this.getUserService().getUserGroupForUID(usergroup, B2BUserGroupModel.class);
		final B2BCustomerModel customer = getCustomerForUid(user);
		removedMemberFromUserGroup(userGroupModel, customer);
		this.getModelService().save(userGroupModel);
		return customer;
	}
}
