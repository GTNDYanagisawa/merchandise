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
package de.hybris.platform.ycommercewebservices.security;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;


public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent>
{
	private static final Logger LOG = Logger.getLogger(AuthenticationFailureListener.class);

	private BruteForceAttackCounter bruteForceAttackCounter;
	private ModelService modelService;
	private UserService userService;

	@Override
	public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent ev)
	{
		final String userName = ev.getAuthentication().getName();
		LOG.debug("Authentication failure for user : " + userName);

		getBruteForceAttackCounter().registerLoginFailure(userName);
		if (getBruteForceAttackCounter().isAttack(userName))
		{
			disableUser(userName);
		}
	}

	/**
	 * Disable user account
	 * 
	 * @param user
	 *           user identifier
	 */
	protected void disableUser(final String user)
	{
		try
		{
			final UserModel userModel = getUserService().getUserForUID(StringUtils.lowerCase(user));
			if (!userModel.isLoginDisabled())
			{
				userModel.setLoginDisabled(true);
				getModelService().save(userModel);
				LOG.warn("Account for user '" + user + "' was disabled because of too many authentication failures");
			}
			bruteForceAttackCounter.resetUserCounter(user);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug("Brute force attack attempt for non existing user name " + user);
		}
	}

	public BruteForceAttackCounter getBruteForceAttackCounter()
	{
		return bruteForceAttackCounter;
	}

	@Required
	public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter)
	{
		this.bruteForceAttackCounter = bruteForceAttackCounter;
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

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}