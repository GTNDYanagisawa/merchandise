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
package de.hybris.platform.ycommercewebservices.v1.controller;

import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Controller for {@link CustomerGroupFacade}
 */
@Controller("customerGroupsControllerV1")
@RequestMapping(value = "/{baseSiteId}/customergroups")
public class CustomerGroupsController extends BaseController
{

	private static final Logger LOG = Logger.getLogger(CustomerGroupsController.class);
	@Resource(name = "customerGroupFacade")
	CustomerGroupFacade customerGroupFacade;

	/**
	 * Web service for creating new customer group.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customergroups<br>
	 * Method requires authentication and is restricted <code>HTTPS</code> channel.<br>
	 * Method type : <code>POST</code>. <br>
	 * Method response with 201 Created.
	 * 
	 */
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	public void createNewCustomerGroup(@RequestParam final String uid, @RequestParam(required = false) final String localizedName)
	{
		customerGroupFacade.createCustomerGroup(uid, localizedName);
	}

	/**
	 * Web service for assigning user to a customer group.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customergroups/{groupId}/members<br>
	 * Method requires authentication and is restricted <code>HTTPS</code> channel.<br>
	 * Method type : <code>PUT</code>. <br>
	 * Members ids must be given as put body
	 */
	@RequestMapping(value = "/{uid}/members", method = RequestMethod.PUT)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	public void assignUserToCustomerGroup(@PathVariable(value = "uid") final String customerGroupUid,
			@RequestParam(value = "member") final List<String> members)
	{
		for (final String member : members)
		{
			customerGroupFacade.addUserToCustomerGroup(customerGroupUid, member);
		}

	}

	@RequestMapping(value = "/{uid}/members/{userId:.*}", method = RequestMethod.DELETE)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	public void removeUsersFromCustomerGroup(@PathVariable(value = "uid") final String customerGroupUid,
			@PathVariable(value = "userId") final String userId)
	{
		customerGroupFacade.removeUserFromCustomerGroup(customerGroupUid, userId);
	}

	/**
	 * Web service for getting all customer groups.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customergroups<br>
	 * Method requires authentication and is restricted <code>HTTPS</code> channel.<br>
	 * Method type : <code>GET</code>. <br>
	 * Method handles optional paging parameters:
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @return {@link UserGroupDataList}
	 */
	@RequestMapping(method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public UserGroupDataList getAllCustomerGroups(@RequestParam(required = false, defaultValue = "0") final int currentPage,
			@RequestParam(required = false, defaultValue = "2147483647") final int pageSize)
	{
		final PageOption pageOption = PageOption.createForPageNumberAndPageSize(currentPage, pageSize);
		return customerGroupFacade.getAllCustomerGroups(pageOption);
	}

	@RequestMapping(value = "/{uid}", method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public UserGroupData getCustomerGroup(@PathVariable(value = "uid") final String uid,
			@RequestParam(required = false, defaultValue = "BASIC") final String options)
	{
		return customerGroupFacade.getCustomerGroup(uid, getOptions(options));
	}

	protected Set<UserGroupOption> getOptions(final String options)
	{
		final String optionsStrings[] = options.split(",");

		final Set<UserGroupOption> opts = new HashSet<UserGroupOption>();
		for (final String option : optionsStrings)
		{
			opts.add(UserGroupOption.valueOf(option));
		}
		return opts;
	}

}
