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
package de.hybris.platform.webservices.processchain;

import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.processor.RequestProcessor;



/**
 * 
 * Configurable chain abstraction. Used to prepare chain parameters proper to its call context as request type , passed
 * in Dto, resource information, default chain execution logic. Used in {@link AbstractResource} via
 * {AbstractResource#lookupConfigurableRequestProcessorChain} method lookup.
 * 
 */
public interface ConfigurableRequestProcessChain<RESOURCE> extends RequestProcessChain
{
	/**
	 * Callback interface for logic covering 'default' logic which should be invoked in case chain ends successfully.
	 */
	interface RequestExecution
	{
		void execute(AbstractResponseBuilder response);
	}

	/**
	 * Configuration method called inside {@link AbstractResource} should be called before {@link #doProcess()}.
	 * Initializes the chain instance with required in current context - request type, passed in DTO ,
	 * {@link AbstractResponseBuilder} and {@link RequestExecution} - for the proper chain processing.
	 * 
	 */
	void configure(RequestProcessor.RequestType type, Object dto, final AbstractResponseBuilder<RESOURCE, ?, ?> result,
			RequestExecution execution);

}
