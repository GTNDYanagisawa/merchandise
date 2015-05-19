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

import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;
import de.hybris.platform.commercefacades.customer.CustomerFacade;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


/**
 * Authentication handler invoked after a successful authentication of a Punch Out user.
 */
public class PunchOutAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{

	private RedirectStrategy redirectStrategy;
	private CustomerFacade customerFacade;
	private String targetUrl;

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException
	{
		getCustomerFacade().loginSuccess();

		String url = (String) request.getAttribute(B2bpunchoutaddonConstants.SEAMLESS_PAGE);
		if (url == null)
		{
			url = this.getTargetUrl();
		}

		getRedirectStrategy().sendRedirect(request, response, url);
	}

	public RedirectStrategy getRedirectStrategy()
	{
		return redirectStrategy;
	}

	public void setRedirectStrategy(final RedirectStrategy redirectStrategy)
	{
		this.redirectStrategy = redirectStrategy;
	}

	public CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	public String getTargetUrl()
	{
		return targetUrl;
	}

	public void setTargetUrl(final String targetUrl)
	{
		this.targetUrl = targetUrl;
	}

}
