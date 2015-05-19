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
package de.hybris.platform.b2bacceleratorservices.company;


import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;


public interface B2BGroupCycleValidator
{
	/**
	 * Validate the groups given a group and group member
	 * 
	 * @param principalGroupModel
	 * @param groupMember
	 * @return Boolean result based on whether cyclic groups were detected or not
	 */
	boolean validateGroups(PrincipalGroupModel principalGroupModel, PrincipalGroupModel groupMember);

	/**
	 * Validate the members given a group and group member
	 * 
	 * @param principalGroupModel
	 * @param member
	 * @return Boolean result based on whether cyclic groups were detected or not
	 */
	boolean validateMembers(PrincipalGroupModel principalGroupModel, PrincipalModel member);
}
