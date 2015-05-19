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
package de.hybris.platform.commercefacades.customergroups.impl;

import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.customergroups.exceptions.InvalidCustomerGroupException;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link CustomerGroupFacade}
 */
public class DefaultCustomerGroupFacade implements CustomerGroupFacade
{
	private String baseCustomerGroupId;

	private ModelService modelService;
	private UserService userService;
	private I18NService i18NService;

	private ConfigurablePopulator<UserGroupModel, UserGroupData, UserGroupOption> customerGroupMembersPopulator;
	private Converter<UserGroupModel, UserGroupData> userGroupConverter;


	@Override
	public void createCustomerGroup(final String uid, final String localizedName)
	{
		final UserGroupModel baseCustomerGroup = getBaseCustomerGroup();
		final UserGroupModel customerGroup = getModelService().create(UserGroupModel.class);
		customerGroup.setUid(uid);
		final Locale currentLocale = getI18NService().getCurrentLocale();
		customerGroup.setLocName(localizedName, currentLocale);

		final Set<PrincipalGroupModel> parentGroups = new HashSet<PrincipalGroupModel>();
		parentGroups.add(baseCustomerGroup);
		customerGroup.setGroups(parentGroups);

		getModelService().save(customerGroup);
		getModelService().refresh(baseCustomerGroup);
	}

	@Override
	public void addUserToCustomerGroup(final String customerGroupId, final String userId)
	{
		Assert.notNull(customerGroupId);
		Assert.notNull(userId);
		final UserModel user = getUserById(userId);
		final UserGroupModel group = getCustomerGroupById(customerGroupId);

		final Set<PrincipalModel> members = group.getMembers();
		final HashSet<PrincipalModel> modifiedMembers = new HashSet<PrincipalModel>(members);
		modifiedMembers.add(user);
		group.setMembers(modifiedMembers);
		getModelService().save(group);
		getModelService().refresh(user);
	}


	@Override
	public void removeUserFromCustomerGroup(final String customerGroupId, final String userId)
	{
		Assert.notNull(customerGroupId);
		Assert.notNull(userId);
		final UserModel user = getUserById(userId);
		final UserGroupModel group = getCustomerGroupById(customerGroupId);

		final Set<PrincipalModel> members = new HashSet<PrincipalModel>(group.getMembers());
		members.remove(user);
		group.setMembers(members);
		getModelService().save(group);
		getModelService().refresh(user);
	}

	@Override
	public List<UserGroupData> getCustomerGroupsForCurrentUser()
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		final UserGroupModel baseCustomerGroup = getBaseCustomerGroup();
		final Set<UserGroupModel> allUserGroups = getUserService().getAllUserGroupsForUser(currentUser);
		final List<UserGroupData> customerGroups = new ArrayList<UserGroupData>();

		for (final UserGroupModel userGroup : allUserGroups)
		{
			if (getUserService().isMemberOfGroup(userGroup, baseCustomerGroup) || baseCustomerGroup.equals(userGroup))
			{
				customerGroups.add(getUserGroupConverter().convert(userGroup));
			}
		}
		return customerGroups;
	}

	@Override
	public UserGroupData getCustomerGroup(final String customerGroupId, final Set<UserGroupOption> options)
	{
		Assert.notNull(customerGroupId);
		final UserGroupModel group = getCustomerGroupById(customerGroupId);
		final UserGroupData userGroupData = getUserGroupConverter().convert(group);
		getCustomerGroupMembersPopulator().populate(group, userGroupData, options);
		return userGroupData;
	}

	protected UserGroupModel getBaseCustomerGroup()
	{
		final UserGroupModel customerGroup = getUserService().getUserGroupForUID(getBaseCustomerGroupId());
		if (customerGroup == null)
		{
			throw new IllegalStateException("No group called " + getBaseCustomerGroupId() + " found in the system.");
		}
		return customerGroup;
	}

	protected UserGroupModel getCustomerGroupById(final String customerGroupId)
	{
		final UserGroupModel group = getUserService().getUserGroupForUID(customerGroupId);
		if (isCustomerGroup(group))
		{
			return group;
		}
		throw new InvalidCustomerGroupException(group);
	}

	/**
	 * @param group
	 * @return true if the <code>group</code> is a direct sub-group of configured baseCustomerGroup
	 */
	protected boolean isCustomerGroup(final UserGroupModel group)
	{
		final UserGroupModel customerGroup = getBaseCustomerGroup();
		return getUserService().isMemberOfGroup(group, customerGroup) || group.equals(customerGroup);
	}

	protected UserModel getUserById(final String userId)
	{
		return getUserService().getUserForUID(userId);
	}

	@Override
	public List<UserGroupData> getCustomerGroupsForUser(final String uid)
	{
		final UserModel user = getUserService().getUserForUID(uid);
		final Set<UserGroupModel> userGroups = getUserService().getAllUserGroupsForUser(user);
		final UserGroupModel baseCustomerGroup = getBaseCustomerGroup();
		final List<UserGroupData> customerGroups = new ArrayList<UserGroupData>();
		if (CollectionUtils.isNotEmpty(userGroups))
		{
			for (final UserGroupModel group : userGroups)
			{
				if (getUserService().isMemberOfGroup(group, baseCustomerGroup) || baseCustomerGroup.equals(group))
				{
					customerGroups.add(getUserGroupConverter().convert(group));
				}
			}
		}
		return customerGroups;
	}

	@Override
	public UserGroupDataList getAllCustomerGroups(final PageOption pageOption)
	{
		final UserGroupModel group = getBaseCustomerGroup();
		final List<UserGroupData> result = new ArrayList<UserGroupData>();
		result.add(getUserGroupConverter().convert(group));
		for (final PrincipalModel principal : group.getMembers())
		{
			if (principal instanceof UserGroupModel)
			{
				final UserGroupModel userGroup = (UserGroupModel) principal;
				result.add(getUserGroupConverter().convert(userGroup));
			}
		}
		return sortAndGetSubListDependsOnPageOption(result, pageOption);
	}

	protected static class UserGroupDataComparator implements Comparator<UserGroupData>
	{
		@Override
		public int compare(final UserGroupData object1, final UserGroupData object2)
		{
			return object1.getUid().compareTo(object2.getUid());
		}
	}

	protected UserGroupDataList sortAndGetSubListDependsOnPageOption(final List<UserGroupData> input, final PageOption pageOption)
	{
		final UserGroupDataList userGroupDataList = new UserGroupDataList();

		userGroupDataList.setPageSize(Integer.valueOf(pageOption.getPageSize()));
		userGroupDataList.setCurrentPage(Integer.valueOf(pageOption.getPageNumber()));
		userGroupDataList.setTotalNumber(Integer.valueOf(input.size()));
		final Integer numberOfPages = Integer.valueOf((int) (Math.ceil((double) input.size() / pageOption.getPageSize())));
		userGroupDataList.setNumberOfPages(numberOfPages);
		if (input.isEmpty())
		{
			userGroupDataList.setUserGroups(Collections.<UserGroupData> emptyList());
			return userGroupDataList;
		}

		Collections.sort(input, new UserGroupDataComparator());

		int fromIndexInclusive = 0;
		int toIndexExclusive = 0;
		final int maxIndexExclusive = input.size();

		fromIndexInclusive = pageOption.getPageStart();
		if (pageOption.getPageStart() > maxIndexExclusive)
		{
			userGroupDataList.setUserGroups(Collections.<UserGroupData> emptyList());
			return userGroupDataList;
		}

		toIndexExclusive = pageOption.getPageStart() + pageOption.getPageSize();
		if (toIndexExclusive > maxIndexExclusive)
		{
			toIndexExclusive = maxIndexExclusive;
		}

		userGroupDataList.setUserGroups(new ArrayList<UserGroupData>(input.subList(fromIndexInclusive, toIndexExclusive)));
		return userGroupDataList;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setUserGroupConverter(final Converter<UserGroupModel, UserGroupData> userGroupConverter)
	{
		this.userGroupConverter = userGroupConverter;
	}

	protected Converter<UserGroupModel, UserGroupData> getUserGroupConverter()
	{
		return userGroupConverter;
	}

	@Required
	public void setCustomerGroupMembersPopulator(
			final ConfigurablePopulator<UserGroupModel, UserGroupData, UserGroupOption> customerGroupMembersPopulator)
	{
		this.customerGroupMembersPopulator = customerGroupMembersPopulator;
	}

	protected ConfigurablePopulator<UserGroupModel, UserGroupData, UserGroupOption> getCustomerGroupMembersPopulator()
	{
		return customerGroupMembersPopulator;
	}

	@Required
	public void setBaseCustomerGroupId(final String value)
	{
		this.baseCustomerGroupId = value;
	}

	public String getBaseCustomerGroupId()
	{
		return baseCustomerGroupId;
	}
}
