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
package de.hybris.platform.b2bpunchoutaddon.security;

import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.Serializable;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class PunchOutAuthenticationProvider extends PunchOutCoreAuthenticationProvider implements Serializable
{
	public static final String PUNCHOUT_CREDENTIALS = "0f8fad5b-d9cb-469f-a165-70867728950e";

	private UserService userService;
	private ModelService modelService;
	private CartService cartService;

	@SuppressWarnings("finally")
	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException
	{
		if (authentication.getCredentials().equals(PUNCHOUT_CREDENTIALS))
		{
			final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

			// check if the user of the cart matches the current user and if the
			// user is not anonymous. If otherwise, remove delete the session cart as it might
			// be stolen / from another user
			final String sessionCartUserId = getCartService().getSessionCart().getUser().getUid();

			if (!username.equals(sessionCartUserId) && !sessionCartUserId.equals(userService.getAnonymousUser().getUid()))
			{
				getCartService().setSessionCart(null);
			}

		}
		else
		{
			throw new BadCredentialsException(messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		return super.authenticate(authentication);
	}



	public UserService getUserService()
	{
		return userService;
	}


	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}


	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	public CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

}
