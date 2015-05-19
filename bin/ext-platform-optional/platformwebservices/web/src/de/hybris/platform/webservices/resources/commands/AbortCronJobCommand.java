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

import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.jalo.Job;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.webservices.AbstractCommand;
import de.hybris.platform.webservices.ServiceLocator;

import org.apache.log4j.Logger;


/**
 * the Command for aborting the cronjob
 */
public class AbortCronJobCommand extends AbstractCommand<CronJobModel>
{

	private static final Logger LOG = Logger.getLogger(AbortCronJobCommand.class);

	@Override
	public Object execute(final CronJobModel resourceEntity, final Object requestEntity) throws Exception
	{

		final ServiceLocator services = this.getResource().getServiceLocator();
		final CronJob cronJob = services.getModelService().getSource(resourceEntity);
		final Job job = services.getModelService().getSource(resourceEntity.getJob());
		if (services.getCronJobService().isRunning(resourceEntity))
		{
			if (job.isAbortable(cronJob))
			{
				resourceEntity.setRequestAbort(Boolean.TRUE);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("AbortCronJobCommand on (" + resourceEntity.getCode() + ") is aborted. The Job: " + job.getCode()
							+ " is abortable.");
				}
			}
			else
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("AbortCronJobCommand on (" + resourceEntity.getCode() + ") isn't aborted. The Job: " + job.getCode()
							+ " isn't abortable.");
				}
			}
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("AbortCronJobCommand on (" + resourceEntity.getCode() + ") isn't aborted. The CronJob is not running.");
			}
		}

		return null;
	}
}
