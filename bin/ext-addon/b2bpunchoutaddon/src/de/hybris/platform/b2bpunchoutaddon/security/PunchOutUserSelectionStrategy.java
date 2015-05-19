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

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;


/**
 * Punch Out user selection strategy allowing for
 */
public interface PunchOutUserSelectionStrategy
{

	/**
	 * Selects a user by the given {@code userId} and {@code punchoutSession}.
	 * 
	 * @param userId
	 *           The user's ID.
	 * @param userGroups
	 *           The user groups related to the given user (not mandatory for existent users).
	 * @param punchoutSession
	 *           The Punch Out session.
	 * @return The {@link UserModel} for the given {@code userId}.
	 */
	UserModel select(String userId, Collection<UserGroupModel> userGroups, PunchOutSession punchoutSession);

}
