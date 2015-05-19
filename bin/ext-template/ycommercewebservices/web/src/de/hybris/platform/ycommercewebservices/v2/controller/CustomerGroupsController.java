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
 */
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.customergroups.exceptions.InvalidCustomerGroupException;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.commercewebservicescommons.dto.user.MemberListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.PrincipalWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller for {@link de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade}
 * 
 * @pathparam groupId Group identifier
 * @pathparam userId User identifier
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/customergroups")
public class CustomerGroupsController extends BaseController
{

	private static final Logger LOG = Logger.getLogger(CustomerGroupsController.class);

	private static String USER_GROUP_OPTIONS = "";
	private static final Set<UserGroupOption> OPTIONS;
	static
	{
		for (final UserGroupOption option : UserGroupOption.values())
		{
			USER_GROUP_OPTIONS = USER_GROUP_OPTIONS + option.toString() + " ";
		}
		USER_GROUP_OPTIONS = USER_GROUP_OPTIONS.trim().replace(" ", YcommercewebservicesConstants.OPTIONS_SEPARATOR);
		OPTIONS = getOptions(USER_GROUP_OPTIONS);
	}

	@Resource(name = "customerGroupFacade")
	CustomerGroupFacade customerGroupFacade;
	@Resource(name = "principalListDTOValidator")
	Validator principalListDTOValidator;
	@Resource(name = "userGroupDTOValidator")
	Validator userGroupDTOValidator;
	@Resource
	UserService userService;

	/**
	 * Creates new customer group - direct subgroup of customergroup. Requires CUSTOMERMANAGERGROUP role.
	 * 
	 * @formparam groupId Id of new customer group.
	 * @formparam localizedName Name in current locale.
	 */
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	public void createNewCustomerGroup(@RequestParam final String groupId,
			@RequestParam(required = false) final String localizedName)
	{
		customerGroupFacade.createCustomerGroup(groupId, localizedName);
	}

	/**
	 * Creates new customer group - direct subgroup of customergroup. Requires CUSTOMERMANAGERGROUP role.
	 * 
	 * @param userGroup
	 *           user group object with id and name
	 * @bodyparams uid,name,members.uid
	 */
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	public void createNewCustomerGroup(@RequestBody final UserGroupWsDTO userGroup)
	{
		validate(userGroup, "userGroup", userGroupDTOValidator);

		customerGroupFacade.createCustomerGroup(userGroup.getUid(), userGroup.getName());
		if (userGroup.getMembers() != null)
		{
			for (final PrincipalWsDTO member : userGroup.getMembers())
			{
				customerGroupFacade.addUserToCustomerGroup(userGroup.getUid(), member.getUid());
			}
		}
	}

	/**
	 * Assign users to a customer group.
	 * 
	 * @formparam members List of users ids to assign to customer group. List should be in form:
	 *            members=uid1&members=uid2...
	 */
	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PATCH)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	public void assignUserToCustomerGroup(@PathVariable final String groupId,
			@RequestParam(value = "members") final List<String> members)
	{
		checkMembers(members, "You cannot add user to group :" + groupId + ".");
		for (final String member : members)
		{
			customerGroupFacade.addUserToCustomerGroup(groupId, member);
		}
	}

	/**
	 * Assign users to a customer group.
	 * 
	 * @param members
	 *           List of users to assign to customer group.
	 * @bodyparams members.uid
	 */
	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	public void assignUserToCustomerGroup(@PathVariable final String groupId, @RequestBody final MemberListWsDTO members)
	{
		validate(members.getMembers(), "members", principalListDTOValidator);

		for (final PrincipalWsDTO member : members.getMembers())
		{
			customerGroupFacade.addUserToCustomerGroup(groupId, member.getUid());
		}
	}

	/**
	 * Set members for user group. Members will be replaced with given list.
	 * 
	 * @formparam members List of users id to set for customer group.. List should be in form:
	 *            members=uid1&members=uid2...
	 */
	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PUT)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	public void setUserListForCustomerGroup(@PathVariable final String groupId,
			@RequestParam(required = false, value = "members") final List<String> members)
	{
		setUserListForCustomerGroupInternal(groupId, members);
	}

	private void setUserListForCustomerGroupInternal(final String groupId, List<String> members)
	{
		if (members == null)
		{
			members = new ArrayList<>();
		}

		final UserGroupData userGroup = customerGroupFacade.getCustomerGroup(groupId,
				Collections.singleton(UserGroupOption.MEMBERS));

		final HashSet<String> oldMembers = new HashSet();
		for (final PrincipalData member : userGroup.getMembers())
		{
			oldMembers.add(member.getUid());
		}

		final HashSet<String> newMembers = new HashSet();
		for (final String member : members)
		{
			newMembers.add(member);
		}

		final Collection<String> membersToRemove = CollectionUtils.subtract(oldMembers, newMembers);
		checkMembers(membersToRemove, "You cannot remove user from group :" + groupId + ".");
		final Collection<String> membersToAdd = CollectionUtils.subtract(newMembers, oldMembers);
		checkMembers(membersToAdd, "You cannot add user to group :" + groupId + ".");

		for (final String memberToRemove : membersToRemove)
		{
			customerGroupFacade.removeUserFromCustomerGroup(groupId, memberToRemove);
		}

		for (final String memberToAdd : membersToAdd)
		{
			customerGroupFacade.addUserToCustomerGroup(groupId, memberToAdd);
		}
	}

	private void checkMembers(final Collection<String> membersList, final String errorMessagePrefix)
	{
		for (final String member : membersList)
		{
			if (!userService.isUserExisting(member))
			{
				throw new RequestParameterException(errorMessagePrefix + " User '" + member
						+ "' doesn't exist or you have no privileges");
			}
		}
	}

	/**
	 * Set members for user group. Members will be replaced with given list.
	 * 
	 * @param members
	 *           List of users to set for customer group.
	 * @bodyparams members.uid
	 */
	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	public void setUserListForCustomerGroup(@PathVariable final String groupId, @RequestBody final MemberListWsDTO members)
	{
		final List<String> membersIds = new ArrayList();

		if (members.getMembers() != null)
		{
			if (!members.getMembers().isEmpty())
			{
				validate(members.getMembers(), "members", principalListDTOValidator);
			}

			for (final PrincipalWsDTO member : members.getMembers())
			{
				membersIds.add(member.getUid());
			}
		}

		setUserListForCustomerGroupInternal(groupId, membersIds);
	}

	/**
	 * Remove user from customer group.
	 */
	@RequestMapping(value = "/{groupId}/members/{userId:.*}", method = RequestMethod.DELETE)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	public void removeUsersFromCustomerGroup(@PathVariable final String groupId,
			@PathVariable(value = "userId") final String userId)
	{
		checkMembers(Collections.singleton(userId), "You cannot remove user from group :" + groupId + ".");
		customerGroupFacade.removeUserFromCustomerGroup(groupId, userId);
	}

	/**
	 * Returns all customer groups that are direct subgroups of customergroup. Requires CUSTOMERMANAGERGROUP.
	 * 
	 * @queryparam currentPage Current page number (starts with 0)
	 * @queryparam pageSize Number of customer group returned in one page
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return All customer groups that are direct subgroups of customergroup
	 */
	@RequestMapping(method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public UserGroupListWsDTO getAllCustomerGroups(
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(defaultValue = "BASIC") final String fields)
	{
		final PageOption pageOption = PageOption.createForPageNumberAndPageSize(currentPage, pageSize);
		final UserGroupDataList userGroupDataList = customerGroupFacade.getAllCustomerGroups(pageOption);
		final UserGroupListWsDTO dto = dataMapper.map(userGroupDataList, UserGroupListWsDTO.class, fields);
		return dto;
	}

	/**
	 * Returns customer group with specified groupId. Requires CUSTOMERMANAGERGROUP role.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Customer group with specified groupId
	 * @throws InvalidCustomerGroupException
	 *            When requested group is not sub group of customergroup
	 */
	@RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public UserGroupWsDTO getCustomerGroup(@PathVariable final String groupId,
			@RequestParam(defaultValue = "BASIC") final String fields)
	{
		final UserGroupData userGroupData = customerGroupFacade.getCustomerGroup(groupId, OPTIONS);
		final UserGroupWsDTO dto = dataMapper.map(userGroupData, UserGroupWsDTO.class, fields);
		return dto;
	}

	private static Set<UserGroupOption> getOptions(final String options)
	{
		final String optionsStrings[] = options.split(",");

		final Set<UserGroupOption> opts = new HashSet<>();
		for (final String option : optionsStrings)
		{
			opts.add(UserGroupOption.valueOf(option));
		}
		return opts;
	}

}
