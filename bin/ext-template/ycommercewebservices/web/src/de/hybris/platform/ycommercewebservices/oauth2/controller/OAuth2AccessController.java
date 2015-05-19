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
package de.hybris.platform.ycommercewebservices.oauth2.controller;

import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller("accessConfirmationController")
@SessionAttributes(types = AuthorizationRequest.class)
public class OAuth2AccessController
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(OAuth2AccessController.class);

	private ClientDetailsService clientDetailsService;

	@RequestMapping(value = "/oauth/confirm_access", method = RequestMethod.GET)
	public ModelAndView getAccessConfirmation(@ModelAttribute final AuthorizationRequest clientAuth) throws Exception
	{
		final ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
		final TreeMap<String, Object> model = new TreeMap<String, Object>();
		model.put("auth_request", clientAuth);
		model.put("client", client);
		return new ModelAndView("access_confirmation", model);
	}

	@Resource(name = "clientDetails")
	public void setClientDetailsService(final ClientDetailsService clientDetailsService)
	{
		this.clientDetailsService = clientDetailsService;
	}
}
