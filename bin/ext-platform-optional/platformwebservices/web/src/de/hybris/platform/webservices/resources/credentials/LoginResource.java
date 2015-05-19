/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.resources.credentials;

import java.security.Principal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import de.hybris.platform.webservices.AbstractResource;


/**
 * Dummy request, just to do the validation of the provided credentials.
 */
@Path("/login")
public class LoginResource extends AbstractResource<Principal>
{
	/**
	 * If credentials are not correct the response contains the appropriate http error code, otherwise contains status
	 * 200 OK.
	 * 
	 * @return {@link Response}
	 */
	@GET
	public Response getLoginResponse()
	{
		final Principal user = getResourceValue();

		if (user == null)
		{
			getResponse().status(Response.Status.UNAUTHORIZED);
		}

		return getResponse().build();
	}

	/**
	 * Returns logged user or null
	 */
	@Override
	protected Principal readResource(final String resourceId)
	{
		return securityCtx.getUserPrincipal();
	}

}
