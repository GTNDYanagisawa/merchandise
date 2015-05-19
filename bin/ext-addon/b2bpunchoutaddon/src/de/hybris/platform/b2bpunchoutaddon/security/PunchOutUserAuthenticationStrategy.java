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

import de.hybris.platform.core.model.user.UserModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Authentication strategy for Punch Out users.
 */
public interface PunchOutUserAuthenticationStrategy
{

	/**
	 * Authenticates a user into the system.
	 * 
	 * @param user
	 *           the user
	 * @param request
	 *           the HTTP request
	 * @param response
	 *           the HTTP response
	 */
	void authenticate(UserModel user, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Logs out a user from the system.
	 */
	void logout();

}
