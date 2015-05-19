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
package de.hybris.platform.yb2bacceleratorcore.setup;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncCronJobModel;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.yb2bacceleratorcore.model.MultipleCatalogsSyncCronJobModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Abstraction of the dependent {@link CatalogVersionSyncCronJobModel} performs in determined order for a given by
 * {@link MultipleCatalogsSyncCronJobModel} attributes :
 * <ul>
 * <li>{@link MultipleCatalogsSyncCronJobModel#getContentCatalogs()}</li>
 * <li>{@link MultipleCatalogsSyncCronJobModel#getProductCatalog()}</li>
 * </ul>
 * 
 * @since 4.6
 * @spring.bean powertoolsCatalogSyncJobPerformable
 */
public class MultipleCatalogsSyncJobPerformable extends AbstractJobPerformable<MultipleCatalogsSyncCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(MultipleCatalogsSyncJobPerformable.class.getName());

	private SetupSyncJobService setupSyncJobService;

	@Override
	public PerformResult perform(final MultipleCatalogsSyncCronJobModel cronJob)
	{
		Preconditions.checkNotNull(cronJob, "Given cron job should not be null");
		Preconditions.checkArgument(!CollectionUtils.isEmpty(cronJob.getContentCatalogs()),
				"Given cron job's content catalog should not be empty");
		Preconditions.checkNotNull(cronJob.getProductCatalog(), "Given cron job's product catalog should not be null");

		//adjust dependency in case it is not setup
		final Set<String> contentIds = new HashSet<String>();
		for (final CatalogModel model : cronJob.getContentCatalogs())
		{
			contentIds.add(model.getId());
		}
		getSetupSyncJobService().assignDependentSyncJobs(cronJob.getProductCatalog().getId(), contentIds);

		final List<String> allCatalogs = new ArrayList<String>(contentIds);
		allCatalogs.add(cronJob.getProductCatalog().getId());

		return callInternal(allCatalogs);
	}

	protected PerformResult callInternal(final List<String> allCatalogs)
	{
		PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		try
		{
			for (final String singleCall : allCatalogs)
			{
				final PerformResult singleResult = getSetupSyncJobService().executeCatalogSyncJob(singleCall);
				if (singleResult.getResult() != CronJobResult.SUCCESS)
				{
					result = singleResult;
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("One of the cronjob computations caused an exception : " + e.getMessage());
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
			result = new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		return result;
	}

	protected SetupSyncJobService getSetupSyncJobService()
	{
		return setupSyncJobService;
	}

	@Required
	public void setSetupSyncJobService(final SetupSyncJobService setup)
	{
		this.setupSyncJobService = setup;
	}
}
