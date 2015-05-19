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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import java.util.List;


/**
 * Represents the UserStatusList object. <br>
 * 
 */
public interface UserStatusList extends Cloneable
{

	/**
	 * Adds an additional User Status to the User Status List.<br>
	 * 
	 * @param userStatus
	 *           User Status to be added to the User Stattus List
	 * @return true, if status was added to the list
	 */
	public boolean addUserStatus(UserStatus userStatus);

	/**
	 * Returns the List of User Statuses.<br>
	 * 
	 * @return List of User Statuses
	 */
	public List<UserStatus> getUserStatusList();

	/**
	 * Returns the List of Active User Statuses.<br>
	 * 
	 * @return List of Active User Statuses
	 */
	public UserStatusList getActiveUserStatusList();

	/**
	 * Returns the string representation of the User Status List.<br>
	 * 
	 * @return String representation of the User Status List
	 */
	@Override
	public String toString();

	/**
	 * Clones the Object. Because this class only contains immutable objects, there is no difference between a shallow
	 * and deep copy.
	 * 
	 * @return deep-copy of this object
	 */
	public Object clone();

}