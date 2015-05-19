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
package de.hybris.platform.acceleratorservices.dataexport.generic.impl;

import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.acceleratorservices.model.export.ExportDataCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Job that fires off a hybris event which eventually starts off the pipeline.
 */
public class DefaultExportDataJobPerformable extends AbstractJobPerformable<ExportDataCronJobModel>
{
	private EventService eventService;

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	@Override
	public PerformResult perform(final ExportDataCronJobModel cronJob)
	{
		final ExportDataEvent event = new ExportDataEvent();

		populateEvent(cronJob, event);

		getEventService().publishEvent(event);
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected void populateEvent(final ExportDataCronJobModel cronJob, final ExportDataEvent event)
	{
		event.setCode(cronJob.getCode());
		event.setBaseStore(cronJob.getBaseStore());
		event.setSite(cronJob.getCmsSite());
		event.setLanguage(cronJob.getLanguage());
		event.setCurrency(cronJob.getCurrency());
		event.setUser(cronJob.getUser());
		event.setThirdPartyHost(cronJob.getThirdPartyHost());
		event.setThirdPartyUsername(cronJob.getThirdPartyUsername());
		event.setThirdPartyPassword(cronJob.getThirdPartyPassword());
		event.setDataGenerationPipeline(cronJob.getDataGenerationPipeline());
	}
}
