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
package de.hybris.platform.webservices.processor.impl;

import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.SecurityStrategy;
import de.hybris.platform.webservices.processchain.RequestProcessChain;
import de.hybris.platform.webservices.processor.RequestProcessor;


/**
 * 
 * Processor filter for webservices request implementing access restriction based on {@link SecurityStrategy}.
 * 
 */
public class AccessGrantRequestProcessor<RESOURCE> implements RequestProcessor<RESOURCE>
{
	@Override
	public void doProcess(final RequestProcessor.RequestType type, final Object dto,
			final AbstractResponseBuilder<RESOURCE, ?, ?> responseBuilder, final RequestProcessChain chain)
	{
		final boolean isAllowed = responseBuilder.getResource().isAccessGrantedExternal();

		if (!isAllowed)
		{
			return;
		}

		chain.doProcess();
	}

}
