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
package de.hybris.platform.yb2bacceleratorstorefront.security.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.yb2bacceleratorstorefront.security.B2BUserGroupProvider;

import java.util.Set;


/**
 * Default provider for B2b User group.
 */
public class DefaultB2BUserGroupProvider implements B2BUserGroupProvider
{
	private UserService userService;
	private Set<String> authorizedGroups;
	private Set<String> authorizedGroupsToCheckOut;
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorstorefront.security.B2BUserGroupProvider#getAllowedUserGroup()
	 */
	@Override
	public Set<String> getAllowedUserGroup()
	{
		return authorizedGroups;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorstorefront.security.B2BUserGroupProvider#isUserAuthorized()
	 */
	@Override
	public boolean isCurrentUserAuthorized()
	{
		final UserModel user = getUserService().getCurrentUser();
		return checkIfUserAuthorized(user);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.yb2bacceleratorstorefront.security.B2BUserGroupProvider#isUserAuthorized(de.hybris.platform
	 * .core.model.user.UserModel)
	 */
	@Override
	public boolean isUserAuthorized(final UserModel user)
	{
		return checkIfUserAuthorized(user);
	}

	@Override
	public boolean isUserAuthorized(final String loginName)
	{
		final UserModel user = getUserService().getUserForUID(loginName);
		return checkIfUserAuthorized(user);
	}

	@Override
	public boolean isUserEnabled(final String userId)
	{
		final B2BCustomerModel user = getB2BCustomerService().getUserForUID(userId);
		return user.getActive().booleanValue();
	}

	protected boolean checkIfUserAuthorized(final UserModel user)
	{
		boolean isAuthorized = false;
		for (final PrincipalGroupModel group : user.getGroups())
		{
			if (authorizedGroups.contains(group.getUid()))
			{
				isAuthorized = true;
				break;
			}
		}
		return isAuthorized;
	}

	@Override
	public boolean isCurrentUserAuthorizedToCheckOut()
	{
		final UserModel user = getUserService().getCurrentUser();
		return checkIfUserAuthorizedToCheckOut(user);
	}

	@Override
	public boolean isUserAuthorizedToCheckOut(final UserModel user)
	{
		return checkIfUserAuthorizedToCheckOut(user);
	}

	@Override
	public boolean isUserAuthorizedToCheckOut(final String loginName)
	{
		final UserModel user = getUserService().getUserForUID(loginName);
		return checkIfUserAuthorizedToCheckOut(user);
	}

	protected boolean checkIfUserAuthorizedToCheckOut(final UserModel user)
	{
		boolean isAuthorized = false;
		for (final PrincipalGroupModel group : user.getGroups())
		{
			if (getAuthorizedGroupsToCheckOut().contains(group.getUid()))
			{
				isAuthorized = true;
				break;
			}
		}
		return isAuthorized;
	}



	/**
	 * @return the authorizedGroupsToCheckOut
	 */
	public Set<String> getAuthorizedGroupsToCheckOut()
	{
		return authorizedGroupsToCheckOut;
	}


	/**
	 * @param authorizedGroupsToCheckOut
	 *           the authorizedGroupsToCheckOut to set
	 */
	public void setAuthorizedGroupsToCheckOut(final Set<String> authorizedGroupsToCheckOut)
	{
		this.authorizedGroupsToCheckOut = authorizedGroupsToCheckOut;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the authorizedGroups
	 */
	protected Set<String> getAuthorizedGroups()
	{
		return authorizedGroups;
	}

	/**
	 * @param authorizedGroups
	 *           the authorizedGroups to set
	 */
	public void setAuthorizedGroups(final Set<String> authorizedGroups)
	{
		this.authorizedGroups = authorizedGroups;
	}

	/**
	 * @return the b2BCustomerService
	 */
	public B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2BCustomerService()
	{
		return b2BCustomerService;
	}

	/**
	 * @param b2bCustomerService
	 *           the b2BCustomerService to set
	 */
	public void setB2BCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService)
	{
		b2BCustomerService = b2bCustomerService;
	}
}
