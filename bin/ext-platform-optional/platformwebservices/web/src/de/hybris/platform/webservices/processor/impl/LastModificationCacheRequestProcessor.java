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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.processchain.RequestProcessChain;
import de.hybris.platform.webservices.processor.RequestProcessor;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Request;

import org.apache.log4j.Logger;


/**
 * 
 * Processing filter which calculates the lastModfied marker for a RESOURCE. Performed before default processing. If the
 * result of the {@link Request#evaluatePreconditions(Date)} 'decides' the data is cached
 * {@link #doProcess(de.hybris.platform.webservices.processor.RequestProcessor.RequestType, Object, AbstractResponseBuilder, RequestProcessChain)}
 * , will return false, causing interruption of the chain and avoiding the default processing of the Get response.
 */
public class LastModificationCacheRequestProcessor<RESOURCE> implements RequestProcessor<RESOURCE>
{
	private static final Logger LOG = Logger.getLogger(LastModificationCacheRequestProcessor.class);



	@Override
	public void doProcess(final RequestProcessor.RequestType type, final Object dto,
			final AbstractResponseBuilder<RESOURCE, ?, ?> responseBuilder, final RequestProcessChain chain)
	{
		if (type == RequestProcessor.RequestType.GET)
		{
			final Request request = responseBuilder.getResource().getRequest();
			final RESOURCE resource = responseBuilder.getResourceValue();
			Date lastModfied = new Date(0);


			if (resource instanceof ItemModel)
			{
				final ItemModel internalModel = (ItemModel) resource;
				lastModfied = internalModel.getModifiedtime();
			}
			if (resource instanceof List<?>)
			{
				final List<?> internalModelList = (List<?>) resource;
				for (final Object model : internalModelList)
				{
					if (model instanceof ItemModel && ((ItemModel) model).getModifiedtime() != null)
					{
						if (((ItemModel) model).getModifiedtime().after(lastModfied))
						{
							lastModfied = ((ItemModel) model).getModifiedtime();
						}
					}
				}
			}

			if (doHandle(responseBuilder, request, lastModfied))
			{
				return; //do not process default logic in AbstractResource
			}
		}
		chain.doProcess();

	}

	private boolean doHandle(final AbstractResponseBuilder<RESOURCE, ?, ?> responseBuilder, final Request request,
			final Date lastModfied)
	{
		if (lastModfied == null)
		{
			//none etag or lastmodified marker found
			return handleUncached(responseBuilder, lastModfied);
			///
		}
		else
		{
			if (request.evaluatePreconditions(lastModfied) != null)
			{
				return handleCached(responseBuilder, lastModfied);

			}
			else
			{
				return handleUncached(responseBuilder, lastModfied);
			}
		}
	}

	private boolean handleCached(final AbstractResponseBuilder response, final Date lastModfied)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("The WS call for item " + this + " was received with  last modifed " + lastModfied
					+ " therefore caching was used.");
		}
		response.getResponse().lastModified(lastModfied);//pass also last modification
		response.getResponse().status(304);
		return true;
	}

	private boolean handleUncached(final AbstractResponseBuilder responseBuilder, final Date lastModfied)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("The WS call for item " + responseBuilder.getResource() + " was received with " + "last modifed "
					+ lastModfied + " therefore caching was not used.");
		}
		responseBuilder.getResponse().lastModified(lastModfied);
		return false;
	}

}
