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
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2boccaddon.dto.order.B2BPaymentTypeListWsDTO;
import de.hybris.platform.b2boccaddon.dto.order.B2BPaymentTypeWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.yb2bacceleratorfacades.flow.B2BCheckoutFlowFacade;
import de.hybris.platform.ycommercewebservices.v2.controller.BaseController;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@ApiVersion("v2")
public class B2BMiscsController extends BaseController
{
	@Resource(name = "b2bCheckoutFlowFacade")
	private B2BCheckoutFlowFacade checkoutFlowFacade;

	/**
	 * Gets a list of available payment types in the B2B checkout process.
	 * 
	 * @return a representation of {@link de.hybris.platform.b2boccaddon.dto.order.B2BPaymentTypeListWsDTO} which
	 *         contains a list of B2B Payment Types
	 */
	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@RequestMapping(value = "/{baseSiteId}/paymenttypes", method = RequestMethod.GET)
	@ResponseBody
	public B2BPaymentTypeListWsDTO getPaymentTypes(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<? extends B2BPaymentTypeData> paymentTypeDatas = checkoutFlowFacade.getPaymentTypesForCheckoutSummary();

		final B2BPaymentTypeListWsDTO dto = new B2BPaymentTypeListWsDTO();
		dto.setPaymentTypes(dataMapper.mapAsList(paymentTypeDatas, B2BPaymentTypeWsDTO.class, fields));

		return dto;
	}
}
