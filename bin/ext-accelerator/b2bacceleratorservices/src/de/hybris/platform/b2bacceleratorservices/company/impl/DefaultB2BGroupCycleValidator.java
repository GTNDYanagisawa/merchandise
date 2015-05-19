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
package de.hybris.platform.b2bacceleratorservices.company.impl;

import de.hybris.platform.b2bacceleratorservices.company.B2BGroupCycleValidator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.HashSet;
import java.util.Set;


public class DefaultB2BGroupCycleValidator implements B2BGroupCycleValidator
{
	@Override
	public boolean validateGroups(final PrincipalGroupModel principalGroupModel, final PrincipalGroupModel groupMember)
	{
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(principalGroupModel.getGroups());
		groups.add(groupMember);
		principalGroupModel.setGroups(groups);
		final boolean cycleDetected = checkGroups(principalGroupModel);
		groups.remove(groupMember);
		return !cycleDetected;
	}

	@Override
	public boolean validateMembers(final PrincipalGroupModel principalGroupModel, final PrincipalModel member)
	{
		final Set<PrincipalModel> members = new HashSet<PrincipalModel>(principalGroupModel.getMembers());
		members.add(member);
		principalGroupModel.setMembers(members);
		final boolean cycleDetected = checkMembers(principalGroupModel);
		members.remove(member);
		return !cycleDetected;
	}

	protected boolean checkGroups(final PrincipalGroupModel principalGroup)
	{
		if (principalGroup.getGroups() != null)
		{
			for (final PrincipalGroupModel group : principalGroup.getGroups())
			{
				final Set<PrincipalGroupModel> allGroups = getAllGroups(group);
				if (allGroups.contains(principalGroup))
				{
					// cycle detected
					return true;
				}
			}
		}
		return false;
	}

	protected boolean checkMembers(final PrincipalGroupModel principalGroup)
	{
		if (principalGroup.getMembers() != null)
		{
			for (final PrincipalModel principal : principalGroup.getMembers())
			{
				if (principal instanceof PrincipalGroupModel)
				{
					final Set<PrincipalGroupModel> allGroups = getAllGroups(principalGroup);
					if (allGroups.contains(principal))
					{
						// cycle detected
						return true;
					}
				}
			}
		}
		return false;
	}

	protected Set<PrincipalGroupModel> getAllGroups(final PrincipalModel principal)
	{
		final Set<PrincipalGroupModel> allGroups = new HashSet<PrincipalGroupModel>();
		addGroups(allGroups, principal.getGroups());
		return allGroups;
	}

	protected void addGroups(final Set<PrincipalGroupModel> allGroups, final Set<PrincipalGroupModel> groups)
	{
		if (groups != null)
		{
			for (final PrincipalGroupModel group : groups)
			{
				if (!allGroups.contains(group))
				{
					allGroups.add(group);
					addGroups(allGroups, group.getGroups());
				}
			}
		}
	}
}
