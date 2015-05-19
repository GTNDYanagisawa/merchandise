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
package de.hybris.platform.webservices.processchain.impl;

import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.processchain.ConfigurableRequestProcessChain;
import de.hybris.platform.webservices.processor.RequestProcessor;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Implementation of the chain is responsible for processing all {@link RequestProcessor} starting from list
 * {@link #processors}. Every {@link RequestProcessor} itself decides if it want to fire the successor.
 * 
 */
public class DefaultRequestProcessChain<RESOURCE> implements ConfigurableRequestProcessChain<RESOURCE>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultRequestProcessChain.class);

	private List<RequestProcessor<RESOURCE>> processors;

	/**
	 * Execution context for a default logic after the chain finishes.
	 */
	private RequestExecution execution;

	/**
	 * Type of the request for the chain.
	 */
	private RequestProcessor.RequestType type;

	/**
	 * DTO passed in to operate on.
	 */
	private Object dto;

	/**
	 * The int which is used to maintain the current position in the processor chain.
	 */
	private int currentProcessor = 0;

	/**
	 * Response builder object to get underlying resource/model.
	 */
	private AbstractResponseBuilder<RESOURCE, ?, ?> responseBuilder;

	@Override
	public void configure(final RequestProcessor.RequestType type, final Object dto,
			final AbstractResponseBuilder<RESOURCE, ?, ?> result, final RequestExecution execution)
	{
		if (currentProcessor > 0)
		{
			throw new IllegalStateException("Chain configuration called while chain is triggered already");
		}
		this.type = type;
		this.responseBuilder = result;
		this.execution = execution;
		this.dto = dto;
	}

	@Required
	public void setProcessors(final List<RequestProcessor<RESOURCE>> processors)
	{
		if (processors == null)
		{
			throw new IllegalArgumentException("Processor list can not be null");
		}
		this.processors = processors;
	}

	@Override
	public void doProcess()
	{
		if (processors == null)
		{
			throw new IllegalStateException("No processors configured");
		}
		if (type == null)
		{
			throw new IllegalStateException("Configuration required before starting chain.");
		}
		// Call the next processor if there is one
		if (currentProcessor < processors.size())
		{
			final RequestProcessor<RESOURCE> processor = processors.get(currentProcessor++);

			processor.doProcess(type, dto, responseBuilder, this);
		}
		// We fell off the end of the chain -- call the execution instance
		else
		{
			execution.execute(responseBuilder);
		}
	}

}
