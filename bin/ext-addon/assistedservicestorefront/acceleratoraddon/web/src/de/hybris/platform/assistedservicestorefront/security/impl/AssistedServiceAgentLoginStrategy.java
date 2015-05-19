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
package de.hybris.platform.assistedservicestorefront.security.impl;

import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


/**
 * Implementation of {@link AutoLoginStrategy} for assisted service agent.
 */
public class AssistedServiceAgentLoginStrategy implements AutoLoginStrategy
{
	private static final Logger LOG = Logger.getLogger(AssistedServiceAgentLoginStrategy.class);

	private AuthenticationManager authenticationManager;
	private CustomerFacade customerFacade;
	private GUIDCookieStrategy guidCookieStrategy;

	@Override
	public void login(final String username, final String password, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		AssistedServiceAuthenticationToken token = new AssistedServiceAuthenticationToken(new AssistedServiceAgentPrincipal(
				username), password);
		token.setDetails(new WebAuthenticationDetails(request));
		try
		{
			final Authentication authentication = getAuthenticationManager().authenticate(token);
			// You may ask - what's the point in using custom token and principal?
			//             - the point is, that SecurityContextHolder stores ASM agent, but UserService stores emulated user.
			//               There is a check "SecurityUserCheckBeforeControllerHandler",
			//               it skips validation when principal isn't an instance of String.
			token = new AssistedServiceAuthenticationToken(new AssistedServiceAgentPrincipal(username), password,
					authentication.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(token);
			getCustomerFacade().loginSuccess();
			getGuidCookieStrategy().setCookie(request, response);
		}
		catch (final Exception e)
		{
			SecurityContextHolder.getContext().setAuthentication(null);
			LOG.error("Failure during autoLogin", e);
		}
	}

	protected AuthenticationManager getAuthenticationManager()
	{
		return authenticationManager;
	}

	@Required
	public void setAuthenticationManager(final AuthenticationManager authenticationManager)
	{
		this.authenticationManager = authenticationManager;
	}

	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	@Required
	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	@Required
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}
}
