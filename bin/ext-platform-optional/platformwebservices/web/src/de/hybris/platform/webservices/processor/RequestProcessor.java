/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.processor;


import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.processchain.RequestProcessChain;




/**
 * 
 * Process chain element for perform specific logic (like javax.servlet.Filter element). Decides if right before the
 * return perform next element in the chain by {@link RequestProcessChain#doProcess()} .
 */
public interface RequestProcessor<RESOURCE>
{
	/**
	 * Enumeration determines what kind of request we are processing
	 */
	static enum RequestType
	{
		GET, POST, PUT, DELETE
	}

	/**
	 * Performed right before {@link de.hybris.platform.webservices.AbstractResponseBuilder#processRequest()}. Result of
	 * that method decides to perform default logic for processing the response.
	 */
	void doProcess(RequestProcessor.RequestType type, Object dto, final AbstractResponseBuilder<RESOURCE, ?, ?> responseBuilder,
			RequestProcessChain chain);

}
