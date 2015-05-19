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
package de.hybris.platform.yb2bacceleratorcore.setup.impl;

import de.hybris.platform.commerceservices.setup.impl.DefaultSetupSyncJobService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.yb2bacceleratorcore.model.MultipleCatalogsSyncCronJobModel;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * 
 * Specialized {@link DefaultSetupSyncJobService} performing some preconfigured (imported previously)
 * {@link MultipleCatalogsSyncCronJobModel} instance
 * 
 */
public class B2BDefaultSetupSyncJobService extends DefaultSetupSyncJobService
{

	private FlexibleSearchService flexibleSearchService;
	private CronJobService cronJobService;

	private static final String CRON_JOB_SUBFIX = "SyncCronJob";

	@Override
	public PerformResult executeCatalogSyncJob(final String catalog)
	{
		final MultipleCatalogsSyncCronJobModel cronJobModel = getCronJobIfExists(catalog);

		getModelService().save(cronJobModel);

		cronJobService.performCronJob(cronJobModel, true);
		return new PerformResult(cronJobModel.getResult(), cronJobModel.getStatus());
	}


	protected MultipleCatalogsSyncCronJobModel getCronJobIfExists(final String catalog)
	{
		final MultipleCatalogsSyncCronJobModel example = new MultipleCatalogsSyncCronJobModel();
		example.setCode(catalog + CRON_JOB_SUBFIX);

		final MultipleCatalogsSyncCronJobModel cronJobModel = flexibleSearchService.getModelByExample(example);

		Preconditions.checkNotNull(cronJobModel);



		return cronJobModel;
	}

	@Required
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
