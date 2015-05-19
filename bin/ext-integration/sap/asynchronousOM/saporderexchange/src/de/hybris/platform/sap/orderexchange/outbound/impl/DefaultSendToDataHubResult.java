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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;

import org.springframework.http.HttpStatus;


/**
 * Container for the Result of the Datahub sending process
 */
@SuppressWarnings("javadoc")
public class DefaultSendToDataHubResult implements SendToDataHubResult
{
	public static final SendToDataHubResult OKAY = new DefaultSendToDataHubResult(HttpStatus.OK, "Sent successfully");
	public static final SendToDataHubResult SENDING_FAILED = new DefaultSendToDataHubResult(
			SendToDataHubResult.SENDING_FAILED_CODE, "Sending failed for unknown reason");
	public static final SendToDataHubResult MESSAGE_HANDLING_ERROR = new DefaultSendToDataHubResult(
			SendToDataHubResult.MESSAGE_HANDLING_ERROR, "Sending failed for unknown reason");
	private final String errorText;
	private HttpStatus httpStatus;
	private final int errorCode;

	public boolean isSuccess()
	{
		return httpStatus != null && getErrorCode() == 200;
	}

	public int getErrorCode()
	{
		return this.errorCode;
	}

	public String getErrorText()
	{
		return errorText;
	}

	public DefaultSendToDataHubResult(final HttpStatus httpStatus, final String errorText)
	{
		this.httpStatus = httpStatus;
		this.errorText = errorText;
		this.errorCode = httpStatus.value();
	}

	public DefaultSendToDataHubResult(final int errorCode, final String errorText)
	{
		this.errorCode = errorCode;
		this.errorText = errorText;
	}

	@Override
	public String toString()
	{
		return "httpStatus=" + httpStatus + ", errorText=" + errorText;
	}
}
