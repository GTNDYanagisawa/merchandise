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
package de.hybris.platform.b2b.occ.v2.controllers;

import de.hybris.platform.b2b.occ.security.SecuredAccessConstants;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.commercewebservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main Controller for Users
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users")
@ApiVersion("v2")
public class B2BUsersController
{
	private static final Logger LOG = Logger.getLogger(B2BUsersController.class);
	@Resource(name = "b2bCustomerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	/**
	 * Returns a B2B user profile.
	 * 
	 * @return user detail information
	 */
	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "b2bocc.B2BUsersController.getUser.priority")
	@ResponseBody
	public UserWsDTO getUser(@RequestParam(defaultValue = "DEFAULT") final String fields)
	{
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final UserWsDTO dto = dataMapper.map(customerData, UserWsDTO.class, fields);
		return dto;
	}
}
