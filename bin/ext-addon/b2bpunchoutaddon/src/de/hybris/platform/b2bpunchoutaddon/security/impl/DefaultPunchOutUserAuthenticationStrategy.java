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

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2bpunchoutaddon.security.PunchOutAuthenticationProvider;
import de.hybris.platform.b2bpunchoutaddon.security.PunchOutUserAuthenticationStrategy;
import de.hybris.platform.core.model.user.UserModel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


public class DefaultPunchOutUserAuthenticationStrategy implements PunchOutUserAuthenticationStrategy
{
	private AuthenticationProvider authenticationProvider;
	private List<AuthenticationSuccessHandler> authenticationSuccessHandlers = Collections.emptyList();

	@Override
	public void authenticate(final UserModel user, final HttpServletRequest request, final HttpServletResponse response)
	{
		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUid(),
				PunchOutAuthenticationProvider.PUNCHOUT_CREDENTIALS);
		token.setDetails(new WebAuthenticationDetails(request));
		try
		{
			final Authentication authentication = getAuthenticationProvider().authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			for (final AuthenticationSuccessHandler successHandler : getAuthenticationSuccessHandlers())
			{
				successHandler.onAuthenticationSuccess(request, response, authentication);
			}
		}
		catch (final AuthenticationException | IOException | ServletException exc)
		{
			SecurityContextHolder.getContext().setAuthentication(null);
			throw new PunchOutException(PunchOutResponseCode.ERROR_CODE_AUTH_FAILED, "Could not authenticate user", exc);
		}
	}

	@Override
	public void logout()
	{
		SecurityContextHolder.clearContext();
	}

	protected AuthenticationProvider getAuthenticationProvider()
	{
		return authenticationProvider;
	}

	@Required
	public void setAuthenticationProvider(final AuthenticationProvider authenticationProvider)
	{
		this.authenticationProvider = authenticationProvider;
	}

	public List<AuthenticationSuccessHandler> getAuthenticationSuccessHandlers()
	{
		return authenticationSuccessHandlers;
	}

	@Required
	public void setAuthenticationSuccessHandlers(final List<AuthenticationSuccessHandler> authenticationSuccessHandlers)
	{
		this.authenticationSuccessHandlers = authenticationSuccessHandlers;
	}
}
