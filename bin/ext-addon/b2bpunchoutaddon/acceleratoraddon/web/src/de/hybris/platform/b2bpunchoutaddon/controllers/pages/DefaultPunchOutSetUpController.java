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

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.cxml.CXML;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller that handles the Punch Out Setup request.
 */
@Component
public class DefaultPunchOutSetUpController implements PunchOutController
{

	private static final Logger LOG = Logger.getLogger(DefaultPunchOutSetUpController.class);

	@Resource
	private PunchOutService punchOutService;

	/**
	 * Receives a request from the punchout provider and sends it the information to access the hybris site.
	 * 
	 * @param requestBody
	 *           The cXML file with the punchout user requisition.
	 * @return A cXML file with the access information.
	 */
	@RequestMapping(value = "/punchout/cxml/setup", method = RequestMethod.POST)
	@ResponseBody
	public CXML handlePunchOutSetUpRequest(@RequestBody final CXML requestBody)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("cXML: " + requestBody);
		}

		final CXML punchoutSetUpResponse = punchOutService.processPunchOutSetUpRequest(requestBody);
		return punchoutSetUpResponse;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public CXML handleException(final Exception exc)
	{
		LOG.error("Error occurred while processing PunchOut Setup Request", exc);

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
