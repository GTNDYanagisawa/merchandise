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

import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commercewebservicescommons.dto.voucher.VoucherWsDTO;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main Controller for Vouchers
 *
 * @pathparam code Voucher identifier (code)
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/vouchers")
public class VouchersController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(VouchersController.class);
	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;

	/**
	 * Returns details of a single voucher specified by a voucher code.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Voucher details
	 * @throws VoucherOperationException
	 *            When voucher with given code doesn't exist
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	public VoucherWsDTO getVoucherByCode(@PathVariable final String code, @RequestParam(defaultValue = "BASIC") final String fields)
			throws VoucherOperationException
	{
		final VoucherWsDTO dto = dataMapper.map(voucherFacade.getVoucher(code), VoucherWsDTO.class, fields);
		return dto;
	}
}
