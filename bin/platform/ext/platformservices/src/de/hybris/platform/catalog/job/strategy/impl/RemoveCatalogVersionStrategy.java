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
package de.hybris.platform.catalog.job.strategy.impl;

import de.hybris.platform.catalog.job.strategy.RemoveStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.RemoveCatalogVersionCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Collections;

import org.apache.log4j.Logger;


/**
 * Specific remove strategy for a single {@link CatalogVersionModel}.
 */
public class RemoveCatalogVersionStrategy extends AbstractRemoveStrategy implements
		RemoveStrategy<RemoveCatalogVersionCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(RemoveCatalogVersionStrategy.class.getName());

	@Override
	public PerformResult remove(final RemoveCatalogVersionCronJobModel cronJob)
	{

		LOG.info("Started removeCatalogVersionStrategy for a job " + cronJob.getCode() + " for removing catalog version : "
				+ cronJob.getCatalogVersion().getVersion());

		removeCatalogVersionCollection(Collections.singletonList(cronJob.getCatalogVersion()), cronJob, catalogVersionDao
				.getOrderedComposedTypes());

		if (!isAlive(cronJob))
		{
			LOG.info("Could not Remove CatalogVersion " + cronJob.getCatalogVersion());

			return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
		else
		{
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
	}

	protected boolean isAlive(final RemoveCatalogVersionCronJobModel cronJob)
	{
		return ((de.hybris.platform.catalog.jalo.Catalog) modelService.getSource(cronJob.getCatalog())).isAlive();
	}
}
