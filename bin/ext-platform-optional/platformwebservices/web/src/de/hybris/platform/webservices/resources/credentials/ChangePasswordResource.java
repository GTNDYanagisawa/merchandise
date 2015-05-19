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

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.persistence.security.SaltedMD5PasswordEncoder;
import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.dto.credentials.PasswordDTO;


/**
 * Sets a new password for the user.
 */
@Path("/changepassword")
public class ChangePasswordResource extends AbstractResource<Principal>
{
	/**
	 * Sets a new password for authentificated user.
	 * 
	 * @param dto
	 *           - PasswordDTO
	 * @return - http status OK or BAD_REQUEST
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putNewPassword(final PasswordDTO dto)
	{
		final Principal userAuth = getResourceValue();
		if (userAuth == null)
		{
			getResponse().status(Response.Status.UNAUTHORIZED);
		}
		else
		{
			User user = null;
			UserModel userModel = null;
			final String newRawPassword = dto.getPassword();
			String newEncPassword = null;
			if (newRawPassword == null || newRawPassword.compareTo("") == 0)
			{
				getResponse().status(Response.Status.BAD_REQUEST);
				getResponse().entity("Use <newpassword>NO EMPTY</newpassword> tags!");
			}
			else
			{
				userModel = serviceLocator.getUserService().getUserForUID(userAuth.getName());
				final SaltedMD5PasswordEncoder encoder = new SaltedMD5PasswordEncoder();
				newEncPassword = encoder.encode(userModel.getUid(), newRawPassword);
				user = serviceLocator.getModelService().getSource(userModel);
				user.setEncodedPassword(newEncPassword, "MD5");
			}
		}
		return getResponse().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.sandbox.AbstractResource#readResource()
	 */
	@Override
	protected Principal readResource(final String resourceId)
	{
		return securityCtx.getUserPrincipal();
	}
}
