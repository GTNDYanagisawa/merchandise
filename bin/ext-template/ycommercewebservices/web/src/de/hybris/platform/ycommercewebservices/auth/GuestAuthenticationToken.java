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
 */

/**
 * 
 */
package de.hybris.platform.ycommercewebservices.auth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;


/**
 * 
 */
public class GuestAuthenticationToken extends AbstractAuthenticationToken
{
	private final String email;

	/**
	 * @param authorities
	 */
	public GuestAuthenticationToken(final String email, final Collection<? extends GrantedAuthority> authorities)
	{
		super(authorities);
		this.email = email;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials()
	{
		return null;
	}

	@Override
	public Object getPrincipal()
	{
		return email;
	}
}
