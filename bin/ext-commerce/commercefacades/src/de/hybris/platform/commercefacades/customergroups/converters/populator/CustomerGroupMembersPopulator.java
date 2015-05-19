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
package de.hybris.platform.commercefacades.customergroups.converters.populator;

import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * @author krzysztof.kwiatosz
 * 
 */
public class CustomerGroupMembersPopulator implements ConfigurablePopulator<UserGroupModel, UserGroupData, UserGroupOption>
{

	private Converter<UserGroupModel, UserGroupData> userGroupConverter;
	private Converter<PrincipalModel, PrincipalData> principalConverter;


	@Override
	public void populate(final UserGroupModel source, final UserGroupData target, final Collection<UserGroupOption> options)
			throws ConversionException
	{
		if (source.getMembers() != null)
		{
			final List<PrincipalData> members = new ArrayList<PrincipalData>();
			final List<UserGroupData> subgroups = new ArrayList<UserGroupData>();
			for (final PrincipalModel member : source.getMembers())
			{
				if (member instanceof UserGroupModel)
				{
					subgroups.add(userGroupConverter.convert((UserGroupModel) member));
				}
				else
				{
					members.add(principalConverter.convert(member));
				}
			}
			if (options.contains(UserGroupOption.MEMBERS))
			{
				target.setMembers(members);
			}
			if (options.contains(UserGroupOption.SUBGROUPS))
			{
				target.setSubGroups(subgroups);
			}
			target.setMembersCount(Integer.valueOf(members.size()));
		}
	}


	@Required
	public void setUserGroupConverter(final Converter<UserGroupModel, UserGroupData> userGroupConverter)
	{
		this.userGroupConverter = userGroupConverter;
	}


	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}
}
