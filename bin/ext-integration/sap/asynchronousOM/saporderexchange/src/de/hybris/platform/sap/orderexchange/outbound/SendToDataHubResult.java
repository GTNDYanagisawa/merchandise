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
package de.hybris.platform.sap.orderexchange.outbound;

/**
 * Result container for sending raw items to DataHub
 */
public interface SendToDataHubResult
{
	@SuppressWarnings("javadoc")
	int SENDING_FAILED_CODE = -1;
	@SuppressWarnings("javadoc")
	int MESSAGE_HANDLING_ERROR = -2;

	/**
	 * @return true if sending was successful
	 */
	boolean isSuccess();

	@SuppressWarnings("javadoc")
	int getErrorCode();

	@SuppressWarnings("javadoc")
	String getErrorText();
}
