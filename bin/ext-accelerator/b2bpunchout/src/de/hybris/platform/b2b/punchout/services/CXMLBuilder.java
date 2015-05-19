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
package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.punchout.util.CXmlDateUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.lang.math.RandomUtils;
import org.cxml.CXML;
import org.cxml.Response;
import org.cxml.Status;
import org.joda.time.DateTime;


public final class CXMLBuilder
{
	private static final int RANDOM_INT_LIMIT = 10000;

	private final CXmlDateUtil dateUtil = new CXmlDateUtil();

	private final CXML cXML;

	private final CXMLElementBrowser cxmlElementBrowser;

	private String responseMessage;

	private String responseCode;

	private CXMLBuilder()
	{
		cXML = createNewCXML();
		cxmlElementBrowser = new CXMLElementBrowser(cXML);
	}

	public static CXMLBuilder newInstance()
	{
		return new CXMLBuilder();
	}

	private CXML createNewCXML()
	{
		final CXML instance = new CXML();

		instance.setTimestamp(dateUtil.formatDate(new Date()));
		instance.setPayloadID(generatePayload());
		// xml:lang is optional according to documentation and accepts default values, we set it
		// to en as default for now to simplify implementation
		instance.setXmlLang("en-US");
		return instance;
	}

	/**
	 * Generates a new payload ID in the required format: datetime.process id.random number@hostname. Note: Process ID is
	 * skipped as there's no easy way to get it.
	 * 
	 * @return the payload ID
	 */
	private String generatePayload()
	{
		return String.format("%s.%s@%s", Long.valueOf(new DateTime().getMillis()),
				Integer.valueOf(RandomUtils.nextInt(RANDOM_INT_LIMIT)), findHostName());
	}

	/**
	 * @return the host name or just 'localhost' if not able to determine it
	 */
	private String findHostName()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch (final UnknownHostException e)
		{
			return "localhost";
		}
	}

	/**
	 * @return the newly created {@link CXML} instance
	 */
	public CXML create()
	{
		if (responseCode != null || responseMessage != null)
		{
			final Status status = new Status();
			status.setCode(responseCode);
			status.setText(responseMessage);

			if (cxmlElementBrowser.hasResponse())
			{
				final Response response = cxmlElementBrowser.findResponse();
				response.setStatus(status);
			}
			else
			{
				final Response response = new Response();
				response.setStatus(status);
				cXML.getHeaderOrMessageOrRequestOrResponse().add(response);

			}
		}
		return cXML;
	}

	public CXMLBuilder withResponseCode(final String code)
	{
		this.responseCode = code;
		return this;
	}

	public CXMLBuilder withResponseMessage(final String message)
	{
		this.responseMessage = message;
		return this;
	}


}
