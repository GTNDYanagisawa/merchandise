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
package de.hybris.platform.webservices.resources.commands;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.webservices.AbstractCommand;
import de.hybris.platform.webservices.ServiceLocator;

import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;


/**
 * the Command for starting new cronjob
 */
public class StartCronJobCommand extends AbstractCommand<CronJobModel>
{

	private static final Logger LOG = Logger.getLogger(StartCronJobCommand.class);

	@Override
	public Object execute(final CronJobModel resourceEntity, final Object requestEntity) throws Exception
	{

		boolean synchronous;
		String synchronousStr = null;
		final ServiceLocator services = this.getResource().getServiceLocator();

		final UriInfo uriInfo = this.getResource().getUriInfo();
		if (uriInfo.getQueryParameters().get("synchronous") != null)
		{
			synchronousStr = uriInfo.getQueryParameters().get("synchronous").iterator().next();
		}

		synchronous = Boolean.parseBoolean(synchronousStr);

		services.getCronJobService().performCronJob(resourceEntity, synchronous);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("StartCronJobCommand on (" + resourceEntity.getCode() + ") is completed.");
		}
		return null;

	}
}
