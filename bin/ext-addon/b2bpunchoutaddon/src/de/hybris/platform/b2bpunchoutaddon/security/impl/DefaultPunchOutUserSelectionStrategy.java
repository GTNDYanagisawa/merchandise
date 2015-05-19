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
package de.hybris.platform.b2bpunchoutaddon.security.impl;

import de.hybris.platform.b2b.punchout.PunchOutContact;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2bpunchoutaddon.security.PunchOutUserSelectionStrategy;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Default strategy implementation for {@link PunchOutUserSelectionStrategy} that makes sure we create a new user if
 * none exists for the provided {@link PunchOutContact}.
 */
public class DefaultPunchOutUserSelectionStrategy implements PunchOutUserSelectionStrategy
{
	private UserService userService;
	private ModelService modelService;
	private CustomerAccountService customerAccountService;

	private Collection<String> acceptedUserTypes = Collections.singleton("B2BCustomer");

	/**
	 * Tries to find a user with the given {@code userId}. If the user does not exist, creates a new one.
	 */
	@Override
	public UserModel select(final String userId, final Collection<UserGroupModel> userGroups, final PunchOutSession punchoutSession)
	{
		final UserModel user = findUser(userId);
		validateUser(user, userGroups);
		return user;
	}

	private UserModel findUser(final String userId)
	{
		try
		{
			return getUserService().getUserForUID(userId);
		}
		catch (final UnknownIdentifierException exc)
		{
			return null;
		}
	}

	/**
	 * Validates that the given user is part of all {@code groups} and is at least one of {@link #getAcceptedUserTypes()}
	 * .
	 * 
	 * @param user
	 *           The user to validate.
	 * @param groups
	 *           The groups that the user belongs to.
	 */
	protected void validateUser(final UserModel user, final Collection<UserGroupModel> groups)
	{
		if ((user == null) || (!user.getAllGroups().containsAll(groups))
				&& (getAcceptedUserTypes().contains(getModelService().getModelType(user))))
		{
			throw new PunchOutException(PunchOutResponseCode.ERROR_CODE_AUTH_FAILED, "User: " + user.getUid() + " is not authorized");
		}
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public Collection<String> getAcceptedUserTypes()
	{
		return acceptedUserTypes;
	}

	public void setAcceptedUserTypes(final Collection<String> acceptedUserTypes)
	{
		this.acceptedUserTypes = acceptedUserTypes;
	}

	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

}
