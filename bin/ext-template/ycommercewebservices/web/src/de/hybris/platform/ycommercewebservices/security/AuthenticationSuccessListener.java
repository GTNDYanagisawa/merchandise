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

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;



public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent>
{
	private static final Logger LOG = Logger.getLogger(AuthenticationSuccessListener.class);

	private BruteForceAttackCounter bruteForceAttackCounter;

	@Override
	public void onApplicationEvent(final AuthenticationSuccessEvent event)
	{
		final String username = event.getAuthentication().getName();
		bruteForceAttackCounter.resetUserCounter(username);
		LOG.debug("Authentication success for user : " + username);
	}

	public BruteForceAttackCounter getBruteForceAttackCounter()
	{
		return bruteForceAttackCounter;
	}

	public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter)
	{
		this.bruteForceAttackCounter = bruteForceAttackCounter;
	}
}