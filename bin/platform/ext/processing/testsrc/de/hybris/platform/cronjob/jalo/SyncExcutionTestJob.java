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
package de.hybris.platform.cronjob.jalo;

import de.hybris.platform.cronjob.jalo.CronJob.CronJobResult;

import org.apache.log4j.Logger;


public class SyncExcutionTestJob extends Job
{
	private static final Logger log = Logger.getLogger(SyncExcutionTestJob.class);

	@Override
	protected CronJobResult performCronJob(final CronJob cronJob) throws AbortCronJobException
	{
		final CronJob cjToPerform = (CronJob) cronJob.getProperty("nested");

		cjToPerform.getJob().perform(cjToPerform, true);

		log.warn("outer");

		return cronJob.getFinishedResult(true);
	}
}
