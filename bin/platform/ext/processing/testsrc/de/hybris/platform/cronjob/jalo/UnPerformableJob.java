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


/**
 * this job isn't performable, at all!!!
 */
public class UnPerformableJob extends TestJob
{
	@Override
	protected boolean canPerform(final CronJob cronJob)
	{
		return false;
	}

	@Override
	protected CronJobResult performCronJob(final CronJob cronJob)
	{
		throw new IllegalStateException("... calling 'performCronJob(final CronJob cronJob)' shouldn't be possible at all!!!");
	}
}
