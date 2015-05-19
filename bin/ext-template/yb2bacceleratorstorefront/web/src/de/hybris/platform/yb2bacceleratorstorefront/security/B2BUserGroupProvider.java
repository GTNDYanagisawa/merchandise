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
package de.hybris.platform.yb2bacceleratorstorefront.security;

import de.hybris.platform.core.model.user.UserModel;

import java.util.Set;


/**
 * Provider for B2b User group.
 */
public interface B2BUserGroupProvider
{
	/**
	 * @return authorized user groups
	 */
	Set<String> getAllowedUserGroup();

	/**
	 * Checks if current user belongs to at least one group that is authorized.
	 * 
	 * @return true if current user is authorized.
	 */
	boolean isCurrentUserAuthorized();

	/**
	 * Checks if given user belongs to at least one group that is authorized.
	 * 
	 * @param user
	 *           to verify
	 * @return true if current user is authorized.
	 */
	boolean isUserAuthorized(UserModel user);

	/**
	 * Checks if given user belongs to at least one group that is authorized.
	 * 
	 * @param loginName
	 *           (the user UID) to verify
	 * @return true if current user is authorized.
	 */
	boolean isUserAuthorized(String loginName);

	/**
	 * Checks if given user belongs to at least one group that is authorized to checkout.
	 * 
	 * @param loginName
	 *           (the user UID) to verify
	 * @return true if current user is authorized.
	 */
	boolean isUserAuthorizedToCheckOut(String loginName);

	/**
	 * Checks if given user belongs to at least one group that is authorized to checkout.
	 * 
	 * @param user
	 *           to verify
	 * @return true if current user is authorized.
	 */
	boolean isUserAuthorizedToCheckOut(UserModel user);

	/**
	 * Checks if current user belongs to at least one group that is authorized to checkout.
	 * 
	 * @return true if current user is authorized.
	 */
	boolean isCurrentUserAuthorizedToCheckOut();

	/**
	 * @param userId
	 *           the user UID
	 * @return true if user is active.
	 */
	boolean isUserEnabled(String userId);
}
