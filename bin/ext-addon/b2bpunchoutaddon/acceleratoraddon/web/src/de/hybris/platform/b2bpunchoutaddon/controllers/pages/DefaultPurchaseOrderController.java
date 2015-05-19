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
package de.hybris.platform.b2bpunchoutaddon.controllers.pages;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2bpunchoutaddon.services.PunchOutUserAuthenticationService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.cxml.CXML;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Purchase order controller default implementation.
 */
@Component
public class DefaultPurchaseOrderController implements PunchOutController
{
	private static final Logger LOG = Logger.getLogger(DefaultPurchaseOrderController.class);

	@Resource
	private PunchOutService punchOutService;

	@Resource
	private CartService cartService;

	@Resource
	private PunchOutUserAuthenticationService orderPunchOutUserAuthenticationService;

	@Resource
	private PunchOutSessionService punchoutSessionService;

	/**
	 * Handles a Order Request from the Punch Out Provider.
	 * 
	 * @param requestBody
	 *           The cXML containing the order to be processed.
	 * @param request
	 *           The servlet request.
	 * @param response
	 *           The servlet response.
	 * @return A cXML with the Order Response, containing the status of the processing of the order.
	 */
	@RequestMapping(value = "/punchout/cxml/order", method = RequestMethod.POST)
	@ResponseBody
	public CXML handlePunchOutPurchaseOrderRequest(@RequestBody final CXML requestBody, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		CXML cxml = null;
		final String identity = punchOutService.retrieveIdentity(requestBody);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Order Identity:" + identity);
		}

		// if user exists
		if (identity != null)
		{
			orderPunchOutUserAuthenticationService.authenticate(identity, false, punchoutSessionService.getCurrentPunchOutSession(),
					request, response);

			final CartModel cartModel = cartService.getSessionCart();

			cxml = punchOutService.processPurchaseOrderRequest(requestBody, cartModel);
		}
		else
		{
			final String message = "Unable to find user " + identity;
			LOG.error(message);
			cxml = CXMLBuilder.newInstance().withResponseCode(PunchOutResponseCode.ERROR_CODE_AUTH_FAILED)
					.withResponseMessage(message).create();
		}

		return cxml;
	}

	@ExceptionHandler
	@ResponseBody
	public CXML handleException(final Exception exc)
	{
		LOG.error("Could not process PunchOut Order Request", exc);

		CXML response = null;

		if (exc instanceof PunchOutException)
		{
			final PunchOutException punchoutException = (PunchOutException) exc;
			response = CXMLBuilder.newInstance().withResponseCode(punchoutException.getErrorCode())
					.withResponseMessage(PunchOutException.PUNCHOUT_EXCEPTION_MESSAGE).create();
		}
		else
		{
			response = CXMLBuilder.newInstance().withResponseCode(PunchOutResponseCode.INTERNAL_SERVER_ERROR)
					.withResponseMessage(PunchOutException.PUNCHOUT_EXCEPTION_MESSAGE).create();
		}

		return response;
	}
}
