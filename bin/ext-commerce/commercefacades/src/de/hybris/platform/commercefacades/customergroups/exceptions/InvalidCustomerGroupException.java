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
package de.hybris.platform.commercefacades.customergroups.exceptions;

import de.hybris.platform.core.model.user.UserGroupModel;


/**
 * @author krzysztof.kwiatosz
 * 
 */
public class InvalidCustomerGroupException extends RuntimeException
{

	private final UserGroupModel group;

	public InvalidCustomerGroupException(final UserGroupModel group)
	{
		super("UserGroup [" + group.getUid() + "] is not member of customergroup");
		this.group = group;
	}

	/**
	 * @return the group
	 */
	public UserGroupModel getGroup()
	{
		return group;
	}
}
