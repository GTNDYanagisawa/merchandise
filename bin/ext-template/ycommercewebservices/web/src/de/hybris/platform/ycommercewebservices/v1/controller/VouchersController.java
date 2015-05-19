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

import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main Controller for Vouchers
 */
@Controller("vouchersControllerV1")
@RequestMapping(value = "/{baseSiteId}/vouchers")
public class VouchersController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(VouchersController.class);
	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;

	/**
	 * Web service for getting voucher information by voucher code.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/vouchers/abc-9PSW-EDH2-RXKA <br>
	 * This method requires authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 * 
	 * @param code
	 *           - voucher code - must be given as path variable.
	 * 
	 * @return {@link VoucherData} which will be marshaled to JSON or XML based on Accept-Header
	 * @throws {@link VoucherOperationException} if voucher cannot be found
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	public VoucherData getVoucherByCode(@PathVariable final String code) throws VoucherOperationException
	{
		return voucherFacade.getVoucher(code);
	}
}
