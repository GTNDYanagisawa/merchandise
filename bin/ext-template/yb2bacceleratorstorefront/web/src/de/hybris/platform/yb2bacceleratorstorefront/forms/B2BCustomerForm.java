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
package de.hybris.platform.yb2bacceleratorstorefront.forms;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;


/**
 * Pojo for 'B2BCustomer' form.
 */
public class B2BCustomerForm extends UpdateProfileForm
{
	private boolean active;
	private String uid;
	private String parentB2BUnit;
	private Collection<String> roles = new ArrayList<String>();
	private Collection<CustomerData> approvers;
	private Collection<B2BUserGroupData> approverGroups;
	private Collection<B2BUserGroupData> permissionGroups;
	private Collection<B2BPermissionData> permissions;
	private String email;

	public String getUid()
	{
		return uid;
	}

	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(final boolean active)
	{
		this.active = active;
	}

	public Collection<CustomerData> getApprovers()
	{
		return approvers;
	}

	public void setApprovers(final Collection<CustomerData> approvers)
	{
		this.approvers = approvers;
	}

	public Collection<B2BUserGroupData> getApproverGroups()
	{
		return approverGroups;
	}

	public void setApproverGroups(final Collection<B2BUserGroupData> approverGroups)
	{
		this.approverGroups = approverGroups;
	}

	public Collection<B2BUserGroupData> getPermissionGroups()
	{
		return permissionGroups;
	}

	public void setPermissionGroups(final Collection<B2BUserGroupData> permissionGroups)
	{
		this.permissionGroups = permissionGroups;
	}

	public Collection<B2BPermissionData> getPermissions()
	{
		return permissions;
	}

	public void setPermissions(final Collection<B2BPermissionData> permissions)
	{
		this.permissions = permissions;
	}

	public Collection<String> getRoles()
	{
		return roles;
	}

	public void setRoles(final Collection<String> roles)
	{
		this.roles = roles;
	}

	public String getParentB2BUnit()
	{
		return parentB2BUnit;
	}

	public void setParentB2BUnit(final String parentB2BUnit)
	{
		this.parentB2BUnit = parentB2BUnit;
	}

	@NotNull(message = "{profile.email.invalid}")
	@Size(min = 1, max = 255, message = "{profile.email.invalid}")
	@Email(message = "{profile.email.invalid}")
	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}
}
